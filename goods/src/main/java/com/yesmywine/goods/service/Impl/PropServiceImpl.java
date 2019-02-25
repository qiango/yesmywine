
package com.yesmywine.goods.service.Impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.goods.bean.*;
import com.yesmywine.goods.dao.*;
import com.yesmywine.goods.entity.CategoryProperty;
import com.yesmywine.goods.entityProperties.*;
import com.yesmywine.goods.entityProperties.Properties;
import com.yesmywine.goods.service.CommonService;
import com.yesmywine.goods.service.ProService;
import com.yesmywine.httpclient.bean.HttpBean;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.*;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;


import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by hz on 1/6/17.
 */
@Service
@Transactional
public class PropServiceImpl extends BaseServiceImpl<com.yesmywine.goods.entityProperties.Properties, Integer> implements ProService {
    @Autowired
    private PropertiesDao propertiesDao;
    @Autowired
    private ProperValueDao properValueDao;
//    @Autowired
//    private PropGoodsDao propGoodsDao;
    @Autowired
    private GoodsDao goodsRepository;
    @Autowired
    private CategoryPropertyDao categoryPropertyDao;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private SpuDao spuDao;
    @Autowired
    private CommonService<com.yesmywine.goods.entityProperties.Properties> commonService;
    @Autowired
    private CommonService<PropertiesValue> commonServiceValue;
    @Autowired
    private CategoryDao categoryDao;

