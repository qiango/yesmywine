package com.yesmywine.user.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.user.entity.UserInformation;

import java.util.List;

/**
 * Created by SJQ on 2017/4/20.
 */
public interface UserInformationDao extends BaseRepository<UserInformation,Integer> {
    UserInformation findByUserId(Integer userId);

    UserInformation findByPhoneNumber(String phone);

    UserInformation findByUserName(String userName);

    UserInformation findByUserNameOrPhoneNumber(String phoneNumber, String phoneNumber1);

    List findByNickNameOrPhoneNumberOrEmail(String nickName, String phoneNumber, String email);
}
