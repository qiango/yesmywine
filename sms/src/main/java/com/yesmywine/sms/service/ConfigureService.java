package com.yesmywine.sms.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.sms.entity.Configure;
import com.yesmywine.util.error.yesmywineException;

import java.util.Map;

/**
 * Created by wangdiandian on 2017/5/8.
 */
public interface ConfigureService extends BaseService<Configure,Integer> {
    String updateSave(Map<String,String> param) throws yesmywineException;

}
