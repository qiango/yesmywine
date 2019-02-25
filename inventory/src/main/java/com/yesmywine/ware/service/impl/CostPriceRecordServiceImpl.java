package com.yesmywine.ware.service.impl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.ware.dao.CostPriceRecordDao;
import com.yesmywine.ware.entity.CostPriceRecord;
import com.yesmywine.ware.service.CostPriceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by SJQ on 2017/4/17.
 */
@Service
@Transactional
public class CostPriceRecordServiceImpl extends BaseServiceImpl<CostPriceRecord, Integer>
        implements CostPriceRecordService {
    @Autowired
    private CostPriceRecordDao costPriceRecordDao;

    @Override
    public CostPriceRecord findByYearAndMounthAndSkuCode(int year, int month, String skuCode) {
        return costPriceRecordDao.findByYearAndMounthAndSkuCode(year, month, skuCode);
    }
}
