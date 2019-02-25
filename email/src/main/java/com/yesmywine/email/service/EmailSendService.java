package com.yesmywine.email.service;

import com.yesmywine.util.error.yesmywineException;

import java.util.Map;

/**
 * Created by wangdiandian on 2017/5/16.
 */
public interface EmailSendService {
    String send(Map<String, String> param) throws yesmywineException;
}
