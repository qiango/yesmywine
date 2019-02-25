package com.yesmywine.ware.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.ware.entity.AdjustInvoices;
import org.springframework.cache.annotation.CacheConfig;

/**
 * Created by SJQ on 2017/4/1.
 */
@CacheConfig(cacheNames = "adjustInvoices")
public interface AdjustInvoicesDao extends BaseRepository<AdjustInvoices, Integer> {
}
