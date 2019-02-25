package com.yesmywine.goods.service.Impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.sdicons.json.mapper.MapperException;
import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.goods.bean.EntryMode;
import com.yesmywine.goods.bean.IsUse;
import com.yesmywine.goods.bean.SupplierTypeEnum;
import com.yesmywine.goods.dao.*;
import com.yesmywine.goods.entity.Sku;
import com.yesmywine.goods.entity.SkuCommonProp;
import com.yesmywine.goods.entity.SkuProp;
import com.yesmywine.goods.entityProperties.Category;
import com.yesmywine.goods.entityProperties.Properties;
import com.yesmywine.goods.entityProperties.PropertiesValue;
import com.yesmywine.goods.entityProperties.Supplier;
import com.yesmywine.goods.service.CommonService;
import com.yesmywine.goods.service.SkuService;
import com.yesmywine.goods.util.DoExchange;
import com.yesmywine.httpclient.bean.HttpBean;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.SynchronizeUtils;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.json.JSONArray;
import org.json.JSONObject;
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by hz on 2/13/17.
 */
@Service
@Transactional
public class SkuServiceImpl extends BaseServiceImpl<Sku, Integer> implements SkuService {
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private PropertiesDao propertiesDao;
    @Autowired
    private ProperValueDao properValueDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private SupplierDao supplierDao;
    @Autowired
    private CommonService<Sku> commonService;
    @Autowired
    private SkuPropDao skuPropDao;
    @Autowired
    private SkuCommonDao skuCommonDao;
    @Autowired
    private CategoryPropertyDao categoryPropertyDao;
    private String skuString;


    public String deleteSku(Integer skuId) throws yesmywineException {//删除ｓｋｕ
        Sku sku = skuDao.findOne(skuId);
        if (sku.getIsUse() == IsUse.yes) {
            ValueUtil.isError( "该sku被用,不可删除");
        }

        List<Sku> skuList = new ArrayList<>();
        skuList.add(sku);
        sendToOMS(skuList,2);

        HttpBean httpBean = new HttpBean(Dictionary.DIC_HOST + "/dic/sysCode/itf", RequestMethod.get);
        httpBean.addParameter("sysCode", "cg_c");
        httpBean.run();
        String temp = httpBean.getResponseContent();
        String data = ValueUtil.getFromJson(temp, "data");
        com.alibaba.fastjson.JSONArray jsonArray = com.alibaba.fastjson.JSONArray.parseArray(data);
        for (int j = 0; j < jsonArray.size(); j++) {
            com.alibaba.fastjson.JSONObject jsonObject = jsonArray.getJSONObject(j);
            if (jsonObject.get("entityCode").equals("1")) {
                Map<String, String> map = new HashMap<>();
                map.put("id", skuId.toString());
                if (this.commonService.synchronous(map, Dictionary.MALL_HOST + "/goods/sku/synchronous", 2)) {
                    skuDao.delete(skuId);
                    return "success";
                } else {
                    sendToOMS(skuList,0);
                }
                return "同步失败";
            }
        }
        return "success";
    }

    public Sku showSku(Integer skuId) {
        Sku sku = skuDao.findOne(skuId);
//        String propty=sku.getProperty();
        List<SkuProp> skuProp = sku.getSkuProp();
        Map<String, String> map = new HashMap<>();
//        JSONObject jsonObject = new JSONObject(propty);
//        Iterator iterator = jsonObject.keys();
        for (int j = 0; j < skuProp.size(); j++) {
//            while (iterator.hasNext()) {
            Integer propertiesId = skuProp.get(j).getPropertiesId();
            Integer propValue1 = skuProp.get(j).getPropValue();
            String propName = propertiesDao.findOne(propertiesId).getCnName();
            String propValue = properValueDao.findOne(propValue1).getCnValue();
            map.put(propName, propValue);
//            }
        }
//        sku.setCategory(categoryDao.findOne(sku.getCategoryId()));
//        sku.setSupplier(supplierDao.findOne(sku.getSupplierId()));
        sku.setProperty(map.toString().replace("=", ":"));
        return sku;

    }

