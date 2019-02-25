package com.yesmywine.ware.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.ware.entity.Stores;
import org.springframework.cache.annotation.CacheConfig;

/**
 * Created by SJQ on 2017/3/28.
 */
@CacheConfig(cacheNames = "stores")
public interface StoresDao extends BaseRepository<Stores, Integer> {
    Stores findByStoreCode(String storeCode);
}
