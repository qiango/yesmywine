package com.yesmywine.logistics.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yesmywine.logistics.dao.ExpressRuleDao;
import com.yesmywine.logistics.dao.LogisticsRuleDao;
import com.yesmywine.logistics.dao.ShipperDao;
import com.yesmywine.logistics.entity.ExpressRule;
import com.yesmywine.logistics.entity.LogisticsRule;
import com.yesmywine.logistics.entity.Shippers;
import com.yesmywine.logistics.service.CostCalculationService;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangdiandian on 2017/3/30.
 */
@Service
public class CostCalculationImpl implements CostCalculationService {
    @Autowired
    private ShipperDao shipperDao;
    @Autowired
    private ExpressRuleDao expressRuleDao;
    @Autowired
    private LogisticsRuleDao logisticsRuleDao;
//        1.包裹总箱数  packageCount
//        2.包裹总重量  packageWeight
//        3.包裹金额   packageAmount
//        4.代收金额（货到付款部分的金额） collectionAmount
//        5.承运商类型（快递、物流）shipperType  express快递,logistics物流
//        6.仓库（快递需要，物流不需要）warehouseCode
//        7.配送区域 distributionArea
//        8.发货or退货 status(delivery,return)
//        9.承运商Code(shipperCode)
//       10.是否保价费（isInitialPremium）
        //运费:freight
        //代收手续费:collectionFee
        //POS机代收手续费:posCollectionFee
        //保价费:insuredFee
        //退货产生的费用:returnFee
    public Map<String, Object> costCalculation(String json) throws yesmywineException {//物流费计算
        JsonParser jsonParser = new JsonParser();
        JSONArray array = JSON.parseArray(json);
        JsonArray arr = jsonParser.parse(json).getAsJsonArray();
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < arr.size(); i++) {
            String shipperCode = arr.get(i).getAsJsonObject().get("shipperCode").getAsString();
            Shippers  shp=shipperDao.findByShipperCodeAndDeleteEnum(shipperCode,0);
//          物流  -运费：送货费 + Max(包裹总重量 * 费率, 最低收费)
//                -如发生代收行为(没有被拒收)，再加代收手续费：Max(代收金额 * 代收费率, 最低代收费)
//                -如发生pos代收行为(没有被拒收)，再加POS机代收手续费：代收金额 *  POS机费率
//                -如需保价，再加保价费：包裹金额 >= 开始保价费 ? Max( 包裹金额 * 保价费率，最低保价费率) : 0
            Integer shipperType= shp.getShipperType();
            if (shipperType==1) {//物流
                String distributionArea = arr.get(i).getAsJsonObject().get("distributionArea").getAsString();
                LogisticsRule logisticsRule = logisticsRuleDao.findByAreaNameContainingAndDeleteEnumAndShipperId(distributionArea, 0,shp.getId());
                Double deliveryCharge = logisticsRule.getDeliveryCharge();//送货费
                Double packageWeight = arr.get(i).getAsJsonObject().get("packageWeight").getAsDouble();//包裹总重量
                Double minimumCharge = shp.getMinimumCharge();//最低收费
                Double rate = logisticsRule.getRate();//费率
                Double max = Math.max(packageWeight * rate, minimumCharge);//Max(包裹总重量 * 费率, 最低收费)
                Double freight = deliveryCharge + max;
                map.put("freight", freight);
                JsonElement collectionAmount1 = arr.get(i).getAsJsonObject().get("collectionAmount");
                if (collectionAmount1 != null) {//代收金额
                    Double collectionAmount = arr.get(i).getAsJsonObject().get("collectionAmount").getAsDouble();//代收金额
                    Double collectingRate = shp.getCollectingRate();//代收费率
                    Double lowestCollecting = shp.getLowestCollecting();//最低代收费
                    Double collectionFee = Math.max(collectionAmount * collectingRate, lowestCollecting);// Max(代收金额 * 代收费率, 最低代收费)
                    map.put("collectionFee", collectionFee);
                    Double posRate = shp.getPosRate();//POS机费率
                    Double posCollectionFee = collectionAmount * posRate;
                    map.put("posCollectionFee", posCollectionFee);
                }
                JsonElement packageAmount1 = arr.get(i).getAsJsonObject().get("packageAmount");
                if (packageAmount1 != null) {//包裹金额
                    Double packageAmount = arr.get(i).getAsJsonObject().get("packageAmount").getAsDouble();//包裹金额
                    Double initialPremium = shp.getInitialPremium();//开始保价费
                    Double insuredRate = shp.getInsuredRate();//保价费率
                    Double lowestInsuredRate = shp.getInsuredRate();//最低保价费率
                    //如需保价，再加保价费：包裹金额 >= 开始保价费 ? Max( 包裹金额 * 保价费率，最低保价费率) : 0
                    Double insuredFee = packageAmount >= initialPremium ? Math.max(packageAmount * insuredRate, lowestInsuredRate) : 0;
                    map.put("insuredFee", insuredFee);
                }
            }else{//快递
                /// （快递）按箱 -运费：  首箱费率 +（包裹总箱数 - 1）* 次箱费率
                String distributionArea = arr.get(i).getAsJsonObject().get("distributionArea").getAsString();
                String warehouseCode = arr.get(i).getAsJsonObject().get("warehouseCode").getAsString();
                ExpressRule expressRule = expressRuleDao.findByAreaNameContainingAndDeleteEnumAndWarehouseCodeAndTypeAndShipperId(distributionArea, 0,warehouseCode,0,shp.getId());
                ValueUtil.verifyNotExist(expressRule,"此地区暂无按箱配送规则");
                Double firstRate=expressRule.getFirstRate();//首箱费率
                Integer packageCount = arr.get(i).getAsJsonObject().get("packageCount").getAsInt();//包裹总箱数
                Double secondRate=expressRule.getSecondRate();//次箱费率
                //（快递）按箱 -运费：
                Double boxfreight=firstRate+(packageCount-1)*secondRate;//首箱费率 +（包裹总箱数 - 1）* 次箱费率

                //  (快递）按重量-运费：  首重费率 + Max(0, Ceiling((包裹总重量 - 首重重量) / 续重重量)) * 续重费率
                Double packageWeight = arr.get(i).getAsJsonObject().get("packageWeight").getAsDouble();//包裹总重量
                ExpressRule expressRule2 = expressRuleDao.findByAreaNameContainingAndDeleteEnumAndWarehouseCodeAndTypeAndShipperId(distributionArea, 0,warehouseCode,1,shp.getId());
                ValueUtil.verifyNotExist(expressRule2,"此地区暂无按重配送规则");
                Double firstRate2=expressRule2.getFirstRate();//首重费率
                Double firstWeight2=expressRule2.getFirstWeight();//首重重量（元）
                Double secondWeight2=expressRule2.getSecondWeight();//续重重量（元）
                Double secondRate2=expressRule2.getSecondRate();///续重费率（元）
                //  (快递）按重量-运费
                Double weightfreight=firstRate2+Math.max(0,Math.ceil((packageWeight-firstWeight2)/secondWeight2))*secondRate2;//首重费率 + Max(0, Ceiling((包裹总重量 - 首重重量) / 续重重量)) * 续重费率
                    if(boxfreight<weightfreight){
                        //   （快递）按箱 -运费：  首箱费率 +（包裹总箱数 - 1）* 次箱费率
//                -如发生代收行为(没有被拒收)，再加代收手续费:   Max(代收金额 * 代收费率, 最低代收费)
//                -如发生pos代收行为(没有被拒收)，再加POS机代收手续费：  代收金额 *  POS机费率     
//                -如需保价，再加保价费：包裹金额 >= 开始保价费 ?   Max( 包裹金额 * 保价费率，最低保价费率) : 0
//                -退货产生的费用：首箱退费率 +  （包裹总箱数 -1） * 次箱退费率
                        map.put("freight", boxfreight);
                        JsonElement collectionAmount1 = arr.get(i).getAsJsonObject().get("collectionAmount");
                        if (collectionAmount1 != null) {//代收金额
                            Double collectionAmount = arr.get(i).getAsJsonObject().get("collectionAmount").getAsDouble();//代收金额
                            Double collectingRate = shp.getCollectingRate();//代收费率
                            Double lowestCollecting = shp.getLowestCollecting();//最低代收费
                            Double collectionFee = Math.max(collectionAmount * collectingRate, lowestCollecting);// Max(代收金额 * 代收费率, 最低代收费)
                            map.put("collectionFee", collectionFee);// Max(代收金额 * 代收费率, 最低代收费)
                            Double posRate = shp.getPosRate();//POS机费率
                            Double posCollectionFee = collectionAmount * posRate;//代收金额 *POS机费率 
                            map.put("posCollectionFee", posCollectionFee);
                        }
                        JsonElement isInitialPremium = arr.get(i).getAsJsonObject().get("isInitialPremium");
                        if (isInitialPremium .equals("yes")) {
                            Double packageAmount = arr.get(i).getAsJsonObject().get("packageAmount").getAsDouble();//包裹金额
                            Double initialPremium = shp.getInitialPremium();//开始保价费
                            Double insuredRate = shp.getInsuredRate();//保价费率
                            Double lowestInsuredRate = shp.getInsuredRate();//最低保价费率
                            //包裹金额 >= 开始保价费 ?   Max( 包裹金额 * 保价费率，最低保价费率) : 0
                            Double insuredFee = packageAmount >= initialPremium ? Math.max(packageAmount * insuredRate, lowestInsuredRate) : 0;
                            map.put("insuredFee", insuredFee);
                        }
                        JsonElement status1 = arr.get(i).getAsJsonObject().get("status");
                        if(status1!=null){
                            String status = arr.get(i).getAsJsonObject().get("status").getAsString();
                            if(status.equals("return")){
                                Double firstRefundRate=expressRule.getFirstRefundRate();//首箱退费率（元）
                                Double secondRefundRate=expressRule.getSecondRefundRate();//次箱退费率（元）
                                // -退货产生的费用：首箱退费率 +  （包裹总箱数 -1） * 次箱退费率
                                Double returnFee=firstRefundRate+(packageCount-1)*secondRefundRate;
                                map.put("returnFee", returnFee);
                            }
                        }
                    }else{
                        //  (快递）按重量-运费：  首重费率 + Max(0, Ceiling((包裹总重量 - 首重重量) / 续重重量)) * 续重费率
//               -如发生代收行为(没有被拒收)，再加代收手续费:   Max(代收金额 * 代收费率, 最低代收费)
//               -如发生pos代收行为(没有被拒收)，再加POS机代收手续费：  代收金额 *  POS机费率
//               -如需保价，再加保价费：{包裹金额}>=开始保价费 ?   Max( 包裹金额 * 保价费率，最低保价费率) : 0
//               -退货产生的费用: 首重退费率 +   Max(0,Ceiling((包裹总重量 - 首重重量) / 续重重量)) * 续重退费率
                        map.put("freight", weightfreight);
                        JsonElement collectionAmount1 = arr.get(i).getAsJsonObject().get("collectionAmount");
                        if (collectionAmount1 != null) {//代收金额
                            Double collectionAmount = arr.get(i).getAsJsonObject().get("collectionAmount").getAsDouble();//代收金额
                            Double collectingRate = shp.getCollectingRate();//代收费率
                            Double lowestCollecting = shp.getLowestCollecting();//最低代收费
                            Double collectionFee = Math.max(collectionAmount * collectingRate, lowestCollecting);// Max(代收金额 * 代收费率, 最低代收费)
                            map.put("collectionFee", collectionFee);// Max(代收金额 * 代收费率, 最低代收费)
                            Double posRate = shp.getPosRate();//POS机费率
                            Double posCollectionFee = collectionAmount * posRate;//代收金额 *POS机费率 
                            map.put("posCollectionFee", posCollectionFee);
                        }
                        JsonElement isInitialPremium = arr.get(i).getAsJsonObject().get("isInitialPremium");
                        if (isInitialPremium .equals("yes")) {
                            Double packageAmount = arr.get(i).getAsJsonObject().get("packageAmount").getAsDouble();//包裹金额
                            Double initialPremium = shp.getInitialPremium();//开始保价费
                            Double insuredRate = shp.getInsuredRate();//保价费率
                            Double lowestInsuredRate = shp.getInsuredRate();//最低保价费率
                            //包裹金额 >= 开始保价费 ?   Max( 包裹金额 * 保价费率，最低保价费率) : 0
                            Double insuredFee = packageAmount >= initialPremium ? Math.max(packageAmount * insuredRate, lowestInsuredRate) : 0;
                            map.put("insuredFee", insuredFee);
                        }
                        JsonElement status1 = arr.get(i).getAsJsonObject().get("status");
//               -退货产生的费用: 首重退费率 +   Max(0,Ceiling((包裹总重量 - 首重重量) / 续重重量)) * 续重退费率
                        if(status1!=null){
                            String status = arr.get(i).getAsJsonObject().get("status").getAsString();
                            if(status.equals("return")){
                                Double firstRefundRate=expressRule.getFirstRefundRate();///首重退费率（元）
                                Double secondRefundRate=expressRule.getSecondRefundRate();//续重退费率（元）
                                Double returnFee=firstRefundRate+Math.max(0,Math.ceil((packageWeight-firstWeight2)/secondWeight2))*secondRefundRate;
                                map.put("returnFee", returnFee);
                            }
                        }
                    }
                    }
                }
            return map;
    }
}