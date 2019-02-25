package com.yesmywine.ware.service.impl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.ware.entity.WarehousesHistory;
import com.yesmywine.ware.service.WarehousesHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by SJQ on 2017/2/10.
 */
@Service
@Transactional
public class WarehouseHistoryServiceImpl extends BaseServiceImpl<WarehousesHistory, Integer> implements WarehousesHistoryService {
}
