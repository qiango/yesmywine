package com.yesmywine.logistics.service.Impl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.logistics.dao.AreaDao;
import com.yesmywine.logistics.dao.ExpressRuleDao;
import com.yesmywine.logistics.dao.ShipperDao;
import com.yesmywine.logistics.entity.Area;
import com.yesmywine.logistics.entity.ExpressRule;
import com.yesmywine.logistics.entity.Shippers;
import com.yesmywine.logistics.service.ExpressRuleService;
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
public class ExpressRuleImpl extends BaseServiceImpl<ExpressRule,Integer> implements ExpressRuleService {

    @Autowired
    private ExpressRuleDao expressRuleDao;
    @Autowired
    private ShipperDao shipperDao;
    @Autowired
    private AreaDao areaDao;

    public String addExpressRule(Map<String, String> param) throws yesmywineException {//新增承运商费用(快递)规则
        String type = param.get("type");
        String warehouseId=param.get("warehouseId");
        List<ExpressRule> expressRules=expressRuleDao.findByWarehouseIdAndDeleteEnumAndType(Integer.valueOf(warehouseId),0,Integer.valueOf(type));
        String distributionArea=param.get("distributionArea");
        if (distributionArea.contains(";")) {
            ValueUtil.isError("每条规则只能添加一个区域");
        }
//        if(expressRules!=null){
//            for(int i = 0; i<expressRules.size(); i++){
//                String area=expressRules.get(i).getDistributionArea();
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
        if(expressRules!=null) {
            for (int i = 0; i < expressRules.size(); i++) {
                String area = expressRules.get(i).getDistributionArea();
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

        if (type.equals("0")) {//快递按箱规则
            ValueUtil.verify(param, new String[]{"type", "distributionAreaName",
                    "warehouseId", "distributionArea", "firstRate", "secondRate",
                    "firstRefundRate", "secondRefundRate", "shipperId"});

            ExpressRule expressRule = new ExpressRule();
            expressRule.setType(0);
            expressRule.setDistributionAreaName(param.get("distributionAreaName"));
            expressRule.setWarehouseId(Integer.valueOf(param.get("warehouseId")));
            expressRule.setDistributionArea(param.get("distributionArea"));
            expressRule.setAreaName(param.get("areaName"));
            expressRule.setFirstRate(Double.valueOf(param.get("firstRate")));
            expressRule.setSecondRate(Double.valueOf(param.get("secondRate")));
            expressRule.setFirstRefundRate(Double.valueOf(param.get("firstRefundRate")));
            expressRule.setSecondRefundRate(Double.valueOf(param.get("secondRefundRate")));
            expressRule.setDeleteEnum(0);
            expressRule.setShipperId(Integer.valueOf(param.get("shipperId")));
            expressRuleDao.save(expressRule);
            return "success";
        } else {//快递按重量规则
            ValueUtil.verify(param, new String[]{"type", "distributionAreaName",
                    "warehouseId", "distributionArea", "firstWeight", "firstRate",
                    "secondWeight", "secondRate", "firstRefundRate", "secondRefundRate", "shipperId"});
            ExpressRule expressRule = new ExpressRule();
            expressRule.setType(1);
            expressRule.setDistributionAreaName(param.get("distributionAreaName"));
            expressRule.setWarehouseId(Integer.valueOf(param.get("warehouseId")));
            expressRule.setDistributionArea(param.get("distributionArea"));
            expressRule.setAreaName(param.get("areaName"));
            expressRule.setFirstWeight(Double.valueOf(param.get("firstWeight")));
            expressRule.setFirstRate(Double.valueOf(param.get("firstRate")));
            expressRule.setSecondWeight(Double.valueOf(param.get("secondWeight")));
            expressRule.setSecondRate(Double.valueOf(param.get("secondRate")));
            expressRule.setFirstRefundRate(Double.valueOf(param.get("firstRefundRate")));
            expressRule.setSecondRefundRate(Double.valueOf(param.get("secondRefundRate")));
            expressRule.setDeleteEnum(0);
            expressRule.setShipperId(Integer.valueOf(param.get("shipperId")));
            expressRuleDao.save(expressRule);
            return "success";
        }
    }

    public Map<String, Object> updateLoad(Integer id) throws yesmywineException {//加载显示承运商费用(快递)规则
        ValueUtil.verify(id, "idNull");
        Map<String, Object> map = new HashMap<>();
        ExpressRule expressRule = expressRuleDao.findOne(id);
        map.put("expressRule",expressRule);
         return  map;
        }

    public String delete(Integer id) throws yesmywineException {//删除承运商费用(快递)规则
        ValueUtil.verify(id, "idNull");
        ExpressRule expressRule = expressRuleDao.findOne(id);
        Shippers shippers=shipperDao.findOne(Integer.valueOf(expressRule.getShipperId()));
        if(shippers.getStatus()==1){//承运商被停用了才能修改规则信息
            ValueUtil.isError( "承运商已启用");
        }

        expressRule.setDeleteEnum(1);
            expressRuleDao.save(expressRule);
            return  "success";
        }
    public String updateSave(Map<String, String> param) throws yesmywineException {//修改保存承运商费用(快递)规则
        Integer expressId = Integer.parseInt(param.get("id"));
        ExpressRule expressRule = expressRuleDao.findOne(expressId);
        Shippers shippers=shipperDao.findOne(expressRule.getShipperId());
        if(shippers.getStatus()==1){//承运商被停用了才能修改规则信息
            ValueUtil.isError( "承运商已启用");
        }
        String warehouseId=param.get("warehouseId");
        String type = param.get("type");
        List<ExpressRule> expressRules=expressRuleDao.findByWarehouseIdAndDeleteEnumAndTypeAndIdNot(Integer.valueOf(warehouseId),0,expressId,Integer.valueOf(type));
        String distributionArea=param.get("distributionArea");
        if (distributionArea.contains(";")) {
            ValueUtil.isError("每条规则只能添加一个区域");
        }
//        if(expressRules!=null){
//            for(int i = 0; i<expressRules.size(); i++){
//                String area=expressRules.get(i).getDistributionArea();
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
        if(expressRules!=null) {
            for (int i = 0; i < expressRules.size(); i++) {
                String area = expressRules.get(i).getDistributionArea();
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
        if (type.equals("0")) {//快递按箱规则
            ValueUtil.verify(param, new String[]{"type", "distributionAreaName",
                    "warehouseId", "distributionArea", "firstRate", "secondRate",
                    "firstRefundRate", "secondRefundRate", "shipperId"});
            expressRule.setType(Integer.valueOf(param.get("type")));
            expressRule.setDistributionAreaName(param.get("distributionAreaName"));
            expressRule.setWarehouseId(Integer.valueOf(param.get("warehouseId")));
            expressRule.setDistributionArea(param.get("distributionArea"));
            expressRule.setAreaName(param.get("areaName"));
            expressRule.setFirstRate(Double.valueOf(param.get("firstRate")));
            expressRule.setSecondRate(Double.valueOf(param.get("secondRate")));
            expressRule.setFirstRefundRate(Double.valueOf(param.get("firstRefundRate")));
            expressRule.setSecondRefundRate(Double.valueOf(param.get("secondRefundRate")));
            expressRule.setDeleteEnum(0);
            expressRule.setShipperId(Integer.valueOf(param.get("shipperId")));
            expressRuleDao.save(expressRule);
            return "success";
        } else {//快递按重量规则
            ValueUtil.verify(param, new String[]{"type", "distributionAreaName",
                    "warehouseId", "distributionArea", "firstWeight", "firstRate",
                    "secondWeight", "secondRate", "firstRefundRate", "secondRefundRate", "shipperId"});
            expressRule.setType(Integer.valueOf(param.get("type")));
            expressRule.setDistributionAreaName(param.get("distributionAreaName"));
            expressRule.setWarehouseId(Integer.valueOf(param.get("warehouseId")));
            expressRule.setDistributionArea(param.get("distributionArea"));
            expressRule.setAreaName(param.get("areaName"));
            expressRule.setFirstWeight(Double.valueOf(param.get("firstWeight")));
            expressRule.setFirstRate(Double.valueOf(param.get("firstRate")));
            expressRule.setSecondWeight(Double.valueOf(param.get("secondWeight")));
            expressRule.setSecondRate(Double.valueOf(param.get("secondRate")));
            expressRule.setFirstRefundRate(Double.valueOf(param.get("firstRefundRate")));
            expressRule.setSecondRefundRate(Double.valueOf(param.get("secondRefundRate")));
            expressRule.setDeleteEnum(0);
            expressRule.setShipperId(Integer.valueOf(param.get("shipperId")));
            expressRuleDao.save(expressRule);
            return "success";
        }
    }

    public String  expressRulePlus(String distributionArea,Integer warehouseId,Integer type,Integer shipperId ) throws yesmywineException {
            List<ExpressRule> expressRules=expressRuleDao.findByWarehouseIdAndDeleteEnumAndTypeAndShipperId(warehouseId,0,type,shipperId);
            if(expressRules!=null){
                for(int i = 0; i<expressRules.size(); i++){
                    String area=expressRules.get(i).getDistributionArea();
                    String[] s = area.split(";");
                    for (int j = 0; j< s.length; j++) {
                        System.out.println(s[j]);
                        String[] x = distributionArea.split(";");
                        for (int k = 0; k < x.length; k++) {
                            if(s[j].equals(x[k])){
                                StringBuffer sb = new StringBuffer();
                                String[] y = x[k].split(",");
                                for (int l = 0; l < y.length; l++) {
                                    Area area1=areaDao.findByAreaNo(Integer.valueOf(y[l]));
                                    sb.append(area1.getCityName());
                                }
                                ValueUtil.isError( ""+sb.toString()+"已添加");
                            }
                        }
                    }
                }
            }
        return null;
    }
}
