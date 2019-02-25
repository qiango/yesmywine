
package com.yesmywine.goods.controller;

import com.sdicons.json.mapper.MapperException;
import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.goods.bean.EntryMode;
import com.yesmywine.goods.dao.CategoryDao;
import com.yesmywine.goods.dao.ProperValueDao;
import com.yesmywine.goods.dao.PropertiesDao;
import com.yesmywine.goods.dao.SkuDao;
import com.yesmywine.goods.entity.Sku;
import com.yesmywine.goods.entity.SkuCommonProp;
import com.yesmywine.goods.entity.SkuProp;
import com.yesmywine.goods.entityProperties.Category;
import com.yesmywine.goods.entityProperties.Properties;
import com.yesmywine.goods.entityProperties.PropertiesValue;
import com.yesmywine.goods.entityProperties.Supplier;
import com.yesmywine.goods.service.Impl.SkuServiceImpl;
import com.yesmywine.goods.service.SkuService;
import com.yesmywine.goods.util.Utils;
import com.yesmywine.httpclient.bean.HttpBean;
import com.yesmywine.util.basic.*;
import com.yesmywine.util.error.yesmywineException;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hz on 3/15/17.
 */
@RestController
@RequestMapping("/goods/sku")
public class SkuController {
    @Autowired
    private SkuService skuService;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private PropertiesDao propertiesDao;
    @Autowired
    private ProperValueDao properValueDao;
    @Autowired
    private CategoryDao categoryDao;

    @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize) {   //查看所有Sku
        MapUtil.cleanNull(params);
        if (null != params.get("all") && params.get("all").toString().equals("true")) {
            List<Sku> all = skuService.findAll();
            List<Sku> content = new ArrayList<>();
            for(Sku sku:all){
//                String property = sku.getProperty();
                List<SkuProp> skuProps = sku.getSkuProp();
//                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(property);
                com.alibaba.fastjson.JSONObject RejsonObject = new com.alibaba.fastjson.JSONObject();
                for(SkuProp skuProp:skuProps){
                    Properties properties = this.propertiesDao.findOne(skuProp.getPropertiesId());
                    PropertiesValue propertiesValue = this.properValueDao.findOne(skuProp.getPropValue());
                    RejsonObject.put(properties.getCnName(), propertiesValue.getCnValue());
                }
                sku.setProperty(RejsonObject.toJSONString());
                content.add(sku);
            }
            return ValueUtil.toJson(content);
        } else if (null != params.get("all")) {
            params.remove(params.remove("all").toString());
        }

        if(ValueUtil.notEmpity(params.get("skuId"))){
            Sku sku = skuService.findOne(Integer.valueOf(params.get("skuId").toString()));
            List<SkuProp> skuProps = sku.getSkuProp();
            com.alibaba.fastjson.JSONObject RejsonObject = new com.alibaba.fastjson.JSONObject();
            for(SkuProp skuProp:skuProps){
                Properties properties = this.propertiesDao.findOne(skuProp.getPropertiesId());
                PropertiesValue propertiesValue = this.properValueDao.findOne(skuProp.getPropValue());
                skuProp.setPropName(properties.getCnName());
                skuProp.setPropValueName(propertiesValue.getCnValue());
                RejsonObject.put(properties.getCnName(), propertiesValue.getCnValue());
            }
            List<SkuCommonProp> list=sku.getSkuCommonProp();
            for(SkuCommonProp skuCommonProp:list){
                String propValueName=null;
                Properties properties = this.propertiesDao.findOne(skuCommonProp.getPropId());
                String propValueId=skuCommonProp.getPropValueId();
                if(null!=propValueId&&!propValueId.equals("")){
                    if(properties.getEntryMode()== EntryMode.lists) {
                        PropertiesValue propertiesValue = this.properValueDao.findOne(Integer.parseInt(propValueId));
                        propValueName = propertiesValue.getCnValue();
                    }
                }
                skuCommonProp.setPropName(properties.getCnName());
                skuCommonProp.setPropValueName(propValueName);
            }
            sku.setProperty(RejsonObject.toJSONString());
            return ValueUtil.toJson(HttpStatus.SC_OK,sku);
        }

        if(ValueUtil.notEmpity(params.get("categoryId"))&& Utils.isNum(params.get("categoryId").toString())){
            Integer categoryId = Integer.valueOf(params.get("categoryId").toString()) ;
            Category category = new Category();
            category.setId(categoryId);
            params.remove(params.remove("categoryId").toString());
            params.put("category",category);
        }else {
            params.remove("categoryId");
        }
        if(ValueUtil.notEmpity(params.get("supplierId"))&& Utils.isNum(params.get("supplierId").toString())){
            Integer supplierId = Integer.valueOf(params.get("supplierId").toString()) ;
            Supplier supplier = new Supplier();
            supplier.setId(supplierId);
            params.remove(params.remove("supplierId").toString());
            params.put("supplier",supplier);
        }else {
            params.remove("supplierId");
        }
        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }
        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());
        if(ValueUtil.isEmpity(params.get("skuName_l"))){
            params.remove("skuName_l");
        }
        if(ValueUtil.isEmpity(params.get("code_l"))){
            params.remove("code_l");
        }
        pageModel.addCondition(params);
        PageModel all = skuService.findAll(pageModel);
