package com.yesmywine.ware.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.ware.entity.SendCommonHistory;
import org.springframework.cache.annotation.CacheConfig;

/**
 * Created by SJQ on 2017/3/29.
 */
@CacheConfig(cacheNames = "sendHistory")
public interface SendCommonHistoryDao extends BaseRepository<SendCommonHistory, Integer> {
}
