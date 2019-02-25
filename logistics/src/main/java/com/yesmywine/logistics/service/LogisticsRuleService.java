package com.yesmywine.logistics.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.logistics.entity.LogisticsRule;
import com.yesmywine.util.error.yesmywineException;

import java.util.Map;

/**
 * Created by wangdiandian on 2017/3/30.
 */
public interface LogisticsRuleService extends BaseService<LogisticsRule,Integer> {
    String addLogisticsRule(Map<String, String> param) throws yesmywineException;//新增物流规则

    Map<String, Object> updateLoad(Integer id) throws yesmywineException;//加载物流规则

    String updateSave(Map<String, String> param) throws yesmywineException;//跟新物流规则

    String delete(Integer id) throws yesmywineException;//删除物流规则

    String logisticsRuleplus(String distributionArea,Integer shipperId) throws yesmywineException;//配送区域不可重复

}
