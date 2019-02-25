package com.yesmywine.ware.controller;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.util.basic.MapUtil;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.entity.Channels;
import com.yesmywine.ware.entity.Stores;
import com.yesmywine.ware.entity.Warehouses;
import com.yesmywine.ware.service.ChannelsService;
import com.yesmywine.ware.service.StoresService;
import com.yesmywine.ware.service.WarehousesService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by SJQ on 2017/3/27.
 */
@RestController
@RequestMapping("/inventory/stores")
public class StoresControllor {
    @Autowired
    private StoresService storesService;

    @Autowired
    private ChannelsService channelsService;

    @Autowired
    private WarehousesService warehouseService;


    /*
    *@Author Gavin
    *@Description 查看门店信息列表
    *@Date 2017/3/27 11:29
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize, Integer id) {
        try {
            MapUtil.cleanNull(params);
            if (id != null) {
                Stores stores = storesService.findOne(id);
                ValueUtil.verifyNotExist(stores, "无此门店");
                return ValueUtil.toJson(stores);
            }

            if (null != params.get("all") && params.get("all").toString().equals("true")) {
                return ValueUtil.toJson(storesService.findAll());
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
            pageModel = storesService.findAll(pageModel);
            return ValueUtil.toJson(pageModel);

        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    /*
    *@Author Gavin
    *@Description 展示门店详情
    *@Date 2017/3/27 11:29
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/code", method = RequestMethod.GET)
    public String show(String stroeCode) {
        try {
            ValueUtil.verify(stroeCode);
            Stores stores = storesService.findByStoreCode(stroeCode);
            ValueUtil.verifyNotExist(stores, "无此编码的门店");
            if (stores.getIfConfig().equals(0)) {
                ValueUtil.isError("该门店尚未配置渠道");
            }
            return ValueUtil.toJson(stores);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    /*
    *@Author Gavin
    *@Description 新增门店信息（提供给门店系统，同步门店信息）
    *@Date 2017/3/27 11:29
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/itf",method = RequestMethod.POST)
    public String create(Stores stores, @RequestParam Map<String, String> params) {
        try {
            ValueUtil.verify(params, new String[]{"storeName", "storeCode"});
            stores.setIfConfig(0);
            storesService.checkCodeRepeat(stores.getStoreCode());
            storesService.save(stores);
            return ValueUtil.toJson(HttpStatus.SC_CREATED, stores);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }

    }

    /*
    *@Author Gavin
    *@Description 修改门店信息（提供给门店系统，同步门店信息）
    *@Date 2017/3/27 11:29
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(method = RequestMethod.PUT)
    public String update(@RequestParam Map<String, String> params) {
        try {

            ValueUtil.verify(params, new String[]{"id", "channelId", "warehouseId"});
            Integer id = Integer.valueOf(params.get("id"));
            Integer channelId = Integer.valueOf(params.get("channelId"));
            Integer warehouseId = Integer.valueOf(params.get("warehouseId"));
            Stores store = storesService.findOne(id);
            store.setIfConfig(1);
            Channels channels = channelsService.findOne(channelId);
            Warehouses warehouse = warehouseService.findOne(warehouseId);
            if (null != channels && null != warehouse) {
                store.setChannel(channels);
                store.setWarehouse(warehouse);
            } else {
                return ValueUtil.toError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "无此仓库或渠道");
            }
            storesService.save(store);

            //同步给门店系统
            synchronizetionToStors(store);
            return ValueUtil.toJson(HttpStatus.SC_CREATED, store);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    /*
    *@Author Gavin
    *@Description 将信息同步给门店
    *@Date 2017/3/27 14:16
    *@Email gavinsjq@sina.com
    *@Params
    */
    private void synchronizetionToStors(Stores stores) {
    }

    /*
    *@Author Gavin
    *@Description 删除门店信息（提供给门店系统，同步门店信息）
    *@Date 2017/3/27 11:29
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable Integer Id) {

        return null;
    }


}
