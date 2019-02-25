package com.yesmywine.cache.service;

import java.util.List;

/**
 * Created by light on 2017/3/7.
 */
public interface InventoryService {

    String setSeckill(Integer goodsSkuID, Integer count);

    void setActivity(List<Integer> ids) throws Exception;
}
