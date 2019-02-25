package com.yesmywine.logistics.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.logistics.entity.Shippers;
import com.yesmywine.util.error.yesmywineException;

import java.util.Map;

/**
 * Created by wangdiandian on 2017/3/27.
 */
public interface ShipperService extends BaseService<Shippers,Integer> {
    String addShipper(Map<String, String> param) throws yesmywineException;//新增承运商

    Map<String, Object> updateLoad(Integer id) throws yesmywineException;//加载承运商

    String updateSave(Map<String, String> param) throws yesmywineException;//跟新承运商

    String delete(Integer id) throws yesmywineException;//删除承运商

    String updateStatus(Map<String, String> param) throws yesmywineException;//修改状态

}
