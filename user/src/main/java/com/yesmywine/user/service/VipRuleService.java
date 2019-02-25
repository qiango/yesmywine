package com.yesmywine.user.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.user.entity.VipRule;

/**
 * Created by SJQ on 2017/4/20.
 */
public interface VipRuleService extends BaseService<VipRule,Integer> {
    VipRule findByMallId(Integer mallId);

    void deleteByMallId(Integer mallId);
}
