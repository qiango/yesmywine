package com.yesmywine.logistics.service;

import com.yesmywine.util.error.yesmywineException;

import java.util.Map;

/**
 * Created by wangdiandian on 2017/3/31.
 */
public interface CostCalculationService {
    Map<String, Object> costCalculation(String json) throws yesmywineException;//物流费计算

}
