package com.yesmywine.ware.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.SynchronizeUtils;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.dao.SendChannelsHistoryDao;
import com.yesmywine.ware.dao.SendChannelsHistoryDetailDao;
import com.yesmywine.ware.dao.WarehouseChannelDao;
import com.yesmywine.ware.entity.*;
import com.yesmywine.ware.service.*;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SJQ on 2017/4/17.
 */
@Service
@Transactional
public class SendChannelHistoryServiceImpl extends BaseServiceImpl<SendChannelHistory, Integer> implements SendChannelHistoryService {
    @Autowired
    private WarehousesChannelService warehouseChannelService;
    @Autowired
    private WarehouseChannelDao warehouseChannelDao;
    @Autowired
    private ChannelsInventoryService channelsInventoryService;
    @Autowired
    private ChannelsService channelsService;
    @Autowired
    private WarehousesService warehousesService;
    @Autowired
    private SendChannelsHistoryDao sendChannelsHistoryDao;
    @Autowired
    private SendChannelsHistoryDetailDao sendChannelsHistoryDetailDao;

    @Override
    public String sentChannelInventory(Integer mainChannelId, Integer skuId, Integer count) throws yesmywineException {
        Channels querychannels = channelsService.findOne(mainChannelId);
        ValueUtil.verifyNotExist(querychannels, "渠道不存在");
        ChannelsInventory channelsInventory = channelsInventoryService.findByChannelAndSkuId(querychannels, skuId);
        if (channelsInventory.getUseCount() >= count) {
            channelsInventory.setUseCount(channelsInventory.getUseCount() - count);
            channelsInventory.setAllCount(channelsInventory.getAllCount() - count);
        } else {
            ValueUtil.isError("该渠道可用库存不足");
        }

        SendChannelHistory sendChannelHistory = new SendChannelHistory();
        sendChannelHistory.setSkuId(skuId);
        sendChannelHistory.setSkuCode(channelsInventory.getSkuCode());
        sendChannelHistory.setSkuName(channelsInventory.getSkuName());
        sendChannelHistory.setCount(count);
        sendChannelHistory.setChannel(channelsInventory.getChannel());
        sendChannelsHistoryDao.save(sendChannelHistory);

        //从库存最小的开始向通用库存中分配
        List<WarehousesChannel> list = null;
        try {
            list = warehouseChannelDao.findByChannelCodeAndSkuCodeOrderByUseCountAsc(querychannels.getChannelCode(), channelsInventory.getSkuCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<WarehousesChannel> updateList = new ArrayList<>();
        List<SendChannelHistoryDetails> historyDetailList = new ArrayList<>();
        //减少分配仓库库存
        for (int i = 0; i < list.size(); i++) {

            WarehousesChannel warehouseChannel = list.get(i);
            Integer useCount = warehouseChannel.getUseCount();
            if (useCount == 0) {
                continue;
            }
            Warehouses warehouse = warehouseChannel.getWarehouse();
            SendChannelHistoryDetails sendChannelHistoryDetail = new SendChannelHistoryDetails();
            sendChannelHistoryDetail.setSkuId(skuId);
            sendChannelHistoryDetail.setSkuCode(channelsInventory.getSkuCode());
            sendChannelHistoryDetail.setChannel(channelsInventory.getChannel());
            sendChannelHistoryDetail.setWarehouse(warehouse);
            sendChannelHistoryDetail.setHistoryId(sendChannelHistory.getId());
            count = useCount - count;
            if (count < 0) {
                addCommonChannelInventory(skuId, warehouseChannel.getSkuCode(), warehouseChannel.getSkuName(), querychannels.getChannelCode(), warehouse.getWarehouseCode(), warehouseChannel.getUseCount());
                warehouseChannel.setUseCount(0);
                warehouseChannel.setOverall(warehouseChannel.getOverall() - useCount);
                sendChannelHistoryDetail.setCount(warehouseChannel.getUseCount());
                updateList.add(warehouseChannel);
                historyDetailList.add(sendChannelHistoryDetail);
                count = -count;
            } else {
                addCommonChannelInventory(skuId, warehouseChannel.getSkuCode(), warehouseChannel.getSkuName(), querychannels.getChannelCode(), warehouse.getWarehouseCode(), useCount - count);
                warehouseChannel.setUseCount(count);
                warehouseChannel.setOverall(warehouseChannel.getOverall() - (useCount - count));
                sendChannelHistoryDetail.setCount(useCount - count);
                updateList.add(warehouseChannel);
                historyDetailList.add(sendChannelHistoryDetail);
                break;
            }
        }

        warehouseChannelService.save(updateList);
        channelsInventoryService.save(channelsInventory);
        sendChannelsHistoryDetailDao.save(historyDetailList);
        Channels channels = channelsService.findOne(mainChannelId);
        if (channels.getChannelCode().equals("GW") || channels.getChannelCode().equals("HT")) {
            JSONObject sendChannelJSON = new JSONObject();
            sendChannelJSON.put("skuId", channelsInventory.getSkuId());
            sendChannelJSON.put("skuCode", channelsInventory.getSkuCode());
            sendChannelJSON.put("skuName", channelsInventory.getSkuName());
            sendChannelJSON.put("count", sendChannelHistory.getCount());
            String result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channelInventory/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "subToCommon", sendChannelJSON), RequestMethod.post);

            if (result == null || !result.equals("201")) {
                sendChannelHistory.setSynStatus(0);
                sendChannelsHistoryDao.save(sendChannelHistory);
                //发送站内信
            }
        }
        return "SUCCESS";
    }

