package com.yesmywine.user.service.impl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.user.dao.VipRuleDao;
import com.yesmywine.user.entity.VipRule;
import com.yesmywine.user.service.VipRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by SJQ on 2017/4/20.
 */
@Service
@Transactional
public class VipRuleServiceImpl extends BaseServiceImpl<VipRule,Integer>
        implements VipRuleService {
    @Autowired
    private VipRuleDao vipRuleDao;


    public VipRule findByMallId(Integer mallId) {
        return vipRuleDao.findByMallId(mallId);
    }

    public void deleteByMallId(Integer mallId) {
        vipRuleDao.deleteByMallId(mallId);
    }
}
