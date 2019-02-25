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
import com.yesmywine.util.number.DoubleUtils;
import com.yesmywine.ware.Utils.SKUUtils;
import com.yesmywine.ware.bean.HistoryType;
import com.yesmywine.ware.dao.*;
import com.yesmywine.ware.entity.*;
import com.yesmywine.ware.service.*;
import com.yesmywine.ware.task.ComputeCostPriceTask;
import org.apache.commons.collections.map.HashedMap;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.net.URLDecoder;
import java.util.*;

/**
 * Created by SJQ on SUCCESS7/2/10.
 */
@Service
@Transactional
public class ChannelsInventoryServiceImpl extends BaseServiceImpl<ChannelsInventory, Integer> implements ChannelsInventoryService {
    @Autowired
    private ChannelsInventoryDao channelsInventoryDao;

    @Autowired
    private WarehouseChannelDao warehouseChannelDao;

    @Autowired
    private WarehouseHistoryDao warehouseHistoryDao;

    @Autowired
    private ChannelsService channelsService;

    @Autowired
    private WarehousesService warehouseService;

    @Autowired
    private AllotApplyService allotApplyService;

    @Autowired
    private ReleaseFreezeFailedRecordDao releaseFreezeFailedRecordDao;

    @Autowired
    private CostPriceRecordService costPriceRecordService;

    @Autowired
    private StoresDao storeDao;

    @Autowired
    private DiscrepancyBillsService discrepancyBillsService;

    @Autowired
    private AllotCommandDao commandDao;

    @Override
    public ChannelsInventory findByChannelCodeAndSkuCode(String channelCode, String skuCode) {
        return channelsInventoryDao.findByChannelCodeAndSkuCode(channelCode, skuCode);
    }

