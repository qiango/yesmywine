package com.yesmywine.goods.service.Impl;

import com.yesmywine.goods.bean.DeleteEnum;
import com.yesmywine.goods.dao.*;
import com.yesmywine.goods.entity.Sku;
import com.yesmywine.goods.entityProperties.Properties;
import com.yesmywine.goods.entityProperties.PropertiesValue;
import com.yesmywine.goods.entityProperties.Supplier;
import com.yesmywine.httpclient.bean.HttpBean;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.SynchronizeUtils;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.goods.service.*;
import com.yesmywine.util.error.yesmywineException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.resources.Messages_pt_BR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hz on 2017/5/8.
 */
@Service
public class ImportServiceImpl implements ImportService {

    @Autowired
    private ProService propService;
    @Autowired
    private PropertiesDao propertiesDao;
    @Autowired
    private ProperValueService properValueService;
    @Autowired
    private ProperValueDao properValueDao;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private SkuService skuService;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SupplierDao supplierDao;

    @Override
    public List<Map<String, Object>> importPropAndPropValue(List<Map<String, Object>> list) {

        List<Map<String, Object>> reList = new ArrayList();

        if(ValueUtil.isEmpity(list)){
            return list;
        }
        for(Map<String,Object> map:list){
            Map<String, Object> reMap = new HashMap<>();
            Map<String,String> param=new HashMap<>();
            String cnName = map.get("cnName").toString();
            String code = map.get("code").toString();
            String isSku = map.get("isSku").toString();
            String canSearch = map.get("canSearch").toString();
            String entryMode = map.get("entryMode").toString();
            String prop=map.get("propValue").toString();
            param.put("cnName",cnName);
            param.put("code",code);
            param.put("isSku",isSku);
            param.put("canSearch",canSearch);
            param.put("entryMode",entryMode);

            try{
                String s = propService.addPrpo(param);
                if(!"success".equals(s)){
                    reMap = map;
                    reMap.put("erro", s);
                    reList.add(reMap);
                }
                Integer id = propertiesDao.findByCode(code).getId();
                if(isSku.equals("yes")&&ValueUtil.notEmpity(prop)){
                    if(prop.contains(":")){
                        com.alibaba.fastjson.JSONArray jsonArray=new com.alibaba.fastjson.JSONArray();
                        String[] valueName=prop.split(",");
                        for (int i=0;i<valueName.length;i++) {
                            com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
                            String [] ss=valueName[i].split(":");
                            String valueReal = ss[0];
                            String valueCode=ss[1];
                            jsonObject.put("value",valueReal);
                            jsonObject.put("code",valueCode);
                            jsonArray.add(jsonObject);
                        }
                        Map map1=new HashMap<>();
                        map1.put("propertiesId",id);
                        map1.put("valueJson",jsonArray);
                        String ss=properValueService.addPrpoValue(map1);
                        if(!ss.equals("success")){
                            reMap = map;
                            reMap.put("erro", ss);
                            reList.add(reMap);
                        }

                    }else {
                        ValueUtil.isError("请使用英文符号");
                    }
                }
                if(isSku.equals("no")&&entryMode.equals("lists")&&ValueUtil.notEmpity(prop)){
                    String[] value=prop.split(",");
                    com.alibaba.fastjson.JSONArray jsonArray=new com.alibaba.fastjson.JSONArray();
                    for(int i=0;i<value.length;i++){
                        com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
                        String va=value[i];
                        jsonObject.put("value",va);
                        jsonObject.put("code","");
                        jsonArray.add(jsonObject);
                    }
                    Map map1=new HashMap<>();
                    map1.put("propertiesId",id);
                    map1.put("valueJson",jsonArray);
                    String ss=properValueService.addPrpoValue(map1);
                    if(!ss.equals("success")){
                        reMap = map;
                        reMap.put("erro", ss);
                        reList.add(reMap);
                    }
                }
            } catch (Exception e) {
                reMap = map;
                reMap.put("erro", e);
                reList.add(reMap);
            }

        }
        return reList;
    }

