package com.yesmywine.user.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.user.entity.VipRule;

/**
 * Created by SJQ on 2017/4/20.
 */
public interface VipRuleDao extends BaseRepository<VipRule,Integer> {

    VipRule findByMallId(Integer mallId);

    void deleteByMallId(Integer mallId);
}