    public com.alibaba.fastjson.JSONArray getSku(Integer categoryId, Integer type) {   //通过分类id拿到属性及值
        Map<String, List<PropertiesValue>> sku = new HashMap<>();//(0:sku属性及值,1:普通属性,2:全部属性)
        List<com.yesmywine.goods.entityProperties.Properties> prop = null;
        if (ValueUtil.isEmpity(categoryId)) {
            switch (type) {
//                case 0:prop=propertiesDao.findByIsSkuAndDeleteEnum(IsSku.yes,DeleteEnum.NOT_DELETE);break;
//                case 1:prop=propertiesDao.findByIsSkuAndDeleteEnum(IsSku.no,DeleteEnum.NOT_DELETE);break;
//                case 2:prop=propertiesDao.findByDeleteEnum(DeleteEnum.NOT_DELETE);break;
            }
        } else {
            Category category = new Category();
            category.setId(categoryId);
            switch (type) {
                case 0:
//                    prop = propertiesDao.findByCategoryAndIsSkuAndDeleteEnum(category, IsSku.yes, DeleteEnum.NOT_DELETE);
                    break;
                case 1:
//                    prop = propertiesDao.findByCategoryAndIsSkuAndDeleteEnum(category, IsSku.no, DeleteEnum.NOT_DELETE);
                    break;
                case 2:
//                    prop = propertiesDao.findByCategoryAndDeleteEnum(category, DeleteEnum.NOT_DELETE);
                    break;
            }
        }
        com.alibaba.fastjson.JSONArray jsonArray1 = new com.alibaba.fastjson.JSONArray();
        prop.forEach(k -> {
            List<PropertiesValue> values = properValueDao.findByPropertiesId(k.getId());
            com.alibaba.fastjson.JSONObject jsonObject1 = new com.alibaba.fastjson.JSONObject();
            com.alibaba.fastjson.JSONArray jsonArray2 = new com.alibaba.fastjson.JSONArray();
            jsonObject1.put("value", k.getId());
            jsonObject1.put("label", k.getCnName());
            jsonArray1.add(jsonObject1);
            for (int i = 0; i < values.size(); i++) {
                com.alibaba.fastjson.JSONObject jsonObject2 = new com.alibaba.fastjson.JSONObject();
                jsonObject2.put("value", values.get(i).getId());
                jsonObject2.put("label", values.get(i).getCnValue());
                jsonArray2.add(jsonObject2);
            }
            if (jsonArray2.size() > 0) {
                jsonObject1.put("children", jsonArray2);
            }
        });
        return jsonArray1;
    }

