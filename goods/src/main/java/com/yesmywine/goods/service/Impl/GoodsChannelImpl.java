package com.yesmywine.goods.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.goods.bean.Item;
import com.yesmywine.goods.dao.*;
import com.yesmywine.goods.entity.*;
import com.yesmywine.goods.entityProperties.Category;
import com.yesmywine.goods.entityProperties.Channel;
import com.yesmywine.goods.service.CommonService;
import com.yesmywine.goods.service.GoodsChannelService;
import com.yesmywine.httpclient.bean.HttpBean;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.SynchronizeUtils;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ${shuang} on 2017/3/16.
 */
@Service
@Transactional
public class GoodsChannelImpl extends BaseServiceImpl<GoodsChannel, Integer> implements GoodsChannelService {

    @Autowired
    GoodsDao goodsDao;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    SkuDao skuDao;
    @Autowired
    GoodsChannelDao goodsChannelDao;
    @Autowired
    private CommonService<Goods> commonService;
    @Autowired
    private ChannelDao channelDao;



    //下发预售
    @Override
    public   List setGoodsChannel(Integer goodsId, String[] params, Integer operate) throws yesmywineException {

            Goods goods= goodsDao.findOne(goodsId);
            List< String> failedlist=new ArrayList<>();
            for (int i = 0; i <=params.length-1 ; i++) {
                String multiple= params[i];
                String[] splitone = multiple.split(";");
                Integer channelId=Integer.valueOf(splitone[0]);
//                String channelName=splitone[1];
                String channelCode=splitone[1];
                System.out.println(goodsChannelDao.findByGoodsIdAndChannelId(goodsId,channelId));
                if(ValueUtil.isEmpity(goodsChannelDao.findByGoodsIdAndChannelId(goodsId,channelId))){
                    GoodsChannel goodsChannel=new GoodsChannel();
                    goodsChannel.setGoodsId(goodsId);
                    goodsChannel.setGoodsName(goods.getGoodsName());
//                    goodsChannel.setSkuId(goods.getSkuIdString());
                    List<GoodsSku> goodsSkus = new ArrayList<>();
                    List<GoodsSku> goodsSku = goods.getGoodsSku();
                    for(int j=0;j<goodsSku.size();j++){
                        goodsSkus.add(goodsSku.get(j));
                    }
                    goodsChannel.setGoodsSku(goodsSkus);
                    goodsChannel.setPrice(goods.getPrice());
                    goodsChannel.setItem(goods.getItem().toString());
                    goodsChannel.setChannelId(channelId);
                    goodsChannel.setGoodsCode(goods.getGoodsCode());
                    goodsChannel.setChannelCode(channelCode);
                    goodsChannel.setOperate(operate);
//                    goodsChannel.setChannelName(channelName);
                    goodsChannelDao.save(goodsChannel);

                    sendGoodsToOMS(goods,goodsChannel);
                    HttpBean httpBean = new HttpBean(Dictionary.DIC_HOST+"/dic/sysCode/itf", RequestMethod.get);
                    httpBean.addParameter("sysCode", "cg_c");
                    httpBean.run();
                    String temp = httpBean.getResponseContent();
                    String data = ValueUtil.getFromJson(temp, "data");
                    JSONArray jsonArray = JSONArray.parseArray(data);
                    for(int j=0;j<jsonArray.size(); j++){
                        JSONObject jsonObject = jsonArray.getJSONObject(j);
                        if(jsonObject.get("entityCode").equals(channelId.toString())){
//                    if("1".equals("1") && channelId == 1){
                            Map<String, String> map1 = new HashMap<>();
                            Channel one = this.channelDao.findOne(channelId);
                            if(ValueUtil.notEmpity(one)){
                                map1.put("channelName", one.getChannelName());
                            }

                            map1.put("id", goods.getId().toString());

                            List<GoodsSku> skuList=goods.getGoodsSku();
                            com.alibaba.fastjson.JSONArray jsonArraySku = new com.alibaba.fastjson.JSONArray();
                            for (GoodsSku goodsSkuSend:skuList) {
                                Integer skuId=goodsSkuSend.getSkuId();
                                Sku one1 = this.skuDao.findOne(skuId);
                                List<SkuCommonProp> skuCommonProp = one1.getSkuCommonProp();
                                JSONArray jsonArray1 = new JSONArray();
//                                List<Integer> propList  = new ArrayList<>();
                                for(SkuCommonProp skuCP: skuCommonProp){
                                    JSONObject jsonObject1 = new JSONObject();
                                    jsonObject1.put("propId", skuCP.getPropId());
                                    jsonObject1.put("propValue", skuCP.getPropValueId());
                                    jsonObject1.put("propType", skuCP.getType());
                                    jsonArray1.add(jsonObject1);
                                }
                                Integer count=goodsSkuSend.getCount();
//                                Sku sku=skuDao.findOne(skuId);
                                com.alibaba.fastjson.JSONObject json1 = new com.alibaba.fastjson.JSONObject();
                                json1.put("count",count);
                                json1.put("skuId",skuId);
                                json1.put("code",one1.getCode());
                                String imageId = one1.getImageId();
                                com.alibaba.fastjson.JSONArray jsonArrayImage = com.alibaba.fastjson.JSONArray.parseArray(imageId);
                                json1.put("image",jsonArrayImage);
                                json1.put("prop", jsonArray1);
                                jsonArraySku.add(json1);
                            }
                            map1.put("skuIdString", jsonArraySku.toJSONString());
                            map1.put("channelId", channelId.toString());
                            map1.put("channelCode", channelCode);
                            map1.put("operate", goodsChannel.getOperate().toString());


                            if(!this.commonService.synchronousGoods(goods, channelId, 0, map1)){
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                HashMap<String,Object> map=new HashMap<>();
                                map.put("erro","同步失败");
                                failedlist.add(map.toString().replace("=",":"));
                            }
                        }
                    }



                }else {
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("goodsName",goods.getGoodsName());
//                    map.put("channelName",channelName);

                    failedlist.add(map.toString().replace("=",":"));
                }
            }
      return failedlist;
    }

