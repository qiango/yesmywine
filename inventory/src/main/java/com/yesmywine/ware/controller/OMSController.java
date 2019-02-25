package com.yesmywine.ware.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.util.basic.ExcelHelper;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.entity.ChannelsInventory;
import com.yesmywine.ware.entity.WarehousesChannel;
import com.yesmywine.ware.service.ChannelsInventoryService;
import com.yesmywine.ware.service.WarehousesChannelService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

/**
 * Created by SJQ on 2017/4/5.
 */
@RestController
@RequestMapping("/inventory/oms")
public class OMSController {

    @Autowired
    private ChannelsInventoryService channelsInventoryService;

    @Autowired
    private WarehousesChannelService warehouseChannelService;

    @RequestMapping(method = RequestMethod.GET)
    public String imports() {
        String path = "C:\\Users\\Administrator\\Desktop\\excel.xlsx";
        List<String> list = null;
        try {
            list = ExcelHelper.exportListFromExcel(new File(path), 0);
            assertNotNull(list);
            JSONArray array = new JSONArray();
            for (String str : list) {
                String[] dataArray = str.split(";");
                String channelCode = dataArray[1];
                String warehouseCode = dataArray[2];
                String skuCode = dataArray[3];
                Integer count = Integer.valueOf(Double.valueOf(dataArray[4]).intValue());
                JSONObject object = new JSONObject();
                object.put("channelCode", channelCode);
                object.put("warehouseCode", warehouseCode);
                object.put("skuCode", skuCode);
                object.put("count", count);
                array.add(object);
                if (array.size() % 500 == 0) {
                    stock(array.toJSONString(), null);
                    array.clear();
                }
            }
            stock(array.toJSONString(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ValueUtil.toJson(list);
    }

    /*
    *@Author SJQ
    *@Description 查看仓库库存表
    *@CreateTime
    *@Params
    */
    @RequestMapping(value = "/cwIndex", method = RequestMethod.POST)
    public String warehouseChannelIndex(String jsonData) {
        try {
            JSONArray jsonArray = JSON.parseArray(jsonData);
            JSONArray jsonArray1 = new JSONArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String skuCode = jsonObject.getString("skuCode");
                String channelCode = jsonObject.getString("channelCode");
                String warehouseCode = jsonObject.getString("warehouseCode");
                ValueUtil.verify(skuCode, "skuCode");
                ValueUtil.verify(channelCode, "channelCode");
                ValueUtil.verify(warehouseCode, "warehouseCode");
                WarehousesChannel warehouseChannel = warehouseChannelService.findByChannelCodeAndWarehouseCodeAndSkuCode(channelCode, warehouseCode, skuCode);
                if (warehouseChannel == null) {
                    ValueUtil.isError("skuCode:" + skuCode + "   channelCode:" + channelCode
                            + "   warehouseCode:" + warehouseCode + "   无此数据，请联系管理员核对");
                }
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("skuCode", warehouseChannel.getSkuCode());
                jsonObject1.put("skuName", warehouseChannel.getSkuName());
                jsonObject1.put("channelCode", warehouseChannel.getChannelCode());
                jsonObject1.put("warehouseCode", warehouseChannel.getWarehouseCode());
                jsonObject1.put("count", warehouseChannel.getUseCount());
                jsonArray1.add(jsonObject1);
            }
            return ValueUtil.toJson(jsonArray1);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    /*
    *@Author SJQ
    *@Description oms采购入库
    *@CreateTime
    *@Params
    */
    @RequestMapping(value = "/stock", method = RequestMethod.POST)
    public String stock(String jsonData, String storeCode) {
        try {
            String result = channelsInventoryService.omsStock(jsonData, storeCode);
            return ValueUtil.toJson(HttpStatus.SC_CREATED, result);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }


    /*
    *@Author Gavin
    *@Description oms查询渠道库存
    *@Date 2017/3/28 16:02
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/cIndex", method = RequestMethod.POST)
    public String channelInventoryIndex(String jsonData) {
        try {
            JSONArray jsonArray = JSON.parseArray(jsonData);
            JSONArray jsonArray1 = new JSONArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String skuCode = jsonObject.getString("skuCode");
                String channelCode = jsonObject.getString("channelCode");
                ValueUtil.verify(skuCode, "skuCode");
                ValueUtil.verify(channelCode, "channelCode");
                ChannelsInventory channelsInventory = channelsInventoryService.findByChannelCodeAndSkuCode(channelCode, skuCode);
                if (channelsInventory == null) {
                    ValueUtil.isError("skuCode:" + skuCode + "   channelCode:" + channelCode
                            + "   无此数据，请联系管理员核对");
                }

                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("skuCode", channelsInventory.getSkuCode());
                jsonObject1.put("skuName", channelsInventory.getSkuName());
                jsonObject1.put("channelCode", channelsInventory.getChannelCode());
                jsonObject1.put("count", channelsInventory.getUseCount());
                jsonArray1.add(jsonObject1);
            }
            return ValueUtil.toJson(jsonArray1);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    /*
    *@Author Gavin
    *@Description oms申请冻结库存接口
    *@Date 2007/3/16 15:31
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/freeze", method = RequestMethod.POST)
    public String freeze(String jsonData) {
        try {
            String result = channelsInventoryService.freeze(jsonData);
            return ValueUtil.toJson(HttpStatus.SC_CREATED, result);
        } catch (yesmywineException e) {
            return ValueUtil.toJson(e.getCode(), e.getMessage());
        }
    }

    /*
    *@Author Gavin
    *@Description oms扣减库存通知
    *@Date 2007/3/16 16:18
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/subFreeze", method = RequestMethod.POST)
    public String subFreeze(String jsonData) {
        try {
            String result = channelsInventoryService.subFreeze(jsonData);
            return ValueUtil.toJson(HttpStatus.SC_CREATED, result);
        } catch (yesmywineException e) {
            return ValueUtil.toJson(e.getCode(), e.getMessage());
        }
    }

    /*
    *@Author Gavin
    *@Description 取消订单 oms释放冻结库存通知
    *@Date 2007/3/16 16:18
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/releaseFreeze", method = RequestMethod.POST)
    public String releaseFreeze(String jsonData) {
        try {
            String result = channelsInventoryService.releaseFreeze(jsonData);
            return ValueUtil.toJson(HttpStatus.SC_CREATED, result);
        } catch (yesmywineException e) {
            return ValueUtil.toJson(e.getCode(), e.getMessage());
        }
    }
}
