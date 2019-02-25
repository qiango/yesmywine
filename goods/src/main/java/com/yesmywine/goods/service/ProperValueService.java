package com.yesmywine.goods.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.goods.entityProperties.PropertiesValue;

import java.util.List;
import java.util.Map;

/**
 * Created by hz on 2017/4/26.
 */
public interface ProperValueService extends BaseService<PropertiesValue, Integer> {

    String addPrpoValue(Map param);

    String addPrpoValue(Integer propertiesId, String value, String code);

    String deletePropValue(String id);

    Boolean findByCnValueAndPropertiesId(String cnValue, Integer propId);

    List<PropertiesValue> findByPropertiesId(Integer propId);
}
