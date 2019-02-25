package com.yesmywine.ware.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.SynchronizeUtils;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.dao.WarehouseChannelDao;
import com.yesmywine.ware.dao.WarehouseDao;
import com.yesmywine.ware.entity.Warehouses;
import com.yesmywine.ware.entity.WarehousesChannel;
import com.yesmywine.ware.service.WarehousesService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

/**
 * Created by SJQ on 2017/2/10.
 */
@Service
@Transactional
public class WarehouseServiceImpl extends BaseServiceImpl<Warehouses, Integer>
        implements WarehousesService {
    @Autowired
    private WarehouseDao warehouseDao;

    @Autowired
    private WarehouseChannelDao warehouseChannelDao;

    @Override
    public List getGoodInWhichswarehouse(String skuId) {
        List<WarehousesChannel> wgList = warehouseChannelDao.findBySkuId(skuId);
        return wgList;
    }

    @Override
    public List<WarehousesChannel> getGoodsInWarehouse(String warehouseCode) {
        List<WarehousesChannel> wgList = warehouseChannelDao.findByWarehouseCode(warehouseCode);
        return wgList;
    }

    @Override
    public Boolean findByWarehouseName(String warehouseName) {
        Warehouses warehouse = warehouseDao.findByWarehouseName(warehouseName);
        if (warehouse == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Boolean checkIsNull(Integer warehouseId) {
        Warehouses warehouse = warehouseDao.findOne(warehouseId);
        List<WarehousesChannel> wgList = warehouseChannelDao.findByWarehouseCode(warehouse.getWarehouseCode());
        if (wgList.size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Warehouses create(Warehouses warehouse, Map<String, String> params) throws yesmywineException {
        ValueUtil.verify(params, new String[]{"warehouseName", "warehouseCode", "warehouseProvince", "warehouseProvinceId",
                "warehouseCity", "warehouseCityId", "warehouseRegion", "warehouseRegionId", "warehouseAddress", "type", "contactName", "phone", "telephone"});
        if (findByWarehouseName(params.get("warehouseName"))) {
            ValueUtil.isError("名称重复");
        }

        String relationId = null;
        if(params.get("type").equals("2")){//如果是未清关仓
            relationId = params.get("relationId");
            if(relationId!=null){
                Warehouses relationWare = warehouseDao.findOne(Integer.valueOf(relationId));
                if(!relationWare.getType().equals(3)){
                    ValueUtil.isError("非已清关仓，无法关联");
                }
                ValueUtil.verifyNotExist(relationWare,"所选的已清关仓不存在");
                if(relationWare.getRelationCode()!=null&&!relationWare.getRelationCode().equals("")){
                    ValueUtil.isError("该清关仓，已关联其他未清关仓！");
                }
                relationWare.setRelationCode(relationId);
                warehouse.setRelationCode(relationId);
            }else{
                ValueUtil.isError("未清关仓必须关联一个已清关仓！");
            }
        }
        checkWarehouseCodeRepead(warehouse.getWarehouseCode());
//        warehouse.setWarehouseCode("W"+String.valueOf(new Date().getTime()));
        warehouse.setCanDelete(true);
        warehouseDao.save(warehouse);
        //向oms、商城库存服务同步仓库信息
        sentToOmsWarehouse(warehouse, 0);
        String mall_result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/warehouses/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "save", warehouse), RequestMethod.post);
        if (mall_result == null || !mall_result.equals("201")) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            //回滚oms仓库信息
            sentToOmsWarehouse(warehouse, 2);
            ValueUtil.isError("仓库创建失败：无法同步在商城中创建新仓库！");
        }
        return warehouse;
    }

    private void checkWarehouseCodeRepead(String warehouseCode) throws yesmywineException {
        Warehouses warehouses = warehouseDao.findByWarehouseCode(warehouseCode);
        if(warehouses!=null){
            ValueUtil.isError("仓库编码重复");
        }
    }

    private void sentToOmsWarehouse(Warehouses warehouse, Integer status) throws yesmywineException {//status 0-增  1-改  2-删
        JSONObject requestJson = new JSONObject();
        requestJson.put("function", status);
        JSONObject dataJson = new JSONObject();
        dataJson.put("locationCode", warehouse.getWarehouseCode());
        dataJson.put("locationName", warehouse.getWarehouseName());
        switch (warehouse.getType()) {
            case 0:
                dataJson.put("locationType", "门店仓");
                break;
            case 1:
                dataJson.put("locationType", "实体仓");
                break;
            case 2:
                dataJson.put("locationType", "未清关仓");
                break;
            case 3:
                dataJson.put("locationType", "已清关仓");
                break;
        }
        dataJson.put("provinceCode", warehouse.getWarehouseProvinceId());
        dataJson.put("provinceName", warehouse.getWarehouseProvince());
        dataJson.put("cityCode", warehouse.getWarehouseCityId());
        dataJson.put("cityName", warehouse.getWarehouseCity());
        dataJson.put("districtCode", warehouse.getWarehouseRegionId());
        dataJson.put("districtName", warehouse.getWarehouseRegion());
        dataJson.put("address", warehouse.getWarehouseAddress());
        dataJson.put("contactPerson", warehouse.getContactName());
        dataJson.put("mobile", warehouse.getPhone());
        dataJson.put("telephone", warehouse.getTelephone());
        dataJson.put("fax", warehouse.getFax());
        dataJson.put("email", warehouse.getEmail());
        requestJson.put("data", dataJson);
        //向oms同步仓库信息
        String result = SynchronizeUtils.getOmsResult(Dictionary.OMS_HOST, "/updateBaseLocation", RequestMethod.post, "", requestJson.toJSONString());
        if (result != null) {
            String respStatus = ValueUtil.getFromJson(result, "status");
            if (!respStatus.equals("success")) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("向OMS同步仓库失败,原因："+ValueUtil.getFromJson(result, "message"));
            }
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("OMS服务无法连接，向OMS同步仓库失败");
        }
    }

    @Override
    public Warehouses update(Warehouses warehouse, Map<String, String> params) throws yesmywineException {
        ValueUtil.verify(params, new String[]{"id", "warehouseName", "warehouseProvince", "warehouseProvinceId",
                "warehouseCity", "warehouseCityId", "warehouseRegion", "warehouseRegionId", "warehouseAddress", "type", "contactName", "phone", "telephone"});
        Warehouses oldWarehouse = warehouseDao.findOne(warehouse.getId());
        if (!oldWarehouse.getWarehouseName().equals(warehouse.getWarehouseName())) {
            if (findByWarehouseName(params.get("warehouseName"))) {
                ValueUtil.isError("名称重复");
            }
        }
        if (!warehouse.getWarehouseCode().equals(oldWarehouse.getWarehouseCode())) {
            ValueUtil.isError("仓库编码不可修改");
        }
        if (!warehouse.getType().equals(oldWarehouse.getType())) {
            ValueUtil.isError("仓库");
        }
        warehouseDao.save(warehouse);
        //向oms、商城库存服务同步仓库信息
        sentToOmsWarehouse(warehouse, 1);
        String result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/warehouses/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "update", warehouse), RequestMethod.post);
