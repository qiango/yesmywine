package com.yesmywine.ware.controller;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.db.base.biz.RedisCache;
import com.yesmywine.util.basic.MapUtil;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.Utils.Utils;
import com.yesmywine.ware.entity.Channels;
import com.yesmywine.ware.entity.ChannelsInventory;
import com.yesmywine.ware.service.ChannelsInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by SJQ on 2007/1/4.
 *
 * @Description: 渠道库存管理
 */
@RestController
@RequestMapping("/inventory/channelInventory")
public class ChannelsInventoryController {
    @Autowired
    private ChannelsInventoryService channelsInventoryService;

    /*
    *@Author Gavin
    *@Description 渠道库存查询（在channelInventory表中,PASS层后台使用）
    *@Date 2007/3/16 14:59
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize) {
        MapUtil.cleanNull(params);

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


        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }
        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());
        pageModel.addCondition(params);
        pageModel = channelsInventoryService.findAll(pageModel);
        return ValueUtil.toJson(pageModel);
    }

    /*
   *@Author SJQ
   *@Description 根据skuId及渠道id查询商品库存
   *@CreateTime
   *@Params
   */
    @RequestMapping(value = "/skuInventory/itf", method = RequestMethod.GET)
    public String getSkuInventory(Integer skuId, Integer channelId) {
        try {
            if (null != skuId && null != channelId) {
//                if (null == RedisCache.get("skuId_"+String.valueOf(skuId))) {
                Channels channels = new Channels();
                channels.setId(channelId);
                ChannelsInventory channelsInventory = channelsInventoryService.findByChannelAndSkuId(channels, skuId);
                if (channelsInventory == null) {
                    ValueUtil.isError("无此渠道或SKU");
                }
                RedisCache.set("skuId_" + String.valueOf(skuId), channelsInventory);
                RedisCache.expire("skuId_" + String.valueOf(skuId), 7 * 24 * 3600);
                return ValueUtil.toJson(channelsInventory);
//                } else {
//                    return ValueUtil.toJson(JSON.parse(RedisCache.get(String.valueOf(skuId))));
//                }
            } else if (null != skuId && null == channelId) {
                List<ChannelsInventory> list = channelsInventoryService.findBySkuId(skuId);
                return ValueUtil.toJson(list);
            } else if (null == skuId && null != channelId) {
                List<ChannelsInventory> list = channelsInventoryService.findByChannelId(channelId);
                return ValueUtil.toJson(list);
            }
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
        return null;
    }

}