    private String sendGoodsToOMS(Goods goods, GoodsChannel goodsChannel) throws yesmywineException {
        JSONObject requestJson = new JSONObject();
        requestJson.put("function",0);
        JSONObject dataJson = new JSONObject();
        dataJson.put("goodsCode",goods.getGoodsCode());
        dataJson.put("goodsName",goods.getGoodsName());
        dataJson.put("customerCode",goodsChannel.getChannelCode());
        dataJson.put("customerName",goodsChannel.getChannelName());
        dataJson.put("type","实体");
        switch (goodsChannel.getItem()){
            case "single":
                dataJson.put("item","单品");
                break;
            case "plural":
                dataJson.put("item","组合商品");
                break;
            case "fictitious":
                dataJson.put("item","单品");
                dataJson.put("type","虚拟");
                break;

        }
        dataJson.put("goodsPrice",goods.getPrice());
        Category category = categoryDao.findOne(Integer.valueOf(goods.getCategoryId()));
        Category parentCate = category.getParentName();
        Category grandParCate = null;
        if(parentCate!=null){
            grandParCate = parentCate.getParentName();
        }

        if(grandParCate!=null){
            dataJson.put("primaryName",grandParCate.getCategoryName());
            dataJson.put("secondName",parentCate.getCategoryName());
            dataJson.put("thirdName",category.getCategoryName());
        }else if(parentCate!=null&&grandParCate==null){
            dataJson.put("primaryName",parentCate.getCategoryName());
            dataJson.put("secondName",category.getCategoryName());
        }else if(parentCate==null&&grandParCate==null){
            dataJson.put("primaryName",category.getCategoryName());
        }
        JSONArray skuJsonArray = new JSONArray();
//        String skuJsonStr = goods.getSkuIdString();
        List<GoodsSku> goodsSku = goods.getGoodsSku();
//        JSONArray strArray = JSON.parseArray(skuJsonStr);
        for(int i = 0;i<goodsSku.size();i++){
            JSONObject skuJsonObject = new JSONObject();
            GoodsSku goodsSku1 = goodsSku.get(i);
            Integer skuId = goodsSku1.getSkuId();
            Integer count = goodsSku1.getCount();
            Sku sku = skuDao.findOne(skuId);
            skuJsonObject.put("goodsCode",goods.getGoodsCode());
            skuJsonObject.put("skuCode",sku.getCode());
            skuJsonObject.put("baseUnitQuantity",count);
            skuJsonArray.add(skuJsonObject);
        }

        dataJson.put("BaseCustomerGoods",skuJsonArray);
        requestJson.put("data",dataJson);
        String result = SynchronizeUtils.getOmsResult(Dictionary.OMS_HOST,"/updateBaseGoods",RequestMethod.post,"",requestJson.toJSONString());
        if(result!=null) {
            String status = ValueUtil.getFromJson(result,"status");
            String message = ValueUtil.getFromJson(result,"message");
            if(!status.equals("success")){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("向OMS下发商品失败,原因："+message);
            }
        }else{
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("向OMS下发商品失败");
        }
        return "SUCCESS";
    }

