package com.yesmywine.ware.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.ware.entity.CostPriceRecord;

/**
 * Created by SJQ on 2017/4/17.
 */
public interface CostPriceRecordDao extends BaseRepository<CostPriceRecord, Integer> {
    CostPriceRecord findByYearAndMounthAndSkuCode(int year, int month, String skuCode);
}
