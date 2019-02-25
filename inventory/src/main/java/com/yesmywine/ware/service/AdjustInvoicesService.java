package com.yesmywine.ware.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.entity.AdjustInvoices;

/**
 * Created by SJQ on 2017/4/1.
 */
public interface AdjustInvoicesService extends BaseService<AdjustInvoices, Integer> {
    String adjustCommand(String jsonData, String storeCode) throws yesmywineException;
}
