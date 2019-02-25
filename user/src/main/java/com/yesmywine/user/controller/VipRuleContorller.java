package com.yesmywine.user.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.user.entity.VipRule;
import com.yesmywine.user.service.VipRuleService;
import com.yesmywine.util.basic.ValueUtil;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by SJQ on 2017/4/20.
 */
@RestController
@RequestMapping("/user/vipRule/syn")
public class VipRuleContorller {

    @Autowired
    private VipRuleService vipRuleService;

    @RequestMapping(method = RequestMethod.POST)
    public String synLevel(String jsonData){
        JSONObject jsonObject = JSON.parseObject(jsonData);
        String status = jsonObject.getString("msg");
        if(status.equals("save")){
            save(jsonObject);
        }else if(status.equals("update")){
            update(jsonObject);
        }else if(status.equals("delete")){
            delete(jsonObject);
        }
        return ValueUtil.toJson(HttpStatus.SC_CREATED,"SUCCESS");
    }

    private void delete(JSONObject jsonObject) {
        String mallId = jsonObject.getString("data");
        vipRuleService.deleteByMallId(Integer.valueOf(mallId));
    }

    private void update(JSONObject jsonObject) {
        JSONObject dataJson = jsonObject.getJSONObject("data");
        String mallId = dataJson.getString("id");
        String vipName = dataJson.getString("vipName");
        String requireValue = dataJson.getString("requireValue");
        String keep = dataJson.getString("keep");
        String url = dataJson.getString("url");
        String keepDays = dataJson.getString("keepDays");
        String discount = dataJson.getString("discount");

        VipRule vipRule = vipRuleService.findByMallId(Integer.valueOf(mallId));
        vipRule.setVipName(vipName);
        vipRule.setRequireValue(Integer.valueOf(requireValue));
        vipRule.setKeep(Integer.valueOf(keep));
        vipRule.setKeepDays(Integer.valueOf(keepDays));
        vipRule.setUrl(url);
        vipRule.setDiscount(Double.valueOf(discount));
        vipRuleService.save(vipRule);
    }

    private void save(JSONObject jsonObject) {
        JSONObject dataJson = jsonObject.getJSONObject("data");
        String mallId = dataJson.getString("id");
        String vipName = dataJson.getString("vipName");
        String requireValue = dataJson.getString("requireValue");
        String keep = dataJson.getString("keep");
        String url = dataJson.getString("url");
        String keepDays = dataJson.getString("keepDays");
        String discount = dataJson.getString("discount");
        VipRule vipRuleOld = vipRuleService.findByMallId(Integer.valueOf(mallId));
        if(ValueUtil.isEmpity(vipRuleOld)){
            VipRule vipRule = new VipRule();
            vipRule.setMallId(Integer.valueOf(mallId));
            vipRule.setVipName(vipName);
            vipRule.setRequireValue(Integer.valueOf(requireValue));
            vipRule.setKeep(Integer.valueOf(keep));
            vipRule.setKeepDays(Integer.valueOf(keepDays));
            vipRule.setUrl(url);
            vipRule.setDiscount(Double.valueOf(discount));
            vipRuleService.save(vipRule);
        }else {
            vipRuleOld.setVipName(vipName);
            vipRuleOld.setRequireValue(Integer.valueOf(requireValue));
            vipRuleOld.setKeep(Integer.valueOf(keep));
            vipRuleOld.setKeepDays(Integer.valueOf(keepDays));
            vipRuleOld.setUrl(url);
            vipRuleOld.setDiscount(Double.valueOf(discount));
            vipRuleService.save(vipRuleOld);
        }


    }

}
