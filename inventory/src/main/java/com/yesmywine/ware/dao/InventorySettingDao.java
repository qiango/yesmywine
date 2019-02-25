package com.yesmywine.ware.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.ware.entity.InventorySetting;
import org.springframework.cache.annotation.CacheConfig;

/**
 * Created by SJQ on 2017/1/17.
 *
 * @Description:
 */
@CacheConfig(cacheNames = "inventorySetting")
public interface InventorySettingDao extends BaseRepository<InventorySetting, Integer> {
}
