package com.yesmywine.user.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.user.entity.UserInformation;

import java.util.Map;

/**
 * Created by SJQ on 2017/4/20.
 */
public interface UserInformationService extends BaseService<UserInformation,Integer> {
    UserInformation findByUserId(Integer userId);

    UserInformation findByPhoneNumber(String phone);

    UserInformation findByUserName(String userName);

    UserInformation findByUserNameOrPhoneNumber(String phoneNumber, String phoneNumber1);

    String localConsume(Map<String, String> params, Integer userId);
}
