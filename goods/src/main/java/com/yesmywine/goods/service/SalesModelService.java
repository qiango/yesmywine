package com.yesmywine.goods.service;

import com.yesmywine.util.error.yesmywineException;

/**
 * Created by light on 2017/3/16.
 */
public interface SalesModelService {
    Object choose(Integer goodsId, Integer salesModelCode) throws yesmywineException;
}
