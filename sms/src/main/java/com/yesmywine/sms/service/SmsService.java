package com.yesmywine.sms.service;

import com.yesmywine.util.error.yesmywineException;

import java.util.Map;

/**
 * Created by wangdiandian on 2017/5/5.
 */
public interface SmsService {
    String sendSms(Map<String,String> param)throws yesmywineException;
    String bulkSms(Map<String,String> param)throws yesmywineException;
    String smsReport(Map<String,String> param)throws yesmywineException;
    String getReplySms(Map<String,String> param)throws yesmywineException;
}
