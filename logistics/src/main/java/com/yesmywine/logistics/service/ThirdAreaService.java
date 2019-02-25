package com.yesmywine.logistics.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.logistics.entity.ThirdArea;
import com.yesmywine.util.error.yesmywineException;

import java.util.List;
import java.util.Map;

/**
 * Created by wangdiandian on 2017/7/21.
 */
public interface ThirdAreaService  extends BaseService<ThirdArea, Integer> {
    String createThirdArea(Map<String, String> param)throws yesmywineException;//新增第三方城市

    ThirdArea updateLoad(Integer id) throws yesmywineException;//加载第三方城市

    String updateSave(Map<String, String> param) throws yesmywineException;//跟新第三方城市

    String delete(Integer id) throws yesmywineException;//删除第三方城市

    List<ThirdArea> query(Integer areaId) throws yesmywineException;//查询第三方城市
}
