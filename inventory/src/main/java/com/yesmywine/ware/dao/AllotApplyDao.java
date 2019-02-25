package com.yesmywine.ware.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.ware.entity.AllotApply;
import com.yesmywine.ware.entity.Channels;
import com.yesmywine.ware.entity.Warehouses;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by SJQ on 2017/1/17.
 *
 * @Description:
 */
@CacheConfig(cacheNames = "allotApply")
public interface AllotApplyDao extends BaseRepository<AllotApply, Integer> {
    List<AllotApply> findByIdIn(Integer[] applyIds);

    AllotApply findByChannelAndWarehouseAndSkuId(Channels channels, Warehouses warehouse, Integer skuId);

    AllotApply findByChannelAndWarehouseAndSkuIdAndStatus(Channels channels, Warehouses warehouse, Integer skuId, Integer status);

    AllotApply findByChannelAndWarehouseAndSkuIdAndStatusAndType(Channels channels, Warehouses warehouse, Integer skuId,Integer status, String type);

    List<AllotApply > findByAllotCode(String allotCode);

    @Query(value = "select aa.channelId from allotApply aa GROUP BY aa.channelId", nativeQuery = true)
    List<Integer> getApplyChannels();

    @Query(value = "select aa.warehouseId from allotApply aa where aa.channelId=:channelId GROUP BY aa.warehouseId", nativeQuery = true)
    List<Integer> getApplyChannelWarehouses(@Param("channelId") Integer channelId);

//    AllotApply findByChannelAndAllotwarehouseAndSkuIdAndApplyStatus(Channels channel, Warehouses allotWarehouse, Integer integer, Integer status);
}
