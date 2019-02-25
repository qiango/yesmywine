package com.yesmywine.user.service.impl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.user.dao.UserInformationDao;
import com.yesmywine.user.entity.UserInformation;
import com.yesmywine.user.service.UserInformationService;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.SynchronizeUtils;
import com.yesmywine.util.basic.ValueUtil;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by SJQ on 2017/4/20.
 */
@Service
@Transactional
public class UserInformationServiceImpl extends BaseServiceImpl<UserInformation,Integer>
        implements UserInformationService {

    @Autowired
    private UserInformationDao userInformationDao;

    public UserInformation findByUserId(Integer userId) {
        return userInformationDao.findByUserId(userId);
    }

    public UserInformation findByPhoneNumber(String phone) {

        return userInformationDao.findByPhoneNumber(phone);
    }

    public UserInformation findByUserName(String userName) {
        return userInformationDao.findByUserName(userName);
    }

    public UserInformation findByUserNameOrPhoneNumber(String phoneNumber, String phoneNumber1) {
        return userInformationDao.findByUserNameOrPhoneNumber(phoneNumber,phoneNumber1);
    }

    public String localConsume(Map<String, String> params, Integer userId) {
        Integer bean = Integer.valueOf(params.get("consumeBean"));
        UserInformation userInformation=userInformationDao.findOne(userId);
        BigDecimal bigDecimal3 = new BigDecimal(userInformation.getBean());
        BigDecimal bigDecimal4 = new BigDecimal(bean);
        Double result2 = bigDecimal3.subtract(bigDecimal4).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
        if(result2<0){
            result2 =0.0;
        }
        userInformation.setBean(result2);

        String code1 = SynchronizeUtils.getCode(Dictionary.MALL_HOST,"/userservice/userInfomation/synchronization", ValueUtil.toJson(userInformation),RequestMethod.post);
        //向mall用户中心同步个人信息。
//        HttpBean newHttp = new HttpBean(Dictionary.mall_userservice + "/userservice/userInfomation/synchronization", RequestMethod.post);
//        newHttp.addParameter("jsonData", ValueUtil.toJson(userInformation));
//        newHttp.run();
//        String temp1 = newHttp.getResponseContent();
//        String code1 = ValueUtil.getFromJson(temp1, "code");


        if(ValueUtil.notEmpity(code1)&&code1.equals("201")){
            userInformation.setSynStatus(1);
            userInformationDao.save(userInformation);
            return ValueUtil.toJson(HttpStatus.SC_CREATED,"success");
        }else {
            userInformation.setSynStatus(0);
            userInformationDao.save(userInformation);
            return ValueUtil.toJson(HttpStatus.SC_INTERNAL_SERVER_ERROR,"同步失败");
        }

    }
}