    @Override
    public List<Map<String, Object>> importSupplier(List<Map<String, Object>> list)throws yesmywineException {
        List<Map<String, Object>> reList = new ArrayList();

        if(ValueUtil.isEmpity(list)){
            return list;
        }
        for(Map<String, Object> map: list){
            String area=map.get("area").toString();
            String cityName=map.get("city").toString();
            String areaName=null==area?cityName:area;
            HttpBean httpBean = new HttpBean(Dictionary.PAAS_HOST+"/logistics/area/itf",RequestMethod.get);
            httpBean.addParameter("areaName", areaName);
            httpBean.run();
            String result = httpBean.getResponseContent();
            String code = ValueUtil.getFromJson(result, "code");
            if(code==null||!code.equals("200")){
                ValueUtil.isError("获取省市县id失败")  ;
            }
            String json = ValueUtil.getFromJson(result, "data");
            String[] split = json.split(",");
            Map<String, String> param = new HashMap<>();
            Map<String, Object> reMap = new HashMap<>();
            param.put("序号", map.get("序号").toString());
            param.put("supplierName", map.get("supplierName").toString());
            param.put("supplierType", map.get("supplierType").toString());
            param.put("supplierCode",map.get("supplierCode").toString());
            param.put("province", map.get("province").toString());
            param.put("provinceId", split[0]);
            param.put("city", map.get("city").toString());
            param.put("cityId", split[1]);
            param.put("area", map.get("area").toString());
            param.put("areaId", split[2]);
            param.put("address", map.get("address").toString());
            param.put("postCode", map.get("postCode").toString());
            param.put("contact", map.get("contact").toString());
            param.put("telephone", map.get("telephone").toString());
            param.put("mobilePhone", map.get("mobilePhone").toString());
            param.put("fax", map.get("fax").toString());
            param.put("mailbox", map.get("mailbox").toString());
            param.put("grade", map.get("grade").toString());
            param.put("accountNumber", map.get("accountNumber").toString());
            param.put("credit", map.get("credit").toString());
            param.put("procurementCycl", map.get("procurementCycl").toString());
            param.put("paymentType", map.get("paymentType").toString());
            param.put("invoiceCompany", map.get("invoiceCompany").toString());
            param.put("primarySupplier", map.get("primarySupplier").toString());
            param.put("merchantIdentification", map.get("merchantIdentification").toString());
            param.put("productManager", map.get("productManager").toString());
            param.put("bank", map.get("bank").toString());
            param.put("bankAccount", map.get("bankAccount").toString());
            param.put("dutyParagraph", map.get("dutyParagraph").toString());
            param.put("paymentDays", map.get("paymentDays").toString());
            try {
                String s = this.supplierService.addSupplier(param);
                if("erro".equals(s)){
                    reMap = map;
                    reMap.put("erro", s);
                    reList.add(reMap);
                }

            } catch (Exception e) {
                reMap = map;
                reMap.put("erro", e);
                reList.add(reMap);
            }
        }
        return reList;
    }

    @Override
    public List<Map<String, Object>> importSku(List<Map<String, Object>> list)throws yesmywineException {
        List<Map<String, Object>> reList = new ArrayList();

        if(ValueUtil.isEmpity(list)){
            return list;
        }
        for(Map<String, Object> map: list){
            String categoryName=map.get("categoryName").toString();
            Integer idByCategoryName = categoryDao.findIdByCategoryName(categoryName);
            if(null==idByCategoryName){
                ValueUtil.isError(categoryName+"这一分类不存在");
            }
            String supplierName=map.get("supplierName").toString();
            Supplier supplier=supplierDao.findBySupplierNameAndDeleteEnum(supplierName, DeleteEnum.NOT_DELETE);
            if(null==supplier){
                ValueUtil.isError(supplierName+"这一渠道不存在");
            }
            Integer supplierId=supplier.getId();
            String skuName=map.get("skuName").toString();
            String type=map.get("type").toString();
            Integer types=Integer.parseInt(type);
            String prop=map.get("propJson").toString();
            String[] propName=null;
            com.alibaba.fastjson.JSONArray a=new com.alibaba.fastjson.JSONArray();
            com.alibaba.fastjson.JSONObject j=new com.alibaba.fastjson.JSONObject();
            if(prop.contains(":")){
              propName=prop.split(",");
                for (int i=0;i<propName.length;i++){
                    String [] ss=propName[i].split(":");
                    String propNameReal=ss[0];
                    Properties byCnName = this.propService.findByCnName(propNameReal);
                    if(ValueUtil.isEmpity(byCnName)){
                        ValueUtil.isError("属性为"+propName+"不存在");
                    }
                    Integer propId=byCnName.getId();
                    String properCode=ss[1];
                    PropertiesValue p=properValueDao.findByCodeAndPropertiesId(properCode,propId);
                    if(ValueUtil.isEmpity(p)){
                        ValueUtil.isError("属性值为"+properCode+"不存在");
                    }
                    Integer propValeId=p.getId();
                    j.put(propId.toString(),propValeId.toString());

                }
                a.add(j);
            }else {
                ValueUtil.isError("请用英文符号");
            }
            Map<String, Object> reMap = new HashMap<>();

            try {
                String s = this.skuService.Create(supplierId,skuName,idByCategoryName,a.toJSONString(),types);
                if(!"success".equals(s)){
                    reMap = map;
                    reMap.put("erro", s);
                    reList.add(reMap);
                }

            } catch (Exception e) {
                reMap = map;
                reMap.put("erro", e);
                reList.add(reMap);
            }
        }
        return reList;
    }

