package com.yesmywine.ware.service.impl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.ware.entity.DiscrepancyBills;
import com.yesmywine.ware.service.DiscrepancyBillsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by SJQ on 2017/6/9.
 */
@Service
@Transactional
public class DiscrepancyBillsServiceImpl extends BaseServiceImpl<DiscrepancyBills, Integer>
        implements DiscrepancyBillsService {
}
