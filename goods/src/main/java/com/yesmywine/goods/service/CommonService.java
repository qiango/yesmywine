package com.yesmywine.goods.service;

import java.util.Map;

/**
 * Created by light on 2017/4/7.
 */
public interface CommonService<T> {

    Boolean synchronous(Integer id, String url, Integer synchronous);

    Boolean synchronous(T t, String url, Integer synchronous);

    Boolean synchronous(Map<String, String> map, String url, Integer synchronous);

    Boolean synchronousGoods(Map<String, String> map, Integer channelId, Integer synchronous);

    Boolean synchronous(T t, String url, Integer synchronous, Map<String, String> map);

    Boolean synchronousGoods(T t, Integer channelId, Integer synchronous, Map<String, String> map);

}
