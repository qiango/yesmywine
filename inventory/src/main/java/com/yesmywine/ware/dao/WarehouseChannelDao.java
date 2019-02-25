package com.yesmywine.ware.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.ware.entity.Channels;
import com.yesmywine.ware.entity.Warehouses;
import com.yesmywine.ware.entity.WarehousesChannel;
import org.springframework.cache.annotation.CacheConfig;

import java.util.List;

/**
 * Created by SJQ on 2017/1/10.
 *
 * @Description:
 */
@CacheConfig(cacheNames = "warehouseChannel")
public interface WarehouseChannelDao extends BaseRepository<WarehousesChannel, Integer> {
    List<WarehousesChannel> findBySkuId(String skuId);

    WarehousesChannel findBySkuCodeAndWarehouseCode(String skuId, String warehouseCode);

    List<WarehousesChannel> findByWarehouseCode(String warehouseCode);

    WarehousesChannel findByChannelAndWarehouseAndSkuCode(Channels channels, Warehouses warehouse, String skuCode);

    List<WarehousesChannel> findByChannelCodeAndSkuCodeOrderByUseCountAsc(String channelCode, String skuCode);

    WarehousesChannel findByChannelCodeAndWarehouseCodeAndSkuCode(String channelCode, String warehouseCode, String skuCode);

    List<WarehousesChannel> findByWarehouseCodeAndSkuCodeOrderByUseCountDesc(String warehouseCode, String skuCode);
}
