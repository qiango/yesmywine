package com.yesmywine.sms.service;

import com.yesmywine.util.error.yesmywineException;

import java.util.Map;

/**
 * Created by wangdiandian on 2017/5/10.
 */
public interface SendServiceService {
    String sms(Map<String,String> map)throws yesmywineException;
    String smsBatch(String json)throws yesmywineException;

    }
