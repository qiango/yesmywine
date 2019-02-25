package com.yesmywine.logistics.service.Impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.logistics.common.SynchronizeLogistics;
import com.yesmywine.logistics.dao.ExpressRuleDao;
import com.yesmywine.logistics.dao.LogisticsRuleDao;
import com.yesmywine.logistics.dao.ShipperDao;
import com.yesmywine.logistics.entity.ExpressRule;
import com.yesmywine.logistics.entity.LogisticsRule;
import com.yesmywine.logistics.entity.Shippers;
import com.yesmywine.logistics.service.ShipperService;
import com.yesmywine.logistics.service.ThirdShipperService;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.SynchronizeUtils;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangdiandian on 2017/3/27.
 */
@Service
@Transactional
public class ShipperImpl extends BaseServiceImpl<Shippers,Integer> implements ShipperService {

    @Autowired
    private ShipperDao shipperDao;
    @Autowired
    private ExpressRuleDao expressRuleDao;
    @Autowired
    private LogisticsRuleDao logisticsRuleDao;
    @Autowired
    private ThirdShipperService thirdShipperService;

    public String addShipper(Map<String, String> param) throws yesmywineException {//新增承运商
        ValueUtil.verify(param, new String[]{"shipperName","shipperCode",
                "shipperType", "collectingRate", "lowestCollecting", "posRate",
                "initialPremium", "insuredRate", "lowestInsuredRate", "status"});

        String shipperName=param.get("shipperName");
        Shippers shippers1=shipperDao.findByShipperNameAndDeleteEnum(shipperName,0);
        if(shippers1!=null){
            ValueUtil.isError("承运商名称已存在");
        }
//        String shipperCode = Encode.getSalt(3);//生成承运商编码
        String shipperCode= param.get("shipperCode");
        Shippers shippers2=shipperDao.findByShipperCodeAndDeleteEnum(shipperCode,0);
        if(shippers2!=null){
            ValueUtil.isError("承运商编码已存在");
        }
        Shippers shippers = new Shippers();
        shippers.setShipperName(param.get("shipperName"));
        shippers.setShipperCode(shipperCode);
        shippers.setDepict(param.get("depict"));
        String shipperType = param.get("shipperType");
        switch (shipperType) {
            case "0":
                shippers.setShipperType(0);
                break;
            default:
                shippers.setShipperType(1);
                shippers.setMinimumCharge(Double.valueOf(param.get("minimumCharge")));
                break;
        }
        shippers.setCollectingRate(Double.valueOf(param.get("collectingRate")));
        shippers.setLowestCollecting(Double.valueOf(param.get("lowestCollecting")));
        shippers.setPosRate(Double.valueOf(param.get("posRate")));
        shippers.setInitialPremium(Double.valueOf(param.get("initialPremium")));
        shippers.setInsuredRate(Double.valueOf(param.get("insuredRate")));
        shippers.setLowestInsuredRate(Double.valueOf(param.get("lowestInsuredRate")));
        String status = param.get("status");
        shippers.setStatus(Integer.valueOf(status));
        shippers.setDeleteEnum(0);
        shipperDao.save(shippers);
        //向OMS同步承运商信息
        sendToOMSShipper(shippers,0);

        String result = SynchronizeLogistics.create(ValueUtil.toJson(HttpStatus.SC_CREATED, shippers));
        if (result == null || !result.equals("201")) {
            TransactionAspectSupport.currentTransactionStatus().isRollbackOnly();
            //删除之前同步到oms的承运商
            sendToOMSShipper(shippers,2);
            ValueUtil.isError("同步失败！");
        }


        return  "success";
    }