    public String addPrpo(Map<String, String> parm) throws yesmywineException {   //新增属性
//        Integer categoryId = Integer.parseInt(parm.get("categoryId"));
        String isSku = parm.get("isSku");   //0是
        String canSearch = parm.get("canSearch");//0是
        String cnName = parm.get("cnName");
        String code = parm.get("code");
        if(null!=propertiesDao.findByCode(code)){
            ValueUtil.isError("该编码已存在");
        }
        if(null!=propertiesDao.findByCnName(cnName)){
            ValueUtil.isError("该属性已存在");
        }

        com.yesmywine.goods.entityProperties.Properties properties = new com.yesmywine.goods.entityProperties.Properties();
        properties.setCnName(cnName);
        properties.setCode(parm.get("code"));
//        properties.setEnName(enName);
//        properties.setCategory(categoryDao.findOne(categoryId));
        properties.setIsUse(IsUse.no);
        if (isSku.equals("yes")) {
            properties.setIsSku(IsSku.yes);
            if (canSearch.equals("no")) {
                properties.setCanSearch(CanSearch.no);
            } else
                properties.setCanSearch(CanSearch.yes);
            properties.setEntryMode(EntryMode.nullall);
                propertiesDao.save(properties);
                parm.put("id",properties.getId().toString());
                    if(!this.commonService.synchronous(parm, Dictionary.MALL_HOST+ "/goods/properties/synchronous", 0)){
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        ValueUtil.isError("同步失败");
                    }
                    return "success";
//            Integer propertiesId = propertiesDao.findId();
//            if(ValueUtil.notEmpity(parm.get("valueJson"))){
//                String valueJson= parm.get("valueJson");
//                JsonParser jsonParser = new JsonParser();
//                JsonArray arr = jsonParser.parse(valueJson).getAsJsonArray();
//                for (int i = 0; i < arr.size(); i++) {
//                    PropertiesValue propertiesValue = new PropertiesValue();
////                    propertiesValue.setCnName(cnName);
//                    propertiesValue.setPropertiesId(propertiesId);
//                    String value = arr.get(i).getAsJsonObject().get("value").getAsString();
//                    String code = arr.get(i).getAsJsonObject().get("code").getAsString();
//                    propertiesValue.setCode(code);
//                    propertiesValue.setCnValue(value);
//                    properValueDao.save(propertiesValue);
//                }
//            }
//            return "success";
        } else
            properties.setIsSku(IsSku.no);
        if (canSearch.equals("no")) {
            properties.setCanSearch(CanSearch.no);
        } else {
            properties.setCanSearch(CanSearch.yes);
        }
        String entryMode = parm.get("entryMode");
        if(entryMode.equals("manual")){
            properties.setEntryMode(EntryMode.manual);
            try {
                propertiesDao.save(properties);
                parm.put("id",properties.getId().toString());
                if(!this.commonService.synchronous(parm, Dictionary.MALL_HOST+ "/goods/properties/synchronous", 0)){
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    ValueUtil.isError("同步失败");
                }
                return "success";
            }catch (Exception e){
                return "该属性已存在";
            }
        }else {
            properties.setEntryMode(EntryMode.lists);
            propertiesDao.save(properties);
            parm.put("id", properties.getId().toString());
            if (!this.commonService.synchronous(parm, Dictionary.MALL_HOST + "/goods/properties/synchronous", 0)) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("同步失败");
            }
            return "success";
        }
    }

    @Override
    public Map<String, String> addPrpoByImport(Map<String, String> parm) throws yesmywineException {
        Map<String, String> reMap = new HashMap<>();
        String isSku = parm.get("isSku");   //0是
        String canSearch = parm.get("canSearch");//0是
        String cnName = parm.get("cnName");

        if(null!=propertiesDao.findByCnName(cnName)){
            reMap.put("erro", "该属性已存在");
            return reMap;
        }
//        String enName = parm.get("enName");


//        Category category=new Category();
//        category.setId(categoryId);
//        if(null!=propertiesDao.findByCategoryAndCnName(category,cnName)){
//            return "该属性在此分类下已有,不可新建";
//        }
        com.yesmywine.goods.entityProperties.Properties properties = new com.yesmywine.goods.entityProperties.Properties();
        properties.setCnName(cnName);
//        properties.setEnName(enName);
//        properties.setCategory(categoryDao.findOne(categoryId));
        properties.setIsUse(IsUse.no);
        if (isSku.equals("yes")) {
            properties.setIsSku(IsSku.yes);
            if (canSearch.equals("no")) {
                properties.setCanSearch(CanSearch.no);
            } else
                properties.setCanSearch(CanSearch.yes);
            properties.setEntryMode(EntryMode.nullall);
            propertiesDao.save(properties);
            parm.put("id",properties.getId().toString());
            if(ValueUtil.isEmpity(parm.get("valueJson"))){
                if(!this.commonService.synchronous(parm, Dictionary.MALL_HOST+ "/goods/properties/synchronous", 0)){
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    reMap.put("erro", "同步失败");
                    return reMap;
                }
                reMap.put("success", properties.getId().toString());
                return reMap;
            }
            Integer propertiesId = propertiesDao.findId();
            if(ValueUtil.notEmpity(parm.get("valueJson"))){
                String valueJson= parm.get("valueJson");
                JsonParser jsonParser = new JsonParser();
                JsonArray arr = jsonParser.parse(valueJson).getAsJsonArray();
                for (int i = 0; i < arr.size(); i++) {
                    PropertiesValue propertiesValue = new PropertiesValue();
//                    propertiesValue.setCnName(cnName);
                    propertiesValue.setPropertiesId(propertiesId);
                    String value = arr.get(i).getAsJsonObject().get("value").getAsString();
                    String code = arr.get(i).getAsJsonObject().get("code").getAsString();
                    propertiesValue.setCode(code);
                    propertiesValue.setCnValue(value);
                    properValueDao.save(propertiesValue);
                }
            }
            reMap.put("success", properties.getId().toString());
            return reMap;
        } else
            properties.setIsSku(IsSku.no);
        if (canSearch.equals("no")) {
            properties.setCanSearch(CanSearch.no);
        } else {
            properties.setCanSearch(CanSearch.yes);
        }
        String entryMode = parm.get("entryMode");
        if(entryMode.equals("manual")){
            properties.setEntryMode(EntryMode.manual);
            try {
                propertiesDao.save(properties);
                if(!this.commonService.synchronous(parm, Dictionary.MALL_HOST+ "/goods/properties/synchronous", 0)){
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    reMap.put("erro", "同步失败");
                    return reMap;
                }
                reMap.put("success", properties.getId().toString());
                return reMap;
            }catch (Exception e){
                reMap.put("erro", "该属性已存在");
                return reMap;
            }

        }else {
            properties.setEntryMode(EntryMode.lists);
            propertiesDao.save(properties);
            Integer propertiesId = propertiesDao.findId();

            if(ValueUtil.notEmpity(parm.get("valueJson"))) {
                String valueJson = parm.get("valueJson");

                String[] arr = valueJson.split(",");
                for (int i = 0; i < arr.length; i++) {
                    PropertiesValue propertiesValue = new PropertiesValue();
                    propertiesValue.setPropertiesId(propertiesId);
//                    propertiesValue.setCnName(cnName);
                    propertiesValue.setCnValue(arr[i]);
                    properValueDao.save(propertiesValue);
                }
            }

        }

        propertiesDao.save(properties);
        if(!this.commonService.synchronous(parm, Dictionary.MALL_HOST+ "/goods/properties/synchronous", 0)){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            reMap.put("erro", "同步失败");
            return reMap;
        }
        reMap.put("success", properties.getId().toString());
        return reMap;
    }

    public String updateProp(Integer id,String canSearch,String cnName,String isSku,String entryMode) throws yesmywineException {
        com.yesmywine.goods.entityProperties.Properties entity = propertiesDao.findOne(id);
        Properties byCnName = propertiesDao.findByCnName(cnName);
        if(null!=byCnName&&byCnName.getId()!=(int)id){
            ValueUtil.isError("该属性名已存在");
        }
        if (entity.getIsUse() == IsUse.no) {
            entity.setCnName(cnName);
            if (canSearch.equals("yes")) {
                entity.setCanSearch(CanSearch.yes);
            } else {
                entity.setCanSearch(CanSearch.no);
            }
            if (isSku.equals("yes")) {
                entity.setIsSku(IsSku.yes);
                entity.setEntryMode(EntryMode.nullall);
            } else if (entryMode.equals("manual")) {  //手动
                entity.setIsSku(IsSku.no);
                entity.setEntryMode(EntryMode.manual);
            } else {
                entity.setIsSku(IsSku.no);
                entity.setEntryMode(EntryMode.lists);
            }
            propertiesDao.save(entity);

            Map<String, String> map = new HashMap<>();
            map.put("id", entity.getId().toString());
            if(!this.commonService.synchronous(entity, Dictionary.MALL_HOST+ "/goods/properties/synchronous", 1, map)){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("同步失败");
            }
            return "success";
        } else if (canSearch.equals("yes")) {
            entity.setCanSearch(CanSearch.yes);
        } else {
            entity.setCanSearch(CanSearch.no);
        }
        entity.setCnName(cnName);
        propertiesDao.save(entity);
        Map<String, String> map = new HashMap<>();
        map.put("id", entity.getId().toString());
//        map.put("categoryId", categoryId.toString());

        if(!this.commonService.synchronous(entity, Dictionary.MALL_HOST+ "/goods/properties/synchronous", 1, map)) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("同步失败");
        }

