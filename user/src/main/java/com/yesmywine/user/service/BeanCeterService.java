package com.yesmywine.user.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.user.entity.BeanCenterFlow;
import com.yesmywine.util.error.yesmywineException;

import java.util.List;
import java.util.Map;


/**
 * Created by ${shuang} on 2017/3/30.
 */
public interface BeanCeterService extends BaseService<BeanCenterFlow,Integer> {
    Map<String, List> settleAccounts(String startDate, String endDate) throws yesmywineException;

}
