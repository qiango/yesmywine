package com.yesmywine.goods.service;

import com.yesmywine.util.error.yesmywineException;

import java.util.List;
import java.util.Map;

/**
 * Created by hz on 2017/5/8.
 */
public interface ImportService {

    List importPropAndPropValue(List<Map<String, Object>> list);

    List importSupplier(List<Map<String, Object>> list)throws yesmywineException;

    List importSku(List<Map<String, Object>> list)throws yesmywineException;

    List importGoods(List<Map<String, Object>> list);

    List importCategory(List<Map<String, Object>> list) throws yesmywineException;

}
