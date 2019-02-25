package com.yesmywine.logistics.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.logistics.entity.ExpressRule;
import com.yesmywine.util.error.yesmywineException;

import java.util.Map;

/**
 * Created by wangdiandian on 2017/3/30.
 */
public interface ExpressRuleService extends BaseService<ExpressRule,Integer> {
    String addExpressRule(Map<String, String> param) throws yesmywineException;//新增按箱规则

    Map<String, Object> updateLoad(Integer id) throws yesmywineException;//加载按箱规则

    String updateSave(Map<String, String> param) throws yesmywineException;//跟新按箱规则

    String delete(Integer id) throws yesmywineException;//删除按箱规则

    String expressRulePlus(String distributionArea,Integer warehouseId,Integer type,Integer shipperId )throws yesmywineException;//配送区域不可重复

}
