package com.yesmywine.sms.service.serviceImpl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.sms.dao.ConfigureDao;
import com.yesmywine.sms.entity.Configure;
import com.yesmywine.sms.service.ConfigureService;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by wangdiandian on 2017/5/8.
 */
@Service
public class ConfigureImpl  extends BaseServiceImpl<Configure,Integer> implements ConfigureService {
    @Autowired
    private ConfigureDao configureDao;

    public String updateSave(Map<String,String> param)throws yesmywineException {//跟新基本配置
        ValueUtil.verify(param, new String[]{"account","password","sign"});

        String id=param.get("id");
        String account=param.get("account");
        String password=param.get("password");
        String sign=param.get("sign");
        String subcode=param.get("subcode");
        Configure configure=configureDao.findOne(Integer.valueOf(id));
        configure.setAccount(account);
        configure.setPassword(password);
        configure.setSign(sign);
        configure.setSubcode(subcode);
        configureDao.save(configure);
        return "success";
    }
}
