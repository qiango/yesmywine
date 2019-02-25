package com.yesmywine.goods.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.yesmywine.goods.bean.Item;
import com.yesmywine.goods.dao.ChannelDao;
import com.yesmywine.goods.dao.GoodsChannelDao;
import com.yesmywine.goods.dao.GoodsDao;
import com.yesmywine.goods.entity.Goods;
import com.yesmywine.goods.entity.GoodsChannel;
import com.yesmywine.goods.entity.GoodsSku;
import com.yesmywine.goods.entityProperties.Channel;
import com.yesmywine.goods.service.GoodsChannelService;
import com.yesmywine.goods.service.SalesModelService;
import com.yesmywine.httpclient.bean.HttpBean;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by light on 2017/3/16.
 */
@Service
public class SalesModelServiceImpl implements SalesModelService{

    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsChannelService goodsChannelService;
    @Autowired
    private GoodsChannelDao goodsChannelDao;
    @Autowired
    private ChannelDao channelDao;

    @Override
    public Object choose(Integer goodsId, Integer salesModelCode) throws yesmywineException {
        Goods goods = goodsDao.findOne(goodsId);
        if(null == goods){
            return "没有此商品！";
        }
        if(1 == salesModelCode){//预售
         if(Item.single.equals(goods.getItem()) || Item.single == goods.getItem()){//单品
//             Integer skuId = Integer.parseInt(goods.getSkuIdString());
             JsonParser jsonParser = new JsonParser();
//             JsonArray arr2 = jsonParser.parse(goods.getSkuIdString()).getAsJsonArray();

             List<GoodsSku> goodsSku = goods.getGoodsSku();

             for (int j = 0; j < goodsSku.size(); j++) {
                 String skuId = goodsSku.get(j).getSkuId().toString();
                 //根据skuId查询所有渠道的库存
                 String result = this.goodsChannelService.http(skuId);
                 String fromJson = ValueUtil.getFromJson(result, "data");
                 if(ValueUtil.isEmpity(fromJson)|| "[]".equals(fromJson)){
                     HttpBean httpBean = new HttpBean(Dictionary.DIC_HOST+"/dic/sysCode/itf", RequestMethod.get);
                     httpBean.addParameter("sysCode", "channel_salesModel");
                     httpBean.run();
                     String temp = httpBean.getResponseContent();
                     String data = ValueUtil.getFromJson(temp, "data");
                     com.alibaba.fastjson.JSONArray jsonArray1 = com.alibaba.fastjson.JSONArray.parseArray(data);

                     Integer entityCode = null;
                     String entityValue = null;

                     for(int k=0;k<jsonArray1.size(); k++) {
                         com.alibaba.fastjson.JSONObject jsonObject = jsonArray1.getJSONObject(k);
                         entityCode= Integer.valueOf(jsonObject.get("entityCode").toString());
                         entityValue = jsonObject.get("entityValue").toString();

                     }
                     JSONArray array = new JSONArray();
                     JSONObject jsonObject = new JSONObject();
                     JSONObject js = new JSONObject();

                     js.put("id", entityCode);
                     js.put("channelCode", entityValue);
                     Channel one = this.channelDao.findOne(entityCode);
                     js.put("channelName", one.getChannelName());
                     js.put("type", one.getType());
                     jsonObject.put("channel", js);
                     array.add(jsonObject);
                     return array;
                 }
                 JsonArray arr = jsonParser.parse(fromJson).getAsJsonArray();
                 int length = arr.size();
                 for (int i = 0; i < length; i++) {
                     if (0 == arr.size()) {
                         break;
                     }
                     if ( 1 == arr.size()) {
                         String useCount = arr.get(0).getAsJsonObject().get("useCount").getAsString();
                         String channelId = arr.get(0).getAsJsonObject().get("channel").getAsJsonObject().get("id").getAsString();
                         Channel one = this.channelDao.findOne(Integer.valueOf(channelId));
                         if(!"0".equals(one.getType())){
                             continue;
                         }
                         GoodsChannel byGoodsIdAndChannelId = this.goodsChannelDao.findByGoodsIdAndChannelId(goodsId, Integer.valueOf(channelId));
                         if (0 == Integer.parseInt(useCount) || ValueUtil.notEmpity(byGoodsIdAndChannelId)) {
                             arr.remove(i);
                         }
                         return arr;
                     }
                     String useCount = arr.get(i).getAsJsonObject().get("useCount").getAsString();
                     String channelId = arr.get(i).getAsJsonObject().get("channel").getAsJsonObject().get("id").getAsString();
                     Channel one = this.channelDao.findOne(Integer.valueOf(channelId));
                     if(!"0".equals(one.getType())){
                         continue;
                     }
                     GoodsChannel byGoodsIdAndChannelId = this.goodsChannelDao.findByGoodsIdAndChannelId(goodsId, Integer.valueOf(channelId));
                     if (0 != Integer.parseInt(useCount) || ValueUtil.notEmpity(byGoodsIdAndChannelId)) {
                         arr.remove(i);
                         if(i == (length-1)){
                             break;
                         }
                         i--;
                     }
                 }
                 return arr;
             }


         }else {//非单品
             return "只有单品可以选择预售。";
         }
        }else if(0 == salesModelCode){//普通
            if(Item.single.equals(goods.getItem())|| Item.single == goods.getItem()){//单品
//                Integer skuId = Integer.parseInt(goods.getSkuIdString());
                JsonParser jsonParser = new JsonParser();
//                JsonArray arr2 = jsonParser.parse(goods.getSkuIdString()).getAsJsonArray();

                List<GoodsSku> goodsSku = goods.getGoodsSku();

                for (int j = 0; j < goodsSku.size(); j++) {
                    String skuId = goodsSku.get(j).getSkuId().toString();
                    //根据skuId查询所有渠道的库存
                    String result = this.goodsChannelService.http(skuId);
                    String fromJson = ValueUtil.getFromJson(result, "data");
                    JsonArray arr = jsonParser.parse(fromJson).getAsJsonArray();
                    int length = arr.size();
                    for (int i = 0; i < length; i++) {
                        if( 0 == arr.size() ){
                            break;
                        }
                        if ( 1 == arr.size()) {
                            String useCount = arr.get(0).getAsJsonObject().get("useCount").getAsString();
                            String channelId = arr.get(0).getAsJsonObject().get("channel").getAsJsonObject().get("id").getAsString();
                            Channel one = this.channelDao.findOne(Integer.valueOf(channelId));
                            if(!"0".equals(one.getType())){
                                continue;
                            }
                            GoodsChannel byGoodsIdAndChannelId = this.goodsChannelDao.findByGoodsIdAndChannelId(goodsId, Integer.valueOf(channelId));
                            if (0 == Integer.parseInt(useCount) || ValueUtil.notEmpity(byGoodsIdAndChannelId)) {
                                arr.remove(i);
                            }
                            if(arr.size()==0){
                                ValueUtil.isError("该商品无库存或无可下发的渠道");
                            }
                            return arr;
                        }
                        String useCount = arr.get(i).getAsJsonObject().get("useCount").getAsString();
                        String channelId = arr.get(i).getAsJsonObject().get("channel").getAsJsonObject().get("id").getAsString();
                        Channel one = this.channelDao.findOne(Integer.valueOf(channelId));
                        if(one==null){
                            continue;
                        }
                        if(!"0".equals(one.getType())){
                            continue;
                        }
                        GoodsChannel byGoodsIdAndChannelId = this.goodsChannelDao.findByGoodsIdAndChannelId(goodsId, Integer.valueOf(channelId));
                        if (0 == Integer.parseInt(useCount) || ValueUtil.notEmpity(byGoodsIdAndChannelId)) {
                            arr.remove(i);
                            if(i == (length-1)){
                                break;
                            }
                            i--;
                        }
                    }
                    if(arr.size()==0){
                        ValueUtil.isError("该商品无库存或无可下发的渠道");
                    }
                    return arr;
                }
//                Integer[] inventorys = null;
//                for (Integer inventory : inventorys) {
//                    if(0 == inventory){
//                        return "库存不足，此商品不可做为普通商品下发";
//                    }else {
//                        return "";//TODO
//                    }
//                }

            }else {//非单品

                JsonParser jsonParser = new JsonParser();

                List<GoodsSku> goodsSku = goods.getGoodsSku();

                for (int i = 0; i < goodsSku.size(); i++) {
                    String skuId = goodsSku.get(i).getSkuId().toString();
//                JsonArray arr = jsonParser.parse(goods.getSkuIdString()).getAsJsonArray();
//                for (int i = 0; i < arr.size(); i++) {
//                    String skuId = arr.get(i).getAsJsonObject().get("skuId").getAsString();
                    //根据skuId查询所有渠道的库存
                    String result = this.goodsChannelService.http(skuId);
                    String fromJson = ValueUtil.getFromJson(result, "data");
                    JsonParser jsonParser2 = new JsonParser();
                    JsonArray arr2 = jsonParser2.parse(fromJson).getAsJsonArray();
                    int length = arr2.size();
                    for (int j = 0; j < length; j++) {
                        if(0 == arr2.size()){
                            break;
                        }
                        if ( 1 == arr2.size()) {
                            String useCount = arr2.get(0).getAsJsonObject().get("useCount").getAsString();
                            String channelId = arr2.get(0).getAsJsonObject().get("channel").getAsJsonObject().get("id").getAsString();
                            Channel one = this.channelDao.findOne(Integer.valueOf(channelId));
                            if(!"0".equals(one.getType())){
                                continue;
                            }
                            GoodsChannel byGoodsIdAndChannelId = this.goodsChannelDao.findByGoodsIdAndChannelId(goodsId, Integer.valueOf(channelId));
                            if (0 == Integer.parseInt(useCount) || ValueUtil.notEmpity(byGoodsIdAndChannelId)) {
                                arr2.remove(i);
                            }
                            return arr2;
                        }
                        String useCount = arr2.get(j).getAsJsonObject().get("useCount").getAsString();
                        String channelId = arr2.get(j).getAsJsonObject().get("channel").getAsJsonObject().get("id").getAsString();
                        Channel one = this.channelDao.findOne(Integer.valueOf(channelId));
                        if(!"0".equals(one.getType())){
                            continue;
                        }
                        GoodsChannel byGoodsIdAndChannelId = this.goodsChannelDao.findByGoodsIdAndChannelId(goodsId, Integer.valueOf(channelId));
                        if(0 == Integer.parseInt(useCount) || ValueUtil.notEmpity(byGoodsIdAndChannelId)){
                            arr2.remove(j);
                            if(j == (length-1)){
                                break;
                            }
                            j--;
                        }
                    }
                    return arr2;
//                    Integer[] inventorys = null;
//                    for (Integer inventory : inventorys) {
//                        if(0 == inventory){
//                            return "库存不足，此商品不可做为普通商品下发";
//                        }else {
//                            return "";//TODO
//                        }
//                    }
                }
            }
        }else {
            return "参数错误！";
        }
        return "系统异常，稍后再试！";
    }
}
