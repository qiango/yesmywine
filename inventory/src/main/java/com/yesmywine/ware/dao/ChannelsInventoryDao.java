package com.yesmywine.ware.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.ware.entity.Channels;
import com.yesmywine.ware.entity.ChannelsInventory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by SJQ on 2017/1/9.
 *
 * @Description:
 */
@CacheConfig(cacheNames = "channelsInventory")
public interface ChannelsInventoryDao extends BaseRepository<ChannelsInventory, Integer> {
    List<ChannelsInventory> findBySkuId(Integer skuId);

    List<ChannelsInventory> findBySkuIdIn(String[] goodsSKUIds);

    List<ChannelsInventory> findByChannelId(Integer channelId);

    ChannelsInventory findByChannelAndSkuId(Channels channels, Integer skuId);

    @Query(value = "select sum(useCount) from ChannelsInventory ci where skuId=:skuId group by ci.skuId")
    Integer findBySkuIdCount(@Param("skuId") Integer skuId);

    ChannelsInventory findByChannelCodeAndSkuCode(String channelCode, String skuCode);

}
