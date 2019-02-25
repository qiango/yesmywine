package com.yesmywine.ware.controller;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.util.basic.MapUtil;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.Utils.Utils;
import com.yesmywine.ware.entity.AllotApply;
import com.yesmywine.ware.entity.AllotCommand;
import com.yesmywine.ware.entity.Channels;
import com.yesmywine.ware.entity.Warehouses;
import com.yesmywine.ware.service.AllotApplyService;
import com.yesmywine.ware.service.AllotCommandService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by SJQ on 2017/1/18.
 *
 * @Description:
 */
@RestController
@RequestMapping("/inventory/allot")
public class AllotApplyController {

    @Autowired
    private AllotApplyService allotApplyService;
    @Autowired
    private AllotCommandService commandService;

    /*
    *@Author Gavin
    *@Description 需要调拨的渠道列表
    *@Date 2017/5/12 14:25
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/channels", method = RequestMethod.GET)
    public String getApplyChannels() {
        List<Channels> channelsList = allotApplyService.getApplyChannels();
        return ValueUtil.toJson(channelsList);
    }

    /*
    *@Author Gavin
    *@Description 需要调拨的渠道的仓库列表
    *@Date 2017/5/12 14:25
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/warehouses", method = RequestMethod.GET)
    public String getApplyWarehouses(Integer channelId) {
        List<Warehouses> warehousesList = allotApplyService.getApplyWarehouses(channelId);
        return ValueUtil.toJson(warehousesList);
    }

    /*
    *@Author SJQ
    *@Description 调拨申请列表
    *@CreateTime
    *@Params
    */
    @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize, Integer id) {
        MapUtil.cleanNull(params);

        if (id != null) {
            AllotApply allotApply = allotApplyService.findOne(id);
            return ValueUtil.toJson(allotApply);
        }

        if (null != params.get("all") && params.get("all").toString().equals("true")) {
            return ValueUtil.toJson(allotApplyService.findAll());
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
        pageModel = allotApplyService.findAll(pageModel);
        return ValueUtil.toJson(pageModel);
    }

    /*
    *@Author SJQ
    *@Description 调拨申请详情
    *@CreateTime
    *@Params
    */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable Integer id) {
        try {
            ValueUtil.verify(id);
            AllotApply allotApply = allotApplyService.findOne(id);
            return ValueUtil.toJson(allotApply);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    /*
    *@Author Gavin
    *@Description  用于OMS与门店发起调拨申请
    *@Date 2007/3/16 11:35
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/allotApply/itf", method = RequestMethod.POST)
    public String allotApply(String jsonData) {
        try {
            String result = allotApplyService.allotApply(jsonData);
            return ValueUtil.toJson(HttpStatus.SC_CREATED, result);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    /*
    *@Author Gavin
    *@Description   制作调拨指令
    *@Date 2017/4/1 11:25
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/allotCommand", method = RequestMethod.POST)
    public String allotCommand(String jsonData, String userId) {
        try {
            String result = allotApplyService.allotCommand(jsonData, userId);
            return ValueUtil.toJson(HttpStatus.SC_CREATED, result);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }

    }

    /*
    *@Author Gavin
    *@Description   直接制作调拨指令
    *@Date 2017/4/1 11:25
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/rirectAllot", method = RequestMethod.POST)
    public String rirectAllotCommandRirect(Integer channelId,Integer tarWarehouseId, Integer allotWarehouseId, Integer cwIds[], Integer counts[], Integer userId) {
        try {
            String result = allotApplyService.rirectAllotCommandRirect(channelId,tarWarehouseId,cwIds, counts, userId);

            return ValueUtil.toJson(HttpStatus.SC_CREATED, result);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }

    }

    /*
    *@Author Gavin
    *@Description 清关调拨申请
    *@Date 2017/5/25 16:30
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/omsCleanApply/itf", method = RequestMethod.POST)
    public String omsCleanApply(String jsonData) {
        try {
            String result = allotApplyService.omsCleanApply(jsonData);
            return ValueUtil.toJson(HttpStatus.SC_CREATED, result);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }

    }

    /*
   *@Author Gavin
   *@Description  调拨指令审核
   *@Date 2007/3/16 11:35
   *@Email gavinsjq@sina.com
   *@Params
   */
    @RequestMapping(value = "/audit", method = RequestMethod.PUT)
    public String allotAudit(Integer commandId ,String comment,String userId) {
        try {
            String result = allotApplyService.audit( commandId , comment, userId);
            return ValueUtil.toJson(HttpStatus.SC_CREATED, result);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    /*
   *@Author Gavin
   *@Description  调拨指令驳回
   *@Date 2007/3/16 11:35
   *@Email gavinsjq@sina.com
   *@Params
   */
    @RequestMapping(value = "/reject", method = RequestMethod.PUT)
    public String allotReject(Integer commandId ,String comment,String userId) {
        try {
            String result = allotApplyService.reject( commandId , comment, userId);
            return ValueUtil.toJson(HttpStatus.SC_CREATED, result);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    /*
   *@Author Gavin
   *@Description  调拨指令审核
   *@Date 2007/3/16 11:35
   *@Email gavinsjq@sina.com
   *@Params
   */
    @RequestMapping(value = "/command", method = RequestMethod.GET)
    public String commandIndex(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize, Integer id) {
        MapUtil.cleanNull(params);

        if(id!=null){
            AllotCommand command = commandService.findOne(id);
            return ValueUtil.toJson(command);
        }

        if (null != params.get("all") && params.get("all").toString().equals("true")) {
            return ValueUtil.toJson(commandService.findAll());
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

        if (params.get("tarWarehouseId") != null && Utils.isNum(params.get("tarWarehouseId").toString())) {
            Integer warehouseId = Integer.valueOf(params.get("tarWarehouseId").toString());
            Warehouses warehouse = new Warehouses();
            warehouse.setId(warehouseId);
            params.remove(params.remove("tarWarehouseId").toString());
            params.put("tarWarehouseId", warehouse);
        } else if (params.get("tarWarehouseId_ne") != null && Utils.isNum(params.get("tarWarehouseId_ne").toString())) {
            Integer warehouseId = Integer.valueOf(params.get("tarWarehouseId_ne").toString());
            Warehouses warehouse = new Warehouses();
            warehouse.setId(warehouseId);
            params.remove(params.remove("tarWarehouseId_ne").toString());
            params.put("tarWarehouseId_ne", warehouse);
        }

        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }
        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());
        pageModel.addCondition(params);
        pageModel = commandService.findAll(pageModel);

        return ValueUtil.toJson(pageModel);
    }


}