//            if (valueJson == null) {
//                return "success";
//            } else if (entity.getIsSku() == IsSku.yes) {
//                JsonParser jsonParser = new JsonParser();
//                JsonArray arr = jsonParser.parse(valueJson).getAsJsonArray();
//                for (int i = 0; i < arr.size(); i++) {
//                    String value = arr.get(i).getAsJsonObject().get("value").getAsString();
//                    String code = arr.get(i).getAsJsonObject().get("code").getAsString();
//                    List<PropertiesValue> p=properValueDao.findByCnValueAndPropertiesId(value, id);
//                    if (0==p.size()) {
//                        PropertiesValue propertiesValue = new PropertiesValue();
//                        propertiesValue.setCnName(cnName);
//                        propertiesValue.setPropertiesId(id);
//                        propertiesValue.setCode(code);
//                        propertiesValue.setCnValue(value);
//                        Map<String, String> map1 = new HashMap<>();
//                        map1.put("id", propertiesValue.getId().toString());
////                        map.put("categoryId", categoryId.toString());
//                        if(this.commonServiceValue.synchronous(propertiesValue, Dictionary.goodsUrl+ "/goods/properties/synchronous", 1, map1)){
//                            properValueDao.save(propertiesValue);
//                        }
//                        return "同步失败";
//                    }else {
//                        return "该属性下属性值已存在";
//                    }
//                }
//            } else {
//                String[] arr = valueJson.split(",");
//                for (int i = 0; i < arr.length; i++) {
//                    List<PropertiesValue> propertiesValue1=properValueDao.findByCnValueAndPropertiesId(arr[i],id);
//                    if (0 == propertiesValue1.size()){
//                        PropertiesValue propertiesValue = new PropertiesValue();
//                        propertiesValue.setPropertiesId(id);
//                        propertiesValue.setCnName(cnName);
//                        propertiesValue.setCnValue(arr[i]);
//                        Map<String, String> map1 = new HashMap<>();
//                        map1.put("id", propertiesValue.getId().toString());
////                        map.put("categoryId", categoryId.toString());
//                        if(this.commonServiceValue.synchronous(propertiesValue, Dictionary.goodsUrl+ "/goods/properties/synchronous", 1, map1)){
//                            properValueDao.save(propertiesValue);
//                        }
//                        return "同步失败";
//                    }else{
//                        return "该属性下属性值已经存在";
//                    }
//                }
//            }

        return "success";
//        }
//        return "同步失败";

    }

    public String updateAdd(Integer propId, String code, String value){
        PropertiesValue propertiesValue = new PropertiesValue();
        propertiesValue.setCnValue(value);
        propertiesValue.setCode(code);
        propertiesValue.setPropertiesId(propId);
        properValueDao.save(propertiesValue);
        return "success";
    }

    public String deleteProp(Integer propId) throws yesmywineException {
        List<CategoryProperty> byPropertyId = categoryPropertyDao.findByPropertyId(propId);
        if(byPropertyId.size()!=0){
            ValueUtil.isError("该属性被用，不可删除");
        }
        com.yesmywine.goods.entityProperties.Properties properties = propertiesDao.findOne(propId);
        propertiesDao.delete(properties);
        if(!this.commonService.synchronous(properties, Dictionary.MALL_HOST+ "/goods/properties/synchronous", 2)){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("同步失败");
        }
        List<PropertiesValue> propList=properValueDao.findByPropertiesId(propId);
            properValueDao.delete(propList);
        return "success";
    }

    @Override
    public com.yesmywine.goods.entityProperties.Properties findByCnName(String cnName) {
        return this.propertiesDao.findByCnName(cnName);
    }