//        String mall_result = SynchronizeWithMallWarehouses.update(ValueUtil.toJson(HttpStatus.SC_CREATED, warehouse));
        if (result == null || !result.equals("201")) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            //回滚oms仓库信息
            sentToOmsWarehouse(warehouseDao.findOne(warehouse.getId()), 1);
            ValueUtil.isError("仓库修改失败：无法同步在商城中修改仓库！");
        }
        return warehouse;
    }

    @Override
    public Warehouses findByWarehouseCode(String warehouseCode) {
        return warehouseDao.findByWarehouseCode(warehouseCode);
    }

    @Override
    public void delete(Integer warehouseId) throws yesmywineException {
        ValueUtil.verify(warehouseId);
        //检查仓库中是否有商品存在
        Boolean isNull = checkIsNull(warehouseId);
        if (isNull) {
            Warehouses warehouse = warehouseDao.findOne(warehouseId);
            if (warehouse.getCanDelete() != null && !warehouse.getCanDelete()) {
                ValueUtil.isError("该渠仓库止删除！");
            }
            warehouseDao.delete(warehouseId);
            //向oms、商城库存服务同步仓库信息
            sentToOmsWarehouse(warehouse, 2);
            String mall_result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/warehouses/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "delete", warehouseId), RequestMethod.post);
////            String mall_result = SynchronizeWithMallWarehouses.delete(ValueUtil.toJson(HttpStatus.SC_CREATED, warehouse));
            if (mall_result == null || !mall_result.equals("201")) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                sentToOmsWarehouse(warehouseDao.findOne(warehouseId), 0);
                ValueUtil.isError("仓库删除失败：无法同步在商城中创删除仓库！");
            }
        } else {
            ValueUtil.isError("仓库中存在商品，无法删除");
        }
    }
}
