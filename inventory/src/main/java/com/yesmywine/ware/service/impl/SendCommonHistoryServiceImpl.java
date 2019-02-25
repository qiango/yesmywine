package com.yesmywine.ware.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.SynchronizeUtils;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.ware.dao.SendCommonHistoryDao;
import com.yesmywine.ware.entity.*;
import com.yesmywine.ware.service.ChannelsInventoryService;
import com.yesmywine.ware.service.ChannelsService;
import com.yesmywine.ware.service.SendCommonHistoryService;
import com.yesmywine.ware.service.WarehousesChannelService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SJQ on 2017/4/17.
 */
@Service
@Transactional
public class SendCommonHistoryServiceImpl extends BaseServiceImpl<SendCommonHistory, Integer> implements SendCommonHistoryService {
    @Autowired
    private WarehousesChannelService warehouseChannelService;
    @Autowired
    private ChannelsInventoryService channelsInventoryService;
    @Autowired
    private ChannelsService channelsService;
    @Autowired
    private SendCommonHistoryDao sendCommonHistoryDao;

    @Override
    public String sentCommonInventory(Integer id, Integer[] channelIds, Integer[] counts) throws Exception {
        WarehousesChannel warehouseChannel = warehouseChannelService.findOne(id);
        if (null == warehouseChannel) {
            ValueUtil.isError("此仓库下无通用库存");
        }
        Integer skuId = warehouseChannel.getSkuId();
        Channels channels = warehouseChannel.getChannel();
        Warehouses warehouse = warehouseChannel.getWarehouse();
        Integer overCount = warehouseChannel.getOverall();
        ValueUtil.verify(id);
        ValueUtil.verify(channelIds);
        ValueUtil.verify(counts);
        Integer count_plus = 0;
        List<SendCommonHistory> historyList = new ArrayList<>();
        for (int i = 0; i < channelIds.length; i++) {
            Integer channelId = channelIds[i];
            Integer count = counts[i];
            count_plus += count;
            Channels targetChannel = channelsService.findOne(channelId);
            if (targetChannel == null) {
                ValueUtil.isError("无目标渠道！");
            }
            WarehousesChannel isExist = warehouseChannelService.findByChannelCodeAndWarehouseCodeAndSkuCode(targetChannel.getChannelCode(), warehouse.getWarehouseCode(), warehouseChannel.getSkuCode());
            if (null == isExist) {
                WarehousesChannel warehouseChannel2 = new WarehousesChannel();
                warehouseChannel2.setChannel(targetChannel);
                warehouseChannel2.setChannelCode(targetChannel.getChannelCode());
                warehouseChannel2.setSkuId(warehouseChannel.getSkuId());
                warehouseChannel2.setSkuCode(warehouseChannel.getSkuCode());
                warehouseChannel2.setSkuName(warehouseChannel.getSkuName());
                warehouseChannel2.setWarehouse(warehouse);
                warehouseChannel2.setWarehouseCode(warehouse.getWarehouseCode());
                warehouseChannel2.setOverall(count);
                warehouseChannel2.setUseCount(count);
                warehouseChannel2.setFreezeCount(0);
                warehouseChannel2.setEnRouteCount(0);
                warehouseChannelService.save(warehouseChannel2);

                ChannelsInventory channelsInventory = channelsInventoryService.findByChannelCodeAndSkuCode(targetChannel.getChannelCode(), warehouseChannel.getSkuCode());
                if (null == channelsInventory) {
                    ChannelsInventory channelsInventory1 = new ChannelsInventory();
                    channelsInventory1.setSkuId(warehouseChannel.getSkuId());
                    channelsInventory1.setSkuCode(warehouseChannel.getSkuCode());
                    channelsInventory1.setSkuName(warehouseChannel.getSkuName());
                    channelsInventory1.setAllCount(count);
                    channelsInventory1.setUseCount(count);
                    channelsInventory1.setFreezeCount(0);
                    channelsInventory1.setEnRouteCount(0);
                    channelsInventory1.setChannel(targetChannel);
                    channelsInventory1.setChannelCode(targetChannel.getChannelCode());
                    channelsInventoryService.save(channelsInventory1);
                } else {
                    channelsInventory.setAllCount(channelsInventory.getAllCount() + count);
                    channelsInventory.setUseCount(channelsInventory.getUseCount() + count);
                    channelsInventoryService.save(channelsInventory);
                }
            } else {
                isExist.setUseCount(isExist.getUseCount() + count);
                isExist.setOverall(isExist.getOverall() + count);
                warehouseChannelService.save(isExist);

                ChannelsInventory channelsInventory = channelsInventoryService.findByChannelCodeAndSkuCode(isExist.getChannelCode(), warehouseChannel.getSkuCode());
                channelsInventory.setAllCount(channelsInventory.getAllCount() + count);
                channelsInventory.setUseCount(channelsInventory.getUseCount() + count);
                channelsInventoryService.save(channelsInventory);
            }

            SendCommonHistory sendCommonHistory = new SendCommonHistory();
            sendCommonHistory.setSkuId(warehouseChannel.getSkuId());
            sendCommonHistory.setSkuCode(warehouseChannel.getSkuCode());
            sendCommonHistory.setSkuName(warehouseChannel.getSkuName());
            sendCommonHistory.setChannel(targetChannel);
            sendCommonHistory.setWarehouse(warehouseChannel.getWarehouse());
            sendCommonHistory.setCount(count);
            //向商城同步数据
            if (targetChannel.getChannelCode().equals("GW") || targetChannel.getChannelCode().equals("HT")) {
                JSONObject sendCommonJson = new JSONObject();
                sendCommonJson.put("skuId", warehouseChannel.getSkuId());
                sendCommonJson.put("skuCode", warehouseChannel.getSkuCode());
                sendCommonJson.put("count", count);
                String result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channelInventory/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "stock", sendCommonJson), RequestMethod.post);
                if (result == null || !result.equals("201")) {
                    sendCommonHistory.setSynStatus(0);
                }
            }
            historyList.add(sendCommonHistory);
        }
        //记录分配信息
        sendCommonHistoryDao.save(historyList);
        if (overCount < count_plus) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("已分配数量大于可分配数量！");
        }
        warehouseChannel.setOverall(overCount - count_plus);
        warehouseChannel.setUseCount(warehouseChannel.getUseCount() - count_plus);
        warehouseChannelService.save(warehouseChannel);
        //修改同用渠道总库存
        List<Channels> channelsList = channelsService.findByType(3);
        Channels queryChannels = channelsList.get(0);
        ChannelsInventory channelsInventory = channelsInventoryService.findByChannelCodeAndSkuCode(queryChannels.getChannelCode(), warehouseChannel.getSkuCode());
        channelsInventory.setAllCount(channelsInventory.getAllCount() - count_plus);
        channelsInventory.setUseCount(channelsInventory.getUseCount() - count_plus);
        channelsInventoryService.save(channelsInventory);
        return "SUCCESS";
    }
}
