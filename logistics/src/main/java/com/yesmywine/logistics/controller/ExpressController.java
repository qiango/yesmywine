//package com.hzbuvi.logistics.controller;
//
//
//import LogisticsRuleService;
//import com.hzbuvi.logistics.vientiane.*;
//
//import ValueUtil;
//import yesmywineException;
//import org.apache.http.HttpStatus;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
///**
// * Created by wangdiandian on 2017/5/12.
// */
//@RestController
//@RequestMapping("/logistics/express")
//public class ExpressController {
//
//    @Autowired
//    private LogisticsRuleService logisticsRuleService;
//
//    @RequestMapping(method = RequestMethod.POST)
//    public String create(@RequestParam Map<String, String> param) {//宅急送物流信息
//        try {
//            return ValueUtil.toJson(HttpStatus.SC_CREATED, logisticsRuleService.addLogisticsRule(param));
//        } catch (yesmywineException e) {
//            return ValueUtil.toError(e.getCode(),e.getMessage());
//        }
//    }
//    public static void main(String[] args) throws Exception {
//
//        EwinshineInte ewinshineInte = new EwinshineInte();
//        EwinshineIntePortType ewinshineInteHttpPort = ewinshineInte.getEwinshineInteHttpPort();
//        String result = ewinshineInteHttpPort.getSuccSendOrderInfo("万象物流", "21285286", "2445");
//        System.out.print(result);
//
////        ZJSTracking zjsTracking = new ZJSTracking();
////        ZJSTrackingSoap zjsTrackingSoap = zjsTracking.getZJSTrackingSoap();
////        String s = zjsTrackingSoap.get("MuShiMaoYi", "ddd", "17D09187-CB5D-4C83-8D42-1B32C1BE03A2");
////        Get get = new Get();
////        GetResponse getResponse = new GetResponse();
////        String getResult = getResponse.getGetResult();
////
////        System.out.print(s);
//
//
//    }
//}