    @Override
    public String exchange(Integer goodsId,Integer channelId) throws yesmywineException {
        GoodsChannel goodsChannel = goodsChannelDao.findByGoodsIdAndChannelId(goodsId, channelId);
        if (goodsChannel.getItem().equals("single")) {//单品

//            JsonParser jsonParser = new JsonParser();
//            JsonArray arr2 = jsonParser.parse(goodsChannel.getSkuId()).getAsJsonArray();
            List<GoodsSku> goodsSku = goodsChannel.getGoodsSku();
            for (int j = 0; j < goodsSku.size(); j++) {
                Integer skuId = goodsSku.get(j).getSkuId();

                String returnCode = this.http(skuId.toString(), goodsChannel.getChannelId());
                if (goodsChannel.getOperate() == 1) {//预售状态
                    Integer count;
                    try {
                        count = Integer.valueOf(ValueUtil.getFromJson(returnCode, "data", "useCount"));
                    }catch (Exception e){
                        return "该商品库存为0,不可转换";
                    }
                    if (count > 0) {
                        goodsChannel.setOperate(0);//设置为在售


                        //通知商城
                        HttpBean httpBean = new HttpBean(Dictionary.DIC_HOST+"/dic/sysCode/itf", RequestMethod.get);
                        httpBean.addParameter("sysCode", "cg_c");
                        httpBean.run();
                        String temp = httpBean.getResponseContent();
                        String data = ValueUtil.getFromJson(temp, "data");
                        JSONArray jsonArray = JSONArray.parseArray(data);
                        for(int i=0;i<jsonArray.size(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.get("entityCode").equals("1") && channelId == 1) {

                                Map<String, String> map = new HashMap<>();
                                map.put("goodsId", goodsId.toString());

//                                List<GoodsSku> skuList=goods.getGoodsSku();
                                com.alibaba.fastjson.JSONArray jsonArraySku = new com.alibaba.fastjson.JSONArray();
//                                for (GoodsSku goodsSkuSend:skuList) {
//                                    Integer skuId=goodsSkuSend.getSkuId();
                                    Integer count1=goodsSku.get(j).getCount();
//                                    Sku sku=skuDao.findOne(skuId);
                                    com.alibaba.fastjson.JSONObject json1 = new com.alibaba.fastjson.JSONObject();
                                    json1.put("count",count1);
                                    json1.put("skuId",skuId);
                                    Sku one1 = this.skuDao.findOne(skuId);
//
                                List<SkuCommonProp> skuCommonProp = one1.getSkuCommonProp();
                                JSONArray jsonArray1 = new JSONArray();
//                                List<Integer> propList  = new ArrayList<>();
                                for(SkuCommonProp skuCP: skuCommonProp){
                                    JSONObject jsonObject1 = new JSONObject();
                                    jsonObject1.put("propId", skuCP.getPropId());
                                    jsonObject1.put("propValue", skuCP.getPropValueId());
                                    jsonObject1.put("propType", skuCP.getType());
                                    jsonArray1.add(jsonObject1);
                                }

                                json1.put("code",one1.getCode());
                                json1.put("prop",jsonArray1);
                                jsonArraySku.add(json1);
//                                }

                                map.put("skuIdString", jsonArraySku.toJSONString());
                                map.put("channelId", channelId.toString());
                                map.put("operate", "0");
                                map.put("id", goodsId.toString());
                                Goods one = this.goodsDao.findOne(goodsId);
                                if (this.commonService.synchronous(one, Dictionary.MALL_HOST+ "/goods/goods/synchronous", 0, map)) {
//                                    if (this.commonService.synchronousGoods(map, channelId, 1)) {
                                    goodsChannelDao.save(goodsChannel);
                                    return "success";
                                } else {
                                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                    return "同步失败";
                                }
                            }
                        }

                    } else {
                        return "库存为0";
                    }

                } else {//在售状态
                    Integer allcount = Integer.valueOf(ValueUtil.getFromJson(returnCode, "data", "allCount"));
                    if (allcount == 0) {
                        goodsChannel.setOperate(1);//设置为预售货
                        //通知商城
                        HttpBean httpBean = new HttpBean(Dictionary.DIC_HOST+"/dic/sysCode/itf", RequestMethod.get);
                        httpBean.addParameter("sysCode", "cg_c");
                        httpBean.run();
                        String temp = httpBean.getResponseContent();
                        String data = ValueUtil.getFromJson(temp, "data");
                        JSONArray jsonArray = JSONArray.parseArray(data);
                        for(int i=0;i<jsonArray.size(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.get("entityCode").equals("1") && channelId == 1) {

                                Map<String, String> map = new HashMap<>();
                                map.put("goodsId", goodsId.toString());

                                com.alibaba.fastjson.JSONArray jsonArraySku = new com.alibaba.fastjson.JSONArray();
                                Integer count1=goodsSku.get(j).getCount();
                                com.alibaba.fastjson.JSONObject json1 = new com.alibaba.fastjson.JSONObject();
                                json1.put("count",count1);
                                json1.put("skuId",skuId);
                                Sku one1 = this.skuDao.findOne(skuId);
                                json1.put("code",one1.getCode());
                                jsonArraySku.add(json1);

                                map.put("skuIdString", jsonArraySku.toJSONString());
                                map.put("channelId", channelId.toString());
                                map.put("operate", "1");
                                map.put("id", goodsId.toString());
                                Goods one = this.goodsDao.findOne(goodsId);
                                if (this.commonService.synchronous(one, Dictionary.MALL_HOST+ "/goods/goods/synchronous", 0, map)) {
                                    goodsChannelDao.save(goodsChannel);
                                    return "success";
                                } else {
                                    return "同步失败";
                                }
                            }
                        }

                    } else {
                        return "库存不为0";
                    }
                }
            }

        } else //组合商品
        {
//            if(goodsChannel.getOperate()==1){
//                //todo
//                goodsChannel.getSkuId();
//                return true;
//            }else {
//                return  false;
//            }
            return "组合商品不可以转";
        }

        return "系统异常";
    }

