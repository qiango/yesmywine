package com.yesmywine.ware.service.impl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.dao.StoresDao;
import com.yesmywine.ware.entity.Stores;
import com.yesmywine.ware.service.StoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by SJQ on 2017/3/28.
 */
@Service
@Transactional
public class StoresServiceImpl extends BaseServiceImpl<Stores, Integer> implements StoresService {
    @Autowired
    private StoresDao storesDao;

    @Override
    public Stores checkCodeRepeat(String storeCode) throws yesmywineException {
        Stores stores = storesDao.findByStoreCode(storeCode);
        if (stores != null) {
            ValueUtil.isError("门店编码重复");
        }
        return null;
    }

    @Override
    public Stores findByStoreCode(String code) {
        Stores stores = storesDao.findByStoreCode(code);
        return stores;
    }
}
