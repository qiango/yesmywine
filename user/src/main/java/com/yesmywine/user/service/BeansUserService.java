package com.yesmywine.user.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.user.entity.BeanUserFlow;
import com.yesmywine.util.error.yesmywineException;

/**
 * Created by ${shuang} on 2017/3/28.
 */
public interface BeansUserService extends BaseService<BeanUserFlow,Integer> {

    Double beansCreate(String userName,String phoneNumber, String oderNumber, Integer point, String channelCode) throws yesmywineException;

    String consume(String userName,String phoneNumber, String oderNumber, Integer bean, String channelCode) throws yesmywineException;


    String beanFlowSys(String jsonData);

    String sytomall(Integer beanUserFlowId);
}
