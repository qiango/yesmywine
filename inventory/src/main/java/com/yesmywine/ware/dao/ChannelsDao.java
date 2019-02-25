package com.yesmywine.ware.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.ware.entity.Channels;
import org.springframework.cache.annotation.CacheConfig;

import java.util.List;

/**
 * Created by SJQ on 2017/1/9.
 *
 * @Description:
 */
@CacheConfig(cacheNames = "offlineChannels")
public interface ChannelsDao extends BaseRepository<Channels, Integer> {

    List<Channels> findByChannelName(String name);

    List<Channels> findByParentChannelIsNull();

    List<Channels> findByType(Integer type);

    Channels findByChannelCode(String channelCode);
}
