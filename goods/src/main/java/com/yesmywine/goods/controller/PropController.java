
package com.yesmywine.goods.controller;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.goods.dao.ProperValueDao;
import com.yesmywine.goods.dao.PropertiesDao;
import com.yesmywine.goods.entityProperties.Properties;
import com.yesmywine.goods.entityProperties.PropertiesValue;
import com.yesmywine.goods.service.ProService;
import com.yesmywine.util.basic.MapUtil;
import com.yesmywine.util.basic.Threads;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by hz on 1/6/17.
 */
@RestController
@RequestMapping("/goods/properties")
public class PropController {
    @Autowired
    private ProService propService;
    @Autowired
    private ProperValueDao properValueDao;
    @Autowired
    private PropertiesDao propertiesDao;

    @RequestMapping(method = RequestMethod.GET)
    public String page(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize) {   //查看
        MapUtil.cleanNull(params);
        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }

        if (null != params.get("all") && params.get("all").toString().equals("true")) {
            return ValueUtil.toJson(propService.findAll());
        } else if (null != params.get("all")) {
            params.remove(params.remove("all").toString());
        }

        if(ValueUtil.notEmpity(params.get("propertiesId"))){
            return ValueUtil.toJson(HttpStatus.SC_OK,propService.findOne(Integer.valueOf(params.get("propertiesId").toString())));
        }
//        if(ValueUtil.notEmpity(params.get("categoryId"))&& Utils.isNum(params.get("categoryId").toString())){
//            Integer categoryId = Integer.valueOf(params.get("categoryId").toString()) ;
//            Category category = new Category();
//            category.setId(categoryId);
//            params.remove(params.remove("categoryId").toString());
//            params.put("category",category);
//        }else {
//            params.remove("categoryId");
//        }
        if(ValueUtil.isEmpity(params.get("cnName_l"))){
            params.remove("cnName_l");
        }
        if(ValueUtil.isEmpity(params.get("code_l"))){
            params.remove("code_l");
        }
//        if(ValueUtil.isEmpity(params.get("categoryId_l"))){
//            params.remove("categoryId_l");
//        }
//        params.put("deleteEnum_eq_com.hzbuvi.goods.bean.DeleteEnum","NOT_DELETE");
        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());
        pageModel.addCondition(params);
        return ValueUtil.toJson(HttpStatus.SC_OK,propService.findAll(pageModel));
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(@RequestParam Map<String, String> param) {   //增加属性及值
        try {
            String result = propService.addPrpo(param);
            if("success".equals(result)){
                return ValueUtil.toJson(HttpStatus.SC_CREATED,"success");
            }
            return ValueUtil.toError("500", result);
            //同步到商城
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

//    @RequestMapping(value = "/showOne", method = RequestMethod.GET)
//    public String showOne(Integer propId) {
//        return ValueUtil.toJson(HttpStatus.SC_OK, propService.findOne(propId));
//    }

    @RequestMapping(value = "/showPropValue", method = RequestMethod.GET)
    public String showPropValue(String propId) { //通过属性id组找到该属性下的值
        String [] prop=propId.split(",");
        com.alibaba.fastjson.JSONArray jsonArray2 = new com.alibaba.fastjson.JSONArray();
        for(int i=0;i<prop.length;i++){
            List<PropertiesValue> p=properValueDao.findByPropertiesId(Integer.parseInt(prop[i]));
            com.alibaba.fastjson.JSONArray jsonArray1 = new com.alibaba.fastjson.JSONArray();
            for(int j=0;j<p.size();j++){
                com.alibaba.fastjson.JSONObject jsonObject1 = new com.alibaba.fastjson.JSONObject();
                jsonObject1.put("value",p.get(j).getId());
                jsonObject1.put("label",p.get(j).getCnValue());
                jsonObject1.put("code",p.get(j).getCode());
                jsonArray1.add(jsonObject1);
            }
            com.alibaba.fastjson.JSONObject jsonObject2 = new com.alibaba.fastjson.JSONObject();
            jsonObject2.put("id",prop[i]);
            if(jsonArray1.size()>0){
                jsonObject2.put("prop",jsonArray1);
            }
            jsonArray2.add(jsonObject2);
        }
        return ValueUtil.toJson(HttpStatus.SC_OK,jsonArray2);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String updateSave(Integer propertiesId,String canSearch,String cnName,String isSku,String entryMode) {  //修改属性

       try {
           return ValueUtil.toJson(HttpStatus.SC_CREATED, propService.updateProp(propertiesId,canSearch,cnName,isSku,entryMode));
           //同步到商城
       }catch (yesmywineException e){
           Threads.createExceptionFile("goods",e.getMessage());
           return ValueUtil.toError(e.getCode(),e.getMessage());
       }
    }

//    @RequestMapping(value = "/addUpdate", method = RequestMethod.POST)
//    public String addUpdate(Integer propId, String code, String value) {  //编辑时新增新的属性值
//        try {
//            ValueUtil.verify(propId);
//            ValueUtil.verify(code);
//            ValueUtil.verify(value);
//            return ValueUtil.toJson("201",propService.updateAdd(propId, code, value));
//            //同步到商城
//        } catch (yesmywineException e) {
//            return ValueUtil.toError("500","Erro");
//        }
//    }
//
//    @RequestMapping(value = "/propValue", method = RequestMethod.DELETE)
//    public String deleteValue(Integer propValueId) {  //编辑时删除属性值
//        properValueDao.delete(propValueId);
//        return ValueUtil.toJson("204","success");
//        //同步到商城
//    }

    @RequestMapping(method = RequestMethod.DELETE)
    public String delete( Integer propertiesId) {       //删除属性
        try {
            String s = propService.deleteProp(propertiesId);
            if("success".equals(s)){
                return ValueUtil.toJson(HttpStatus.SC_NO_CONTENT,"success");
            }
            return ValueUtil.toError("500", s);
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
        //同步到商城
    }

//    @RequestMapping(value = "/propCanShow",method = RequestMethod.PUT)
//    public String propCanShow( Integer propertiesId) {       //修改属性是否显示
//        try {
//            ValueUtil.verify(propertiesId,"propertiesId");
//            return ValueUtil.toJson("201",propService.propCanShow(propertiesId));
//            //同步到商城
//        } catch (yesmywineException e) {
//            return ValueUtil.toError(e.getCode(),e.getMessage());
//        }
//    }
//    @RequestMapping(value = "/getValue/{id}", method = RequestMethod.GET)
//    public String getValue(@PathVariable("id") Integer categoryId) { //通过分类获取可查询的属性和值(前台用)
//        try {
//            ValueUtil.verify(categoryId);
//            return ValueUtil.toJson("200", propService.getProperByCategory(categoryId));
//        } catch (yesmywineException e) {
//            return ValueUtil.toError("500", "Erro");
//        }
//    }

//    @RequestMapping(value = "/getPropAndValue", method = RequestMethod.GET)
//    public String getProp(Integer categoryId) { //通过分类获取属性和值
//        try {
//            ValueUtil.verify(categoryId,"categoryId");
//            return ValueUtil.toJson(HttpStatus.SC_OK, propService.getGeneralProp(categoryId));
//        } catch (yesmywineException e) {
//            return ValueUtil.toError(e.getCode(),e.getMessage());
//        }
//    }

//    @RequestMapping(value = "/getMeth/{id}", method = RequestMethod.GET) //查出该分类下的属性值的组合
//    public String getMeth(@PathVariable("id") Integer categoryId) {
//        try {
//            ValueUtil.verify(categoryId);
//            return ValueUtil.toJson("200", propService.getMethods(categoryId));
//        } catch (yesmywineException e) {
//            return ValueUtil.toError("500", "Erro");
//        }
//    }
}

