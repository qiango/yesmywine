
package com.yesmywine.goods.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.goods.entityProperties.Properties;
import com.yesmywine.util.error.yesmywineException;

import java.util.Map;


/**
 * Created by hz on 2/10/17.
 */

public interface ProService extends BaseService<Properties, Integer> {
    String addPrpo(Map<String, String> parm) throws yesmywineException; //新增属性

    Map<String, String> addPrpoByImport(Map<String, String> parm) throws yesmywineException; //新增属性

//    Map<String, List<String>> getProperByCategory(Integer categoryId) throws yesmywineException;

//    Object getMethods(Integer categoryId) throws yesmywineException;

//    Map[] getGeneralProp(Integer categoryId) throws yesmywineException;

    String updateProp(Integer id,String canSearch,String cnName,String isSku,String entryMode) throws yesmywineException;

    String updateAdd(Integer propId, String code, String value) throws yesmywineException;

    String deleteProp(Integer prop) throws yesmywineException;

    Properties findByCnName(String cnName);

//    String propCanShow(Integer propId)throws yesmywineException;
}

