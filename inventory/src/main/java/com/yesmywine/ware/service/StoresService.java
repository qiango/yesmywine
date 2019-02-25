package com.yesmywine.ware.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.entity.Stores;

/**
 * Created by SJQ on 2017/3/28.
 */
public interface StoresService extends BaseService<Stores, Integer> {
    Stores checkCodeRepeat(String storeCode) throws yesmywineException;

    Stores findByStoreCode(String code);
}
