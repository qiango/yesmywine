package com.yesmywine.sms.controller;

import com.yesmywine.sms.service.ConfigureService;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by wangdiandian on 2017/5/8.
 */
@RestController
@RequestMapping("/sms/configure")
public class ConfigureController {//配置账号
    @Autowired
    private ConfigureService configureService;

    @RequestMapping(method = RequestMethod.PUT)
    public String updateSave(@RequestParam Map<String,String>param){//修改账号
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED,configureService.updateSave(param));
        }catch (yesmywineException e){
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }
    @RequestMapping(method = RequestMethod.GET)
    public String show() {//显示配置账号
        return ValueUtil.toJson(configureService.findAll().get(0));
    }
}
