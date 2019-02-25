package com.yesmywine.logistics.service.Impl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.logistics.dao.AreaDao;
import com.yesmywine.logistics.dao.LogisticsRuleDao;
import com.yesmywine.logistics.dao.ShipperDao;
import com.yesmywine.logistics.entity.Area;
import com.yesmywine.logistics.entity.LogisticsRule;
import com.yesmywine.logistics.entity.Shippers;
import com.yesmywine.logistics.service.LogisticsRuleService;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangdiandian on 2017/3/30.
 */
@Service
public class LogisticsRuleImpl  extends BaseServiceImpl<LogisticsRule,Integer> implements LogisticsRuleService {
    @Autowired
    private LogisticsRuleDao logisticsRuleDao;
    @Autowired
    private ShipperDao shipperDao;
    @Autowired
    private AreaDao areaDao;

    public String addLogisticsRule(Map<String, String> param) throws yesmywineException {//新增物流规则
        ValueUtil.verify(param, new String[]{"distributionAreaName",
                "distributionArea", "deliveryCharge", "rate", "shipperId"});
        String shipperId=param.get("shipperId");
        String distributionArea=param.get("distributionArea");
        if (distributionArea.contains(";")) {
            ValueUtil.isError("每条规则只能添加一个区域");
        }
        List<LogisticsRule> logisticsRules=logisticsRuleDao.findByShipperIdAndDeleteEnum(Integer.valueOf(shipperId),0);
//       if(logisticsRules!=null){
//           for(int i = 0; i<logisticsRules.size(); i++){
//               String area=logisticsRules.get(i).getDistributionArea();
//               String[] s = area.split(";");
//               for (int j = 0; j< s.length; j++) {
//                   System.out.println(s[j]);
//                   String[] x = distributionArea.split(";");
//                   for (int k = 0; k < x.length; k++) {
//                       if(s[j].equals(x[k])){
//                           StringBuffer sb = new StringBuffer();
//                           String[] y = x[k].split(",");
//                           for (int l = 0; l < y.length; l++) {
//                               Area area1=areaDao.findByAreaNo(Integer.valueOf(y[l]));
//                               sb.append(area1.getCityName());
//                           }
//                           ValueUtil.isError( ""+sb.toString()+"已添加");
//                       }
//                   }
//               }
//           }
//       }
        if(logisticsRules!=null) {
            for (int i = 0; i < logisticsRules.size(); i++) {
                String area = logisticsRules.get(i).getDistributionArea();
                StringBuffer sb = new StringBuffer();
                String[] B = distributionArea.split(",");
                if (area.equals(distributionArea)) {
                    for (int l = 0; l < B.length; l++) {
                        Area area1 = areaDao.findByAreaNo(Integer.valueOf(B[l]));
                        sb.append(area1.getCityName());
                    }
                    ValueUtil.isError("" + sb.toString() + "已添加");
                }
            }
        }
        LogisticsRule logisticsRule = new LogisticsRule();
        logisticsRule.setDistributionAreaName(param.get("distributionAreaName"));
        logisticsRule.setDistributionArea(param.get("distributionArea"));
        logisticsRule.setAreaName(param.get("areaName"));
        logisticsRule.setDeliveryCharge(Double.valueOf(param.get("deliveryCharge")));
        logisticsRule.setRate(Double.valueOf(param.get("rate")));
        logisticsRule.setDeleteEnum(0);
        logisticsRule.setShipperId(Integer.valueOf(param.get("shipperId")));
        logisticsRuleDao.save(logisticsRule);
         return "success";
        }

    public Map<String, Object> updateLoad(Integer id) throws yesmywineException {//加载显示新增物流规则
        ValueUtil.verify(id, "idNull");
        Map<String, Object> map = new HashMap<>();
        LogisticsRule logisticsRule = logisticsRuleDao.findOne(id);
           map.put("logisticsRule",logisticsRule);
        return  map;
    }

    public String delete(Integer id) throws yesmywineException {//删除新增物流规则
        ValueUtil.verify(id, "idNull");
        LogisticsRule logisticsRule = logisticsRuleDao.findOne(id);
        Shippers shippers=shipperDao.findOne(Integer.valueOf(logisticsRule.getShipperId()));
        if(shippers.getStatus()==1){//承运商被停用了才能修改规则信息
            ValueUtil.isError( "承运商已启用");
        }
        logisticsRule.setDeleteEnum(1);
        logisticsRuleDao.save(logisticsRule);
        return  "success";
    }

