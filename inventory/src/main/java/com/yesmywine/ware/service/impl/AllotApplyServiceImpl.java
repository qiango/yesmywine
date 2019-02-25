package com.yesmywine.ware.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.SynchronizeUtils;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.date.DateUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.Utils.SKUUtils;
import com.yesmywine.ware.bean.AllotType;
import com.yesmywine.ware.dao.*;
import com.yesmywine.ware.entity.*;
import com.yesmywine.ware.service.AllotApplyService;
import com.yesmywine.ware.service.ChannelsInventoryService;
import com.yesmywine.ware.service.WarehousesChannelService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

/**
 * Created by SJQ on 2017/2/9.
 */
@Service
@Transactional
public class AllotApplyServiceImpl extends BaseServiceImpl<AllotApply, Integer> implements AllotApplyService {
    @Autowired
    private AllotApplyDao applyDao;

    @Autowired
    private ChannelsDao channelsDao;

    @Autowired
    private WarehouseDao warehouseDao;

    @Autowired
    private ChannelsInventoryService channelsInventoryService;

    @Autowired
    private WarehousesChannelService warehouseChannelService;

    @Autowired
    private StoresDao storesDao;

    @Autowired
    private AllotCommandDao commandDao;

    @Override
    public String allotApply(String jsonData) throws yesmywineException {
        JSONArray applyArray = JSON.parseArray(jsonData);
        for (int i = 0; i < applyArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) applyArray.get(i);
            String skuCode = jsonObject.getString("skuCode");
            String skuInfo = SKUUtils.getResult(Dictionary.PAAS_HOST, "/goods/sku/code/itf", RequestMethod.get, "code", skuCode);
            if (skuInfo == null) {
                ValueUtil.isError("无此ＳＫＵ或商品服务已关闭");
            }
            String skuId = ValueUtil.getFromJson(skuInfo, "data", "id");
            String skuName = ValueUtil.getFromJson(skuInfo, "data", "skuName");
            String orderNum = jsonObject.getString("orderNum");
            String warehouseCode = jsonObject.getString("warehouseCode");
            String channelCode = jsonObject.getString("channelCode");
            String count = jsonObject.getString("count");
            String type = jsonObject.getString("type");// oms   store  omsClean
            String storeCode = jsonObject.getString("storeCode");
            if (storeCode != null) {
                Stores stores = storesDao.findByStoreCode(storeCode);
                ValueUtil.verifyNotExist(stores, "无此门店");
                Channels channels = stores.getChannel();
                Warehouses warehouses = stores.getWarehouse();
                if (channels == null || warehouses == null) {
                    ValueUtil.isError("门店尚未关联渠道或仓库，无法进行入库操作");
                }
                channelCode = channels.getChannelCode();
                warehouseCode = warehouses.getWarehouseCode();
            }

            Channels channels = channelsDao.findByChannelCode(channelCode);
            Warehouses warehouse = warehouseDao.findByWarehouseCode(warehouseCode);
            if (null == channels || warehouse == null) {
                ValueUtil.isError("无此渠道或仓库");
            }
            AllotApplyDetail applyDetail = new AllotApplyDetail(orderNum, Integer.valueOf(count));
            //判断相同渠道、仓库、sku的申请是否存在
            AllotApply isExist = findByChannelCodeAndWarehouseCodeAndSkuIdAndStatusAndType(channelCode, warehouseCode, Integer.valueOf(skuId), 0, type);
            if (null != isExist) {
                isExist.setCount(isExist.getCount() + Integer.valueOf(count));
                isExist.getDetailSet().add(applyDetail);
                applyDao.save(isExist);
            } else {
                AllotApply allotApply = new AllotApply();
                allotApply.setSkuId(Integer.valueOf(skuId));
                allotApply.setSkuCode(skuCode);
                allotApply.setSkuName(skuName);
                allotApply.setChannel(channels);
                allotApply.setWarehouse(warehouse);
                allotApply.setCount(Integer.valueOf(count));
                if (type.equals(AllotType.STORES)) {
                    allotApply.setType(AllotType.STORES);
                } else if (type.equals(AllotType.OMS)) {
                    allotApply.setType(AllotType.OMS);
                } else if (type.equals(AllotType.OMS_CLEAN)) {
                    allotApply.setType(AllotType.OMS_CLEAN);
                }
                allotApply.setStatus(0);
                allotApply.addDetails(applyDetail);
                applyDao.save(allotApply);
            }
        }
        return "SUCCESS";
    }

    @Override
    public String allotCommand(String jsonData, String userId) throws yesmywineException {
        JSONArray allotArray = JSON.parseArray(jsonData);
        List<JSONObject> applyList = new ArrayList<>();
        for (int m = 0; m < allotArray.size(); m++) {
            JSONObject oneObject = (JSONObject) allotArray.get(m);
            applyList.add(oneObject);
        }
        if (applyList.size() > 1) {
            Collections.sort(applyList, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    Integer allotWarehouseId_1 = Integer.valueOf(o1.getString("allotWarehouseId"));
                    Integer allotWarehouseId_2 = Integer.valueOf(o2.getString("allotWarehouseId"));
                    return allotWarehouseId_1.compareTo(allotWarehouseId_2);
                }
            });
        }
        Integer oldWarehouseId = 0;
        Map<String, Object> map = new HashedMap();
        List<AllotCommand> commandList = new ArrayList<>();
        AllotCommand command = new AllotCommand();
        for (int i = 0; i < applyList.size(); i++) {
            JSONObject oneAllot = applyList.get(i);
            Integer applyId = Integer.valueOf(oneAllot.getString("applyId"));
            Integer allotWarehouseId = Integer.valueOf(oneAllot.getString("allotWarehouseId"));
            Integer allotCount = Integer.valueOf(oneAllot.getString("allotCount"));

            Warehouses allotWarehouse = warehouseDao.findOne(allotWarehouseId);
            ValueUtil.verifyNotExist(allotWarehouse, "无此调拨仓库");
            AllotApply allotApply = applyDao.findOne(applyId);
            ValueUtil.verifyNotExist(allotApply, "无此调拨申请");
            map.put(allotApply.getType(), "11");
            WarehousesChannel tarWarehouseChannel = warehouseChannelService.findByChannelCodeAndWarehouseCodeAndSkuCode(allotApply.getChannel().getChannelCode(), allotApply.getWarehouse().getWarehouseCode(), allotApply.getSkuCode());
            WarehousesChannel allotWarehouseChannel = warehouseChannelService.findByChannelCodeAndWarehouseCodeAndSkuCode(allotApply.getChannel().getChannelCode(), allotWarehouse.getWarehouseCode(), allotApply.getSkuCode());

            if (allotWarehouseChannel == null) {
                ValueUtil.isError("SKU：" + allotApply.getSkuName() + "在调拨仓库仓库：" + allotWarehouse.getWarehouseName() + "中不存在");
            }
            System.out.println(tarWarehouseChannel == null ? 0 : tarWarehouseChannel.getUseCount());
            if (allotCount > allotWarehouseChannel.getUseCount()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError(allotApply.getSkuName() + " 仓库剩余数量小于调拨数量");
            } else if (allotCount < allotApply.getCount() - allotWarehouseChannel.getUseCount()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError(allotApply.getSkuName() + "调拨数量必须大于 （调拨申请数量-目标仓库数量）");
            }

            if (allotWarehouseId == null || allotCount == null) {
                continue;
            }
            String allotCode = "";
            if (oldWarehouseId == allotWarehouseId) {//同一个调拨指令
                AllotDetail detail = new AllotDetail(allotCode, allotApply.getSkuId(), allotApply.getSkuCode(), allotApply.getSkuName(), allotApply.getCount(), allotCount, 0);
                command.addDetails(detail);
                if (applyList.size() - 1 == i) {
                    commandList.add(command);
                }
            } else {//新的调拨指令
                if (i != 0) {
                    commandList.add(command);
                }
                allotCode = "AAC" + new Date().getTime();

                command = new AllotCommand();
                command.setAllotCode(allotCode);
                command.setTarWarehouse(allotApply.getWarehouse());
                command.setAllotwarehouse(allotWarehouse);
                command.setChannel(allotApply.getChannel());
                command.setType(AllotType.OMS);
                command.setStatus(0);
                command.setProducer(userId);

                AllotDetail detail = new AllotDetail(allotCode, allotApply.getSkuId(), allotApply.getSkuCode(), allotApply.getSkuName(), allotApply.getCount(), allotCount, 0);
                command.addDetails(detail);
                if (applyList.size() - 1 == i) {
                    commandList.add(command);
                }
            }
            oldWarehouseId = allotWarehouseId;

            allotApply.setStatus(1);//申请指令处理中
            allotApply.setAllotCode(allotCode);
            applyDao.save(allotApply);
        }

        for (AllotCommand allotCommand : commandList) {
            //冻结库存
            freezeInventory(allotCommand);
            //修改调拨单信息
            allotCommand.setStatus(1);// 调拨指令出库中


        }
        commandDao.save(commandList);
        for (Map.Entry entry : map.entrySet()) {
            String key = (String) entry.getKey();
            if (key.equals("omsClean")) {
                ValueUtil.isError("包含清关仓的调拨申请，无法制作调拨指令，请重新制作");
            }
        }
        return "SUCCESS";
    }

    private String freezeInventory(AllotCommand command) throws yesmywineException {

        Set<AllotDetail> detailSet = command.getDetailSet();
        for (AllotDetail detail : detailSet) {
            JSONArray freezeArray = new JSONArray();
            JSONObject freezeObject = new JSONObject();
            freezeObject.put("skuCode", detail.getSkuCode());
            freezeObject.put("channelCode", command.getChannel().getChannelCode());
            freezeObject.put("warehouseCode", command.getAllotwarehouse().getWarehouseCode());
            freezeObject.put("count", detail.getAllotCount());
            freezeObject.put("type", "yes");
            freezeArray.add(freezeObject);
            String result = channelsInventoryService.freeze(freezeArray.toJSONString());
            if (!result.equals("SUCCESS")) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("SKU : " + detail.getSkuName() + "冻结失败");
            }
        }

        return "SUCCESS";
    }

    private String releaseFreezeInventory(AllotCommand command) throws yesmywineException {

        Set<AllotDetail> detailSet = command.getDetailSet();
        for (AllotDetail detail : detailSet) {
            JSONArray freezeArray = new JSONArray();
            JSONObject freezeObject = new JSONObject();
            freezeObject.put("skuCode", detail.getSkuCode());
            freezeObject.put("channelCode", command.getChannel().getChannelCode());
            freezeObject.put("warehouseCode", command.getAllotwarehouse().getWarehouseCode());
            freezeObject.put("count", detail.getAllotCount());
            freezeObject.put("type", "yes");

            freezeArray.add(freezeObject);
            String result = channelsInventoryService.releaseFreeze(freezeArray.toJSONString());
            if (!result.equals("SUCCESS")) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("SKU : " + detail.getSkuName() + "释放冻结失败");
            }
        }

        return "SUCCESS";
    }

    /*
    *@Author Gavin
    *@Description 向WMS发送出库指令
    *@Date 2017/3/31 11:13
    *@Email gavinsjq@sina.com
    *@Params
    */
    private String  sendRemovalCommandToWMS(AllotCommand command) throws yesmywineException {
        JSONObject xmldata = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("OrderNo", command.getAllotCode());
        header.put("OrderType", "DB");
        header.put("OrderTime", DateUtil.toString(command.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        header.put("CustomerID", "YMJ");
        header.put("SOReference3", command.getAllotCode());//WMS SOReference3 标识为调拨指令编码
        header.put("ConsigneeID", "XN");
        header.put("ConsigneeName", command.getTarWarehouse().getWarehouseName());
        header.put("C_Province", command.getTarWarehouse().getWarehouseProvince());//入库
        header.put("C_City", command.getTarWarehouse().getWarehouseCity());//入库
        header.put("C_Tel1", command.getTarWarehouse().getPhone());
        header.put("C_Mail", command.getTarWarehouse().getEmail());//入库
        header.put("C_Address1 ", command.getTarWarehouse().getWarehouseAddress());
        header.put("UserDefine1",command.getTarWarehouse().getWarehouseCode());//调拨入库仓库code  可变 跟 WarehouseID对应
//        header.put("UserDefine1", "B");//调拨入库仓库code  可变 跟 WarehouseID对应
        header.put("WarehouseID", command.getAllotwarehouse().getWarehouseCode());//出库仓库code
        header.put("CarrierFax",command.getTarWarehouse().getWarehouseProvince());//目的地代码  ？？
//        header.put("CarrierFax", "上海");//目的地代码
        header.put("CarrierId", "EMS");//承运人代码
        header.put("CarrierName", "EMS");//承运人名称
        header.put("RequireDeliveryNo", "");//是否需要面单号
        JSONArray detailsItems = new JSONArray();
        Set<AllotDetail> detailSet = command.getDetailSet();
        int i = 1;
        for (AllotDetail detail : detailSet) {
            JSONObject detailsItem = new JSONObject();
            detailsItem.put("LineNo", i);
            detailsItem.put("CustomerID", "YMJ");
            detailsItem.put("SKU", detail.getSkuCode());
//            detailsItem.put("SKU", "W0406UUU83000458614500006");
            detailsItem.put("LotAtt08", "N");
            detailsItem.put("QtyOrdered", detail.getApplyCount());
            detailsItem.put("D_EDI_15", "A02");
            detailsItem.put("D_EDI_16", "SE");
            detailsItems.add(detailsItem);
            i++;
        }
        header.put("detailsItem", detailsItems);
        JSONObject xmlHeader = new JSONObject();
        xmlHeader.put("header", header);
        xmldata.put("xmldata", xmlHeader);
        String result = SynchronizeUtils.getWmsResult(Dictionary.WMS_HOST, "putSOData","SO", xmldata.toJSONString());
        String returnCode = ValueUtil.getFromJson(result, "Response", "return", "returnCode");
        if (returnCode.equals("0000")) {
            return "SUCCESS";
        }
        String msg = ValueUtil.getFromJson(result, "Response", "return", "returnDesc");
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        ValueUtil.isError(msg);
        return "FAIL";
    }

    @Override
    public AllotApply findByChannelCodeAndWarehouseCodeAndSkuId(String channelCode, String warehouseCode, Integer skuId) {
        Channels channels = channelsDao.findByChannelCode(channelCode);
        Warehouses warehouse = warehouseDao.findByWarehouseCode(warehouseCode);
//        warehouse.setId(warehouseId);
        return applyDao.findByChannelAndWarehouseAndSkuId(channels, warehouse, skuId);
    }

    @Override
    public AllotApply findByChannelCodeAndWarehouseCodeAndSkuIdAndStatusAndType(String channelCode, String warehouseCode, Integer skuId, Integer status, String type) {
        Channels channels = channelsDao.findByChannelCode(channelCode);
        Warehouses warehouse = warehouseDao.findByWarehouseCode(warehouseCode);
//        warehouse.setId(warehouseId);
        return applyDao.findByChannelAndWarehouseAndSkuIdAndStatusAndType(channels, warehouse, skuId, status, type);
    }

    @Override
    public List<AllotApply> findByAllotCode(String allotCode) {
        return applyDao.findByAllotCode(allotCode);
    }

    @Override
    public List<Channels> getApplyChannels() {
        return channelsDao.findAll(applyDao.getApplyChannels());
    }

    @Override
    public List<Warehouses> getApplyWarehouses(Integer channelId) {
        return warehouseDao.findAll(applyDao.getApplyChannelWarehouses(channelId));
    }

    @Override
    public String rirectAllotCommandRirect(Integer channelId, Integer tarWarehouseId, Integer cwIds[], Integer counts[], Integer userId) throws yesmywineException {
        Warehouses tarWarehouse = warehouseDao.findOne(tarWarehouseId);
        Channels channels = channelsDao.findOne(channelId);
        String allotCode = "AAC" + new Date().getTime();
        AllotCommand command = new AllotCommand();
        command.setAllotCode(allotCode);
        command.setTarWarehouse(tarWarehouse);
        command.setChannel(channels);
        command.setType(AllotType.LOCAL);
        command.setStatus(0);
        command.setProducer(String.valueOf(userId));
        Map<Warehouses, Object> map = new HashedMap();
        for (int i = 0; i < cwIds.length; i++) {
            Integer warehouseChannelId = cwIds[i];
            Integer allotCount = counts[i];
            WarehousesChannel allotWarehouseChannel = warehouseChannelService.findOne(warehouseChannelId);
            AllotDetail detail = new AllotDetail(allotCode, allotWarehouseChannel.getSkuId(), allotWarehouseChannel.getSkuCode(), allotWarehouseChannel.getSkuName(), allotCount, allotCount, 0);
            command.addDetails(detail);
            command.setAllotwarehouse(allotWarehouseChannel.getWarehouse());
            map.put(allotWarehouseChannel.getWarehouse(), 0);
        }
        if (map.size() > 1) {
            ValueUtil.isError("只能有一个调拨仓库");
        }

        String freezeResult = freezeInventory(command);

        String result = "";
        if (freezeResult.equals("SUCCESS")) {
            if (tarWarehouse.getRelationCode() != null) {//清关调拨,paas直接做出库，发送入库指令
                //paas直接做出库
                paasRirectOut(command);
                //通知wms入库
                sendWarehouseWarrantToWms(command);
            } else { //否则向WMS发送出库指令
                freezeInventory(command);
                result = sendRemovalCommandToWMS(command);
            }
        } else {
            ValueUtil.isError("冻结库存失败，无法制作调拨指令！");
        }


        if (result.equals("SUCCESS")) {
            //修改调拨单信息
            command.setStatus(1);// 调拨指令出库中
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("WMS无响应,无法制作调拨指令！");
        }
        commandDao.save(command);

        return "SUCCESS";
    }

    private void paasRirectOut(AllotCommand command) throws yesmywineException {
        freezeInventory(command);
        JSONObject xmlObj = new JSONObject();
        JSONObject dataObj = new JSONObject();
        JSONObject orderInfo = new JSONObject();
        JSONArray outArray = new JSONArray();
        Set<AllotDetail> detailSet = command.getDetailSet();
        for (AllotDetail detail : detailSet) {
            JSONObject obj = new JSONObject();
            obj.put("QtyShipped", detail.getApplyCount());
            obj.put("SKU", detail.getSkuCode());
            outArray.add(obj);
        }
        orderInfo.put("orderinfo", outArray);
        dataObj.put("dataObj", orderInfo);
        xmlObj.put("xmldata", dataObj);

        channelsInventoryService.wmsOutOrder(xmlObj.toJSONString());
    }

    private void sendWarehouseWarrantToWms(AllotCommand command) {
        JSONObject xmldata = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("OrderNo", command.getAllotCode());
        header.put("OrderType", "DB");
        header.put("CustomerID", "YMJ");
        Date now = new Date();
        header.put("ASNCreationTime", DateUtil.toString(now, "yyyy-MM-dd HH:mm:ss"));//ASN创建时间
        header.put("UserDefine1", command.getAllotwarehouse().getWarehouseCode());//调拨出库仓库
        header.put("WarehouseID", command.getTarWarehouse().getWarehouseCode());//所属仓库
        header.put("Priority", "3");

        JSONArray detailsItems = new JSONArray();
        int b = 1;
        for (AllotDetail detail : command.getDetailSet()) {
            JSONObject detailsItem = new JSONObject();

            detailsItem.put("LineNo", b);//行号
            detailsItem.put("CustomerID", "YMJ");
            detailsItem.put("SKU", detail.getSkuCode());
            detailsItem.put("ExpectedQty", detail.getApplyCount());

            String variety = null;
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("code", detail.getSkuCode());
            String skuInfo = SynchronizeUtils.getResult(Dictionary.PAAS_HOST, "/goods/sku/code/itf", RequestMethod.get, skuMap, null);
            String isExpensive = ValueUtil.getFromJson(skuInfo,"data","isExpensive");
            if(isExpensive.equals("0")){
                detailsItem.put("LotAtt04", "Y");//是否贵品：Y为贵品，N为非贵品
            }else{
                detailsItem.put("LotAtt04", "N");//是否贵品：Y为贵品，N为非贵品
            }
            detailsItem.put("LotAtt06", "YMJ");//供应商名称
            detailsItem.put("LotAtt08", "N");//质量状态
            detailsItems.add(detailsItem);
            b++;
        }

        header.put("detailsItem", detailsItems);
        JSONObject xmlHeader = new JSONObject();
        xmlHeader.put("header", header);
        xmldata.put("xmldata", xmlHeader);
        String result = SynchronizeUtils.getWmsResult(Dictionary.WMS_HOST , "putASNData","ASN", xmldata.toJSONString());
        String returnCode = ValueUtil.getFromJson(result, "Response", "return", "returnCode");
        if (!returnCode.equals("0000")) {
            command.setSynStatus(0);
            commandDao.save(command);
        }
    }

    @Override
    public String omsCleanApply(String jsonData) throws yesmywineException {
        JSONArray array = JSON.parseArray(jsonData);
        JSONArray applyArray = new JSONArray();
        for (int i = 0; i < array.size(); i++) {
            JSONObject jsonObject = (JSONObject) array.get(i);
            String warehouseCode = jsonObject.getString("warehouseCode");
            String channelCode = jsonObject.getString("channelCode");
            String skuCode = jsonObject.getString("skuCode");
            Integer count = Integer.valueOf(jsonObject.getString("count"));
            String comment = jsonObject.getString("comment");

            Warehouses warehouses = warehouseDao.findByWarehouseCode(warehouseCode);
            Channels channel = channelsDao.findByChannelCode(channelCode);
            if (warehouses == null || channel == null) {
                ValueUtil.isError("无此渠道或仓库");
            }

            if(!warehouses.getType().equals(2)){
                ValueUtil.isError("该仓库不是未清关仓库");
            }

            String relationCode = warehouses.getRelationCode();
            List<Warehouses> warehousesList = warehouseDao.findByRelationCode(relationCode);
            WarehousesChannel allotWarehouse = null;
            WarehousesChannel targetWarehosue = null;

            Warehouses tarWarehouse = null;
            for (Warehouses warehouse : warehousesList) {
                if (warehouse.equals(warehouses)) {
                    allotWarehouse = warehouseChannelService.findByChannelCodeAndWarehouseCodeAndSkuCode(channelCode, warehouse.getWarehouseCode(), skuCode);
                } else {
                    tarWarehouse = warehouse;
                    targetWarehosue = warehouseChannelService.findByChannelCodeAndWarehouseCodeAndSkuCode(channelCode, warehouse.getWarehouseCode(), skuCode);
                }
            }


            if (targetWarehosue == null) {
                targetWarehosue = new WarehousesChannel(tarWarehouse.getWarehouseCode(), allotWarehouse.getSkuId(), skuCode, allotWarehouse.getSkuName(), channelCode, count, count, 0, 0, channel, tarWarehouse);
            } else {
                targetWarehosue.setUseCount(targetWarehosue.getUseCount() + count);
                targetWarehosue.setOverall(targetWarehosue.getOverall() + count);
            }

            allotWarehouse.setUseCount(allotWarehouse.getUseCount() - count);
            allotWarehouse.setOverall(allotWarehouse.getOverall() - count);
            warehouseChannelService.save(targetWarehosue);
            warehouseChannelService.save(allotWarehouse);

//            JSONObject applyObj = new JSONObject();
//            applyObj.put("skuCode",allotWarehouse.getSkuCode());
//            applyObj.put("channelCode",allotWarehouse.getChannelCode());
//            applyObj.put("warehouseCode",allotWarehouse.getWarehouseCode());
//            applyObj.put("orderNum","");
//            applyObj.put("count",count);
//            applyObj.put("type","omsClean");
//            applyArray.add(applyObj);

            //自动生成调拨指令单，每个sku一条
            String allotCode = "AAC" + new Date().getTime();
            AllotDetail detail = new AllotDetail(allotCode, allotWarehouse.getSkuId(), allotWarehouse.getSkuCode(), allotWarehouse.getSkuName(), count, count, count);
            Set<AllotDetail> detailSet = new HashSet<>();
            detailSet.add(detail);
            AllotCommand command = new AllotCommand(allotCode, "system", "system", 3, detailSet, allotWarehouse.getChannel(), targetWarehosue.getWarehouse(), allotWarehouse.getWarehouse(), null, null, AllotType.OMS_CLEAN);
            commandDao.save(command);
            //保存一条调拨申请
            Set<AllotApplyDetail> allotApplyDetails = new HashSet<>();
            AllotApplyDetail applyDetail = new AllotApplyDetail(null, Integer.valueOf(count));
            allotApplyDetails.add(applyDetail);
            AllotApply allotApply = new AllotApply(allotWarehouse.getSkuId(), skuCode, allotWarehouse.getSkuName(), count, comment, AllotType.OMS_CLEAN, 2, allotCode, channel, warehouses, allotApplyDetails);
            applyDao.save(allotApply);
        }

//        allotApply(applyArray.toJSONString());
        return "SUCCESS";
    }

    @Override
    public String omsCleanCommand(String jsonData, Integer userId) throws yesmywineException {
        JSONObject object = JSON.parseObject(jsonData);
        Integer allotWarehouseId = Integer.valueOf(object.getString("allotWarehouseId"));
        JSONArray tarArray = object.getJSONArray("tarWarehouses");
        List<JSONObject> applyList = new ArrayList<>();
        for (int m = 0; m < tarArray.size(); m++) {
            JSONObject oneObject = (JSONObject) tarArray.get(m);
            applyList.add(oneObject);
        }
        Collections.sort(applyList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                Integer tarWarehouseId_1 = Integer.valueOf(o1.getString("tarWarehouseId"));
                Integer tarWarehouseId_2 = Integer.valueOf(o2.getString("tarWarehouseId"));
                return tarWarehouseId_1.compareTo(tarWarehouseId_2);
            }
        });
        Integer oldWarehouseId = 0;
        Map<String, Object> map = new HashedMap();
        List<AllotCommand> commandList = new ArrayList<>();
        for (int i = 0; i < applyList.size(); i++) {
            JSONObject oneAllot = applyList.get(i);
            Integer applyId = Integer.valueOf(oneAllot.getString("applyId"));
            Integer tarWarehouseId = Integer.valueOf(oneAllot.getString("tarWarehouseId"));
            Warehouses tarWarehouse = warehouseDao.findOne(tarWarehouseId);
            ValueUtil.verifyNotExist(tarWarehouse, "无此调拨仓库");
            AllotApply allotApply = applyDao.findOne(applyId);
            ValueUtil.verifyNotExist(allotApply, "无此调拨申请");
            map.put(allotApply.getType(), "11");
            AllotCommand command = new AllotCommand();
            String allotCode = "";
            if (oldWarehouseId == tarWarehouseId) {//同一个调拨指令
                AllotDetail detail = new AllotDetail(allotCode, allotApply.getSkuId(), allotApply.getSkuCode(), allotApply.getSkuName(), allotApply.getCount(), allotApply.getCount(), 0);
                command.addDetails(detail);
            } else {//新的调拨指令
                if (i != 0) {
                    commandList.add(command);
                    commandList.clear();
                }
                allotCode = "AAC" + new Date().getTime();

                command = new AllotCommand();
                command.setAllotCode(allotCode);
                command.setTarWarehouse(tarWarehouse);
                command.setAllotwarehouse(allotApply.getWarehouse());
                command.setChannel(allotApply.getChannel());
                command.setType(AllotType.OMS_CLEAN);
                command.setStatus(0);
                command.setProducer(String.valueOf(userId));

                AllotDetail detail = new AllotDetail(allotCode, allotApply.getSkuId(), allotApply.getSkuCode(), allotApply.getSkuName(), allotApply.getCount(), allotApply.getCount(), 0);
                command.addDetails(detail);
            }
            oldWarehouseId = tarWarehouseId;

            //冻结库存
            String freezeResult = freezeInventory(command);

            String result = "";
            if (freezeResult.equals("SUCCESS")) {
                //向WMS发送出库指令
                result = sendRemovalCommandToWMS(command);
            } else {
                ValueUtil.isError("冻结库存失败，无法制作调拨指令！");
            }


            if (result.equals("SUCCESS")) {
                //修改调拨单信息
                allotApply.setStatus(1);//申请指令处理中
                command.setStatus(1);// 调拨指令出库中
            } else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("WMS无响应,无法制作调拨指令！");
            }
            applyDao.save(allotApply);
            commandDao.save(commandList);
        }

        for (Map.Entry entry : map.entrySet()) {
            String key = (String) entry.getKey();
            if (key.equals("omsClean")) {
                ValueUtil.isError("包含清关仓的调拨申请，无法制作调拨指令，请重新制作");
            }
        }