    @Override
    public String omsStock(String jsonData, String storeCode) throws yesmywineException {
        JSONArray jsonArray = null;
        try {
            jsonArray = JSON.parseArray(jsonData);
        } catch (Exception e) {
            ValueUtil.isError("json数据格式错误");
        }
        if (storeCode != null) {
            Stores stores = storeDao.findByStoreCode(storeCode);
            Channels channels = stores.getChannel();
            Warehouses warehouses = stores.getWarehouse();
            if (channels == null || warehouses == null) {
                ValueUtil.isError("门店尚未关联渠道或仓库，无法进行入库操作");
            }
            String channelCode = channels.getChannelCode();
            String warehouseCode = warehouses.getWarehouseCode();
            for (int k = 0; k < jsonArray.size(); k++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(k);
                jsonObject.put("channelCode", channelCode);
                jsonObject.put("warehouseCode", warehouseCode);
            }

        }

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            String certificateNum = jsonObject.getString("certificateNum");
            String skuCode = jsonObject.getString("skuCode");
            Map<String, Object> map = new HashedMap();
            map.put("code", skuCode);
            String skuInfo = SynchronizeUtils.getResult(Dictionary.PAAS_HOST, "/goods/sku/code/itf", RequestMethod.get, map, null);
            if (skuInfo == null || ValueUtil.getFromJson(skuInfo, "code").equals("500")) {
                ValueUtil.isError("无此SKU或商品服务已关闭");
            }
            String skuId = ValueUtil.getFromJson(skuInfo, "data", "id");
            String skuName = ValueUtil.getFromJson(skuInfo, "data", "sku");
            String warehouseCode = jsonObject.getString("warehouseCode");
            String channelCode = jsonObject.getString("channelCode");
            String orderNum = jsonObject.getString("orderNum");
            String orderType = jsonObject.getString("orderType");
            String count = jsonObject.getString("count");
            String comment = jsonObject.getString("comment");
            String price = jsonObject.getString("price");

            ValueUtil.verify(skuId);
            ValueUtil.verify(warehouseCode);
            ValueUtil.verify(channelCode);
            ValueUtil.verify(count);
            ValueUtil.verify(price);

            Integer iSkuId = Integer.valueOf(skuId);
//            Integer iWarehouseId = Integer.valueOf(warehouseId);
            Integer iCount = Integer.valueOf(count);
            Double iPrice = Double.valueOf(price);

            //判断 该仓库、该渠道下是否有库存，有则相加，无则新增
            Channels queryChannels = channelsService.findByChannelCode(channelCode);
            if (queryChannels == null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("包含不存在的渠道");
            }
            Warehouses queryWarehouse = warehouseService.findByWarehouseCode(warehouseCode);
            if (queryWarehouse == null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("包含不存在的仓库");
            }


            WarehousesChannel ifExist = warehouseChannelDao.findByChannelAndWarehouseAndSkuCode(queryChannels, queryWarehouse, skuCode);
            //根据channelId、skuId判断渠道库存是否有货
            ChannelsInventory channelsInventory = channelsInventoryDao.findByChannelAndSkuId(queryChannels, iSkuId);

            //判断该sku是否有预售
            Integer balanceCount = checkSKUPresellInventory(skuCode, Integer.valueOf(count), ifExist, channelsInventory, queryChannels, channelCode, iSkuId, skuName, queryWarehouse, warehouseCode);
            if (balanceCount != 0) {
                if (ifExist == null) {
                    //向仓库、渠道、sku关联表中插入数据
                    WarehousesChannel warehouseChannel = new WarehousesChannel();
                    warehouseChannel.setChannel(queryChannels);
                    warehouseChannel.setChannelCode(channelCode);
                    warehouseChannel.setSkuId(iSkuId);
                    warehouseChannel.setSkuCode(skuCode);
                    warehouseChannel.setSkuName(skuName);
                    warehouseChannel.setWarehouse(queryWarehouse);
                    warehouseChannel.setWarehouseCode(warehouseCode);
                    warehouseChannel.setOverall(balanceCount);
                    warehouseChannel.setUseCount(balanceCount);
                    warehouseChannel.setFreezeCount(0);
                    warehouseChannel.setEnRouteCount(0);
                    warehouseChannelDao.save(warehouseChannel);


                    if (channelsInventory != null) {
                        //更改渠道库存表渠道sku商品的数量
                        channelsInventory.setUseCount(channelsInventory.getUseCount() + balanceCount);
                        channelsInventory.setAllCount(channelsInventory.getAllCount() + balanceCount);
                        channelsInventoryDao.save(channelsInventory);
                    } else {
                        //向渠道库存表中插入数据
                        channelsInventory = new ChannelsInventory();

                        channelsInventory.setChannel(queryChannels);
                        channelsInventory.setChannelCode(channelCode);
                        channelsInventory.setSkuId(iSkuId);
                        channelsInventory.setSkuCode(skuCode);
                        channelsInventory.setSkuName(skuName);
                        channelsInventory.setAllCount(balanceCount);
                        channelsInventory.setUseCount(balanceCount);
                        channelsInventory.setFreezeCount(0);
                        channelsInventory.setEnRouteCount(0);
                        channelsInventoryDao.save(channelsInventory);
                    }

                } else {
                    //更改數量
                    ifExist.setUseCount(ifExist.getUseCount() + balanceCount);
                    ifExist.setOverall(ifExist.getOverall() + balanceCount);
                    warehouseChannelDao.save(ifExist);

                    channelsInventory.setUseCount(channelsInventory.getUseCount() + balanceCount);
                    channelsInventory.setAllCount(channelsInventory.getAllCount() + balanceCount);
                    channelsInventoryDao.save(channelsInventory);

                }
            }
            //向历史表中插入进货信息
            WarehousesHistory warehouseHistory = new WarehousesHistory();
            warehouseHistory.setWarehouse(queryWarehouse);
            warehouseHistory.setChannel(queryChannels);
            warehouseHistory.setSkuId(iSkuId);
            warehouseHistory.setSkuCode(skuCode);
            warehouseHistory.setSkuName(skuName);
            warehouseHistory.setComment(comment);
            warehouseHistory.setCount(iCount);
            warehouseHistory.setCertificateNum(certificateNum);
            warehouseHistory.setType(HistoryType.IN);
            warehouseHistory.setOrderNum(orderNum);
            warehouseHistory.setOrderType(orderType);
            warehouseHistoryDao.save(warehouseHistory);
            //同步到官网、海淘
            String result = null;
            if (queryChannels.getChannelCode().equals("GW") || queryChannels.getChannelCode().equals("HT")) {
                JSONObject skuJSON = new JSONObject();
                skuJSON.put("skuId", skuId);
                skuJSON.put("skuCode", skuCode);
                skuJSON.put("skuName", skuName);
                skuJSON.put("count", balanceCount);
                result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channelInventory/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "stock", skuJSON), RequestMethod.post);
                if (result == null || !result.equals("201")) {
                    warehouseHistory.setSynStatus(0);
                    warehouseHistoryDao.save(warehouseHistory);
                }
            }
            warehouseHistoryDao.save(warehouseHistory);
            saveInCostPrice(iSkuId, skuCode, iCount, iPrice, skuName);

        }
        return "SUCCESS";
    }

    private Integer checkSKUPresellInventory(String skuCode, Integer count, WarehousesChannel ifExist,
                                             ChannelsInventory channelsInventory, Channels queryChannels, String channelCode,
                                             Integer iSkuId, String skuName, Warehouses queryWarehouse, String warehouseCode) {
        Map<String, Object> indexMap = new HashMap<>();
        indexMap.put("skuCode", skuCode);
        Integer balanceCount = 0;
        String indexResult = SynchronizeUtils.getResult(com.yesmywine.util.basic.Dictionary.MALL_HOST, "/goods/itf/presell/sku", RequestMethod.get, indexMap, null);
        if (indexResult != null) {
            JSONObject object = JSON.parseObject(indexResult);
            Integer data = Integer.valueOf(object.getString("data"));
            Integer freezeCount = 0;
            if (data != null && data > 0) {//改sku有预售
                if (data >= count) {
                    balanceCount = 0;
                    freezeCount = count;
                } else {
                    balanceCount = count - data;
                    freezeCount = data;
                }
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("skuCode", skuCode);
                updateMap.put("count", freezeCount);
                String updaetResult = SynchronizeUtils.getCode(com.yesmywine.util.basic.Dictionary.MALL_HOST, "/goods/itf/presell/sku", RequestMethod.post, updateMap, null);
                if (updaetResult.equals("201")) {
                    //增加冻结库存
                    if (ifExist == null) {
                        //向仓库、渠道、sku关联表中插入数据
                        WarehousesChannel warehouseChannel = new WarehousesChannel();
                        warehouseChannel.setChannel(queryChannels);
                        warehouseChannel.setChannelCode(channelCode);
                        warehouseChannel.setSkuId(iSkuId);
                        warehouseChannel.setSkuCode(skuCode);
                        warehouseChannel.setSkuName(skuName);
                        warehouseChannel.setWarehouse(queryWarehouse);
                        warehouseChannel.setWarehouseCode(warehouseCode);
                        warehouseChannel.setOverall(freezeCount);
                        warehouseChannel.setUseCount(0);
                        warehouseChannel.setFreezeCount(freezeCount);
                        warehouseChannel.setEnRouteCount(0);
                        warehouseChannelDao.save(warehouseChannel);


                        if (channelsInventory != null) {
                            //更改渠道库存表渠道sku商品的数量
                            channelsInventory.setFreezeCount(channelsInventory.getFreezeCount() + freezeCount);
                            channelsInventory.setAllCount(channelsInventory.getAllCount() + freezeCount);
                            channelsInventoryDao.save(channelsInventory);
                        } else {
                            //向渠道库存表中插入数据
                            channelsInventory = new ChannelsInventory();

                            channelsInventory.setChannel(queryChannels);
                            channelsInventory.setChannelCode(channelCode);
                            channelsInventory.setSkuId(iSkuId);
                            channelsInventory.setSkuCode(skuCode);
                            channelsInventory.setSkuName(skuName);
                            channelsInventory.setAllCount(freezeCount);
                            channelsInventory.setUseCount(0);
                            channelsInventory.setFreezeCount(freezeCount);
                            channelsInventory.setEnRouteCount(0);
                            channelsInventoryDao.save(channelsInventory);
                        }

                    } else {
                        //更改數量
                        ifExist.setFreezeCount(ifExist.getFreezeCount() + freezeCount);
                        ifExist.setOverall(ifExist.getOverall() + freezeCount);
                        warehouseChannelDao.save(ifExist);

                        channelsInventory.setFreezeCount(channelsInventory.getFreezeCount() + freezeCount);
                        channelsInventory.setAllCount(channelsInventory.getAllCount() + freezeCount);
                        channelsInventoryDao.save(channelsInventory);
                    }

                    if (queryChannels.getChannelCode().equals("GW") || queryChannels.getChannelCode().equals("HT")) {
                        JSONObject skuJSON = new JSONObject();
                        skuJSON.put("skuId", iSkuId);
                        skuJSON.put("skuCode", skuCode);
                        skuJSON.put("skuName", skuName);
                        skuJSON.put("count", freezeCount);
                        String result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channelInventory/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "addFreeze", skuJSON), RequestMethod.post);
                    }
                }
            } else {
                balanceCount = count;
            }
        } else {
            balanceCount = count;
        }
        return balanceCount;
    }

    private void saveInCostPrice(Integer skuId, String skuCode, Integer count, Double price, String skuName) throws yesmywineException {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        CostPriceRecord costPriceRecord = costPriceRecordService.findByYearAndMounthAndSkuCode(year, month, skuCode);
        if (costPriceRecord == null) {//新品进货
            costPriceRecord = new CostPriceRecord();
            costPriceRecord.setYear(year);
            costPriceRecord.setMounth(month);
            costPriceRecord.setMounthInitCount(0);
            costPriceRecord.setTotalCount(count);
            costPriceRecord.setCostPrice(price);
            costPriceRecord.setTotalPrice(price * count);
            costPriceRecord.setSkuId(skuId);
            costPriceRecord.setSkuCode(skuCode);
            costPriceRecord.setSkuName(skuName);
            costPriceRecordService.save(costPriceRecord);
            //向sku同步
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("skuId", skuId);
            jsonObject.put("skuCode", skuCode);
            jsonObject.put("price", price);
            jsonArray.add(jsonObject);
            List<CostPriceRecord> recordList = new ArrayList<>();
            recordList.add(costPriceRecord);
            //同步给sku模块
            ComputeCostPriceTask computeCostPriceTask = new ComputeCostPriceTask();
            computeCostPriceTask.costPriceCallback(jsonArray.toJSONString(), recordList, costPriceRecordService);
        } else {
            costPriceRecord.setTotalCount(costPriceRecord.getTotalCount() + count);
            Double totalPrice = DoubleUtils.add(costPriceRecord.getTotalPrice(), price * count);
            System.out.println(totalPrice);
            costPriceRecord.setTotalPrice(totalPrice);
            costPriceRecordService.save(costPriceRecord);
        }

    }

    @Override
    public String freeze(String jsonData) throws yesmywineException {
        JSONArray jsonArray = null;
        try {
            jsonArray = JSON.parseArray(jsonData);
        } catch (Exception e) {
            ValueUtil.isError("json数据格式错误");
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            String skuCode = jsonObject.getString("skuCode");
            String warehouseCode = jsonObject.getString("warehouseCode");
            String channelCode = jsonObject.getString("channelCode");
            String type = jsonObject.getString("type");
            Integer count = jsonObject.getInteger("count");

            //冻结渠道仓库库存
            Channels channels = channelsService.findByChannelCode(channelCode);
            Warehouses warehouse = warehouseService.findByWarehouseCode(warehouseCode);

            if (channels == null || warehouse == null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("包含不存在的渠道或仓库");
            }

            WarehousesChannel warehouseChannel = warehouseChannelDao.findByChannelAndWarehouseAndSkuCode(channels, warehouse, skuCode);
            ValueUtil.verifyNotExist(warehouseChannel, "此SKU无仓库库存！");
            Integer usable = warehouseChannel.getUseCount();
            if (usable >= count) {
                warehouseChannel.setUseCount(usable - count);
                warehouseChannel.setFreezeCount(warehouseChannel.getFreezeCount() + count);
                warehouseChannelDao.save(warehouseChannel);
            } else {
                ValueUtil.isError("可用库存小于可冻结库存！");
            }

            //冻结渠道库存
            ChannelsInventory channelsInventory = channelsInventoryDao.findByChannelCodeAndSkuCode(channelCode, skuCode);
            ValueUtil.verifyNotExist(channelsInventory, "此SKU无渠道库存！");
            channelsInventory.setUseCount(channelsInventory.getUseCount() - count);
            channelsInventory.setFreezeCount(channelsInventory.getFreezeCount() + count);
            channelsInventoryDao.save(channelsInventory);

//            if(type!=null&&type.equals("yes")){//需要同步到商城
//                FreezeFailedRecord ffr = new
//
//            }
        }
        return "SUCCESS";
    }

    @Override
    public String subFreeze(String jsonData) throws yesmywineException {
        JSONArray jsonArray = null;
        try {
            jsonArray = JSON.parseArray(jsonData);
        } catch (Exception e) {
            ValueUtil.isError("json数据格式错误");
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            String skuCode = jsonObject.getString("skuCode");
            String warehouseCode = jsonObject.getString("warehouseCode");
            String channelCode = jsonObject.getString("channelCode");
            String orderNum = jsonObject.getString("orderNum");
            String orderType = jsonObject.getString("orderType");
            String certificateNum = jsonObject.getString("certificateNum");
            String strCount = jsonObject.getString("count");

            String comment = jsonObject.getString("comment");

            Integer count = Integer.valueOf(strCount);

            subWarehouseInventoryAndChannelInventory(skuCode, warehouseCode, channelCode, orderNum, orderType, count, comment, certificateNum, true);
        }

        return "SUCCESS";
    }

    @Override
    public String releaseFreeze(String jsonData) throws yesmywineException {
        JSONArray jsonArray = null;
        try {
            jsonArray = JSON.parseArray(jsonData);
        } catch (Exception e) {
            ValueUtil.isError("json数据格式错误");
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            String skuCode = jsonObject.getString("skuCode");
            String warehouseCode = jsonObject.getString("warehouseCode");
            String channelCode = jsonObject.getString("channelCode");
            Integer count = jsonObject.getInteger("count");

//            Integer skuId = Integer.valueOf(str_skuId);

            Channels channels = channelsService.findByChannelCode(channelCode);
            Warehouses warehouse = warehouseService.findByWarehouseCode(warehouseCode);

            if (channels == null || warehouse == null) {
                ValueUtil.isError("仓库或渠道不存在");
            }

            //释放渠道仓库冻结库存
            Channels quseryChannel = channels;
            Warehouses quseryWarehouse = warehouse;
            WarehousesChannel warehouseChannel = warehouseChannelDao.findByChannelAndWarehouseAndSkuCode(quseryChannel, quseryWarehouse, skuCode);
            ValueUtil.verifyNotExist(warehouseChannel, "此SKU无仓库库存！");
            Integer freezeCount = warehouseChannel.getFreezeCount();
            if (freezeCount >= count) {
                warehouseChannel.setUseCount(warehouseChannel.getUseCount() + count);
                warehouseChannel.setFreezeCount(warehouseChannel.getFreezeCount() - count);
                warehouseChannelDao.save(warehouseChannel);
            } else {
                ValueUtil.isError("冻结库存小于解冻库存！");
            }
            //释放渠道冻结库存
            ChannelsInventory channelsInventory = channelsInventoryDao.findByChannelAndSkuId(quseryChannel, warehouseChannel.getSkuId());
            channelsInventory.setUseCount(channelsInventory.getUseCount() + count);
            channelsInventory.setFreezeCount(channelsInventory.getFreezeCount() - count);
            channelsInventoryDao.save(channelsInventory);

            //通知官网、海淘释放冻结库存
//            if (channels.getChannelCode().equals("GW") || channels.getChannelCode().equals("HT")) {
//                JSONObject releaseJson = new JSONObject();
//                releaseJson.put("skuId", channelsInventory.getSkuId());
//                releaseJson.put("skuCode", channelsInventory.getSkuCode());
//                releaseJson.put("count", count);
//                String result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channelInventory/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "releaseFreeze", releaseJson), RequestMethod.post);
//                if (result == null || !result.equals("201")) {
//                    ReleaseFreezeFailedRecord rffr = new ReleaseFreezeFailedRecord();
//                    rffr.setSkuId(skuId);
//                    rffr.setSkuCode(skuCode);
//                    rffr.setSkuName(skuName);
//                    rffr.setCount(count);
//                    rffr.setSynStatus(0);
//                    releaseFreezeFailedRecordDao.save(rffr);
//                    //发送站内信
//                }
//            }
        }

        return "SUCCESS";
    }

    @Override
    public List<ChannelsInventory> findBySkuId(Integer skuId) {
        return channelsInventoryDao.findBySkuId(skuId);
    }

    @Override
    public List<ChannelsInventory> findByChannelId(Integer channelId) {
        return channelsInventoryDao.findByChannelId(channelId);
    }

    @Override
    public String wmsOutOrder(String jsonData) throws yesmywineException {

        JSONArray jsonArray = null;
        try {
            JSONObject jsonOBJ = JSON.parseObject(URLDecoder.decode(jsonData, "UTF-8"));
            jsonArray = jsonOBJ.getJSONObject("xmldata").getJSONObject("data").getJSONArray("orderinfo");
        } catch (Exception e) {
            ValueUtil.isError("json数据格式错误");
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            String certificateNum = jsonObject.getString("OrderNo");
//            Integer count = jsonObject.getInteger("count");
//            String comment = jsonObject.getString("comment");
//            String allotCode = jsonObject.getString("Soreference1");//wms 中的调拨指令编码标识
            String allotCode = jsonObject.getString("OrderNo");//wms 中的调拨指令编码标识
            AllotCommand command = commandDao.findByAllotCode(allotCode);
            Set<AllotDetail> detailSet = command.getDetailSet();
            if (!command.getStatus().equals(1)) {//只有出库中的指令，才能执行入库
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("OrderNo：" + certificateNum + "  此单号的调拨指令状态非出库中，无法进行出库操作");
            }
            JSONArray skuArray = jsonObject.getJSONArray("item");
            String orderNum = null;
            String orderType = "DB";
            String comment = null;
            for (int j = 0; j < skuArray.size(); j++) {
                JSONObject skuObject = (JSONObject) skuArray.get(i);
                Integer outCount = Integer.valueOf(skuObject.getString("QtyShipped"));
                String skuCode = skuObject.getString("SKU");
                String allotWarehouseCode = command.getAllotwarehouse().getWarehouseCode();
                String channelCode = command.getChannel().getChannelCode();
                String targetWarehouseCode = command.getTarWarehouse().getWarehouseCode();
                AllotDetail allotDetail = null;
                for (AllotDetail detail : detailSet) {
                    if (detail.getSkuCode().equals(skuCode)) {
                        allotDetail = detail;
                    }
                }

                WarehousesHistory warehouseHistory = subWarehouseInventoryAndChannelInventory(skuCode, allotWarehouseCode, channelCode, orderNum, orderType, outCount, comment, certificateNum, false);

                addEnRouteCountAtTargetWarehouse(allotDetail.getSkuId(), skuCode, targetWarehouseCode, channelCode, orderNum, orderType, outCount, comment, allotDetail.getSkuName(), warehouseHistory);
                //修改调拨单信息
                command.setOutWarehouse(warehouseHistory);//出库单
                allotDetail.setAllotCount(Integer.valueOf(outCount));//实际发货数量
            }

            command.setStatus(2);//入库中
            commandDao.save(command);

            List<AllotApply> allotApplyList = allotApplyService.findByAllotCode(command.getAllotCode());
            if (allotApplyList.get(0).getType().indexOf("oms") >= 0) {
                //向WMS下发入库单
                sendWarehouseWarrantToWms(command);
            } else if (allotApplyList.get(0).getType().equals("stores")) {
                //向门店下发入库单
                sendWarehouseWarrantToStores(command);
            }
        }

        return "SUCCESS";
    }

    private void sendWarehouseWarrantToStores(AllotCommand allotApply) {
//        Integer skuId = allotApply.getSkuId();
//        String skuCode = allotApply.getSkuCode();
//        String skuName = allotApply.getSkuName();
//        Integer allotCount = allotApply.getAllotCount();
//        Integer applyCount = allotApply.getCount();
//        String allotCode = allotApply.getAllotCode();
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("skuId", skuId);
//        jsonObject.put("skuCode", skuCode);
//        jsonObject.put("skuName", skuName);
//        jsonObject.put("allotCount", allotCount);
//        jsonObject.put("applyCount", applyCount);
//        jsonObject.put("allotCode", allotCode);


    }

    private void sendWarehouseWarrantToWms(AllotCommand command) throws yesmywineException {
        JSONObject xmldata = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("OrderNo", command.getAllotCode());
        header.put("OrderType", "DB");
        header.put("CustomerID", "YMJ");
        Date now = new Date();
        header.put("ASNCreationTime", DateUtil.toString(now, "yyyy-MM-dd HH:mm:ss"));//ASN创建时间
        header.put("UserDefine1", command.getTarWarehouse().getWarehouseCode());//调拨出库仓库
        header.put("WarehouseID", command.getAllotwarehouse().getWarehouseCode());//所属仓库
        header.put("Priority", "3");
        header.put("SerialNoCatch", "Y");

        JSONArray detailsItems = new JSONArray();
        int b = 1;
        for (AllotDetail detail : command.getDetailSet()) {
            JSONObject detailsItem = new JSONObject();

            detailsItem.put("LineNo", b);//行号
            detailsItem.put("CustomerID", "YMJ");
            detailsItem.put("SKU", detail.getSkuCode());
//            detailsItem.put("SKU", "W0406UUU83000458614500006");
            detailsItem.put("ExpectedQty", detail.getApplyCount());

            String variety = null;
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("code", detail.getSkuCode());
            String skuInfo = SynchronizeUtils.getResult(Dictionary.PAAS_HOST, "/goods/sku/code/itf", RequestMethod.get, skuMap, null);

            String isExpensive = ValueUtil.getFromJson(skuInfo, "data", "isExpensive");
            if (isExpensive.equals("0")) {
                detailsItem.put("LotAtt04", "Y");//是否贵品：Y为贵品，N为非贵品
            } else {
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
        String result = SynchronizeUtils.getWmsResult(Dictionary.WMS_HOST, "putASNData", "ASN", xmldata.toJSONString());
        String returnCode = ValueUtil.getFromJson(result, "Response", "return", "returnCode");
        String msg = ValueUtil.getFromJson(result, "Response", "return", "returnDesc");
        if (!returnCode.equals("0000")) {
            command.setSynStatus(2);
            commandDao.save(command);
            ValueUtil.isError("入库同步到WMS失败，原因：" + msg);
        }
    }


    @Override
    public String wmsInOrder(String jsonData) throws yesmywineException {

        JSONArray jsonArray = null;
        try {
            JSONObject jsonOBJ = JSON.parseObject(URLDecoder.decode(jsonData, "UTF-8"));
            jsonArray = jsonOBJ.getJSONObject("xmldata").getJSONObject("data").getJSONArray("orderinfo");
        } catch (Exception e) {
            ValueUtil.isError("json数据格式错误");
        }

        try {
            jsonArray = JSON.parseArray(jsonData);
        } catch (Exception e) {
            ValueUtil.isError("json数据格式错误");
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            String allotCode = jsonObject.getString("OrderNo");
            String certificateNum = jsonObject.getString("ASNreference1");
            AllotCommand command = commandDao.findByAllotCode(allotCode);
            String orderType = "DB";
            String comment = null;
            JSONArray skuArray = jsonObject.getJSONArray("item");
            if (!command.equals(2)) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("ASNreference1：" + certificateNum + "  该入库单的调拨指令状态不是入库中，无法进行入库操作");
            }
            for (int j = 0; j < skuArray.size(); j++) {
                JSONObject skuObject = (JSONObject) jsonArray.get(j);
                String skuCode = skuObject.getString("SKU");
                Integer inCount = Integer.valueOf(skuObject.getString("ReceivedQty"));
                AllotDetail reponseDetail = null;
                for (AllotDetail detail : command.getDetailSet()) {
                    if (detail.getSkuCode().equals(skuCode)) {
                        reponseDetail = detail;
                    }
                }

                WarehousesHistory warehouseHistory = addWhInvenUableAndCInvenUsable(reponseDetail.getSkuId(), command.getTarWarehouse().getWarehouseCode(), command.getChannel().getChannelCode(), skuCode, allotCode, orderType, inCount, comment, certificateNum, reponseDetail.getSkuName());

                //修改调拨单信息
                command.setInWarehouse(warehouseHistory);//入库单
                reponseDetail.setReceiveCount(inCount);//实际收货数量
                command.setStatus(3);//调拨指令完成
//                allotApply.setApplyStatus(2);//调拨申请完成
//                allotApplyService.save(allotApply);

                //若实际收货数量不等于实际发货数量则生成差异单
                if (!reponseDetail.getReceiveCount().equals(reponseDetail.getAllotCount())) {
                    DiscrepancyBills discrepancyBills = new DiscrepancyBills();
                    discrepancyBills.setStatus("0");
                    discrepancyBills.setType("allot");
                    discrepancyBills.setSkuId(reponseDetail.getSkuId());
                    discrepancyBills.setSkuCode(reponseDetail.getSkuCode());
                    discrepancyBills.setSkuName(reponseDetail.getSkuName());
                    discrepancyBills.setAllotCode(reponseDetail.getAllotCode());
                    Integer discrepancy = reponseDetail.getReceiveCount() - reponseDetail.getAllotCount();
                    discrepancyBills.setCount(discrepancy);
                    discrepancyBillsService.save(discrepancyBills);
                }
            }

            List<AllotApply> applyList = allotApplyService.findByAllotCode(allotCode);
            applyList.forEach(allotApply -> {
                allotApply.setStatus(2);
            });
            allotApplyService.save(applyList);

        }
        return "SUCCESS";
    }

    @Override
    public Integer findBySkuIdCount(Integer skuId) {
        return channelsInventoryDao.findBySkuIdCount(skuId);
    }

    @Override
    public ChannelsInventory findByChannelAndSkuId(Channels querychannels, Integer skuId) {
        return channelsInventoryDao.findByChannelAndSkuId(querychannels, skuId);
    }

    //调拨指令，wms或门店入库后，目标仓库增加可用库存，减少在途库存
    private WarehousesHistory addWhInvenUableAndCInvenUsable(Integer skuId, String warehouseCode, String channelCode, String skuCode, String orderNum, String orderType, Integer count, String comment, String certificateNum, String skuName) throws yesmywineException {
        //扣减渠道仓库冻结库存与总库存
        Warehouses warehouse = warehouseService.findByWarehouseCode(warehouseCode);
        Channels channels = channelsService.findByChannelCode(channelCode);
        if (warehouse == null || channels == null) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("包含不存在的仓库或渠道");
        }
        WarehousesChannel warehouseChannel = warehouseChannelDao.findByChannelCodeAndWarehouseCodeAndSkuCode(channelCode, warehouseCode, skuCode);
        ValueUtil.verifyNotExist(warehouseChannel, "仓库库存不存在！");
        warehouseChannel.setEnRouteCount(warehouseChannel.getEnRouteCount() - count);
        warehouseChannel.setUseCount(warehouseChannel.getUseCount() + count);
        warehouseChannelDao.save(warehouseChannel);

        //扣减渠道冻结库存与总库存
        ChannelsInventory channelsInventory = channelsInventoryDao.findByChannelCodeAndSkuCode(channelCode, skuCode);
        channelsInventory.setEnRouteCount(channelsInventory.getEnRouteCount() - count);
        channelsInventory.setUseCount(channelsInventory.getUseCount() + count);
        channelsInventoryDao.save(channelsInventory);
        //向历史表中插入出库记录
        WarehousesHistory warehouseHistory = new WarehousesHistory();
        warehouseHistory.setWarehouse(warehouse);
        warehouseHistory.setCertificateNum(certificateNum);
        warehouseHistory.setChannel(channels);
        warehouseHistory.setSkuId(skuId);
        warehouseHistory.setSkuCode(skuCode);
        warehouseHistory.setSkuName(skuName);
        warehouseHistory.setComment(comment);
        warehouseHistory.setCount(count);
        warehouseHistory.setOrderNum(orderNum);
        warehouseHistory.setOrderType(orderType);
        warehouseHistory.setType(HistoryType.IN);

        warehouseHistoryDao.save(warehouseHistory);

        //同步到官网、海淘  暂不用
//        if (channels2.getChannelCode().equals("GW") || channels2.getChannelCode().equals("HT")) {
//            JSONObject subChanneInventory = new JSONObject();
//            subChanneInventory.put("skuId", channelsInventory.getSkuId());
//            subChanneInventory.put("skuCode", channelsInventory.getSkuCode());
//            subChanneInventory.put("skuName", channelsInventory.getSkuName());
//            subChanneInventory.put("count", count);
//            String result = SynchronizeUtils.getCode(Dictionary.MALL_HOST,"/inventory/channelInventory/syn",ValueUtil.toJson(HttpStatus.SC_CREATED, "subEnRoute",subChanneInventory),RequestMethod.post);
//            if (result == null || !result.equals("201")) {
//                warehouseHistory.setSynStatus(0);
//                warehouseHistoryDao.save(warehouseHistory);
//                //发送站内信
//            }
//        }

        return warehouseHistory;
    }

    /*
    *@Author Gavin
    *@Description 向WMS下发入库单
    *@Date 2017/3/31 17:05
    *@Email gavinsjq@sina.com
    *@Params
    */
    private void sendWarehouseWarrant() {
    }

    //调拨申请，wms出库后，目标仓库增加在途库存
    private void addEnRouteCountAtTargetWarehouse(Integer skuId, String skuCode, String targetWarehouseCode, String channelCode, String orderNum, String orderType, Integer count, String comment, String skuName, WarehousesHistory warehouseHistory) throws yesmywineException {
        Channels queryChannels = channelsService.findByChannelCode(channelCode);
        ValueUtil.verifyNotExist(queryChannels, "渠道编码不存在");
        Warehouses queryWarehouse = warehouseService.findByWarehouseCode(targetWarehouseCode);
        ValueUtil.verifyNotExist(queryWarehouse, "仓库编码不存在");
//        queryWarehouse.setId(targetWarehouseId);
        //判断 该仓库、该渠道下是否有库存，有则相加，无则新增
        WarehousesChannel isExist = warehouseChannelDao.findByChannelCodeAndWarehouseCodeAndSkuCode(channelCode, targetWarehouseCode, skuCode);
        //根据channelId、skuId判断渠道库存是否有货
        ChannelsInventory channelsInventory = channelsInventoryDao.findByChannelCodeAndSkuCode(channelCode, skuCode);
        if (null == isExist) {

            //向仓库、渠道、sku关联表中插入数据
            WarehousesChannel warehouseChannel = new WarehousesChannel();
            warehouseChannel.setChannel(queryChannels);
            warehouseChannel.setChannelCode(queryChannels.getChannelCode());
            warehouseChannel.setSkuId(skuId);
            warehouseChannel.setSkuCode(skuCode);
            warehouseChannel.setSkuName(skuName);
            warehouseChannel.setWarehouse(queryWarehouse);
            warehouseChannel.setWarehouseCode(queryWarehouse.getWarehouseCode());
            warehouseChannel.setOverall(count);
            warehouseChannel.setUseCount(0);
            warehouseChannel.setFreezeCount(0);
            warehouseChannel.setEnRouteCount(count);
            warehouseChannelDao.save(warehouseChannel);

            if (channelsInventory != null) {
                //更改渠道库存表渠道sku商品的数量
                channelsInventory.setEnRouteCount(count);
                channelsInventory.setAllCount(channelsInventory.getAllCount() + count);
                channelsInventoryDao.save(channelsInventory);
            } else {
                //向渠道库存表中插入数据
                channelsInventory = new ChannelsInventory();
                channelsInventory.setChannel(queryChannels);
                channelsInventory.setChannelCode(queryChannels.getChannelCode());
                channelsInventory.setSkuId(skuId);
                channelsInventory.setSkuCode(skuCode);
                channelsInventory.setSkuName(skuName);
                channelsInventory.setAllCount(count);
                channelsInventory.setUseCount(0);
                channelsInventory.setFreezeCount(0);
                channelsInventory.setEnRouteCount(count);
                channelsInventoryDao.save(channelsInventory);
            }

        } else {
            //更改數量
            isExist.setEnRouteCount(isExist.getEnRouteCount() + count);
            isExist.setOverall(isExist.getOverall() + count);
            warehouseChannelDao.save(isExist);

            channelsInventory.setEnRouteCount(channelsInventory.getEnRouteCount() + count);
            channelsInventory.setAllCount(channelsInventory.getAllCount() + count);
            channelsInventoryDao.save(channelsInventory);
        }

        //暂时不用同步到商城
//        if (channels.getChannelCode().equals("GW") || channels.getChannelCode().equals("HT")) {
//            JSONObject subChanneInventory = new JSONObject();
//            subChanneInventory.put("skuId", channelsInventory.getSkuId());
//            subChanneInventory.put("skuCode", channelsInventory.getSkuCode());
//            subChanneInventory.put("skuName", channelsInventory.getSkuName());
//            subChanneInventory.put("count", count);
//            String result = SynchronizeUtils.getCode(Dictionary.MALL_HOST,"/inventory/channelInventory/syn",ValueUtil.toJson(HttpStatus.SC_CREATED, "addEnRoute",subChanneInventory),RequestMethod.post);
//            if (result == null || !result.equals("201")) {
//                warehouseHistory.setSynStatus(0);
//                warehouseHistoryDao.save(warehouseHistory);
//                //发送站内信
//            }
//        }
    }

    //根据订单减少冻结库存
    private WarehousesHistory subWarehouseInventoryAndChannelInventory(String skuCode, String warehouseCode, String channelCode, String orderNum, String orderType, Integer count, String comment, String certificateNum, Boolean b) throws yesmywineException {
        //扣减渠道仓库冻结库存与总库存
        Channels queryChannels = channelsService.findByChannelCode(channelCode);
        ValueUtil.verifyNotExist(queryChannels, "渠道编码不存在");
        Warehouses queryWarehouse = warehouseService.findByWarehouseCode(warehouseCode);
        ValueUtil.verifyNotExist(queryWarehouse, "仓库编码不存在");
        WarehousesChannel warehouseChannel = warehouseChannelDao.findByChannelCodeAndWarehouseCodeAndSkuCode(channelCode, warehouseCode, skuCode);
        ValueUtil.verifyNotExist(warehouseChannel, "此SKU无仓库库存！");
        if (warehouseChannel.getFreezeCount() >= count) {
            warehouseChannel.setOverall(warehouseChannel.getOverall() - count);
            warehouseChannel.setFreezeCount(warehouseChannel.getFreezeCount() - count);
            warehouseChannelDao.save(warehouseChannel);
        } else {
            ValueUtil.isError("冻结库存大于可扣减库存！");
        }
        Integer skuId = warehouseChannel.getSkuId();
        String skuName = warehouseChannel.getSkuName();

        //扣减渠道冻结库存与总库存
        ChannelsInventory channelsInventory = channelsInventoryDao.findByChannelAndSkuId(queryChannels, skuId);
        channelsInventory.setAllCount(channelsInventory.getAllCount() - count);
        channelsInventory.setFreezeCount(channelsInventory.getFreezeCount() - count);
        channelsInventoryDao.save(channelsInventory);

        //向历史表中插入出库记录
        WarehousesHistory warehouseHistory = new WarehousesHistory();
        warehouseHistory.setWarehouse(queryWarehouse);
        warehouseHistory.setCertificateNum(certificateNum);
        warehouseHistory.setChannel(queryChannels);
        warehouseHistory.setSkuId(skuId);
        warehouseHistory.setSkuCode(skuCode);
        warehouseHistory.setSkuName(skuName);
        warehouseHistory.setComment(comment);
        warehouseHistory.setCount(count);
        warehouseHistory.setOrderNum(orderNum);
        warehouseHistory.setOrderType(orderType);
        warehouseHistory.setType(HistoryType.OUT);

//        if (b) {
//            //同步到官网、海淘
//            if (queryChannels.getChannelCode().equals("GW") || queryChannels.getChannelCode().equals("HT")) {
//                JSONObject subChanneInventory = new JSONObject();
//                subChanneInventory.put("skuId", channelsInventory.getSkuId());
//                subChanneInventory.put("skuCode", channelsInventory.getSkuCode());
//                subChanneInventory.put("count", count);
//                String result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channelInventory/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "sub", subChanneInventory), RequestMethod.post);
//                if (result == null || !result.equals("201")) {
//                    warehouseHistory.setSynStatus(0);
//                    //发送站内信
//                }
//            }
//        }

        warehouseHistoryDao.save(warehouseHistory);
        return warehouseHistory;
    }

}