    public String Create(Integer supplierId, String skuName, Integer categoryId, String skuJsonArray, Integer type) throws yesmywineException, MapperException {  //保存sku

        List<Sku> skuList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(skuJsonArray);
        JSONArray jsonArraySend = new JSONArray();
        int length = jsonArray.length();
        Supplier supplier = this.supplierDao.findOne(supplierId);
        Category category1 = categoryDao.findOne(categoryId);
        for (int i = 0; i < length; i++) {
//            StringBuilder stringBuilder = new StringBuilder();
            Sku sku = new Sku();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
//            Category one1 = this.categoryDao.findOne(categoryId);

            sku.setSupplier(supplier);
            sku.setProperty(jsonObject + "");

            sku.setCategory(category1);
            sku.setSkuName(skuName);
            sku.setIsUse(IsUse.no);

            sku.setType(type);
            String skuString = skuName;
//            Iterator iterator = jsonObject.keys();

//            for(int k=0; k<10; k++){
            Set<String> keys = jsonObject.keySet();
//            for(int k=0; k< jsonObject.length(); k++){
//                    String key = keys.iterator().next().toString();
            StringBuilder code = new StringBuilder("                         ");
            code.replace(1,6,category1.getCode());
            code.replace(21,24,supplier.getSupplierCode());
            String[] skuSt = new String[8];
            List<String> skuLi = new ArrayList<>();

            for (String key : keys) {
                System.out.println(jsonObject.getString(key));
                Integer value = Integer.valueOf(jsonObject.getString(key));
                PropertiesValue one = properValueDao.findOne(value);
                if ("1".equals(key)) {
                    String code1 = one.getCode();
                    skuSt[0]=one.getCnValue();
                    code.replace(0, 1, code1);
                } else if ("7".equals(key)) {
                    String code1 = one.getCode();
                    skuSt[1]=one.getCnValue();
                    code.replace(6, 11, code1);
                } else if ("2".equals(key)) {
                    String code1 = one.getCode();
                    skuSt[2]=one.getCnValue();
                    code.replace(11, 14, code1);
                } else if ("3".equals(key)) {
                    String code1 = one.getCode();
                    skuSt[3]=one.getCnValue();
                    code.replace(14, 16, code1);
                } else if ("4".equals(key)) {
                    String code1 = one.getCode();
                    skuSt[4]=one.getCnValue();
                    code.replace(16, 18, code1);
                } else if ("9".equals(key)) {
                    String code1 = one.getCode();
                    skuSt[5]=one.getCnValue();
                    code.replace(18, 19, code1);
                } else if ("5".equals(key)) {
                    String code1 = one.getCode();
                    skuSt[6]=one.getCnValue();
                    code.replace(19, 21, code1);
                } else if ("6".equals(key)) {
                    String code1 = one.getCode();
                    skuSt[7]=one.getCnValue();
                    code.replace(24, 25, code1);
                } else {
                    String code1 = one.getCode();
                    skuLi.add(one.getCnValue());
                    code.append(code1);
                }
            }

            for(int j=0;j<skuSt.length;j++){
                if(ValueUtil.notEmpity(skuSt[j])) {
                    skuString = skuString + " " + skuSt[j];
                }
            }

            for(int f=0;f<skuLi.size();f++){
                skuString = skuString + " " + skuLi.get(f);
            }

            List<SkuProp> list = new ArrayList<>();
            for (Object key : jsonObject.keySet()) {
                if("code".equals(key.toString())){
                    continue;
                }else if ("sku".equals(key.toString())){
                    continue;
                }
                Object propValue = jsonObject.get(key.toString());
                SkuProp skuProp = new SkuProp();
                skuProp.setPropertiesId(Integer.valueOf(key.toString()));
                skuProp.setPropValue(Integer.valueOf(propValue.toString()));
                list.add(skuProp);
            }
            sku.setSkuProp(list);
            String codeSt = code.toString().replace(" ","");
            sku.setCode(codeSt);
            sku.setSku(skuString);
            sku.setIsExpensive(1);
            skuPropDao.save(list);

//            List<CategoryProperty> byCategoryId = this.categoryPropertyDao.findByCategoryId(categoryId);
//            List<SkuCommonProp> skuCommonProps = new ArrayList<>();
//            for (CategoryProperty categoryProperty : byCategoryId) {
//                SkuCommonProp skuCommonProp = new SkuCommonProp();
//                skuCommonProp.setPropId(categoryProperty.getPropertyId());
//                if(ValueUtil.notEmpity(categoryProperty.getPropertyValue())){
//                    skuCommonProp.setPropValueId(categoryProperty.getPropertyValue().getId().toString());
//                }
//                skuCommonProp.setType(categoryProperty.getType());
//                skuCommonProps.add(skuCommonProp);
//            }
//            this.skuCommonDao.save(skuCommonProps);
//            sku.setSkuCommonProp(skuCommonProps);
            Sku byCode = this.skuDao.findByCode(sku.getCode());
            if(ValueUtil.notEmpity(byCode)){
                continue;
            }
            skuList.add(sku);
//            skuDao.save(sku);
            JSONObject jsonObjectProp = new JSONObject();
            jsonObjectProp.put("jsonProp", jsonObject);
            jsonObjectProp.put("sku", sku.getSku());
            jsonObjectProp.put("code", sku.getCode());
            jsonObjectProp.put("skuId", sku.getId());
            jsonArraySend.put(jsonObjectProp);
        }

        //同步到wms
        sendTOWMS(skuList);
        //同步到OMS
        sendToOMS(skuList,0);

        //同步到商城后台
        HttpBean httpBean = new HttpBean(Dictionary.DIC_HOST + "/dic/sysCode/itf", RequestMethod.get);
        httpBean.addParameter("sysCode", "sku_c");
        httpBean.run();
        String temp = httpBean.getResponseContent();
        String data = ValueUtil.getFromJson(temp, "data");
        com.alibaba.fastjson.JSONArray jsonArray1 = com.alibaba.fastjson.JSONArray.parseArray(data);
        for (int j = 0; j < jsonArray1.size(); j++) {
            com.alibaba.fastjson.JSONObject jsonObject = jsonArray1.getJSONObject(j);
            if (jsonObject.get("entityCode").equals("1")) {
                Map<String, String> map = new HashMap<>();
                map.put("suppierId", supplierId.toString());
                map.put("skuName", skuName);
                map.put("categoryId", categoryId.toString());
                map.put("skuJsonArray", jsonArraySend.toString());
                map.put("skuJsonArray", jsonArraySend.toString());
                map.put("type", type.toString());
                if (!this.commonService.synchronous(map, Dictionary.MALL_HOST + "/goods/sku/synchronous", 0)) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    //删除oms新增的sku
                    sendToOMS(skuList,2);
                    ValueUtil.isError("同步到商城后台失败");
                }
            }
        }
        skuDao.save(skuList);
        return "success";
    }

    public void sendTOWMS(List<Sku> skuList) throws yesmywineException, MapperException {
        com.alibaba.fastjson.JSONObject requestJsons = new com.alibaba.fastjson.JSONObject();
        com.alibaba.fastjson.JSONObject requestJsonss = new com.alibaba.fastjson.JSONObject();
        com.alibaba.fastjson.JSONArray jsonArrayNew = new com.alibaba.fastjson.JSONArray();
        for (Sku sku : skuList) {
            String sku_Name = sku.getSku();
            String sku_Code = sku.getCode();
            String costPrice = sku.getCostPrice();
            Integer supplierIdNew=sku.getSupplier().getId();
            Category category=sku.getCategory();
            com.alibaba.fastjson.JSONObject dataJson = new com.alibaba.fastjson.JSONObject();
            dataJson.put("CustomerID","YMJ");
            dataJson.put("SKU", sku_Code);
            dataJson.put("Active_Flag","Y");
            dataJson.put("Descr_C",sku_Name);
            dataJson.put("Price","0.0");
            dataJson.put("ShelfLifeFlag","Y");
            dataJson.put("ShelfLifeType","M");
            dataJson.put("ShelfLife","365");
            if(sku.getIsExpensive().equals(0)){
                dataJson.put("SKU_Group1", "Y");//是否贵品：Y为贵品，N为非贵品
            }else{
                dataJson.put("SKU_Group1", "N");//是否贵品：Y为贵品，N为非贵品
            }
            if(sku.getSupplier().getSupplierType().equals(SupplierTypeEnum.consignment)){
                dataJson.put("SKU_Group2", "Y");//是否经销，Y-经销，N-代销
            }else{
                dataJson.put("SKU_Group2", "N");//是否经销，Y-经销，N-代销
            }
            dataJson.put("SKU_Group4","无此属性");
            dataJson.put("SKU_Group5","无此属性");

            dataJson.put("ReservedField04",sku.getCategory().getCode());
            dataJson.put("ReservedField05",sku.getCategory().getCategoryName());
            dataJson.put("ReservedField02",sku.getCategory().getParentName().getCode());
            dataJson.put("ReservedField03",sku.getCategory().getParentName().getCategoryName());
            dataJson.put("ReservedField01",sku.getCategory().getParentName().getParentName().getCategoryName());
            dataJson.put("FreightClass",sku.getCategory().getParentName().getParentName().getCode());

            dataJson.put("SerialNoCatch","Y");
            dataJson.put("ScanWhenReceive","Y");
            dataJson.put("InboundSerialNoQtyControl","Y");

            dataJson.put("Alternate_SKU1",sku_Code);
            dataJson.put("Alternate_SKU2","无此属性");
            dataJson.put("Alternate_SKU4","无此属性");
            dataJson.put("Alternate_SKU5","无此属性");
            jsonArrayNew.add(dataJson);
        }
        requestJsonss.put("header",jsonArrayNew);
        requestJsons.put("xmldata",requestJsonss);

        String result = SynchronizeUtils.getWmsResult(Dictionary.WMS_HOST , "putSKUData","SKU", requestJsons.toJSONString());
        if(result!=null){
            String returnCode = ValueUtil.getFromJson(result, "Response", "return", "returnCode");
            String returnDesc = ValueUtil.getFromJson(result, "Response", "return", "returnDesc");
            if(!returnCode.equals("0000")){
                ValueUtil.isError("同步WMS失败，原因："+returnDesc);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
    }

    public static void sendToOMS(List<Sku> skuList, Integer status) throws yesmywineException {//0-新增  2-删除
        for(Sku sku:skuList){
            String sku_Name = sku.getSku();
            String sku_Code = sku.getCode();
            String costPrice = sku.getCostPrice();
            Integer skuType = sku.getType();
            com.alibaba.fastjson.JSONObject requestJson = new com.alibaba.fastjson.JSONObject();
            requestJson.put("function", status);
            com.alibaba.fastjson.JSONObject dataJson = new com.alibaba.fastjson.JSONObject();
            dataJson.put("skuName", sku_Name);
            dataJson.put("skuCode", sku_Code);
            dataJson.put("costPrice", costPrice);
            switch (skuType) {
                case 0:
                    dataJson.put("skuType", "实体");
                    break;
                case 1:
                    dataJson.put("skuType", "虚拟");
                    break;
            }
            requestJson.put("data", dataJson);
            //同步到OMS
            String result = SynchronizeUtils.getOmsResult(Dictionary.OMS_HOST, "/updateBaseCustomerSku", RequestMethod.post, "", requestJson.toJSONString());
            if (result != null) {
                String rspStatus = ValueUtil.getFromJson(result, "status");
                String message = ValueUtil.getFromJson(result, "message");
                if (!rspStatus.equals("success")) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    ValueUtil.isError("向OMS同步sku失败,原因："+message);
                }
            } else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("向OMS同步sku失败");
            }
        }
    }

    @Override
    public String Create(Map<String, String> param) {
        try {
            String code = param.get("code");
            String costPrice = param.get("costPrice");
            String skuName = param.get("skuName");
            Sku sku = new Sku();
            sku.setCode(code);
            sku.setCostPrice(costPrice);
            sku.setSkuName(skuName);

            String skuString = skuName;
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();

//            String[] codeArr = new String[8];
//            codeArr[0] = code.substring(0, 1);
            PropertiesValue[] propertiesValueArr = new PropertiesValue[8];
            propertiesValueArr[0] = this.properValueDao.findByCodeAndPropertiesId(code.substring(0, 1), 1);

            String categoryCode = code.substring(1, 6);
//            codeArr[1] = code.substring(6, 11);
            propertiesValueArr[1] = this.properValueDao.findByCodeAndPropertiesId(code.substring(6, 11), 7);

//            codeArr[2] = code.substring(11, 14);
            propertiesValueArr[2] = this.properValueDao.findByCodeAndPropertiesId(code.substring(11, 14), 2);
//            codeArr[3] = code.substring(14, 16);
            propertiesValueArr[3] = this.properValueDao.findByCodeAndPropertiesId(code.substring(14, 16), 3);
//            codeArr[4] = code.substring(16, 18);
            propertiesValueArr[4] = this.properValueDao.findByCodeAndPropertiesId(code.substring(16, 18), 4);
//            codeArr[5] = code.substring(18, 19);
            propertiesValueArr[5] = this.properValueDao.findByCodeAndPropertiesId(code.substring(18, 19), 9);
//            codeArr[6] = code.substring(19, 21);
            propertiesValueArr[6] = this.properValueDao.findByCodeAndPropertiesId(code.substring(19, 21), 5);
            String supplierCode = code.substring(21, 24);
//            codeArr[7] = code.substring(24, 25);
            propertiesValueArr[7] = this.properValueDao.findByCodeAndPropertiesId(code.substring(24, 25), 6);

            Category byCode = this.categoryDao.findByCode(categoryCode);
            sku.setCategory(byCode);

            List<Supplier> bySupplierCode = this.supplierDao.findBySupplierCode(supplierCode);
            sku.setSupplier(bySupplierCode.get(0));

            List<SkuProp> list = new ArrayList<>();
            for (int i = 0; i < propertiesValueArr.length; i++) {
                skuString = skuString + " " + propertiesValueArr[i].getCnValue();
                Integer propertiesId = propertiesValueArr[i].getPropertiesId();
                Integer id = propertiesValueArr[i].getId();
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put(propertiesId.toString(), id.toString());
                jsonObject.put(propertiesId.toString(), id.toString());
//                jsonArray.put(jsonObject);
                SkuProp skuProp = new SkuProp();
                skuProp.setPropertiesId(propertiesId);
                skuProp.setPropValue(id);
                list.add(skuProp);
            }
            String skuJsonArray = jsonArray.put(jsonObject).toString();
//            String skuJsonArray = jsonArray.toString();
            sku.setProperty(jsonObject.toString());
            sku.setSkuProp(list);
            sku.setSku(skuString);
            sku.setType(0);
            this.skuPropDao.save(list);
            this.skuDao.save(sku);

            HttpBean httpBean = new HttpBean(Dictionary.DIC_HOST + "/dic/sysCode/itf", RequestMethod.get);
            httpBean.addParameter("sysCode", "cg_c");
            httpBean.run();
            String temp = httpBean.getResponseContent();
            String data = ValueUtil.getFromJson(temp, "data");
            com.alibaba.fastjson.JSONArray jsonArray1 = com.alibaba.fastjson.JSONArray.parseArray(data);
            for (int j = 0; j < jsonArray1.size(); j++) {
                com.alibaba.fastjson.JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                if (jsonObject1.get("entityCode").equals("1")) {

                    Map<String, String> map = new HashMap<>();
                    map.put("suppierId", bySupplierCode.get(0).getId().toString());
                    map.put("skuName", skuName);
                    map.put("categoryId", byCode.getId().toString());
                    map.put("skuJsonArray", skuJsonArray);
                    map.put("type", sku.getType().toString());

                    if (!this.commonService.synchronous(map, Dictionary.MALL_HOST + "/goods/sku/synchronous", 0)) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return "同步失败";
                    }
                }
            }
        } catch (Exception e) {
            return "erro";
        }

        return "success";
    }

    @Override
    public com.alibaba.fastjson.JSONArray rank(String valueJson) {
        JsonParser jsonParser = new JsonParser();
        JsonArray arr;
        try {
            arr = jsonParser.parse(valueJson).getAsJsonArray();
        } catch (Exception e) {
            return null;
        }

        com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray();

        for (int i = 0; i < arr.size(); i++) {
            Map<String, Object> map2 = new HashMap<>();
            String id = arr.get(i).getAsJsonObject().get("id").getAsString();
            String valueId = arr.get(i).getAsJsonObject().get("valueId").getAsString();
            String[] valueArr = valueId.split(",");
            Map<String, String> map = new HashMap<>();
            String skuString = "";
            for (String value : valueArr) {
                map.put(id, value);
                PropertiesValue one = this.properValueDao.findOne(Integer.valueOf(value));
                if ("".equals(skuString)) {
                    skuString = skuString + one.getCnValue();
                } else {
                    skuString = skuString + " " + one.getCnValue();
                }

            }
            map2.put("label", map);
            map2.put("value", skuString);
            array.add(map2);
        }
        return array;
    }


    public com.alibaba.fastjson.JSONArray rank2(String valueJson) {
        JsonParser jsonParser = new JsonParser();
        JsonArray arr;
        try {
            arr = jsonParser.parse(valueJson).getAsJsonArray();
        } catch (Exception e) {
            return null;
        }

        com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray();
        List<String[]> listValueId = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {

            String valueId = arr.get(i).getAsJsonObject().get("valueId").getAsString();
            String[] arrValueId = valueId.split(",");
            listValueId.add(arrValueId);
        }

        DoExchange doExchange = new DoExchange();
        ArrayList<ArrayList<String>> doChangeListValueId = new ArrayList<>();
        if (listValueId.size() < 2) {
            String[] strings = listValueId.get(0);
            ArrayList<String> re = new ArrayList<>();
            for (String s : strings) {
                re.add(s);
            }
            doChangeListValueId.add(re);
        } else {
            doChangeListValueId = (ArrayList<ArrayList<String>>) doExchange.doChange(listValueId);
        }

        for (int i = 0; i < doChangeListValueId.size(); i++) {
            ArrayList<String> stringsValueId = doChangeListValueId.get(i);
            String value = "";
            String label = "";
            for (String valueId : stringsValueId) {
                PropertiesValue one = this.properValueDao.findOne(Integer.valueOf(valueId));
                if (ValueUtil.notEmpity(value)) {
//                    value = value +","+ "{\""+one.getPropertiesId()+"\"" + ":"+"\""+valueId+"\"}";
                    value = value + "," + one.getPropertiesId() + ":" + valueId;
                } else {
//                    value = "{\""+one.getPropertiesId()+"\"" + ":"+"\""+valueId+"\"}";
                    value = one.getPropertiesId() + ":" + valueId;
                }
                if (ValueUtil.notEmpity(label)) {
                    label = label + " " + one.getCnValue();
                } else {
                    label = one.getCnValue();
                }

            }
            Map<String, Object> map = new HashMap<>();
//            JsonArray asJsonArray = jsonParser.parse(value).getAsJsonArray();
            map.put("value", value);
            map.put("label", label);
//            map.put("asJsonArray", asJsonArray);

            array.add(map);
        }

        return array;
    }


    public com.alibaba.fastjson.JSONObject rank3(String valueJson, Integer supplierId, Integer categoryId) {
        JsonParser jsonParser = new JsonParser();
        JsonArray arr;
        try {
            arr = jsonParser.parse(valueJson).getAsJsonArray();
        } catch (Exception e) {
            return null;
        }
        com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
        com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray();
        List<String[]> listValueId = new ArrayList<>();
        int sum=1;
        for (int i = 0; i < arr.size(); i++) {

            String valueId = arr.get(i).getAsJsonObject().get("valueId").getAsString();
            String[] arrValueId = valueId.split(",");
            sum=arrValueId.length*sum;
            listValueId.add(arrValueId);
        }
        if(listValueId.size()==1){
            for(String s:listValueId.get(0)) {
                PropertiesValue one = this.properValueDao.findOne(Integer.valueOf(s));
                Map<String, Object> map = new HashMap<>();
                map.put("value",one.getPropertiesId()+":"+s);
                map.put("label",one.getCnValue());
                array.add(map);
            }
            json.put("array",array);
            int repetition=sum-array.size();
            if(repetition!=0){
                json.put("msg",repetition);
            }else {
                json.put("msg","success");
            }
            return json;
        }

        DoExchange doExchange = new DoExchange();
        ArrayList<ArrayList<String>> doChangeListValueId = new ArrayList<>();
        if (listValueId.size() < 2) {
            String[] strings = listValueId.get(0);
            ArrayList<String> re = new ArrayList<>();
            for (String s : strings) {
                re.add(s);
            }
            doChangeListValueId.add(re);
        } else {
            doChangeListValueId = (ArrayList<ArrayList<String>>) doExchange.doChange(listValueId);
        }

        for (int i = 0; i < doChangeListValueId.size(); i++) {
            ArrayList<String> stringsValueId = doChangeListValueId.get(i);
            String value = "";
            String label = "";
            StringBuilder code = new StringBuilder("                         ");
            Category one1 = this.categoryDao.findOne(categoryId);
            Supplier one2 = this.supplierDao.findOne(supplierId);
            code.replace(1,6,one1.getCode());
            code.replace(21,24,one2.getSupplierCode());
            for (String valueId : stringsValueId) {
                PropertiesValue one = this.properValueDao.findOne(Integer.valueOf(valueId));
                if (ValueUtil.notEmpity(value)) {
//                    value = value +","+ "{\""+one.getPropertiesId()+"\"" + ":"+"\""+valueId+"\"}";
                    value = value + "," + one.getPropertiesId() + ":" + valueId;
                } else {
//                    value = "{\""+one.getPropertiesId()+"\"" + ":"+"\""+valueId+"\"}";
                    value = one.getPropertiesId() + ":" + valueId;
                }
                if (ValueUtil.notEmpity(label)) {
                    label = label + " " + one.getCnValue();
                } else {
                    label = one.getCnValue();
                }



                if("1".equals(one.getPropertiesId().toString())){
                    String code1 = one.getCode();
                    code.replace(0,1,code1);
                }else if("7".equals(one.getPropertiesId().toString())){
                    String code1 = one.getCode();
                    code.replace(6,11,code1);
                }else if("2".equals(one.getPropertiesId().toString())){
                    String code1 = one.getCode();
                    code.replace(11,14,code1);
                }else if("3".equals(one.getPropertiesId().toString())){
                    String code1 = one.getCode();
                    code.replace(14,16,code1);
                }else if("4".equals(one.getPropertiesId().toString())){
                    String code1 = one.getCode();
                    code.replace(16,18,code1);
                }else if("9".equals(one.getPropertiesId().toString())){
                    String code1 = one.getCode();
                    code.replace(18,19,code1);
                }else if("5".equals(one.getPropertiesId().toString())){
                    String code1 = one.getCode();
                    code.replace(19,21,code1);
                }else if("6".equals(one.getPropertiesId().toString())){
                    String code1 = one.getCode();
                    code.replace(24,25,code1);
                }else {
                    String code1 = one.getCode();
                    code.append(code1);
                }


            }

            String s = code.toString().replaceAll(" ", "");

            Sku byCode = this.skuDao.findByCode(s);
            if(ValueUtil.notEmpity(byCode)){
                continue;
            }

            Map<String, Object> map = new HashMap<>();
//            JsonArray asJsonArray = jsonParser.parse(value).getAsJsonArray();
            map.put("value", value);
            map.put("label", label);
//            map.put("asJsonArray", asJsonArray);

            array.add(map);
        }
        json.put("array",array);
        int repetition=sum-array.size();
        if(repetition!=0){
            json.put("msg",repetition);
        }else {
            json.put("msg","success");
        }
//        for(int i=0;i<array.size();i++){
//            com.alibaba.fastjson.JSONObject jsonObject = array.getJSONObject(1);
//            String value = jsonObject.get("value").toString();
//
//        }

        return json;
    }


    @Override
    public Sku getSkuInfoByCode(String code)throws yesmywineException {
        Sku sku=skuDao.findByCode(code);
        if(null==sku){
            ValueUtil.isError("无此sku");
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
        return sku;
    }

    @Override
    public String updateSkuProp(Integer skuId, Integer isExpensive,String valueJson, String imgIds, String skuName) throws yesmywineException, MapperException {
        Sku sku = skuDao.findOne(skuId);
        List<SkuCommonProp> list = new ArrayList<>();
        JSONObject obj = new JSONObject(valueJson) ;
        Iterator it = obj.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            String value = obj.getString(key);
            if(ValueUtil.notEmpity(value)){
                SkuCommonProp goodsProp = new SkuCommonProp();
                goodsProp.setPropId(Integer.parseInt(key));
                goodsProp.setPropValueId(value);
                list.add(goodsProp);
            }
        }
        skuCommonDao.save(list);
        sku.setSkuCommonProp(list);
        sku.setSkuName(skuName);
        sku.setIsExpensive(isExpensive);
        skuDao.save(sku);
        String goodImg = null;
        if (ValueUtil.notEmpity(imgIds)) {
            String[] imgArr = imgIds.split(";");
            Integer[] arr = new Integer[imgArr.length];
            for (int i = 0; i < imgArr.length; i++) {
                arr[i] = Integer.parseInt(imgArr[i]);
            }
            if (imgIds != null && !imgIds.equals("")) {
                try {
                    goodImg = saveGoodsImg(skuId, arr);
                } catch (yesmywineException e) {
                    e.printStackTrace();
                }
            }
        }
        sku.setImageId(goodImg);
        skuDao.save(sku);
        List<Sku> skuList = new ArrayList<>();
        skuList.add(sku);
        //同步给WMS
        sendTOWMS(skuList);
        //同步给商城
        Map<String, String> map = new HashMap<>();
        map.put("skuId", skuId.toString());
        map.put("skuName", skuName);
        map.put("valueJson", valueJson);
        map.put("imgIds", imgIds);
        map.put("isExpensive",isExpensive.toString());
        if (!this.commonService.synchronous(map, Dictionary.MALL_HOST + "/goods/sku/synchronous", 1)) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("同步到商城sku失败");
        }
        if (!this.commonService.synchronous(map, Dictionary.MALL_HOST + "/goods/goods/synchronousGoods", 1)) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("同步到商城商品失败");
        }
        return "success";
    }

    private String saveGoodsImg(Integer goodsId, Integer[] imgIds) throws yesmywineException {
        try {
            HttpBean httpRequest = new HttpBean(Dictionary.MALL_HOST + "/fileUpload/tempToFormal/itf", RequestMethod.post);
            httpRequest.addParameter("module", "sku");
            httpRequest.addParameter("mId", goodsId);
            String ids = "";
            String imageIds = "";
            for (int i = 0; i < imgIds.length; i++) {
                if (i == 0) {
                    ids = ids + imgIds[i];
//                    imageIds=imageIds+imageId[i];
                } else {
                    ids = ids + "," + imgIds[i];
//                    imageIds=imageIds+":"+imageId[i];
                }
//                category.setImageId(imageIds);
                httpRequest.addParameter("id", ids);
            }
            httpRequest.run();
            String temp = httpRequest.getResponseContent();
            String cd = ValueUtil.getFromJson(temp, "code");
            if (!"201".equals(cd) || ValueUtil.isEmpity(cd)) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("图片上传失败");
            } else {
                com.alibaba.fastjson.JSONArray maps = new com.alibaba.fastjson.JSONArray(imgIds.length);
                String result = ValueUtil.getFromJson(temp, "data");
                JsonParser jsonParser = new JsonParser();
                JsonArray image = jsonParser.parse(result).getAsJsonArray();
                for (int f = 0; f < image.size(); f++) {
                    String id = image.get(f).getAsJsonObject().get("id").getAsString();
                    String name = image.get(f).getAsJsonObject().get("name").getAsString();
                    com.alibaba.fastjson.JSONObject map1 = new com.alibaba.fastjson.JSONObject();
                    map1.put("id", id);
                    map1.put("name", name);
                    maps.add(map1);
                }

                String result1 = maps.toJSONString().replaceAll("\"", "\'");

//                com.alibaba.fastjson.JSONObject map = new com.alibaba.fastjson.JSONObject();
//                for (int i = 0; i < maps.size(); i++) {
//                    com.alibaba.fastjson.JSONObject jsonObject = (com.alibaba.fastjson.JSONObject) maps.get(i);
//                    map.put("id" + i, jsonObject.getString("id"));
//                    map.put("name" + i, jsonObject.getString("name"));
//                }
//                map.put("num", String.valueOf(maps.size()));
                return result1;
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("图片服务出现问题！");
        }
        return null;
    }

    public boolean findByCate(Integer skuId, Integer categoryId) {
        Sku sku = skuDao.findOne(skuId);
        Category category = new Category();
        category.setId(categoryId);
        if (null == sku) {
            return false;
        } else if (null == skuDao.findByIdAndCategory(skuId, category)) {
            return false;
        }
        return true;
    }

}
