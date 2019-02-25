package com.yesmywine.sms.controller;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.sms.service.SmsTemplateService;
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
 * Created by wangdiandian on 2017/5/8.
 */
@RestController
@RequestMapping("/sms/smsTemplate")
public class SmsTemplateController {
    @Autowired
    private SmsTemplateService smsTemplateService;

    @RequestMapping(method = RequestMethod.POST)
    public String create(@RequestParam Map<String,String> param){//新增短信模板
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED,smsTemplateService.creat(param));
        }catch (yesmywineException e){
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }
//    @RequestMapping(method = RequestMethod.DELETE)
//    public String delete(Integer id){//删除短信模板
//        try {
//            return ValueUtil.toJson(HttpStatus.SC_NO_CONTENT,smsTemplateService.delete(id));
//        }catch (yesmywineException e){
//            return ValueUtil.toError(e.getCode(),e.getMessage());
//        }
//    }
    @RequestMapping(method = RequestMethod.PUT)
    public String updateSave(@RequestParam Map<String,String> param){//修改保存短信模板
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED,smsTemplateService.updateSave(param));
        }catch (yesmywineException e){
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params,Integer pageNo,Integer pageSize,Integer id) throws  Exception{//分页查询短信模板

        if(id!=null){
            return ValueUtil.toJson(HttpStatus.SC_OK, smsTemplateService.updateLoad(id));
        }
        if(null!=params.get("all")&&params.get("all").toString().equals("true")){
            return ValueUtil.toJson(smsTemplateService.findAll());
        }else if(null!=params.get("all")){
            params.remove(params.remove("all").toString());
        }
        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }
        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());
        pageModel.addCondition(params);
        pageModel = smsTemplateService.findAll(pageModel);
        return ValueUtil.toJson(HttpStatus.SC_OK,pageModel);
    }
}
