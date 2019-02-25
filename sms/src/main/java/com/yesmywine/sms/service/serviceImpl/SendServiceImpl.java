package com.yesmywine.sms.service.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.sms.dao.ConfigureDao;
import com.yesmywine.sms.dao.SmsTemplateDao;
import com.yesmywine.sms.entity.SmsTemplate;
import com.yesmywine.sms.service.SendServiceService;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by wangdiandian on 2017/5/10.
 */
@Service
public class SendServiceImpl implements SendServiceService {
    @Autowired
    private ConfigureDao configureDao;
    @Autowired
    private SmsTemplateDao smsTemplateDao;
    @Autowired
    private SmsServiceImpl smsService;

    public String sms(Map<String,String> map)throws yesmywineException {
        String code = map.get("code");
        SmsTemplate smsTemplate=smsTemplateDao.findByCode(code);;
        String content= smsTemplate.getContent();
        JSONObject json = JSONObject.parseObject(map.get("json"));
        if(ValueUtil.notEmpity(json)){
            Set<String> jsonKeySet = json.keySet();
            for (String key : jsonKeySet) {
                String value = json.get(key).toString();
                System.out.println("key= " + key + " and value= " + value);
                content = content.replaceAll("\\$\\{(" + key + ")\\}", value);
            }
        }
        System.out.print(content);
        Map<String, String> param = new HashMap<>();
            param.put("phones",map.get("phones"));
            param.put("content",content);
            String result=smsService.sendSms(param);
        return result;
}

    public String smsBatch(String json)throws yesmywineException {

        JSONArray adjustArray = JSON.parseArray(json);
        com.alibaba.fastjson.JSONArray jsonArray = new com.alibaba.fastjson.JSONArray();
        for (int i = 0; i < adjustArray.size(); i++) {
            com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
            JSONObject adjustCommand = (JSONObject) adjustArray.get(i);
            String code = adjustCommand.getString("code");//短信类型
            String phones = adjustCommand.getString("phones");//手机号
            String sendtime = adjustCommand.getString("sendtime");//时间
            String msgid = adjustCommand.getString("msgid");//该批短信编号(32位UUID)，需保证唯一，选填；userId
            String map = adjustCommand.getString("map");//各种变量
            SmsTemplate smsTemplate=smsTemplateDao.findByCode(code);;
            String content = smsTemplate.getContent();//模板内容
//            if(map!=null) {
//                JSONObject jsonObj = JSON.parseObject(map);
//                for (Map.Entry<String, Object> entry : jsonObj.entrySet()) {
//                    content = content.replace(entry.getKey(), entry.getValue().toString());
//                }
//            }
            JSONObject jsons = JSONObject.parseObject(map);
            if(ValueUtil.notEmpity(jsons)){
                Set<String> jsonKeySet = jsons.keySet();
                for (String key : jsonKeySet) {
                    String value = jsons.get(key).toString();
                    System.out.println("key= " + key + " and value= " + value);
                    content = content.replaceAll("\\{\\{(" + key + ")\\}\\}", value);
                }
            }
            jsonObject.put("phones", phones);
            jsonObject.put("sendtime", sendtime);
            jsonObject.put("content", content);
            jsonObject.put("msgid", msgid);
            jsonArray.add(jsonObject);
        }
        Map<String, String> param = new HashMap<>();
        param.put("json", ValueUtil.toJson(jsonArray));
        String result=smsService.bulkSms(param);
            return result;

        }

}