    public String http ( String skuId,Integer channelId){
        HttpBean bean = new HttpBean(Dictionary.PAAS_HOST + "/inventory/channelInventory/skuInventory/itf", RequestMethod.get);
        bean.addParameter("skuId",skuId);
        bean.addParameter("channelId",channelId);
        bean.run();
        return bean.getResponseContent();

    }

    public String http (String skuId){
        HttpBean bean = new HttpBean(Dictionary.PAAS_HOST + "/inventory/channelInventory/skuInventory/itf", RequestMethod.get);
        bean.addParameter("skuId",skuId);
        bean.run();
        return bean.getResponseContent();

    }

    @Override
    public Integer inventory(String skuId, Integer channelId, String item) {

        if(Item.single.toString().equals(item)|| Item.single.toString() == item){//单品
            JsonParser jsonParser = new JsonParser();
            JsonArray arr2 = jsonParser.parse(skuId).getAsJsonArray();
            for (int j = 0; j < arr2.size(); j++) {
                String skuId1 = arr2.get(j).getAsJsonObject().get("skuId").getAsString();
                //根据skuId查询所有渠道的库存
                String result = this.http(skuId1, channelId);
                if(!"500".equals(ValueUtil.getFromJson(result, "code"))){
                    String useCount = ValueUtil.getFromJson(result, "data", "useCount");
                    return Integer.valueOf(useCount);
                }
            }
            return null;

        }else {//非单品
            JsonParser jsonParser = new JsonParser();
            JsonArray arr = jsonParser.parse(skuId).getAsJsonArray();
            Integer[] inventory = new Integer[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                String skuId1 = arr.get(i).getAsJsonObject().get("skuId").getAsString();

                String result = this.http(skuId1, channelId);
                if("500".equals(ValueUtil.getFromJson(result, "code"))){
                    inventory[i] = null;
                }else {
                    String useCount = ValueUtil.getFromJson(result, "data", "useCount");
                    inventory[i] = Integer.valueOf(useCount);
                }

            }
            Integer min = inventory[0];
            for(int j=1; j<inventory.length; j++){
                if(ValueUtil.notEmpity(inventory[j])){
                    if(inventory[j]<min){
                        min = inventory[j];
                    }
                }
            }
                return min;
        }
    }