//        List<Sku> content = all.getContent();
//        List<Sku> content2 = new ArrayList<>();
//        for(Sku sku:content){
//            List<SkuProp> skuProps = sku.getSkuProp();
//            com.alibaba.fastjson.JSONObject RejsonObject = new com.alibaba.fastjson.JSONObject();
//            for(SkuProp skuProp:skuProps){
//                Properties properties = this.propertiesDao.findOne(skuProp.getPropertiesId());
//                PropertiesValue propertiesValue = this.properValueDao.findOne(skuProp.getPropValue());
//                RejsonObject.put(properties.getCnName(), propertiesValue.getCnValue());
//            }
//            sku.setProperty(RejsonObject.toJSONString());
//            content2.add(sku);
//        }
//        all.setContent(content2);
        return ValueUtil.toJson(HttpStatus.SC_OK,all);
    }

    @RequestMapping(value = "/showSkuValue", method = RequestMethod.GET)  //分类下查看sku属性及值
    public String showSku(Integer categoryId,Integer type) {//(0:sku属性及值,1:普通属性,2:全部属性)
        try {
            ValueUtil.verify(type,"type");
            return ValueUtil.toJson(HttpStatus.SC_OK, skuService.getSku(categoryId,type));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.PUT)  //修改成本价
    public String updateCostPrice(String jsonArray) {
        try {
            ValueUtil.verify(jsonArray,"jsonArray");
//            HttpBean httpBean = new HttpBean(ConstantData.goodsUrl+"/goods/sku", RequestMethod.put);
//            httpBean.addParameter("jsonArray", jsonArray);
//            httpBean.run();
//            String temp = httpBean.getResponseContent();
//            String code=ValueUtil.getFromJson(temp,"code");
            String jsonData=jsonArray;
            String code=SynchronizeUtils.getCode(Dictionary.MALL_HOST,"/goods/sku",jsonData, com.yesmywine.httpclient.bean.RequestMethod.put);
            if(!code.equals("200")){
                return "商城修改失败";
            }
            JSONArray jsonArrayNew = new JSONArray(jsonArray);
            List<Sku> skuList=new ArrayList<>();
            for (int i = 0; i < jsonArrayNew.length(); i++) {
                JSONObject jsonObject = jsonArrayNew.getJSONObject(i);
                Sku sku=skuService.findOne(Integer.parseInt(jsonObject.get("skuId") + ""));
                sku.setCostPrice(jsonObject.get("price") + "");
                skuList.add(sku);
            }

            SkuServiceImpl service = new SkuServiceImpl();
            for(Sku sku:skuList){
                String sku_Name = sku.getSkuName();
                String sku_Code  = sku.getCode();
                String costPrice  = sku.getCostPrice();
                Integer skuType = sku.getType();
                com.alibaba.fastjson.JSONObject  requestJson = new com.alibaba.fastjson.JSONObject();
                requestJson.put("function",1);
                com.alibaba.fastjson.JSONObject dataJson = new com.alibaba.fastjson.JSONObject();
                dataJson.put("skuName",sku_Name);
                dataJson.put("skuCode",sku_Code);
                dataJson.put("costPrice",costPrice);
                switch (skuType){
                    case 0:
                        dataJson.put("skuType","实体");
                        break;
                    case 1:
                        dataJson.put("skuType","虚拟");
                        break;
                }
                requestJson.put("data",dataJson);
                //同步到OMS
                String result = SynchronizeUtils.getOmsResult(Dictionary.OMS_HOST,"/updateBaseCustomerSku", com.yesmywine.httpclient.bean.RequestMethod.post,"",requestJson.toJSONString());
                if(result != null ){
                    String status = ValueUtil.getFromJson(result,"status");
                    if(!status.equals("success")){
                        //回滚修改商城的sku
                        HttpBean rollBackHttpBean = new HttpBean(Dictionary.MALL_HOST+"/goods/sku/itf", com.yesmywine.httpclient.bean.RequestMethod.put);
                        rollBackHttpBean.addParameter("jsonArray", jsonArray);
                        rollBackHttpBean.run();
                        String rollBack_temp = rollBackHttpBean.getResponseContent();
                        String rollBack_code=ValueUtil.getFromJson(rollBack_temp,"code");
                        if(!rollBack_code.equals("200")){
                            return "商城回滚修改失败";
                        }
                        ValueUtil.isError("向OMS同步sku失败");
                    }
                }
            }
            skuService.save(skuList);
            return ValueUtil.toJson(HttpStatus.SC_OK,"success");
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }


    @RequestMapping(value = "/code/itf", method = RequestMethod.GET)  //查看单个sku详情
    public String findByCode(String code) {
        try {
            ValueUtil.verify(code,"code");
            return ValueUtil.toJson(HttpStatus.SC_OK, skuService.getSkuInfoByCode(code));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(value = "/showOne", method = RequestMethod.GET)  //查看单个sku详情
    public String show(Integer skuId) {
        try {
            ValueUtil.verify(skuId,"skuId");
            return ValueUtil.toJson(HttpStatus.SC_OK, skuService.showSku(skuId));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

//    @RequestMapping(value = "/showOne", method = RequestMethod.GET)  //查看单个sku详情
//    public String show(Integer skuId) {
//        try {
//            ValueUtil.verify(skuId,"skuId");
//            return ValueUtil.toJson(HttpStatus.SC_OK, skuService.showSku(skuId));
//        } catch (yesmywineException e) {
//            return ValueUtil.toError(e.getCode(),e.getMessage());
//        }
//    }


    @RequestMapping(value = "/findByCategoryId", method = RequestMethod.GET)
    public String show(Integer skuId,Integer categoryId) {
        try {
            ValueUtil.verify(skuId,"skuId");
            ValueUtil.verify(categoryId,"categoryId");
            return ValueUtil.toJson(HttpStatus.SC_OK,skuService.findByCate(skuId, categoryId));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }


    @RequestMapping( method = RequestMethod.POST)  //新建sku
    public String create(Integer supplierId,String skuName,Integer categoryId,String skuJsonArray, Integer type) throws MapperException {
        try {
            ValueUtil.verify(supplierId,"supplierId");
            ValueUtil.verify(skuName,"skuName");
            ValueUtil.verify(categoryId,"categoryId");
            ValueUtil.verify(skuJsonArray,"skuJsonArray");
            ValueUtil.verify(type,"type");
            if(categoryDao.findOne(categoryId).getLevel()!=3){
                    ValueUtil.isError("分类只可为3级");
            }
            String create = skuService.Create(supplierId, skuName, categoryId, skuJsonArray, type);
            if("success".equals(create)) {
                return ValueUtil.toJson(HttpStatus.SC_CREATED, "success");
            }else {
                return ValueUtil.toError("500", create);
            }
            //同步到oms wms 商城
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.DELETE)  //删除sku
    public String deleteSku( Integer skuId) {
        try {
            ValueUtil.verify(skuId,"skuId");
            String result = skuService.deleteSku(skuId);
            if("success".equals(result)){
                return ValueUtil.toJson(HttpStatus.SC_NO_CONTENT, "success");
            }else {
                return ValueUtil.toError("500", result);
            }
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(value = "/rank", method = RequestMethod.GET)  //排列
    public String rank(String valueJson, Integer supplierId, Integer categoryId) {
        try {
            ValueUtil.verify(valueJson,"valueJson");
            com.alibaba.fastjson.JSONObject rank = this.skuService.rank3(valueJson, supplierId, categoryId);
            if(ValueUtil.notEmpity(rank)){
                return ValueUtil.toJson(HttpStatus.SC_OK,rank.get("msg").toString(),rank.get("array"));
            }else {
                return ValueUtil.toError("500", "json格式不正确");
            }
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }
    @RequestMapping(value = "/updateSkuProp", method = RequestMethod.PUT)  //修改skuProp
    public String updateSkuProp(Integer skuId,Integer isExpensive,String valueJson,String imageId,String skuName){//[{"19":"46"},{"20":"48"}]
        try {
            ValueUtil.verify(valueJson,"valueJson");
            ValueUtil.verify(skuId,"skuId");
            ValueUtil.verify(imageId,"imageId");
            ValueUtil.verify(isExpensive,"isExpensive");
            ValueUtil.verify(skuName,"skuName");
            return ValueUtil.toJson(HttpStatus.SC_CREATED,skuService.updateSkuProp(skuId,isExpensive,valueJson, imageId, skuName));
        }  catch (MapperException e) {
            e.printStackTrace();
        }catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
        return null;
    }
}