//    @Override
//    public String propCanShow(Integer propId) throws yesmywineException {
//        com.yesmywine.goods.entityProperties.Properties properties=propertiesDao.findOne(propId);
//        properties.setCanShow(!properties.isCanShow());
//        propertiesDao.save(properties);
//        if(!this.commonService.synchronous(properties, Dictionary.goodsUrl+ "/goods/properties/synchronous", 1)) {
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return "同步失败";
//        }
//        return "success";
//    }

//    public Map<String, List<String>> getProperByCategory(Integer categoryId) {    //通过分类获取可查询的属性和值（前台用）
//        Category category=new Category();
//        category.setId(categoryId);
//        List<Properties> list = propertiesDao.findByCategoryAndCanSearch(category,CanSearch.yes);
//        Map<String, List<String>> properyName = new HashMap<>();
//        for (int i = 0; i < list.size(); i++) {
//            List<PropertiesValue> listValue = properValueDao.findByPropertiesId(list.get(i).getId());
//            String name = propertiesDao.findOne(list.get(i).getId()).getCnName();
//            if (null == properyName.get(name)) {
//                properyName.put(name, new ArrayList<>());
//            }
//            for (int j = 0; j < listValue.size(); j++) {
//                String cnValue = listValue.get(j).getCnValue();
//                properyName.get(name).add(cnValue);
//            }
//        }
//        return properyName;
//    }

//    public Object getMethods(Integer categoryId) {        //查出该分类下的属性值的组合
//        List<Integer> properId = propertiesDao.findIdByCategoryId(categoryId);
//        List<PropGoods> propGoods = new ArrayList<>();
//        for (int j = 0; j < properId.size(); j++) {
//            List<PropertiesValue> listValue = properValueDao.findByPropertiesId(properId.get(j));
//            listValue.forEach(k -> {
//                List<PropGoods> prop = propGoodsDao.findByPropValueId(String.valueOf(k.getId()));
//                prop.forEach(l -> {
//                    propGoods.add(l);
//                });
//            });
//        }
//        List<Map<String, Object>> showOut = new ArrayList<>();
//        Map<Integer, Map<String, Object>> goodsProperties = new HashMap<>();
//        for (int i = 0; i < propGoods.size(); i++) {
//            PropGoods tempPropGoods = propGoods.get(i);
//            if (null == goodsProperties.get(tempPropGoods.getGoodsId())) {
//                goodsProperties.put(tempPropGoods.getGoodsId(), new HashMap<>());
//                goodsProperties.get(tempPropGoods.getGoodsId()).put("goodsId", tempPropGoods.getGoodsId());
//            }
//            goodsProperties.get(tempPropGoods.getGoodsId()).put(tempPropGoods.getCnName(), tempPropGoods.getPropValueId());
//        }
//        goodsProperties.forEach((k, v) -> {
//            showOut.add(v);
//        });
//
//        return showOut;
//    }



//    public Map[] getGeneralProp(Integer categoryId) {   //通过分类属性及部分属性的值
//        Category category=new Category();
//        category.setId(categoryId);
//        List<Properties> propties = propertiesDao.findByCategoryAndDeleteEnum(category,DeleteEnum.NOT_DELETE);
//        Map[] map = new HashMap[propties.size()];
//        for(int i=0;i< propties.size(); i++){
//            Map<String, Object> prop = new HashMap<>();
//            prop.put("value", propties.get(i).getId().toString());
//            String cnName = propties.get(i).getCnName();
//            prop.put("label", cnName);
//            List<PropertiesValue> values = properValueDao.findByPropertiesId(propties.get(i).getId());
//            Map[] map2 = new HashMap[values.size()];
//            for(int j=0;j< values.size(); j++){
//                Map<String, String> value = new HashMap<>();
//                value.put("value", values.get(j).getId().toString());
//                String cnValue = values.get(j).getCnValue();
//                value.put("label", cnValue);
//                map2[j] = value;
//            }
//            prop.put("child", map2);
//            map[i]= prop;
//        }
//        return map;
//    }

    public String getRepertory(String skuId) {  //查看库存
        HttpBean httpRequest = new HttpBean("http://88.88.88.211:8191/inventory/goodsInventory" + "/" + skuId, RequestMethod.get);
        httpRequest.run();
        String amount = httpRequest.getResponseContent();
        return amount;
    }
}
