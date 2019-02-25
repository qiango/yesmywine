package com.yesmywine.cache.controller;

import com.yesmywine.cache.service.InventoryService;
import com.yesmywine.util.basic.ValueUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 库存缓存接口
 * Created by light on 2017/3/7.
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @RequestMapping(value = "/seckill", method = RequestMethod.POST)
    public String setSeckill(Integer goodsSkuId, Integer count){

        return this.inventoryService.setSeckill(goodsSkuId, count);

    }

    @RequestMapping(value = "/activity", method = RequestMethod.POST)
    public String setActivity(List<Integer> ids){
        try{
            this.inventoryService.setActivity(ids);
        }catch (Exception e){
            return ValueUtil.toJson("设置失败");
        }
        return ValueUtil.toJson("success");
    }
}
