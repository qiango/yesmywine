package com.yesmywine.ware.controller;

import com.alibaba.fastjson.JSONObject;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.SynchronizeUtils;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.bean.HistoryType;
import com.yesmywine.ware.entity.*;
import com.yesmywine.ware.service.*;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by SJQ on 2017/4/17.
 */
@RestController
@RequestMapping("/inventory/resynchronize")
public class ResynchronizeController {
    @Autowired
    private WarehousesHistoryService warehouseHistoryService;
    @Autowired
    private ReleaseFreezeRecordService releaseFreezeRecordService;
    @Autowired
    private SendCommonHistoryService sendCommonHistoryService;
    @Autowired
    private SendChannelHistoryService sendChannelHistoryService;
    @Autowired
    private AdjustInvoicesService adjustInvoicesService;

    /*
    *@Author Gavin
    *@Description   出入库单，重新同步
    *@Date 2017/3/28 17:44
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/opip", method = RequestMethod.PUT)
    public String OIResynchronize(Integer id) {
        try {
            ValueUtil.verify(id);
            WarehousesHistory warehouseHistory = warehouseHistoryService.findOne(id);
            if (warehouseHistory == null) {
                ValueUtil.isError("无此数据");
            }
            Integer skuId = warehouseHistory.getSkuId();
            String skuCode = warehouseHistory.getSkuCode();
            String skuName = warehouseHistory.getSkuName();
            Integer count = warehouseHistory.getCount();
            String type = warehouseHistory.getType();

            Channels channels = warehouseHistory.getChannel();

            //同步商城或海淘
            if (channels.getChannelCode().equals("GW") || channels.getChannelCode().equals("HT")) {
                JSONObject skuJSON = new JSONObject();
                skuJSON.put("skuId", skuId);
                skuJSON.put("skuCode", skuCode);
                skuJSON.put("skuName", skuName);
                skuJSON.put("count", count);
                String result = null;
                if (type.equals(HistoryType.IN)) {
                    result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channelInventory/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "stock", skuJSON), com.yesmywine.httpclient.bean.RequestMethod.post);
                } else {
//                    result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channelInventory/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "sub", skuJSON), com.yesmywine.httpclient.bean.RequestMethod.post);
                }
                if (result == null || !result.equals("201")) {
                    ValueUtil.isError("同步失败！");
                    //发送站内信
                } else if (result.equals("201")) {
                    warehouseHistory.setSynStatus(1);
                    warehouseHistoryService.save(warehouseHistory);
                }

            }

            return ValueUtil.toJson(HttpStatus.SC_CREATED, "success");
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return ValueUtil.toError(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR), e.getMessage());
        }
    }

    /*
    *@Author Gavin
    *@Description   释放冻结，重新同步
    *@Date 2017/3/28 17:44
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/releaseFreeze", method = RequestMethod.PUT)
    public String releaseFreezeResynchronize(Integer id) {
        try {
            ValueUtil.verify(id);
            ReleaseFreezeFailedRecord releaseFreezeFailedRecord = releaseFreezeRecordService.findOne(id);
            if (releaseFreezeFailedRecord == null) {
                ValueUtil.isError("无此数据");
            }
            Integer skuId = releaseFreezeFailedRecord.getSkuId();
            String skuCode = releaseFreezeFailedRecord.getSkuCode();
            Integer count = releaseFreezeFailedRecord.getCount();
            //同步商城或海淘
            JSONObject jsonData = new JSONObject();
            jsonData.put("skuId", skuId);
            jsonData.put("skuCode", skuCode);
            jsonData.put("count", -count);
            String result = null;
            result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channelInventory/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "releaseFreeze", jsonData), com.yesmywine.httpclient.bean.RequestMethod.post);
            if (result == null || !result.equals("201")) {
                ValueUtil.isError("同步失败！");
                //发送站内信
            } else if (result.equals("201")) {
                releaseFreezeFailedRecord.setSynStatus(1);
                releaseFreezeRecordService.save(releaseFreezeFailedRecord);
            }
            return ValueUtil.toJson(HttpStatus.SC_CREATED, "同步成功", "success");
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return ValueUtil.toError(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR), e.getMessage());
        }
    }

    /*
    *@Author Gavin
    *@Description   分配渠道库存，重新同步
    *@Date 2017/3/28 17:44
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/sendChannel", method = RequestMethod.PUT)
    public String sendChannelResynchronize(Integer id) {
        try {
            ValueUtil.verify(id);
            SendChannelHistory sendChannelHistory = sendChannelHistoryService.findOne(id);
            if (sendChannelHistory == null) {
                ValueUtil.isError("无此数据");
            }
            Integer skuId = sendChannelHistory.getSkuId();
            String skuCode = sendChannelHistory.getSkuCode();
            Integer count = sendChannelHistory.getCount();
            Channels channels = sendChannelHistory.getChannel();
            //同步商城或海淘
            if (channels.getChannelCode().equals("GW") || channels.getChannelCode().equals("HT")) {
                JSONObject jsonData = new JSONObject();
                jsonData.put("skuId", skuId);
                jsonData.put("skuCode", skuCode);
                jsonData.put("count", count);
                String result = null;
                result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channelInventory/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "sub", jsonData), com.yesmywine.httpclient.bean.RequestMethod.post);
                if (result == null || !result.equals("201")) {
                    ValueUtil.isError("同步失败！");
                    //发送站内信
                } else if (result.equals("201")) {
                    sendChannelHistory.setSynStatus(1);
                    sendChannelHistoryService.save(sendChannelHistory);
                }

            }
            return ValueUtil.toJson(HttpStatus.SC_CREATED, "同步成功", "success");
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return ValueUtil.toError(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR), e.getMessage());
        }
    }

    /*
    *@Author Gavin
    *@Description   分配通用库存，重新同步
    *@Date 2017/3/28 17:44
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/sendCommon", method = RequestMethod.PUT)
    public String sendCommonResynchronize(Integer id) {
        try {
            ValueUtil.verify(id);
            SendCommonHistory sendCommonHistory = sendCommonHistoryService.findOne(id);
            if (sendCommonHistory == null) {
                ValueUtil.isError("无此数据");
            }
            Integer skuId = sendCommonHistory.getSkuId();
            String skuCode = sendCommonHistory.getSkuCode();
            Integer count = sendCommonHistory.getCount();
            Channels channels = sendCommonHistory.getChannel();
            //同步商城或海淘
            if (channels.getChannelCode().equals("GW") || channels.getChannelCode().equals("HT")) {
                JSONObject jsonData = new JSONObject();
                jsonData.put("skuId", skuId);
                jsonData.put("skuCode", skuCode);
                jsonData.put("count", count);
                String result = null;
                result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channelInventory/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "sub", jsonData), com.yesmywine.httpclient.bean.RequestMethod.post);
                if (result == null || !result.equals("201")) {
                    ValueUtil.isError("同步失败！");
                    //发送站内信
                } else if (result.equals("201")) {
                    sendCommonHistory.setSynStatus(1);
                    sendCommonHistoryService.save(sendCommonHistory);
                }

            }
            return ValueUtil.toJson(HttpStatus.SC_CREATED, "同步成功", "success");
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return ValueUtil.toError(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR), e.getMessage());
        }
    }

    /*
    *@Author Gavin
    *@Description   调整单，重新同步
    *@Date 2017/3/28 17:44
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/adjust", method = RequestMethod.PUT)
    public String adjustResynchronize(Integer id) {
        try {
            ValueUtil.verify(id);
            AdjustInvoices adjustInvoices = adjustInvoicesService.findOne(id);
            if (adjustInvoices == null) {
                ValueUtil.isError("无此数据");
            }
            Integer skuId = adjustInvoices.getSkuId();
            String skuCode = adjustInvoices.getSkuCode();
            Integer count = adjustInvoices.getCount();
            Channels channels = adjustInvoices.getChannel();
            //同步商城或海淘
            if (channels.getChannelCode().equals("GW") || channels.getChannelCode().equals("HT")) {
                JSONObject jsonData = new JSONObject();
                jsonData.put("skuId", skuId);
                jsonData.put("skuCode", skuCode);
                String result = null;

                if (count < 0) {
                    jsonData.put("count", -count);
                    result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channelInventory/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "sub", jsonData), com.yesmywine.httpclient.bean.RequestMethod.post);
                } else {
                    jsonData.put("count", count);
                    result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channelInventory/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "sub", jsonData), com.yesmywine.httpclient.bean.RequestMethod.post);
                }

                if (result == null || !result.equals("201")) {
                    ValueUtil.isError("同步失败！");
                    //发送站内信
                } else if (result.equals("201")) {
                    adjustInvoices.setSynStatus(1);
                    adjustInvoicesService.save(adjustInvoices);
                }

            }
            return ValueUtil.toJson(HttpStatus.SC_CREATED, "同步成功", "success");
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return ValueUtil.toError(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR), e.getMessage());
        }
    }
}
