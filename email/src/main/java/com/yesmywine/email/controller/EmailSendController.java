package com.yesmywine.email.controller;

import com.yesmywine.email.service.EmailSendService;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by wangdiandian on 2017/5/16.
 */
@RestController
@RequestMapping("/web/email/emailSend")
public class EmailSendController {
    @Autowired
    private EmailSendService emailSendService;

    @RequestMapping(method = RequestMethod.POST)
    public String create(@RequestParam Map<String,String> param){//电子邮件发送
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED,emailSendService.send(param));
        }catch (yesmywineException e){
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }
}