    @Override
    public List<Map<String, Object>> importGoods(List<Map<String, Object>> list) {
        List<Map<String, Object>> reList = new ArrayList();

        if(ValueUtil.isEmpity(list)){
            return list;
        }
        for(Map<String, Object> map: list){
            Map<String, String> param = new HashMap<>();
            Map<String, Object> reMap = new HashMap<>();
            param.put("goodsName", map.get("goodsName").toString());
            String categoryName = map.get("categoryName").toString();
            Integer idByCategoryName = this.categoryDao.findIdByCategoryName(categoryName);
            param.put("categoryId", idByCategoryName.toString());
            param.put("price", map.get("price").toString());
            param.put("序号", map.get("序号").toString());
            try {
                String skuCode = map.get("skuCode").toString();
                Sku byCode = this.skuDao.findByCode(skuCode);
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("skuId", byCode.getId().toString());
                jsonObject.put("count", "1");
                jsonArray.put(jsonObject);
                param.put("skuIdString", jsonArray.toString());
            }catch (Exception e){
                reMap = map;
                reMap.put("erro", "skuCode不正确");
                reList.add(reMap);
                return reList;
            }
            param.put("item", "single");
            try {
                String s = this.goodsService.addGoods(param);
                if(!"success".equals(s)){
                    reMap = map;
                    reMap.put("erro", s);
                    reList.add(reMap);
                }

            } catch (Exception e) {
                reMap = map;
                reMap.put("erro", e);
                reList.add(reMap);
            }
        }
        return reList;
    }

    @Override
    public List importCategory(List<Map<String, Object>> list)throws yesmywineException {
        List<Map<String, String>> reList = new ArrayList();
        List<Map<String, String>> paramList = new ArrayList();

        if(ValueUtil.isEmpity(list)){
            return list;
        }
        for(Map<String, Object> map: list){
            Map<String, String> param = new HashMap<>();

            String categoryName = map.get("categoryName").toString();

            String prop = map.get("prop").toString();

            String propName=null;
            String[] properValueArr=null;
            if(prop.contains(":")){
                propName = StringUtils.substringBefore(prop, ":");
                String properValues = StringUtils.substringAfter(prop, ":");
                properValueArr = properValues.split(",");

            }else if(prop.contains("：")){
                propName = StringUtils.substringBefore(prop, "：");
                String properValues = StringUtils.substringAfter(prop, "：");
                properValueArr = properValues.split("，");
            }
            Properties byCnName = this.propService.findByCnName(propName);
            if(ValueUtil.isEmpity(byCnName)){
                ValueUtil.isError("属性为"+propName+"的属性不存在");
            }
            Integer propId = byCnName.getId();
            String properValueIds = "";
            for(String cnValue: properValueArr){
                PropertiesValue byCnValueAndPropertiesId = this.properValueDao.findByCnValueAndPropertiesId(cnValue, propId);
                if(null==byCnValueAndPropertiesId){
                    ValueUtil.isError(propName+"下无"+cnValue+"这一属性");
                }
                properValueIds = properValueIds + ";"+ byCnValueAndPropertiesId.getId();
            }
            properValueIds = StringUtils.substring(properValueIds, 1);
//            Boolean flag = false;
//            for(Map<String, String> par: paramList){
//                for(String key:par.keySet()){
//                    String s = par.get(key);
//                    if(categoryName.equals(s)){
////                        JSONArray jsonArray = new JSONArray();
//                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put("propertyId", String.valueOf(propId));
//                        jsonObject.put("valueIds", properValueIds);
////                        jsonArray.put(jsonObject);
//                        JSONArray dataJSON = new JSONArray(par.get("propertyJson"));
//                        dataJSON.put(jsonObject);
//
//                        par.put("propertyJson", dataJSON.toString());
//                        flag = true;
//                    }
//                }
//            }
//
//            if(!flag) {
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("propertyId", String.valueOf(propId));
                jsonObject.put("valueIds", properValueIds);
                jsonArray.put(jsonObject);
                param.put("propertyJson", jsonArray.toString());

                param.put("序号", map.get("序号").toString());
                param.put("categoryName", categoryName);
                Object parentId = map.get("parentId");
                if (ValueUtil.notEmpity(parentId)) {
                    Integer id=categoryDao.findIdByCategoryName(parentId.toString());
                    if(null==id){
                        param.put("parentId",null);
                    }else {
                        param.put("parentId", id.toString());
                    }
                }else {
                    param.put("parentId", null);
                }

                param.put("code", map.get("code").toString());
                param.put("isShow", map.get("isShow").toString());

                paramList.add(param);
            }


        for(Map<String, String> param: paramList) {
            Map<String, String> reMap = new HashMap<>();
            try {
                String parentId = param.get("parentId");
                Integer pId = null;
                if(ValueUtil.notEmpity(parentId)){
                    pId = Integer.valueOf(parentId);
                }
                String s = this.categoryService.insert(param.get("categoryName"), pId, param.get("code"), param.get("isShow"),null,  param.get("propertyJson"));
                if (!"SUCCESS".equals(s)) {
                    reMap = param;
                    reMap.put("erro", s);
                    reList.add(reMap);
                }

            } catch (Exception e) {
                reMap = param;
                reMap.put("erro", e.toString());
                reList.add(reMap);
            }
        }


        return reList;
    }
}
