package com.yesmywine.user.controller;


import com.yesmywine.user.dao.MoneyDao;
import com.yesmywine.user.entity.MonBeans;
import com.yesmywine.user.service.MoneyService;
import com.yesmywine.util.basic.Threads;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hz on 3/27/17.
 */
@RestController
@RequestMapping("/user/money")
public class MoneyController {
    @Autowired
    private MoneyService moneyService;
    @Autowired
    private MoneyDao moneyDao;

    @RequestMapping(method = RequestMethod.GET)
    public String show() {
        try {
            if (0 == moneyDao.findAlls().size()) {
               ValueUtil.isError("比例暂未设置");
            }
            MonBeans monBeans = moneyDao.findAlls().get(0);
            monBeans.setPro(monBeans.getProportion().split(":")[1]);
            return ValueUtil.toJson( monBeans);
        } catch (yesmywineException e) {
            Threads.createExceptionFile("user",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(String proportion){
        try {
            ValueUtil.verify(proportion);
            return ValueUtil.toJson(HttpStatus.SC_CREATED,moneyService.create(proportion));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("user",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(String proportion){
        try {
            ValueUtil.verify(proportion);
            return ValueUtil.toJson(HttpStatus.SC_CREATED,moneyService.update(proportion));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("user",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

}