//        AllotApply allotApply = allotDao.findOne(applyId);
//        ValueUtil.verifyNotExist(allotApply, "无此调拨申请");
//        Integer allCount = allotApply.getAllotCount();
//        Warehouses warehouse = warehouseDao.findOne(warehouseId);
//        if (warehouse == null) {
//            ValueUtil.isError("无此仓库");
//        }
//        allotApply.setWarehouse(warehouse);
//        allotApply.setCount(allotApply.getAllotCount());
//        allotApply.setAllotCode("AAC" + new Date().getTime());
//        allotDao.save(allotApply);
//
//        JSONArray freezeArray = new JSONArray();
//        JSONObject freezeObject = new JSONObject();
//        Integer allotWarehouseId = allotApply.getAllotwarehouse().getId();
//        freezeObject.put("skuCode", allotApply.getSkuCode());
//        freezeObject.put("channelCode", allotApply.getChannel().getChannelCode());
//        freezeObject.put("warehouseCode", allotApply.getAllotwarehouse().getWarehouseCode());
//        freezeObject.put("count", allotApply.getCount());
//        freezeObject.put("type", "yes");
//        freezeArray.add(freezeObject);
//
//        //冻结库存
//        String freezeResult = channelsInventoryService.freeze(freezeArray.toJSONString());
//        String result = "";
//        if (freezeResult.equals("SUCCESS")) {
//            //向WMS发送出库指令
//            result = sendRemovalCommandToWMS(allotApply);
//        } else {
//            ValueUtil.isError("冻结库存失败，无法制作调拨指令！");
//        }
//
//
//        if (result.equals("SUCCESS")) {
//            //修改调拨单信息
//            allotApply.setApplyStatus(1);//申请指令处理中
//            allotApply.setCommandStatus(0);// 调拨指令出库中
////                allotApply.setOperatorId();
//            allotDao.save(allotApply);
//        } else {
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            ValueUtil.isError("WMS无响应,无法制作调拨指令！");
//        }
        return "SUCCESS";
    }

    @Override
    public String audit(Integer commandId, String comment, String userId) throws yesmywineException {
        AllotCommand command = commandDao.findOne(commandId);
        if (command.getStatus() != 0) {
            ValueUtil.isError("非待审核指令无法审核！");
        }
        command.setComment(comment);
        command.setAuditor(userId);
        command.setAuditTime(new Date());
        command.setStatus(1);
        commandDao.save(command);
        //向WMS发送出库指令
        String result = sendRemovalCommandToWMS(command);
        return result;
    }

    @Override
    public String reject(Integer commandId, String comment, String userId) throws yesmywineException {
        AllotCommand command = commandDao.findOne(commandId);
        if (command.getStatus() != 0) {
            ValueUtil.isError("非待审核指令无法审核！");
        }
        ValueUtil.verify(comment, "comment");
        command.setComment(comment);
        command.setAuditor(userId);
        command.setAuditTime(new Date());
        command.setStatus(-1);
        commandDao.save(command);
        //释放冻结库存
        releaseFreezeInventory(command);
        return "SUCCESS";
    }


    public List<AllotApply> findByIdIn(Integer[] applyIds) {
        return applyDao.findByIdIn(applyIds);
    }


}
