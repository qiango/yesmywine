package com.yesmywine.goods.service;

import com.sdicons.json.mapper.MapperException;
import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.goods.entity.Sku;
import com.yesmywine.util.error.yesmywineException;

import java.util.Map;

/**
 * Created by hz on 2/13/17.
 */
public interface SkuService extends BaseService<Sku, Integer> {
//    Map<Integer, Integer> getSku(Long goodsId) throws yesmywineException;
   com.alibaba.fastjson.JSONArray getSku(Integer categoryId, Integer type) throws yesmywineException;

//    Map<String, List<Object>> showSku(Long goodsId) throws yesmywineException;

//    String deleteSkuSpu(Long goodsId) throws yesmywineException;
    boolean findByCate(Integer skuId,Integer categoryId) throws yesmywineException;

    Sku showSku(Integer skuId) throws yesmywineException;

    String deleteSku(Integer skuId)throws yesmywineException;

    String Create(Integer suppierId,String skuName,Integer categoryId,String skuJsonArray, Integer type) throws yesmywineException, MapperException;

    String Create(Map<String, String> param);

    com.alibaba.fastjson.JSONArray rank(String valueJson);

    com.alibaba.fastjson.JSONArray rank2(String valueJson);

    com.alibaba.fastjson.JSONObject rank3(String valueJson, Integer supplierId, Integer categoryId);

    Sku getSkuInfoByCode(String code)throws yesmywineException;

    String updateSkuProp(Integer skuId, Integer isExpensive,String valueJson,String imageId,String skuName) throws yesmywineException, MapperException;
}
