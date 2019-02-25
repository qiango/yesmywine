package com.yesmywine.user.service.impl;


import com.yesmywine.user.dao.MoneyDao;
import com.yesmywine.user.entity.MonBeans;
import com.yesmywine.user.service.MoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by hz on 3/27/17.
 */
@Service
public class MoneyServiceImpl implements MoneyService {
    @Autowired
    private MoneyDao moneyDao;


    public String create(String proportion){
        if(null!=moneyDao.findId()){
            moneyDao.delete(moneyDao.findId());
        }
        MonBeans monBeans=new MonBeans();
        monBeans.setCreateTime(new Date());
        monBeans.setUpdateTime(new Date());
        monBeans.setProportion(proportion);
        moneyDao.save(monBeans);
        return "success";
    }


    public String update(String proportion){
        MonBeans monBeans=moneyDao.findAll().get(0);
        monBeans.setProportion(proportion);
        monBeans.setUpdateTime(new Date());
        moneyDao.save(monBeans);
        return "success";
    }
}