    public String updateSave(Map<String, String> param) throws yesmywineException {//修改保存新增物流规则
        ValueUtil.verify(param, new String[]{"distributionAreaName",
                "distributionArea", "deliveryCharge", "rate", "shipperId","id"});
        String shipperId=param.get("shipperId");
        String id=param.get("id");
        String distributionArea=param.get("distributionArea");
        if (distributionArea.contains(";")) {
            ValueUtil.isError("每条规则只能添加一个区域");
        }
        List<LogisticsRule> logisticsRules=logisticsRuleDao.findByShipperIdAndDeleteEnumAndIdNot(Integer.valueOf(shipperId),0,Integer.valueOf(id));
//        if(logisticsRules!=null){
//            for(int i = 0; i<logisticsRules.size(); i++){
//                String area=logisticsRules.get(i).getDistributionArea();
//                String[] s = area.split(";");
//                for (int j = 0; j< s.length; j++) {
//                    System.out.println(s[j]);
//                    String[] x = distributionArea.split(";");
//                    for (int k = 0; k < x.length; k++) {
//                        if(s[j].equals(x[k])){
//                            StringBuffer sb = new StringBuffer();
//                            String[] y = x[k].split(",");
//                            for (int l = 0; l < y.length; l++) {
//                                Area area1=areaDao.findByAreaNo(Integer.valueOf(y[l]));
//                                sb.append(area1.getCityName());
//                            }
//                            ValueUtil.isError( ""+sb.toString()+"已添加");
//                        }
//                    }
//                }
//            }
//        }
        if(logisticsRules!=null) {
            for (int i = 0; i < logisticsRules.size(); i++) {
                String area = logisticsRules.get(i).getDistributionArea();
                StringBuffer sb = new StringBuffer();
                String[] B = distributionArea.split(",");
                if (area.equals(distributionArea)) {
                    for (int l = 0; l < B.length; l++) {
                        Area area1 = areaDao.findByAreaNo(Integer.valueOf(B[l]));
                        sb.append(area1.getCityName());
                    }
                    ValueUtil.isError("" + sb.toString() + "已添加");
                }
            }
        }
        Integer logisticsRuleId = Integer.parseInt(param.get("id"));
        LogisticsRule logisticsRule = logisticsRuleDao.findOne(logisticsRuleId);
        Shippers shippers=shipperDao.findOne(logisticsRule.getShipperId());
        if(shippers.getStatus()==1){//承运商被停用了才能修改规则信息
            ValueUtil.isError( "承运商已启用");
        }
        logisticsRule.setDistributionAreaName(param.get("distributionAreaName"));
        logisticsRule.setDistributionArea(param.get("distributionArea"));
        logisticsRule.setAreaName(param.get("areaName"));
        logisticsRule.setDeliveryCharge(Double.valueOf(param.get("deliveryCharge")));
        logisticsRule.setRate(Double.valueOf(param.get("rate")));
        logisticsRule.setDeleteEnum(0);
        logisticsRule.setShipperId(Integer.valueOf(param.get("shipperId")));
        logisticsRuleDao.save(logisticsRule);
        return "success";
    }

    public String  logisticsRuleplus(String distributionArea,Integer shipperId) throws yesmywineException {
        List<LogisticsRule> logisticsRules = logisticsRuleDao.findByShipperIdAndDeleteEnum(shipperId, 0);
        if (logisticsRules != null) {
            for (int i = 0; i < logisticsRules.size(); i++) {
                String area = logisticsRules.get(i).getDistributionArea();
                String[] s = area.split(";");
                for (int j = 0; j < s.length; j++) {
                    System.out.println(s[j]);
                    String[] x = distributionArea.split(";");
                    for (int k = 0; k < x.length; k++) {
                        if (s[j].equals(x[k])) {
                            StringBuffer sb = new StringBuffer();
                            String[] y = x[k].split(",");
                            for (int l = 0; l < y.length; l++) {
                                Area area1 = areaDao.findByAreaNo(Integer.valueOf(y[l]));
                                sb.append(area1.getCityName());
                            }
                            ValueUtil.isError("" + sb.toString() + "已添加");
                        }
                    }
                }
            }
        }
        return "";
    }
    }
