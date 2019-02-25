package com.yesmywine.sms.service.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.api.sms.json.JSONHttpClient;
import com.dahantc.api.sms.json.SmsData;
import com.yesmywine.sms.dao.ConfigureDao;
import com.yesmywine.sms.entity.Configure;
import com.yesmywine.sms.service.SmsService;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;

import org.apache.commons.httpclient.URIException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by wangdiandian on 2017/5/5.
 */
@Service
public class SmsServiceImpl implements SmsService {
    @Autowired
    private ConfigureDao configureDao;

    public String sendSms(Map<String, String> param) throws yesmywineException {

        String verification = getSalt(6);//随机生成验证码
        Configure configure = configureDao.rule();
        if(configure==null){
            ValueUtil.isError( "没有配置账号");
        }
        String account = configure.getAccount();//账号
        String password = configure.getPassword();//账号密码
        String sign = configure.getSign();//短信签名
        String subcode = configure.getSubcode();//短信签名对应子码
        String msgid = param.get("msgid");//该批短信编号(32位UUID)，需保证唯一，选填；userId
        String phone = param.get("phones");//电话
        String content = param.get("content");//内容
        Integer results=content.indexOf("code");

        content = content.replace("${code}",verification);
            System.out.print(content);
        Logger LOG = Logger.getLogger(SmsServiceImpl.class);
        JSONHttpClient jsonHttpClient = null;
        String sendhRes = "";
        try {
            jsonHttpClient = new JSONHttpClient("http://www.dh3t.com");
            jsonHttpClient.setRetryCount(1);
            sendhRes = jsonHttpClient.sendSms(account, password, phone, content, sign, subcode);
            LOG.info("提交单条普通短信响应：" + sendhRes);
        } catch (URIException e) {
            e.printStackTrace();
        }
        System.out.print(sendhRes);
        String result = ValueUtil.getFromJson(sendhRes, "result");
        String desc = ValueUtil.getFromJson(sendhRes, "desc");
        if(!result.equals("0")){
            ValueUtil.isError(desc);
        }
        if(results!=-1){
            return verification;
        }
        return "success";
    }

    public String getSalt(int length) {
        char[] chr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            buffer.append(chr[random.nextInt(10)]);
        }
        return buffer.toString();
    }

    public String bulkSms(Map<String, String> param) throws yesmywineException {
        Configure configure = configureDao.rule();
        if(configure==null){
            ValueUtil.isError( "没有配置账号");
        }
        String account = configure.getAccount();//账号
        String password = configure.getPassword();//账号密码
        String sign = configure.getSign();//短信签名
        String subcode = configure.getSubcode();//短信签名对应子码
        String json=param.get("json");
        String jsons = ValueUtil.getFromJson(json, "data");
        JSONArray adjustArray = JSON.parseArray(jsons);
        List<SmsData> list = new ArrayList<>();
        JSONHttpClient jsonHttpClient = null;
        Logger LOG = Logger.getLogger(SmsServiceImpl.class);
        for (int i = 0; i < adjustArray.size(); i++) {
            String verification = getSalt(6);//随机生成验证码
            JSONObject adjustCommand = (JSONObject) adjustArray.get(i);
            String msgid = adjustCommand.getString("msgid");//该批短信编号(32位UUID)，需保证唯一，选填；userId
            String phones = adjustCommand.getString("phones");//手机号
            String sendtime = adjustCommand.getString("sendtime");//时间
            String content = adjustCommand.getString("content");//短信内容
            content = content.replace("verification", verification);
            list.add(new SmsData(phones, content, msgid, sign, subcode, sendtime));
        }
        try {
            jsonHttpClient = new JSONHttpClient("http://www.dh3t.com");
        } catch (URIException e) {
            e.printStackTrace();
        }
        jsonHttpClient.setRetryCount(1);
            String sendBatchRes = jsonHttpClient.sendBatchSms(account,
                    password, list);
            LOG.info("提交批量短信响应：" + sendBatchRes);
        String result = ValueUtil.getFromJson(sendBatchRes, "result");
        String desc = ValueUtil.getFromJson(sendBatchRes, "desc");
        if(!result.equals("0")){
            ValueUtil.isError(desc);
        }
        return result;
    }

    public String smsReport(Map<String, String> param) throws yesmywineException {

        Configure configure = configureDao.rule();
        String account = configure.getAccount();//账号
        String password = configure.getPassword();//账号密码

        Logger LOG = Logger.getLogger(SmsServiceImpl.class);
        JSONHttpClient jsonHttpClient = null;
        String reportRes = "";
        try {
            jsonHttpClient = new JSONHttpClient("http://www.dh3t.com");
            jsonHttpClient.setRetryCount(1);
            reportRes = jsonHttpClient.getReport(account, password);
            LOG.info("获取状态报告响应：" + reportRes);
        } catch (URIException e) {
            e.printStackTrace();
        }
        String result = ValueUtil.getFromJson(reportRes, "result");
        String desc = ValueUtil.getFromJson(reportRes, "desc");
        System.out.print(reportRes);
        if(!result.equals("0")){
            ValueUtil.isError(desc);
        }
        return result;
    }

    public String getReplySms(Map<String, String> param) throws yesmywineException {
        Configure configure = configureDao.rule();
        String account = configure.getAccount();//账号
        String password = configure.getPassword();//账号密码

        Logger LOG = Logger.getLogger(SmsServiceImpl.class);
        JSONHttpClient jsonHttpClient = null;
        String smsRes = "";
        try {
            jsonHttpClient = new JSONHttpClient("http://www.dh3t.com");
            jsonHttpClient.setRetryCount(1);
            smsRes = jsonHttpClient.getSms(account, password);
            LOG.info("获取上行短信响应：" + smsRes);
        } catch (URIException e) {
            e.printStackTrace();
        }
        String result = ValueUtil.getFromJson(smsRes, "result");
        String desc = ValueUtil.getFromJson(smsRes, "desc");
        System.out.print(smsRes);
        if(!result.equals("0")){
            ValueUtil.isError(desc);
        }
        return result;
    }
}