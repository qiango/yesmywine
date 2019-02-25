package com.yesmywine.sms.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.sms.entity.SmsTemplate;
import com.yesmywine.util.error.yesmywineException;

import java.util.Map;

/**
 * Created by wangdiandian on 2017/5/8.
 */
public interface SmsTemplateService extends BaseService<SmsTemplate,Integer> {

    String creat(Map<String, String> param) throws yesmywineException;
    SmsTemplate updateLoad(Integer id) throws yesmywineException;
//    String delete(Integer id) throws yesmywineException;
    String updateSave(Map<String, String> param) throws yesmywineException;
}
