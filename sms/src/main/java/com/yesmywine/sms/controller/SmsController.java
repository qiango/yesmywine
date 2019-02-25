package com.yesmywine.sms.controller;

import com.yesmywine.sms.service.SmsService;

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
 * Created by wangdiandian on 2017/5/5.
 */
@RestController
@RequestMapping("/sms/sms")
public class SmsController {
    @Autowired
    private SmsService smsService;
    @RequestMapping(value = "sendSms",method = RequestMethod.POST)
    public String sendSms(@RequestParam Map<String,String> map){
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED,smsService.sendSms(map));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }
    @RequestMapping( value = "bulkSms",method = RequestMethod.POST)
    public String bulkSms(@RequestParam Map<String,String> map){
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED,smsService.bulkSms(map));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping( value = "smsReport",method = RequestMethod.POST)
    public String smsReport(@RequestParam Map<String,String> map){//短信状态报告
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED,smsService.smsReport(map));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(  value = "getReplySms",method = RequestMethod.POST)
    public String getReplySms(@RequestParam Map<String,String> map){//获取回复短信
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED,smsService.getReplySms(map));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }
}