    @Override
    public List<SendChannelHistoryDetails> historyDetails(Integer id) throws yesmywineException {
        ValueUtil.verify(id, "Id");
        return sendChannelsHistoryDetailDao.findByHistoryId(id);
    }

    //增加通用渠道库存
    public void addCommonChannelInventory(Integer skuId, String skuCode, String skuName, String channelCode, String warehouseCode, Integer count) throws yesmywineException {
        //判断 该仓库、该渠道下是否有库存，有则相加，无则新增
        Channels queryChannel = channelsService.findOne(9999);
        ValueUtil.verifyNotExist(queryChannel, "无此渠道！");
        Warehouses queryWarehouse = warehousesService.findByWarehouseCode(warehouseCode);
        ValueUtil.verifyNotExist(queryWarehouse, "无此仓库！");
        WarehousesChannel ifExist = warehouseChannelDao.findByChannelCodeAndWarehouseCodeAndSkuCode(queryChannel.getChannelCode(), warehouseCode, skuCode);

        //根据channelId、skuId判断渠道库存是否有货
        ChannelsInventory channelsInventory = channelsInventoryService.findByChannelAndSkuId(queryChannel, skuId);
        if (ifExist == null) {
            //向仓库、渠道、sku关联表中插入数据
            WarehousesChannel warehouseChannel = new WarehousesChannel();

            warehouseChannel.setChannel(queryChannel);
            warehouseChannel.setChannelCode(queryChannel.getChannelCode());
            warehouseChannel.setSkuId(skuId);
            warehouseChannel.setSkuCode(skuCode);
            warehouseChannel.setSkuName(skuName);
            warehouseChannel.setWarehouse(queryWarehouse);
            warehouseChannel.setWarehouseCode(queryWarehouse.getWarehouseCode());
            warehouseChannel.setOverall(count);
            warehouseChannel.setUseCount(count);
            warehouseChannel.setFreezeCount(0);
            warehouseChannel.setEnRouteCount(0);
            warehouseChannelService.save(warehouseChannel);


            if (channelsInventory != null) {
                //更改渠道库存表渠道sku商品的数量
                channelsInventory.setUseCount(channelsInventory.getUseCount() + count);
                channelsInventory.setAllCount(channelsInventory.getAllCount() + count);
                channelsInventoryService.save(channelsInventory);
            } else {
                //向渠道库存表中插入数据
                ChannelsInventory newChannelsInventory = new ChannelsInventory();

                newChannelsInventory.setChannel(queryChannel);
                newChannelsInventory.setChannelCode(queryChannel.getChannelCode());
                newChannelsInventory.setSkuId(skuId);
                newChannelsInventory.setSkuCode(skuCode);
                newChannelsInventory.setSkuName(skuName);
                newChannelsInventory.setAllCount(count);
                newChannelsInventory.setUseCount(count);
                newChannelsInventory.setFreezeCount(0);
                newChannelsInventory.setEnRouteCount(0);
                channelsInventoryService.save(newChannelsInventory);
            }

        } else {
            //更改數量
            ifExist.setUseCount(ifExist.getUseCount() + count);
            ifExist.setOverall(ifExist.getOverall() + count);
            warehouseChannelService.save(ifExist);

            channelsInventory.setUseCount(channelsInventory.getUseCount() + count);
            channelsInventory.setAllCount(channelsInventory.getAllCount() + count);
            channelsInventoryService.save(channelsInventory);
        }
    }
}