    public Map<String, Object> updateLoad(Integer id) throws yesmywineException {//加载显示承运商
        ValueUtil.verify(id, "idNull");
        Shippers shippers = shipperDao.findOne(id);
        Map<String, Object> map = new HashMap<>();
        map.put("Shippers", shippers);
        String shipperType=shippers.getShipperType().toString();
        if(shipperType.equals("express")){
            List<ExpressRule> expressRule = expressRuleDao.findByShipperIdAndDeleteEnum(shippers.getId(),0);
            map.put("ExpressRule", expressRule);
        }else {
            List<LogisticsRule> logisticsRules=logisticsRuleDao.findByShipperIdAndDeleteEnum(shippers.getId(),0);
            map.put("LogisticsRules", logisticsRules);
        }
        return map;
    }
    public String delete(Integer id) throws yesmywineException {//删除承运商
        ValueUtil.verify(id, "idNull");
        //承运商在启用状态下不可删除。
        //承运商在停用状态下，但该承运商已经使用或已经配了规则，不可删除。
        Shippers shippers =shipperDao.findOne(id);
        String status=shippers.getStatus().toString();
        if(status.equals("1")){
            ValueUtil.isError("承运商已启用");
        }else {
            List<ExpressRule> expressBoxRule= expressRuleDao.findByShipperIdAndDeleteEnum(shippers.getId(),0);
            List<LogisticsRule> logisticsRules=logisticsRuleDao.findByShipperIdAndDeleteEnum(shippers.getId(),0);
            if(expressBoxRule.size()!=0){
                ValueUtil.isError("快递规则被使用,不可删除");
            }else if(logisticsRules.size()!=0){
                ValueUtil.isError("物流规则被使用,不可删除");
            }
        }
        shippers.setDeleteEnum(1);
        thirdShipperService.deleteByShipperId(shippers.getId());
        shipperDao.save(shippers);

        //向oms同步删除的承运商
        sendToOMSShipper(shippers,2);

        String result = SynchronizeLogistics.delete(ValueUtil.toJson(HttpStatus.SC_CREATED, shippers));
        if (result == null || !result.equals("204")) {
            TransactionAspectSupport.currentTransactionStatus().isRollbackOnly();
            //回滚oms之前的删除的承运商
            sendToOMSShipper(shipperDao.findOne(id),0);
            ValueUtil.isError("承运商创建失败：无法同步在商城中创建承运商！");
        }
        return "success";
    }


    public String updateSave(Map<String, String> param) throws yesmywineException {//修改保存承运商
        Integer shipperId = Integer.parseInt(param.get("id"));
        String shipperName=param.get("shipperName");
        Shippers shippers1=shipperDao.findByShipperNameAndDeleteEnumAndIdNot(shipperName,0,shipperId);
        if(shippers1!=null){
            ValueUtil.isError("承运商名称已存在");
        }
        Shippers shippers = shipperDao.findOne(shipperId);
        shippers.setShipperName(param.get("shipperName"));
        shippers.setDepict(param.get("depict"));
        String shipperType = param.get("shipperType");
        switch (shipperType) {
            case "0":
                shippers.setShipperType(0);
                break;
            default:
                shippers.setShipperType(1);
                shippers.setMinimumCharge(Double.valueOf(param.get("minimumCharge")));
                break;
        }
        shippers.setCollectingRate(Double.valueOf(param.get("collectingRate")));
        shippers.setLowestCollecting(Double.valueOf(param.get("lowestCollecting")));
        shippers.setPosRate(Double.valueOf(param.get("posRate")));
        shippers.setInitialPremium(Double.valueOf(param.get("initialPremium")));
        shippers.setInsuredRate(Double.valueOf(param.get("insuredRate")));
        shippers.setLowestInsuredRate(Double.valueOf(param.get("lowestInsuredRate")));
        shippers.setDeleteEnum(0);
        shipperDao.save(shippers);

        //向OMS同步修改后的承运商信息
        sendToOMSShipper(shippers,1);

        String result = SynchronizeLogistics.create(ValueUtil.toJson(HttpStatus.SC_CREATED, shippers));
        if (result == null || !result.equals("201")) {
            TransactionAspectSupport.currentTransactionStatus().isRollbackOnly();
            //还原OMS到之前的承运商信息
            sendToOMSShipper(shipperDao.findOne(shipperId),1);
            ValueUtil.isError("承运商创建失败：无法同步在商城中创建承运商！");
        }
        return "success";
    }
    public String updateStatus(Map<String, String> param) throws yesmywineException {//修改承运商状态
        Integer shipperId = Integer.parseInt(param.get("id"));
        Integer status = Integer.parseInt(param.get("status"));

        Shippers shippers=shipperDao.findOne(shipperId);
        shippers.setStatus(status);
        shipperDao.save(shippers);
        String result = SynchronizeLogistics.create(ValueUtil.toJson(HttpStatus.SC_CREATED, shippers));
        if (result == null || !result.equals("201")) {
            TransactionAspectSupport.currentTransactionStatus().isRollbackOnly();
            ValueUtil.isError("承运商创建失败：无法同步在商城中创建承运商！");
        }
        return "success";
    }

    public static void sendToOMSShipper(Shippers shippers,Integer status) throws yesmywineException {//status 0-增  1-改  2-删
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
        if(result!=null) {
            String respStatus = ValueUtil.getFromJson(result,"status");
            String message = JSON.parseObject(result).getString("message");
            if(!respStatus.equals("success")){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("向OMS同步承运商失败，原因："+message);
            }
        }else{
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("向OMS同步承运商失败");
        }
    }
}

