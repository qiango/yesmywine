package com.yesmywine.goods.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.goods.entityProperties.Supplier;
import com.yesmywine.util.error.yesmywineException;

import java.util.Map;

/**
 * Created by wangdiandian on 2017/3/15.
 */
    public interface SupplierService extends BaseService<Supplier, Integer> {

    String addSupplier(Map<String, String> param) throws yesmywineException;//新增供应商

    Supplier updateLoad(Integer id) throws yesmywineException;//加载供应商

    String updateSave(Map<String, String> param) throws yesmywineException;//跟新供应商

    String delete(Integer id) throws yesmywineException;//删除供应商

}
