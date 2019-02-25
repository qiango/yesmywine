package com.yesmywine.logistics.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.logistics.entity.Area;
import com.yesmywine.util.error.yesmywineException;

import java.util.List;
import java.util.Map;

/**
 * Created by wangdiandian on 2017/4/13.
 */
public interface AreaService extends BaseService<Area, Integer> {
    String createArea(Integer areaNo,String cityName, Integer parentId)throws yesmywineException;//新增城市

    String delete(Integer id) throws yesmywineException;//删除城市

//    Area updateLoad(Integer id) throws yesmywineException;//加载城市

    String updateSave(Integer id,Integer areaNo,String cityName, Integer parentId) throws yesmywineException;//跟新城市
//

    List<Area> showArea() throws yesmywineException;

    String query(String areaName)throws yesmywineException;


}
