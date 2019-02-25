package com.yesmywine.logistics.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.logistics.entity.Shippers;
import com.yesmywine.logistics.service.ShipperService;
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
    private ShipperService shipperService;

    @RequestMapping
    public String test(){
       List<Shippers> supplierList =  shipperService.findAll();
       for(Shippers shippers:supplierList){
           try {
               sendToOMSShipper(shippers,0);
           } catch (yesmywineException e) {
               e.printStackTrace();
           }
       }

        return null;
    }

    public static void sendToOMSShipper(Shippers shippers, Integer status) throws yesmywineException {//status 0-增  1-改  2-删
        JSONObject requestJson = new JSONObject();
        requestJson.put("function",status);
        JSONObject dataJson = new JSONObject();
        dataJson.put("carrierCode",shippers.getShipperCode());
        dataJson.put("carrierName",shippers.getShipperName());
        Integer type = shippers.getShipperType();
        switch (type){
            case 0 :
                dataJson.put("carrierType","快递");
                break;
            case 1:
                dataJson.put("carrierType","物流");
                break;
        }

        switch (shippers.getStatus()){
            case 0 :
                dataJson.put("carrierstatus","生效");
                break;
            case 1:
                dataJson.put("carrierstatus","失效");
                break;
        }


        requestJson.put("data",dataJson);
        //向oms同步承运商信息
        String  result = SynchronizeUtils.getOmsResult(Dictionary.OMS_HOST,"/updateBaseCarrier", RequestMethod.post,"",requestJson.toJSONString());
    }
}
