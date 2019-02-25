package com.yesmywine.user.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.user.entity.DeliveryAddress;
import com.yesmywine.util.error.yesmywineException;

import java.util.List;
import java.util.Map;

/**
 * Created by SJQ on 2017/4/20.
 */
public interface DeliveryAddressService extends BaseService<DeliveryAddress,Integer> {
//    DeliveryAddress findByMallId(Integer mallId);

    List<DeliveryAddress> findByUserId(Integer userId);

    String synchronous(Map<String,String> param)throws yesmywineException;//同步

}