    @Override
    public Integer inventoryGoodsSku(List<GoodsSku> goodsSkus, Integer channelId, String item) {

        if(goodsSkus.size()==0){
            return 0;
        }

        if(Item.single.toString().equals(item)|| Item.single.toString() == item){//单品
            for (int j = 0; j < goodsSkus.size(); j++) {
                Integer skuId = goodsSkus.get(j).getSkuId();
                //根据skuId查询所有渠道的库存
                String result = this.http(skuId.toString(), channelId);
                if(!"500".equals(ValueUtil.getFromJson(result, "code"))){
                    String useCount = ValueUtil.getFromJson(result, "data", "useCount");
                    return Integer.valueOf(useCount);
                }
            }
            return null;

        }else {//非单品
            Integer[] inventory = new Integer[goodsSkus.size()];
            for (int i = 0; i < goodsSkus.size(); i++) {
                Integer skuId = goodsSkus.get(i).getSkuId();
                String result = this.http(skuId.toString(), channelId);
                if("500".equals(ValueUtil.getFromJson(result, "code"))){
                    inventory[i] = null;
                }else {
                    String useCount = ValueUtil.getFromJson(result, "data", "useCount");
                    inventory[i] = Integer.valueOf(useCount);
                }

            }
            Integer min = inventory[0];
            for(int j=1; j<inventory.length; j++){
                if(ValueUtil.notEmpity(inventory[j])){
                    if(inventory[j]<min){
                        min = inventory[j];
                    }
                }
            }
            return min;
        }
    }

    @Override
    public String updateThirdCode(Integer id,String thirdCode) throws yesmywineException {
        GoodsChannel goodsChannel=goodsChannelDao.findOne(id);
        HttpBean httpBean = new HttpBean(Dictionary.DIC_HOST+"/dic/sysCode/itf", RequestMethod.get);
        httpBean.addParameter("sysCode", "g_channel");
        httpBean.run();
        String temp = httpBean.getResponseContent();
        String data = ValueUtil.getFromJson(temp, "data");
        JSONArray jsonArray = JSONArray.parseArray(data);
        for(int i=0;i<jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.get("entityValue").equals(goodsChannel.getChannelId())) {
                return "官网没有第三方编码";
            }
        }
        goodsChannel.setThirdCode(thirdCode);
        goodsChannelDao.save(goodsChannel);
        return "success";
    }

}

