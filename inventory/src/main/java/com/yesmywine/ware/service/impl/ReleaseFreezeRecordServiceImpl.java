package com.yesmywine.ware.service.impl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.ware.entity.ReleaseFreezeFailedRecord;
import com.yesmywine.ware.service.ReleaseFreezeRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by SJQ on 2017/4/17.
 */
@Service
@Transactional
public class ReleaseFreezeRecordServiceImpl extends BaseServiceImpl<ReleaseFreezeFailedRecord, Integer> implements ReleaseFreezeRecordService {
}
