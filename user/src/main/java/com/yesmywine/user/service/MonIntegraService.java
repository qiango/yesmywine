package com.yesmywine.user.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.user.entity.MonIntegra;
import com.yesmywine.util.error.yesmywineException;

import java.util.Map;

/**
 * Created by hz on 3/27/17.
 */
public interface MonIntegraService extends BaseService<MonIntegra,Integer> {

    String create(Map<String, String> parm) throws yesmywineException;

    String updateSave(Integer monIntralId, String proportion) throws yesmywineException;
}
