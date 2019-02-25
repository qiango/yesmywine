package com.yesmywine.sms.controller;

import com.yesmywine.sms.service.SendServiceService;
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
 * Created by wangdiandian on 2017/5/10.
 */
@RestController
@RequestMapping("/sms/send")
public class SendController {
    @Autowired
    private SendServiceService sendServiceService;
    @RequestMapping(value = "/sendSms/itf",method = RequestMethod.POST)
    public String sendSms(@RequestParam Map<String,String> map){//短信下发
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED,sendServiceService.sms(map));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }
    @RequestMapping(value = "/smsBatch/itf",method = RequestMethod.POST)
    public String smsBatch(String json){//批量短信下发
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED,sendServiceService.smsBatch(json));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }
}
