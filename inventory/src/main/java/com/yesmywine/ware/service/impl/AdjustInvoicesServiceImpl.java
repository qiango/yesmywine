package com.yesmywine.ware.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.SynchronizeUtils;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.Utils.SKUUtils;
import com.yesmywine.ware.dao.AdjustInvoicesDao;
import com.yesmywine.ware.dao.StoresDao;
import com.yesmywine.ware.dao.WarehouseDao;
import com.yesmywine.ware.entity.*;
import com.yesmywine.ware.service.AdjustInvoicesService;
import com.yesmywine.ware.service.ChannelsInventoryService;
import com.yesmywine.ware.service.WarehousesChannelService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by SJQ on 2017/4/1.
 */
@Service
@Transactional
public class AdjustInvoicesServiceImpl extends BaseServiceImpl<AdjustInvoices, Integer> implements AdjustInvoicesService {

    @Autowired
    private WarehouseDao warehouseDao;

    @Autowired
    private AdjustInvoicesDao adjustInvoicesDao;

    @Autowired
    private ChannelsInventoryService channelsInventoryService;

    @Autowired
    private WarehousesChannelService warehouseChannelService;

    @Autowired
    private StoresDao storesDao;

    @Override
    public String adjustCommand(String jsonData, String storeCode) throws yesmywineException {

        JSONObject adjustObject = null;
        try {
            adjustObject = JSON.parseObject(URLDecoder.decode(jsonData, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JSONArray adjustWarehosueArray = adjustObject.getJSONObject("xmldata").getJSONObject("data").getJSONArray("orderinfo");

        for (int m = 0; m < adjustWarehosueArray.size(); m++) {
            JSONObject adjustWarehosueObj = (JSONObject) adjustWarehosueArray.get(m);
            String wmsWarehouseCode = adjustWarehosueObj.getString("WarehouseID");
            JSONArray adjustArray = adjustWarehosueObj.getJSONArray("item");
            for (int k = 0; k < adjustArray.size(); k++) {
                JSONObject jsonObject = (JSONObject) adjustArray.get(k);
                jsonObject.put("warehouseCode", wmsWarehouseCode);
            }

            if (storeCode != null) {
                Stores stores = storesDao.findByStoreCode(storeCode);
                Channels channels = stores.getChannel();
                Warehouses warehouses = stores.getWarehouse();
                if (channels == null || warehouses == null) {
                    ValueUtil.isError("门店尚未关联渠道或仓库，无法进行入库操作");
                }
                String channelCode = channels.getChannelCode();
                String storeWarehouseCode = warehouses.getWarehouseCode();

                for (int k = 0; k < adjustArray.size(); k++) {
                    JSONObject jsonObject = (JSONObject) adjustArray.get(k);
                    jsonObject.put("channelCode", channelCode);
                    jsonObject.put("warehouseCode", storeWarehouseCode);
                }

            }
            for (int i = 0; i < adjustArray.size(); i++) {
                JSONObject adjustCommand = (JSONObject) adjustArray.get(i);
                String skuCode = adjustCommand.getString("SKU");
                String skuInfo = SKUUtils.getResult(Dictionary.PAAS_HOST, "/goods/sku/code", RequestMethod.get, "code", skuCode);
                if (skuInfo == null) {
                    ValueUtil.isError("无此SKU或商品服务已关闭");
                }
                String skuId = ValueUtil.getFromJson(skuInfo, "data", "id");
                String skuName = ValueUtil.getFromJson(skuInfo, "data", "sku");
                String channelCode = adjustCommand.getString("channelCode");
                String warehouseCode = adjustCommand.getString("warehouseCode");
                Warehouses warehouse = warehouseDao.findByWarehouseCode(warehouseCode);
                ValueUtil.verifyNotExist(warehouse, warehouseCode + "仓库不存在");
                List<WarehousesChannel> warehouseChannelList = warehouseChannelService.findByWarehouseCodeAndSkuCodeOrderByUseCountDesc(warehouseCode, skuCode);
                ValueUtil.verifyNotExist(warehouse, "此sku在仓库中不存在");

                Integer count = Integer.valueOf(adjustCommand.getString("Qty"));//调整数量
                String taskId = adjustCommand.getString("taskId");
                WarehousesChannel warehouseChannel = null;
                if (warehouseChannelList.get(0).getChannel().getId().equals(9999)) {
                    warehouseChannel = warehouseChannelList.get(1);
                } else {
                    warehouseChannel = warehouseChannelList.get(0);
                }
                Channels channels = warehouseChannel.getChannel();
                if (warehouseChannel.getUseCount() < count && count < 0) {
                    ValueUtil.isError("要调整的最大渠道仓库库存小于要扣减库存！");
                }
                warehouseChannel.setUseCount(warehouseChannel.getUseCount() + count);
                warehouseChannel.setOverall(warehouseChannel.getOverall() + count);

                ChannelsInventory channelsInventory = channelsInventoryService.findByChannelAndSkuId(channels, Integer.valueOf(skuId));
                channelsInventory.setUseCount(channelsInventory.getUseCount() + count);
                channelsInventory.setAllCount(channelsInventory.getAllCount() + count);

                AdjustInvoices adjustInvoices = new AdjustInvoices();
                adjustInvoices.setChannel(channels);
                adjustInvoices.setWarehouse(warehouse);
                adjustInvoices.setCount(count);
                adjustInvoices.setSkuId(Integer.valueOf(skuId));
                adjustInvoices.setSkuCode(skuCode);
                adjustInvoices.setSkuName(skuName);
                adjustInvoices.setTaskId(taskId);

                warehouseChannelService.save(warehouseChannel);
                channelsInventoryService.save(channelsInventory);
                if (storeCode != null) {//门店调整单
                    adjustInvoices.setType("store");
                } else {//wms调整单
                    adjustInvoices.setType("wms");
                }
                adjustInvoicesDao.save(adjustInvoices);

                //同步商城或海淘
                if (channels.getChannelCode().equals("GW") || channels.getChannelCode().equals("HT")) {
                    JSONObject adjustJSON = new JSONObject();
                    adjustJSON.put("skuId", channelsInventory.getSkuId());
                    adjustJSON.put("skuCode", channelsInventory.getSkuCode());
                    String result = null;
                    if (count < 0) {
                        adjustJSON.put("count", -count);
                        result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channelInventory/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "sub", adjustJSON), RequestMethod.post);

                    } else {
                        adjustJSON.put("count", count);
                        result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channelInventory/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "stock", adjustJSON), RequestMethod.post);

                    }
                    if (result == null || !result.equals("201")) {
                        adjustInvoices.setSynStatus(0);
                        adjustInvoicesDao.save(adjustInvoices);
                        //发送站内信
                    }
                }

            }
        }


        return "SUCCESS";
    }
}
