package com.yesmywine.ware.WebController;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.util.basic.MapUtil;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.Utils.Utils;
import com.yesmywine.ware.entity.Channels;
import com.yesmywine.ware.entity.Warehouses;
import com.yesmywine.ware.entity.WarehousesChannel;
import com.yesmywine.ware.service.WarehousesChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by by on 2017/7/13.
 */
@RestController
@RequestMapping("/inventory/itf/warehouses")
public class CommonController {

    @Autowired
    private WarehousesChannelService warehouseChannelService;

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
}
