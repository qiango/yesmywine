package com.yesmywine.email.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.email.entity.Email;
import com.yesmywine.util.error.yesmywineException;

import java.util.Map;

/**
 * Created by wangdiandian on 2017/5/16.
 */
public interface EmailService  extends BaseService<Email,Integer> {

    String updateSave(Map<String, String> param) throws yesmywineException;
}