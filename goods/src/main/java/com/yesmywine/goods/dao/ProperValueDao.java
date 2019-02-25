package com.yesmywine.goods.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.goods.entityProperties.PropertiesValue;

import java.util.List;

/**
 * Created by hz on 1/6/17.
 */
public interface ProperValueDao extends BaseRepository<PropertiesValue, Integer> {

    List<PropertiesValue> findByPropertiesId(Integer id);
    PropertiesValue findByCnValueAndPropertiesId(String cnValue,Integer propId);
//    PropertiesValue findByCode(String code);
    PropertiesValue findByCodeAndPropertiesId(String code, Integer propId);

}
