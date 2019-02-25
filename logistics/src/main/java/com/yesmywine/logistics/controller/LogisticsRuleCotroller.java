package com.yesmywine.logistics.controller;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.logistics.service.Impl.ExcelImportService;
import com.yesmywine.logistics.service.ImportService;
import com.yesmywine.logistics.service.LogisticsRuleService;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangdiandian on 2017/3/30.
 */

@RestController
@RequestMapping("/logistics/logisticsRule")
public class LogisticsRuleCotroller {

    @Autowired
    private LogisticsRuleService logisticsRuleService;
    @Autowired
    private ImportService importService;

    // 允许上传的格式
    private static final String[] IMAGE_TYPE = new String[] { ".xlsx", ".xls"};

    @RequestMapping(method = RequestMethod.POST)
    public String create(@RequestParam Map<String, String> param) {//新增承运商物流规则
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, logisticsRuleService.addLogisticsRule(param));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public String delete(Integer id) {//删除承运商物流规则
        try {
            return ValueUtil.toJson(HttpStatus.SC_NO_CONTENT, logisticsRuleService.delete(id));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@RequestParam Map<String, String> param) {//修改保存承运商物流规则
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, logisticsRuleService.updateSave(param));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }


    @RequestMapping( method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params,Integer pageNo,Integer pageSize,Integer id)throws yesmywineException {//查询承运商物流规则
        if(id!=null){
            return ValueUtil.toJson(HttpStatus.SC_OK, logisticsRuleService.updateLoad(id));
          }
        params.put("deleteEnum", 0);
        if(null!=params.get("all")&&params.get("all").toString().equals("true")){
            return ValueUtil.toJson(logisticsRuleService.findAll());
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
        pageModel = logisticsRuleService.findAll(pageModel);
        return ValueUtil.toJson(pageModel);
    }

    @RequestMapping(value = "area", method = RequestMethod.GET)
    public String logisticsRuleplus(String distributionArea,Integer shipperId) {//配送区域不可重复
        try {

            return ValueUtil.toJson(HttpStatus.SC_OK, logisticsRuleService.logisticsRuleplus(distributionArea,shipperId));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public String uploadLogisticsRule(@RequestParam("uploadFiles")MultipartFile uploadFiles)
            throws Exception {
        String origName = uploadFiles.getOriginalFilename();
        // 校验格式
        boolean isLegal = false;
        for (String type : IMAGE_TYPE) {
            if (StringUtils.endsWithIgnoreCase(origName, type)) {
                isLegal = true;
                break;
            }
        }
        if(!isLegal){
            return ValueUtil.toError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "文件格式不正确");
        }
        InputStream inputStream = uploadFiles.getInputStream();
        ExcelImportService excelImportService = new ExcelImportService();
        String keyName = "distributionAreaName,areaName,deliveryCharge,rate,shipperName";
        String keyType = "string,string,string,string,string";
        String nullable = "false,false,false,false,false";

        List<Map<String, Object>> list = excelImportService.importEx(keyName, keyType,nullable, (FileInputStream) inputStream, origName);
        List<Map<String, Object>> ReList = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            if(ValueUtil.notEmpity(list.get(i).get("erro"))){
                Map<String, Object> map = new HashMap<>();
                map = list.get(i);
                ReList.add(map);
            }
        }
        if(ReList.size()!=0){
            return ValueUtil.toJson(500, ReList);
        }
        List<Map<String, Object>> listRe = this.importService.importLogisticsRule(list);
        if(listRe.size()==0){
            return ValueUtil.toJson(HttpStatus.SC_CREATED, "success");
        }else {
            for(Map<String, Object> map: listRe){
                ReList.add(map);
            }
            return ValueUtil.toError("500","erro", ReList);
        }
    }
}
