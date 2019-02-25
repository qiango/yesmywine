package com.yesmywine.email.service.Impl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.email.dao.EmailDao;
import com.yesmywine.email.entity.Email;
import com.yesmywine.email.service.EmailService;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by wangdiandian on 2017/5/16.
 */
@Service
public class EmailImpl extends BaseServiceImpl<Email,Integer> implements EmailService {

    @Autowired
    private EmailDao emailDao;



    public String updateSave(Map<String, String> param) throws yesmywineException {//修改保存
        ValueUtil.verify(param, new String[]{"email","emailLoginName","emailPassword","port","encryption"});
        if(ValueUtil.isEmpity(param.get("id"))){
            Email email = new Email();
            email.setEmail(param.get("email"));
            email.setEmailLoginName(param.get("emailLoginName"));
            email.setEmailPassword(param.get("emailPassword"));
            email.setPort(param.get("port"));
            email.setEncryption(param.get("encryption"));
            emailDao.save(email);
        }
        else {
            Integer id = Integer.parseInt(param.get("id"));
            Email email = emailDao.findOne(id);
            email.setEmail(param.get("email"));
            email.setEmailLoginName(param.get("emailLoginName"));
            email.setEmailPassword(param.get("emailPassword"));
            email.setPort(param.get("port"));
            email.setEncryption(param.get("encryption"));
            emailDao.save(email);
        }
        return "success";
    }
}

