package com.yesmywine.goods.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.goods.entity.Goods;
import com.yesmywine.util.error.yesmywineException;

import java.util.List;
import java.util.Map;

/**
 * Created by hz on 2/10/17.
 */
public interface GoodsService extends BaseService<Goods, Integer> {

    String addGoods(Map<String, String> param) throws yesmywineException;//新增商品

    com.alibaba.fastjson.JSONObject updateLoad(Integer id) throws yesmywineException;//加载显示商品

    List<Map> querySku(Integer id) throws yesmywineException;//

    String delete(Integer id) throws yesmywineException;//删除供商品

}
