package com.yesmywine.ware.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.util.basic.JSONUtil;
import com.yesmywine.util.basic.MapUtil;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.Utils.Utils;
import com.yesmywine.ware.entity.Channels;
import com.yesmywine.ware.entity.Warehouses;
import com.yesmywine.ware.entity.WarehousesChannel;
import com.yesmywine.ware.service.WarehousesChannelService;
import com.yesmywine.ware.service.WarehousesHistoryService;
import com.yesmywine.ware.service.WarehousesService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by SJQ on 2017/1/4.
 *
 * @Description: 仓库管理
 */
@RestController
@RequestMapping("/inventory/warehouses")
public class WarehouseController {
    @Autowired
    private WarehousesService warehouseService;

    @Autowired
    private WarehousesChannelService warehouseChannelService;

    @Autowired
    private WarehousesHistoryService warehouseHistoryService;

    /*
    *@Author SJQ
    *@Description PASS查看渠道仓库库存表
    *@CreateTime
    *@Params
    */
    @RequestMapping(value = "/cwIndex", method = RequestMethod.GET)
    public String CWIndex(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize, Integer id) {
        try {
            MapUtil.cleanNull(params);
            if (null != id) {
                WarehousesChannel warehouseChannel = warehouseChannelService.findOne(id);
                ValueUtil.verifyNotExist(warehouseChannel, "此sku无仓库库存");
                return ValueUtil.toJson(warehouseChannel);
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
            pageModel = warehouseChannelService.findAll(pageModel);
            return ValueUtil.toJson(pageModel);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }


    /*
    *@Author SJQ
    *@Description 创建仓库
    *@CreateTime
    *@Params
    */
    @RequestMapping(method = RequestMethod.POST)
    public String create(Warehouses warehouse, @RequestParam Map<String, String> params) {
        try {
            Warehouses newWarehouse = warehouseService.create(warehouse, params);
            return ValueUtil.toJson(HttpStatus.SC_CREATED, newWarehouse);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    /*
    *@Author SJQ
    *@Description 修改仓库
    *@CreateTime
    *@Params
    */
    @RequestMapping(method = RequestMethod.PUT)
    public String update(Warehouses warehouse, @RequestParam Map<String, String> params) {
        try {
            Warehouses newWarehouse = warehouseService.update(warehouse, params);

            return ValueUtil.toJson(HttpStatus.SC_CREATED, warehouse);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    /*
    *@Author SJQ
    *@Description 仓库查看
    *@CreateTime
    *@Params
    */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Integer id) {
        try {
            ValueUtil.verify(id);
            Warehouses warehouse = warehouseService.findOne(id);

            return ValueUtil.toJson(warehouse);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    /*
    *@Author SJQ
    *@Description 删除仓库
    *@CreateTime
    *@Params
    */
    @RequestMapping(method = RequestMethod.DELETE)
    public String delete(Integer id) {
        try {
            warehouseService.delete(id);
            return ValueUtil.toJson(HttpStatus.SC_NO_CONTENT, "warehouse");
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    /*
    *@Author SJQ
    *@Description 仓库列表
    *@CreateTime
    *@Params
    */
    @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize, Integer id) {
        try {
            MapUtil.cleanNull(params);
            if (id != null) {
                Warehouses warehouse = warehouseService.findOne(id);
                ValueUtil.verifyNotExist(warehouse, "无此仓库");
                Warehouses relationWarehouse = null;
                if(warehouse.getRelationCode()!=null&&!warehouse.getRelationCode().equals("")){
                    relationWarehouse  = warehouseService.findOne(Integer.valueOf(warehouse.getRelationCode()));
                }
                JSONObject object = JSON.parseObject(ValueUtil.getFromJson(ValueUtil.toJson(warehouse),"data"));
                object.put("relationWarehouse",relationWarehouse);
                return ValueUtil.toJson(object);
            }

            if (null != params.get("all") && params.get("all").toString().equals("true")) {
                return ValueUtil.toJson(warehouseService.findAll());
            } else if (null != params.get("all")) {
                params.remove(params.remove("all").toString());
            }
            PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
            if (null != params.get("showFields")) {
                pageModel.setFields(params.remove("showFields").toString());
            }
            if (pageNo != null) params.remove(params.remove("pageNo").toString());
            if (pageSize != null) params.remove(params.remove("pageSize").toString());
            pageModel.addCondition(params);
            pageModel = warehouseService.findAll(pageModel);
            return ValueUtil.toJson(pageModel);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    /*
    *@Author Gavin
    *@Description 名称查重
    *@Date 2017/3/14 16:58
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/checkNameRepeat", method = RequestMethod.GET)
    public String checkNameRepeat(String warehouseName) {
        try {
            ValueUtil.verify(warehouseName);
            Boolean isRepeat = warehouseService.findByWarehouseName(warehouseName);

            return ValueUtil.toJson(isRepeat);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }

    }

    /*
    *@Author Gavin
    *@Description   仓库进出货历史查询
    *@Date 2017/3/28 17:36
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public String history(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize) {
        MapUtil.cleanNull(params);

        if (params.get("channelId") != null && Utils.isNum(params.get("channelId").toString())) {
            Integer channelId = Integer.valueOf(params.get("channelId").toString());
            Channels channels = new Channels();
            channels.setId(channelId);
            params.remove(params.remove("channelId").toString());
            params.put("channel", channels);
        }

        if (params.get("warehouseId") != null && Utils.isNum(params.get("warehouseId").toString())) {
            Integer warehouseId = Integer.valueOf(params.get("warehouseId").toString());
            Warehouses warehouse = new Warehouses();
            warehouse.setId(warehouseId);
            params.remove(params.remove("warehouseId").toString());
            params.put("warehouse", warehouse);
        }

        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }
        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());
        pageModel.addCondition(params);
        pageModel = warehouseHistoryService.findAll(pageModel);
        return ValueUtil.toJson(pageModel);
    }

}
