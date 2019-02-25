package com.yesmywine.logistics.service.Impl;


import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.yesmywine.httpclient.bean.HttpBean;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.logistics.dao.AreaDao;
import com.yesmywine.logistics.dao.ShipperDao;
import com.yesmywine.logistics.entity.Area;
import com.yesmywine.logistics.entity.Shippers;
import com.yesmywine.logistics.service.ExpressRuleService;
import com.yesmywine.logistics.service.ImportService;
import com.yesmywine.logistics.service.LogisticsRuleService;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.ValueUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hz on 2017/5/8.
 */
@Service
public class ImportServiceImpl implements ImportService {

    @Autowired
    private ExpressRuleService expressRuleService;
    @Autowired
    private LogisticsRuleService logisticsRuleService;
    @Autowired
    private ShipperDao shipperDao;
    @Autowired
    private AreaDao areaDao;


    @Override
    public List<Map<String, Object>> importExpressRule(List<Map<String, Object>> list) {
        List<Map<String, Object>> reList = new ArrayList();

        if(ValueUtil.isEmpity(list)){
            return list;
        }

        for(Map<String, Object> map: list){
            Map<String, String> param = new HashMap<>();
            Map<String, Object> reMap = new HashMap<>();

            String shipperName = map.get("shipperName").toString();
            Shippers shippers=shipperDao.findByShipperNameAndDeleteEnum(shipperName,0);
            if(shippers==null){
                reMap = map;
                reMap.put("erro", "没有承运商:"+shipperName);
                reList.add(reMap);
                return reList;
            }
            String warehouseName = map.get("warehouseName").toString();

            HttpBean httpBean = new HttpBean(Dictionary.PAAS_HOST+"/inventory/warehouses", RequestMethod.get);
            httpBean.addParameter("warehouseName", warehouseName);
            httpBean.run();
            String temp = httpBean.getResponseContent();
//            String data = ValueUtil.getFromJson(temp, "data");
            String code = ValueUtil.getFromJson(temp, "code");
            String warehouseId=null;
            if("200".equals(code)){
               String content =ValueUtil.getFromJson(temp,"data", "content");//仓库id
                JsonParser jsonParser = new JsonParser();
                JsonArray arr = jsonParser.parse(content).getAsJsonArray();
                for (int i = 0; i < arr.size(); i++) {
                    warehouseId = arr.get(i).getAsJsonObject().get("id").getAsString();
                }
            }else {
                reMap = map;
                reMap.put("erro", "没有仓库:"+warehouseName);
                reList.add(reMap);
                return reList;
            }
            String areaName = map.get("areaName").toString();
            String[] name = areaName.split(";");
            String arid = "";
            for (int i = 0; i < name.length; i++) {
                String name1 = name[i];
                String[] area = name1.split(",");
                String id = "";
                for (int j = 0; j < area.length; j++) {
                    Area area1 = areaDao.findByCityName(area[j]);
                    String areaId = area1.getId().toString();
                    if (ValueUtil.isEmpity(id)) {
                        id = areaId;
                    } else {
                        id = id + "," + areaId;
                    }
                }
                if(ValueUtil.isEmpity(arid)){
                    arid= arid+id;
                }else {
                    arid = arid + ";" + id;
                }
            }

            param.put("序号", map.get("序号").toString());
            param.put("type", map.get("type").toString());
            param.put("distributionAreaName", map.get("distributionAreaName").toString());
            param.put("warehouseId", warehouseId);
            param.put("distributionArea", arid);
            param.put("areaName", map.get("areaName").toString());
            param.put("firstRate", map.get("firstRate").toString());
            param.put("firstWeight", map.get("firstWeight").toString());
            param.put("secondRate", map.get("secondRate").toString());
            param.put("secondWeight", map.get("secondWeight").toString());
            param.put("firstRefundRate", map.get("firstRefundRate").toString());
            param.put("secondRefundRate", map.get("secondRefundRate").toString());
            param.put("shipperId", shippers.getId().toString());
            try {
                String s = this.expressRuleService.addExpressRule(param);
                if("erro".equals(s)){
                    reMap = map;
                    reMap.put("erro", s);
                    reList.add(reMap);
                }

            } catch (Exception e) {
                reMap = map;
                reMap.put("erro", e);
                reList.add(reMap);
            }
        }
        return reList;
    }
    @Override
    public List<Map<String, Object>> importLogisticsRule(List<Map<String, Object>> list) {
        List<Map<String, Object>> reList = new ArrayList();

        if(ValueUtil.isEmpity(list)){
            return list;
        }
        for(Map<String, Object> map: list){
            Map<String, String> param = new HashMap<>();
            Map<String, Object> reMap = new HashMap<>();
            String shipperName = map.get("shipperName").toString();
            Shippers shippers=shipperDao.findByShipperNameAndDeleteEnum(shipperName,0);
            if(shippers==null){
                reMap = map;
                reMap.put("erro", "没有承运商:"+shipperName);
                reList.add(reMap);
                return reList;
            }

            String areaName = map.get("areaName").toString();
            String[] name = areaName.split(";");
            String arid = "";
            for (int i = 0; i < name.length; i++) {
                String name1 = name[i];
                String[] area = name1.split(",");
                String id = "";
                for (int j = 0; j < area.length; j++) {
                    Area area1 = areaDao.findByCityName(area[j]);
                    String areaId = area1.getId().toString();
                    if (ValueUtil.isEmpity(id)) {
                        id = areaId;
                    } else {
                        id = id + "," + areaId;
                    }
                }
                if(ValueUtil.isEmpity(arid)){
                    arid= arid+id;
                }else {
                    arid = arid + ";" + id;
                }
            }
            param.put("序号", map.get("序号").toString());
            param.put("distributionAreaName", map.get("distributionAreaName").toString());
            param.put("distributionArea", arid);
            param.put("areaName", map.get("areaName").toString());
            param.put("deliveryCharge", map.get("deliveryCharge").toString());
            param.put("rate", map.get("rate").toString());
            param.put("shipperId", shippers.getId().toString());
            try {
                String s = this.logisticsRuleService.addLogisticsRule(param);
                if("erro".equals(s)){
                    reMap = map;
                    reMap.put("erro", s);
                    reList.add(reMap);
                }
            } catch (Exception e) {
                reMap = map;
                reMap.put("erro", e);
                reList.add(reMap);
            }
        }
        return reList;
    }

}
