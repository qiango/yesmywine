package com.yesmywine.ware.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.ware.entity.CostPriceRecord;

/**
 * Created by SJQ on 2017/4/17.
 */
public interface CostPriceRecordService extends BaseService<CostPriceRecord, Integer> {
    CostPriceRecord findByYearAndMounthAndSkuCode(int year, int month, String skuCode);
}
