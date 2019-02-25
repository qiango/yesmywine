package com.yesmywine.sms.service.serviceImpl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.sms.dao.SmsTemplateDao;
import com.yesmywine.sms.entity.SmsTemplate;
import com.yesmywine.sms.service.SmsTemplateService;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.encode.Encode;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by wangdiandian on 2017/5/8.
 */
@Service
public class SmsTemplateImpl extends BaseServiceImpl<SmsTemplate,Integer> implements SmsTemplateService {

    @Autowired
    private SmsTemplateDao smsTemplateDao;

    public String creat(Map<String, String> param) throws yesmywineException {//新增
//        ValueUtil.verify(param, new String[]{"content","typeName"});
        SmsTemplate smsTemplate = new SmsTemplate();
        String code = Encode.getSalt(10);//随机生成
        smsTemplate.setTypeName(param.get("typeName"));
        smsTemplate.setContent(param.get("content"));
        smsTemplate.setCode(code);
        smsTemplateDao.save(smsTemplate);
        return "success";
    }

    public SmsTemplate updateLoad(Integer id) throws yesmywineException {//加载
        ValueUtil.verify(id, "idNull");
        SmsTemplate smsTemplate = smsTemplateDao.findOne(id);
        return smsTemplate;
    }
//    public String delete(Integer id) throws yesmywineException {//删除
//        ValueUtil.verify(id, "idNull");
//        SmsTemplate smsTemplate = smsTemplateDao.findOne(id);
//        smsTemplateDao.delete(smsTemplate);
//        return "success";
//    }

    public String updateSave(Map<String, String> param) throws yesmywineException {//修改保存
        Integer id = Integer.parseInt(param.get("id"));
        SmsTemplate smsTemplate = smsTemplateDao.findOne(id);
        smsTemplate.setContent(param.get("content"));;
        smsTemplate.setTypeName(param.get("typeName"));
        smsTemplateDao.save(smsTemplate);
        return "success";
    }
}
