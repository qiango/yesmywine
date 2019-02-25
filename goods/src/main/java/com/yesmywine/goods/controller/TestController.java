package com.yesmywine.goods.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.goods.bean.SupplierTypeEnum;
import com.yesmywine.goods.dao.CategoryDao;
import com.yesmywine.goods.dao.GoodsChannelDao;
import com.yesmywine.goods.dao.SkuDao;
import com.yesmywine.goods.entity.Goods;
import com.yesmywine.goods.entity.GoodsChannel;
import com.yesmywine.goods.entity.GoodsSku;
import com.yesmywine.goods.entity.Sku;
import com.yesmywine.goods.entityProperties.Category;
import com.yesmywine.goods.entityProperties.Supplier;
import com.yesmywine.goods.service.GoodsChannelService;
import com.yesmywine.goods.service.GoodsService;
import com.yesmywine.goods.service.SupplierService;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.SynchronizeUtils;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by by on 2017/8/30.
 */
@RestController
@RequestMapping("/test/itf")
public class TestController
{
    @Autowired
    private SupplierService supplierService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsChannelService goodsChannelService;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private GoodsChannelDao goodsChannelDao;
    @Autowired
    private CategoryDao categoryDao;

    @RequestMapping
    public String test(){
       List<Supplier> supplierList =  supplierService.findAll();
       for(Supplier channels:supplierList){
           try {
               sendToOMS(channels,0);
           } catch (yesmywineException e) {
               e.printStackTrace();
           }
       }

       List<Goods> goodsList = goodsService.findAll();
        for(Goods goods:goodsList){
            try {
                GoodsChannel goodsChannel = goodsChannelDao.findByGoodsId(goods.getId());
                if(goodsChannel!=null){
                    sendGoodsToOMS(goods,goodsChannel);
                }
            } catch (yesmywineException e) {
                e.printStackTrace();
            }
        }
        return null;
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
        return "SUCCESS";
    }


    public void sendToOMS(Supplier supplier,Integer status) throws yesmywineException {//status 0-增  1-改  2-删
        JSONObject requestJson = new JSONObject();
        requestJson.put("function",status);
        JSONObject dataJson = new JSONObject();
        dataJson.put("customerCode",supplier.getSupplierCode());
        dataJson.put("customerName",supplier.getSupplierName());
        SupplierTypeEnum type = supplier.getSupplierType();
        switch (type){
            case consignment :
                dataJson.put("customerType","代销");
                break;
            case distribution:
                dataJson.put("customerType","经销");
                break;
            default:
                dataJson.put("customerType","海淘");
                break;
        }
        dataJson.put("contactProvince",supplier.getProvince());
        dataJson.put("contactCity",supplier.getCity());
        dataJson.put("contactDistrict",supplier.getArea());
        dataJson.put("contactAddress",supplier.getAddress());
        dataJson.put("contactPostCode",supplier.getPostCode());
        dataJson.put("contactperson",supplier.getContact());
        dataJson.put("contactTelephone",supplier.getTelephone());
        dataJson.put("contactMobile",supplier.getMobilePhone());
        dataJson.put("contactFax",supplier.getFax());
        dataJson.put("contactEmail",supplier.getMailbox());
        dataJson.put("grade",supplier.getGrade());
        dataJson.put("accountNumber",supplier.getAccountNumber());
        dataJson.put("creditDegree",supplier.getCredit());
        dataJson.put("purchasingCycle",supplier.getProcurementCycl());
        switch (supplier.getPaymentType()){
            case "0" :
                dataJson.put("paymentMethod","支付宝");
                break;
            case "1":
                dataJson.put("paymentMethod","银联");
                break;
            case "2":
                dataJson.put("paymentMethod","直联");
                break;
            case "3":
                dataJson.put("paymentMethod","货到付款");
                break;
            default:
                dataJson.put("paymentMethod","后结帐");
                break;
        }
        dataJson.put("invoiceCompanyName",supplier.getInvoiceCompany());
        dataJson.put("primarySupplier",supplier.getPrimarySupplier());
        dataJson.put("merchantLogo",supplier.getMerchantIdentification());
        dataJson.put("productManager",supplier.getProductManager());
        dataJson.put("depositBank",supplier.getBank());
        dataJson.put("bankAccount",supplier.getBankAccount());
        dataJson.put("dutyParagraph",supplier.getDutyParagraph());
        dataJson.put("accountPeriod",supplier.getPaymentDays());
        requestJson.put("data",dataJson);
        //向oms同步供应商信息
        String  result = SynchronizeUtils.getOmsResult(Dictionary.OMS_HOST,"/updateBaseCustomerSupplier",RequestMethod.post,"",requestJson.toJSONString());
    }
}
