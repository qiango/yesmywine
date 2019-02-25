package com.yesmywine.logistics.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.logistics.entity.ThirdShippers;
import com.yesmywine.util.error.yesmywineException;

import java.util.Map;

/**
 * Created by ${shuang} on 2017/7/21.
 */
public interface ThirdShipperService extends BaseService<ThirdShippers,Integer> {

    String addThirdShipper(Map<String, String> param) throws yesmywineException;//新增承运商

    void deleteByShipperId(Integer id);
}


