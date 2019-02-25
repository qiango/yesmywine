package com.yesmywine.ware.controller;

import com.google.gson.JsonArray;
import com.yesmywine.httpclient.bean.HttpBean;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.dao.WarehouseDao;
import com.yesmywine.ware.entity.Warehouses;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hz on 7/7/17.门店自提算库存
 */
@RestController
@RequestMapping("/inventory/itf/storese")
public class StoresWareHouseController {

    @Autowired
    private WarehouseDao warehouseDao;

    @RequestMapping(method = RequestMethod.GET)
    public String showsWarehouse(Integer receivingAddressId, String skuIds) throws yesmywineException {
        HttpBean httpBean = new HttpBean(Dictionary.MALL_HOST + "/userservice/receivingAddress/itf", com.yesmywine.httpclient.bean.RequestMethod.get);
        httpBean.addParameter("id", receivingAddressId);
        httpBean.run();
        List<Warehouses> list1 = new ArrayList<>();
        String temp = httpBean.getResponseContent();
        String data = ValueUtil.getFromJson(temp,"data");
        if(ValueUtil.isEmpity(data)){
            return ValueUtil.toJson(HttpStatus.SC_OK,list1);
        }
        String provinceId = ValueUtil.getFromJson(temp, "data", "provinceId");
        String cityId = ValueUtil.getFromJson(temp, "data", "cityId");
        String areaId = ValueUtil.getFromJson(temp, "data", "areaId");
        List<Warehouses> list = null;
        try {
            list = warehouseDao.findByWarehouseProvinceIdAndWarehouseCityIdAndWarehouseRegionIdAndType(provinceId, cityId, areaId, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list.size() > 0) {
            for (Warehouses warehouses : list) {
                String[] split = skuIds.split(",");
                for (int i = 0; i < split.length; i++) {
                    HttpBean httpBeans = new HttpBean(Dictionary.PAAS_HOST + "/inventory/itf/warehouses/cwIndex", com.yesmywine.httpclient.bean.RequestMethod.get);
                    httpBeans.addParameter("warehouseId", warehouses.getId());
                    String sku = split[i];
                    httpBeans.addParameter("skuId", Integer.parseInt(sku));
                    httpBeans.run();
                    String temps = httpBeans.getResponseContent();
                    String inventory = ValueUtil.getFromJson(temps, "data", "content");
                    JsonParser jsonParser = new JsonParser();
                    JsonArray arr = jsonParser.parse(inventory).getAsJsonArray();
                    if(arr.size()==0){
                        break;
                    }
                    String useCount = arr.get(0).getAsJsonObject().get("useCount").getAsString();
                    if (Integer.parseInt(useCount) == 0) {
                        break;
                    } else if (Integer.parseInt(useCount) > 0 && i == split.length - 1) {
                        list1.add(warehouses);

                    }
                }
            }

            return ValueUtil.toJson(HttpStatus.SC_OK, list1);
        }
        return ValueUtil.toJson(HttpStatus.SC_OK,list1);
    }


}
