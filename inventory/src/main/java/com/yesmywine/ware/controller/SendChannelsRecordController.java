package com.yesmywine.ware.controller;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.util.basic.MapUtil;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.Utils.Utils;
import com.yesmywine.ware.entity.Channels;
import com.yesmywine.ware.entity.SendChannelHistoryDetails;
import com.yesmywine.ware.entity.Warehouses;
import com.yesmywine.ware.service.SendChannelHistoryService;
import com.yesmywine.ware.entity.SendChannelHistory;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by SJQ on 2017/4/19.
 * 分配渠道库存
 */
@RestController
@RequestMapping("/inventory/sendChannel")
public class SendChannelsRecordController {
    @Autowired
    private SendChannelHistoryService sendChannelHistoryService;

    /*
    *@Author SJQ
    *@Description 分配记录列表
    *@CreateTime
    *@Params
    */
    @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize, Integer id) {
        try {
            MapUtil.cleanNull(params);

            if (id != null) {
                SendChannelHistory sendCommonHistory = sendChannelHistoryService.findOne(id);
                ValueUtil.verifyNotExist(sendCommonHistory, "无此分配记录");
                return ValueUtil.toJson(sendCommonHistory);
            }

            if (null != params.get("all") && params.get("all").toString().equals("true")) {
                return ValueUtil.toJson(sendChannelHistoryService.findAll());
            } else if (null != params.get("all")) {
                params.remove(params.remove("all").toString());
            }

            if (params.get("channelId") != null && Utils.isNum(params.get("channelId").toString())) {
                Integer channelId = Integer.valueOf(params.get("channelId").toString());
                Channels channels = new Channels();
                channels.setId(channelId);
                params.remove(params.remove("channelId").toString());
                params.put("channel", channels);
            } else if (params.get("channelId_ne") != null && Utils.isNum(params.get("channelId_ne").toString())) {
                Integer channelId = Integer.valueOf(params.get("channelId_ne").toString());
                Channels channels = new Channels();
                channels.setId(channelId);
                params.remove(params.remove("channelId_ne").toString());
                params.put("channel_ne", channels);
            }

            if (params.get("warehouseId") != null && Utils.isNum(params.get("warehouseId").toString())) {
                Integer warehouseId = Integer.valueOf(params.get("warehouseId").toString());
                Warehouses warehouse = new Warehouses();
                warehouse.setId(warehouseId);
                params.remove(params.remove("warehouseId").toString());
                params.put("warehouse", warehouse);
            } else if (params.get("warehouseId_ne") != null && Utils.isNum(params.get("warehouseId_ne").toString())) {
                Integer warehouseId = Integer.valueOf(params.get("warehouseId_ne").toString());
                Warehouses warehouse = new Warehouses();
                warehouse.setId(warehouseId);
                params.remove(params.remove("warehouseId_ne").toString());
                params.put("warehouse_ne", warehouse);
            }

            PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
            if (null != params.get("showFields")) {
                pageModel.setFields(params.remove("showFields").toString());
            }
            if (pageNo != null) params.remove(params.remove("pageNo").toString());
            if (pageSize != null) params.remove(params.remove("pageSize").toString());
            pageModel.addCondition(params);
            pageModel = sendChannelHistoryService.findAll(pageModel);
            return ValueUtil.toJson(pageModel);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    /*
    *@Author Gavin
    *@Description   渠道库存分配
    *@Date 2017/3/28 17:44
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(method = RequestMethod.POST)
    public String sentChannelInventory(Integer mainChannelId, Integer skuId, Integer count) {
        try {
            ValueUtil.verify(mainChannelId);
            ValueUtil.verify(skuId);
            ValueUtil.verify(count);
            String result = sendChannelHistoryService.sentChannelInventory(mainChannelId, skuId, count);
            return ValueUtil.toJson(HttpStatus.SC_CREATED, "success");
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return ValueUtil.toError(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR), e.getMessage());
        }
    }

    /*
    *Author Gavin
    *Description 渠道分配详情
    *Email
    *Date
    */
    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public String historyDetails(Integer historyId) {
        try {
            List<SendChannelHistoryDetails> detailsList = sendChannelHistoryService.historyDetails(historyId);
            return ValueUtil.toJson(detailsList);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }

    }
}
