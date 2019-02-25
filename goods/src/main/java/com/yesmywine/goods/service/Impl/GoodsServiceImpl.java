package com.yesmywine.goods.service.Impl;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.goods.bean.IsUse;
import com.yesmywine.goods.bean.Item;
import com.yesmywine.goods.dao.*;
import com.yesmywine.goods.entity.Goods;
import com.yesmywine.goods.entity.GoodsChannel;
import com.yesmywine.goods.entity.GoodsSku;
import com.yesmywine.goods.entity.Sku;
import com.yesmywine.goods.service.GoodsService;
import com.yesmywine.goods.service.SkuService;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by hz on 12/8/16.
 */
@Service
@Transactional
public class GoodsServiceImpl extends BaseServiceImpl<Goods, Integer> implements GoodsService {
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private GoodsChannelDao goodsChannelDao;
    @Autowired
    private SkuService skuService;
    @Autowired
    private PropertiesDao propertiesDao;
    @Autowired
    private ProperValueDao properValueDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private GoodsSkuDao goodsSkuDao;
//    @Autowired
//    private CommonService<Goods> commonService;

    public String addGoods(Map<String, String> param) throws yesmywineException {//新增商品
        ValueUtil.verify(param, new String[]{ "goodsName", "item", "price", "skuIdString", "categoryId"});
        String item = param.get("item");
//        if (item.equals("single")) {//判断单品商品是否重复
//            String skuIdString = param.get("skuIdString");
//            JsonParser jsonParser = new JsonParser();
//            JsonArray arr = jsonParser.parse(skuIdString).getAsJsonArray();
//            for (int i = 0; i < arr.size(); i++) {
//                String skuId = arr.get(i).getAsJsonObject().get("skuId").getAsString();
//                Goods goods1 = goodsDao.findByItemAndSkuIdString(Item.single, skuId);
//                if (goods1 != null) {
//                    ValueUtil.isError("sku已被使用");
//                }
//            }
//        }
        Goods goods = new Goods();
        switch (item) {
            case "single":
                goods.setItem(Item.single);
                break;
            case "fictitious":
                goods.setItem(Item.fictitious);
                break;
            default:
                goods.setItem(Item.plural);
                break;
        }

//        goods.setSkuIdString(param.get("skuIdString"));
        String goodsName=param.get("goodsName");
        Goods goodsOldName=goodsDao.findByGoodsName(goodsName);
        if(ValueUtil.notEmpity(goodsOldName)){
             ValueUtil.isError("该商品名已存在");
        }


        JsonParser jsonParser = new JsonParser();
        JsonArray arr = jsonParser.parse(param.get("skuIdString")).getAsJsonArray();
        List<GoodsSku> list = new ArrayList<>();
        for (int f = 0; f < arr.size(); f++) {
            String skuId=arr.get(f).getAsJsonObject().get("skuId").getAsString();
            Sku one = this.skuDao.findOne(Integer.valueOf(skuId));
            if(item.equals(Item.fictitious.toString())){
                if(one.getType()==0){
                    ValueUtil.isError("虚拟商品只能关联虚拟sku");
                }
            }
            String count=arr.get(f).getAsJsonObject().get("count").getAsString();
            GoodsSku goodsSku=new GoodsSku();
            goodsSku.setCount(Integer.parseInt(count));
            goodsSku.setSkuId(Integer.parseInt(skuId));
            list.add(goodsSku);
        }
        goods.setGoodsSku(list);
        goodsSkuDao.save(list);
        goods.setGoodsName(param.get("goodsName"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddssHHmmss");
        String format = dateFormat.format(new Date());
        goods.setGoodsCode("G"+ format);//商品编码 待定
        goods.setPrice(param.get("price"));
        goods.setCategoryId(param.get("categoryId"));
//        String categoryGroup = "L"+ goods.getCategoryId();
//        Category category = this.categoryDao.findOne(Integer.valueOf(goods.getCategoryId()));
//        if(ValueUtil.notEmpity(category) && ValueUtil.notEmpity(category.getParentName())){
//            categoryGroup = categoryGroup + "L" + category.getParentName().getId();
//            Category category1 = this.categoryDao.findOne(category.getParentName().getId());
//            if(ValueUtil.notEmpity(category1) && ValueUtil.notEmpity(category1.getParentName())){
//                categoryGroup = categoryGroup + "L" + category1.getParentName().getId();
//            }
//        }
//        goods.setCategoryGroup(categoryGroup);
        goodsDao.save(goods);

//        Map<String, String> map1 = new HashMap<>();
//        map1.put("id", goods.getId().toString());
//        if(!this.commonService.synchronous(goods, ConstantData.goodsUrl+ "/goods/goods/synchronous", 0, map1)){
//            goodsDao.delete(goods);
//        }

        Map<String, String> map = new HashMap<>();
        Item item1 = goods.getItem();
        if (item1 == Item.single) {
//            JsonParser jsonParser = new JsonParser();
//            JsonArray arr = jsonParser.parse(goods.getSkuIdString()).getAsJsonArray();
            List<GoodsSku> goodsSku = goods.getGoodsSku();
            String skuId = "";
            for (int i = 0; i < goodsSku.size(); i++) {
                String skuId1 = goodsSku.get(i).getSkuId().toString();
                map.put("single", skuId1);
            }
        } else {
//            JsonParser jsonParser = new JsonParser();
//            JsonArray arr = jsonParser.parse(goods.getSkuIdString()).getAsJsonArray();
            List<GoodsSku> goodsSku = goods.getGoodsSku();
            String skuId = "";
            for (int i = 0; i < goodsSku.size(); i++) {
                String skuId1 = goodsSku.get(i).getSkuId().toString();
                skuId = skuId + skuId1 + ",";
            }
            map.put("plural", skuId);
        }
        setSku(map);
        return "success";
    }
     public void setSku(Map<String, String> map){
         Set item=map.keySet();
         Iterator it = item.iterator();
         String next = (String)it.next();
         String skuId=map.get(next);
         if(next.equals("single")){
             Sku sku=skuService.findOne(Integer.parseInt(skuId));
             sku.setIsUse(IsUse.yes);
             skuDao.save(sku);
         }else{
             String []a=skuId.split(",");
             for(int i=0;i<a.length;i++){
                 Sku sku=skuService.findOne(Integer.parseInt(a[i]));
                 sku.setIsUse(IsUse.yes);
                 skuDao.save(sku);
             }
         }
     }

//    public Map<String, Object> updateLoad(Integer id) throws yesmywineException {//加载显示商品
//        ValueUtil.verify(id, "idNull");
//        Goods goods = goodsDao.findOne(id);
//        String item = goods.getItem().toString();
//        if (item.equals("plural")) {
//            com.alibaba.fastjson.JSONArray jsonArray = new com.alibaba.fastjson.JSONArray();
//            JsonParser jsonParser = new JsonParser();
//            JsonArray arr = jsonParser.parse(goods.getSkuIdString()).getAsJsonArray();
//            for (int i = 0; i < arr.size(); i++) {
//                String skuId = arr.get(i).getAsJsonObject().get("skuId").getAsString();
//                String count = arr.get(i).getAsJsonObject().get("count").getAsString();
//                Sku sku = skuDao.findOne(Integer.valueOf(skuId));
//                Gson gson = new Gson();
//                com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
//                jsonObject.put("sku", JSON.parseObject(gson.toJson(sku)));
//                jsonObject.put("count", count);
//                jsonArray.add(jsonObject);
//            }
//            Map<String, Object> map = new HashMap<>();
////            String js = jsonArray.toJSONString();
////            String js = goods.getSkuIdString();
////            JSONObject jsonObject = new JSONObject(js);
////            Iterator iterator = jsonObject.keys();
////            for (int j = 0; j < jsonObject.length(); j++) {
////                while (iterator.hasNext()) {
////                    String key = (String) iterator.next();
////                    Integer value = Integer.valueOf(jsonObject.getString(key));
////                    map.put(key, value);
////                }
////            }
////
////            goods.setSkuIdString(map.toString().replace("=", ":"));
//            map.put("Goods", goods);
//            map.put("property", jsonArray);
//            return map;
//        }
//        Sku sku = skuDao.findOne(Integer.valueOf(goods.getSkuIdString()));
//        Map<String, Object> map = new HashMap<>();
//        map.put("Goods", goods);
//        map.put("Sku",sku);
//        return map;
//
//    }


    public com.alibaba.fastjson.JSONObject updateLoad(Integer goodsId) throws yesmywineException {//加载显示商品

        Goods goods =  goodsDao.findOne(goodsId);
//        String skuString = goods.getSkuIdString();
        List<GoodsSku> goodsSku = goods.getGoodsSku();
        String item = goods.getItem().toString();
        if (item.equals("plural")) {
//            JsonParser jsonParser = new JsonParser();
            com.alibaba.fastjson.JSONArray jsonArray = new com.alibaba.fastjson.JSONArray();
//            JsonArray arr = jsonParser.parse(skuString).getAsJsonArray();
            JSONObject jsonObject1 = new JSONObject();
            for (int i = 0; i < goodsSku.size(); i++) {
                Integer skuId = goodsSku.get(i).getSkuId();
                String count = goodsSku.get(i).getCount().toString();
                Sku sku = skuDao.findOne(skuId);
                JSONObject jsonObject = new JSONObject(sku.getProperty());
                Iterator iterator = jsonObject.keys();
                Map<String, String> map = new HashMap<>();
                for (int j = 0; j < jsonObject.length(); j++) {
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        Integer value = Integer.valueOf(jsonObject.getString(key));
                        String propName = propertiesDao.findOne(Integer.parseInt(key)).getCnName();
                        String propValue = properValueDao.findOne(value).getCnValue();
                        map.put(propName, propValue);
                    }
                }
                sku.setProperty(map.toString().replace("=", ":"));
                jsonObject1.put("sku", sku);
                jsonObject1.put("count", count);
                jsonArray.add(jsonObject1);
            }
            goods.setJsonArray(jsonArray);
//            System.out.printf(goods.toString());
//            return goods;
            com.alibaba.fastjson.JSONObject goodsJson = (com.alibaba.fastjson.JSONObject) JSON.parseObject(ValueUtil.toJson(goods)).get("data");
            com.alibaba.fastjson.JSONObject newJson = new com.alibaba.fastjson.JSONObject();
            newJson.putAll(goodsJson);
            newJson.put("array",jsonArray);
            return  newJson;
        }
//        JsonParser jsonParser = new JsonParser();
//        JsonArray arr = jsonParser.parse(skuString).getAsJsonArray();

        com.alibaba.fastjson.JSONArray jsonArray = new com.alibaba.fastjson.JSONArray();
        for (int i = 0; i < goodsSku.size(); i++) {
            Integer skuId1 = goodsSku.get(i).getSkuId();
            Sku sku = skuDao.findOne(skuId1);
            JSONObject jsonObject = new JSONObject(sku.getProperty());

            Iterator iterator = jsonObject.keys();
            Map<String, String> map = new HashMap<>();
            for (int j = 0; j < jsonObject.length(); j++) {
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    Integer value = Integer.valueOf(jsonObject.getString(key));
                    String propName = propertiesDao.findOne(Integer.parseInt(key)).getCnName();
                    String propValue = properValueDao.findOne(value).getCnValue();
                    map.put(propName, propValue);
                }
            }

            sku.setProperty(map.toString().replace("=", ":"));
            jsonObject.put("sku", sku);
            jsonArray.add(jsonObject);
//        goods.setJsonArray(jsonArray);
        }
        Goods newGoods = new Goods();
        newGoods.setJsonArray(jsonArray);
        com.alibaba.fastjson.JSONObject goodsJson = (com.alibaba.fastjson.JSONObject) JSON.parseObject(ValueUtil.toJson(goods)).get("data");
        com.alibaba.fastjson.JSONObject newJson = new com.alibaba.fastjson.JSONObject();
        newJson.putAll(goodsJson);
        newJson.put("array",jsonArray);
        return  newJson;
     }


