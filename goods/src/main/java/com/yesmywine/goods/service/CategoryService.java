
package com.yesmywine.goods.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.goods.bean.DeleteEnum;
import com.yesmywine.goods.bean.IsShow;
import com.yesmywine.goods.entityProperties.Category;
import com.yesmywine.util.error.yesmywineException;

import java.util.List;

/**
 * Created by hz on 2/10/17.
 */
public interface CategoryService extends BaseService<Category, Integer> {
    String insert(String categoryName, Integer parentId, String code, String isShow,Integer[] imgIds,String propertyJson) throws yesmywineException;

    List<Category> findByDeleteEnumAndIsShow(DeleteEnum deleteEnum, IsShow isShow) throws yesmywineException;

    List<Category> showCategory() throws yesmywineException;

    List<Category> findByDeleteEnum() throws yesmywineException;

    JSONArray getOne(Integer categoryId);

    String update(Integer categoryId, String propertyJson, String delPropertyJson, Integer parentId, String isShow, String categoryName, String imgIds)throws yesmywineException;

    Category physicsDelete(Category category);

    Boolean isHaveChild(Integer categoryId);

    Boolean isPropertyValueUsed(Integer pvId);

    JSONArray getSKUProperty(Integer categoryId);

    JSONArray getOrdinaryProperty(Integer categoryId);

    List<Category> findByLevel(Integer level);

    JSONArray findAllChildrenByParentId(Integer parentId)throws yesmywineException;

    String updateProp(Integer categoryId, Integer propertyId, Integer type) throws yesmywineException;

    List<Category> getAllChildren(Integer parentId);
}



