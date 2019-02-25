package com.yesmywine.ware.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.entity.Channels;
import com.yesmywine.ware.entity.ChannelsInventory;

import java.util.List;

/**
 * Created by SJQ on 2017/1/9.
 *
 * @Description:
 */

public interface ChannelsInventoryService extends BaseService<ChannelsInventory, Integer> {

    ChannelsInventory findByChannelCodeAndSkuCode(String channelCode, String skuCode);

    String omsStock(String jsonData, String storeCode) throws yesmywineException;

    String freeze(String jsonData) throws yesmywineException;

    String subFreeze(String jsonData) throws yesmywineException;

    String releaseFreeze(String jsonData) throws yesmywineException;

    List<ChannelsInventory> findBySkuId(Integer skuId);

    List<ChannelsInventory> findByChannelId(Integer channelId);

    String wmsOutOrder(String jsonData) throws yesmywineException;

    String wmsInOrder(String jsonData) throws yesmywineException;

    Integer findBySkuIdCount(Integer skuId);

    ChannelsInventory findByChannelAndSkuId(Channels querychannels, Integer skuId);
}