    public List<Map> querySku(Integer goodsId) throws yesmywineException {//加载显示sku

        Goods goods =  goodsDao.findOne(goodsId);
//        String skuString = goods.getSkuIdString();
        List<GoodsSku> goodsSku = goods.getGoodsSku();
        String item = goods.getItem().toString();
        if (item.equals("plural")) {
            try {
//                JsonParser jsonParser = new JsonParser();
                List<Map> result = new ArrayList<>();
//                JsonArray arr = jsonParser.parse(skuString).getAsJsonArray();
                for (int i = 0; i < goodsSku.size(); i++) {
                    Map<String, Object> map = new HashMap<>();
                    Integer skuId = goodsSku.get(i).getSkuId();
                    Integer count = goodsSku.get(i).getCount();
                    Sku sku = skuDao.findOne(skuId);
                    map.put("categoryId", sku.getCategory().getCategoryName());
                    map.put("sku", sku.getSku());
                    map.put("supplier", sku.getSupplier().getSupplierName());
                    map.put("code", sku.getCode());
                    map.put("count", count);
                    result.add(map);
                }
//                goods.setJsonArray(jsonArray);
//            System.out.printf(goods.toString());
//            return goods;
//                com.alibaba.fastjson.JSONObject goodsJson = (com.alibaba.fastjson.JSONObject) JSON.parseObject(ValueUtil.toJson(goods)).get("data");
//                com.alibaba.fastjson.JSONObject newJson = new com.alibaba.fastjson.JSONObject();
//                newJson.putAll(goodsJson);
//                newJson.put("array", jsonArray);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return result;
            }catch (Exception e){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
            return null;
        }else {
            try {
//                JsonParser jsonParser = new JsonParser();
//                JsonArray arr = jsonParser.parse(skuString).getAsJsonArray();
                List<Map> result = new ArrayList<>();
                for (int i = 0; i < goodsSku.size(); i++) {
                    Map<String, Object> map = new HashMap<>();
                    Integer skuId = goodsSku.get(i).getSkuId();
                    Integer count=goodsSku.get(i).getCount();
//                    String skuId1 = arr.get(i).getAsJsonObject().get("skuId").getAsString();
                    Sku sku = skuDao.findOne(skuId);
//                    JSONObject jsonObject = new JSONObject(sku.getProperty());
//
//                    Iterator iterator = jsonObject.keys();
//                    Map<String, String> map = new HashMap<>();
//                    for (int j = 0; j < jsonObject.length(); j++) {
//                        while (iterator.hasNext()) {
//                            String key = (String) iterator.next();
//                            Integer value = Integer.valueOf(jsonObject.getString(key));
//                            String propName = propertiesDao.findOne(Integer.parseInt(key)).getCnName();
//                            String propValue = properValueDao.findOne(value).getCnValue();
//                            map.put(propName, propValue);
//                        }
//                    }
//
//                    sku.setProperty(map.toString().replace("=", ":"));
                    map.put("categoryId", sku.getCategory().getCategoryName());
                    map.put("sku", sku.getSku());
                    map.put("supplier", sku.getSupplier().getSupplierName());
                    map.put("code", sku.getCode());
                    map.put("count",count);
                    result.add(map);
//        goods.setJsonArray(jsonArray);
                }
//                Goods newGoods = new Goods();
//                newGoods.setJsonArray(jsonArray);
//                com.alibaba.fastjson.JSONObject goodsJson = (com.alibaba.fastjson.JSONObject) JSON.parseObject(ValueUtil.toJson(goods)).get("data");
//                com.alibaba.fastjson.JSONObject newJson = new com.alibaba.fastjson.JSONObject();
//                newJson.putAll(goodsJson);
//                newJson.put("array", jsonArray);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return result;
            }catch (Exception e){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
            return null;
        }
    }



    public String delete(Integer id) throws yesmywineException {//删除商品
        ValueUtil.verify(id, "idNull");
        //商品如果被使用了（商品下发），则不可删除。
        Goods goods = goodsDao.findOne(id);
        List<GoodsSku> goodsSku = goods.getGoodsSku();
        GoodsChannel goodsChannel = goodsChannelDao.findByGoodsId(id);
        if (goodsChannel != null) {
            ValueUtil.isError("商品已下发,不可删除");
        }
//        for (int i = 0; i < goodsSku.size(); i++) {
//            Integer skuId = goodsSku.get(i).getSkuId();
//            Sku sku = skuDao.findOne(skuId);
//            if (sku.getIsUse()==IsUse.yes) {
//                ValueUtil.isError("sku已被使用");
//            }
//        }
        goodsDao.delete(goods);
        return "success";
        }
    }
//    public String getGoodsNumber(String json) {//获取商品编号
////  {"class":"W","category":"02","particularYear":"000","brand":"KAP","type":"01","degrees":"BV",
//// "capacity":"52","series":"01","varieties":"0","production":"HE","supplier":"B89","productPhase":"0","directuSpply":"1"}
//        String classValue = ValueUtil.getFromJson(json, "class");
//        String categoryValue = ValueUtil.getFromJson(json, "category");
//        String particularYearValue = ValueUtil.getFromJson(json, "particularYear");
//        String brandValue = ValueUtil.getFromJson(json, "brand");
//        String typeValue = ValueUtil.getFromJson(json, "type");
//        String degreesValue = ValueUtil.getFromJson(json, "degrees");
//        String capacityValue = ValueUtil.getFromJson(json, "capacity");
//        String seriesValue = ValueUtil.getFromJson(json, "series");
//        String varietiesValue = ValueUtil.getFromJson(json, "varieties");
//        String productionValue = ValueUtil.getFromJson(json, "production");
//        String supplierValue = ValueUtil.getFromJson(json, "supplier");
//        String productPhaseSpplyValue = ValueUtil.getFromJson(json, "productPhase");
//        String directuSpplyValue = ValueUtil.getFromJson(json, "directuSpply");
//        String goodsNumber = classValue + categoryValue + particularYearValue + brandValue + typeValue + degreesValue +
//                capacityValue + seriesValue + varietiesValue + productionValue + supplierValue + productionValue + productPhaseSpplyValue +
//                directuSpplyValue;
//        return goodsNumber;
//
//    }
//


