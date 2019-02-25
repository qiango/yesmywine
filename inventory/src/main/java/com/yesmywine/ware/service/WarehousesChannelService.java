package com.yesmywine.ware.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.ware.entity.WarehousesChannel;

import java.util.List;

/**
 * Created by SJQ on 2017/3/15.
 */
public interface WarehousesChannelService extends BaseService<WarehousesChannel, Integer> {

    WarehousesChannel findByChannelCodeAndWarehouseCodeAndSkuCode(String channelId, String warehouseCode, String skuCode);

    List<WarehousesChannel> findByWarehouseCodeAndSkuCodeOrderByUseCountDesc(String warehouseCode, String skuCode);
}
