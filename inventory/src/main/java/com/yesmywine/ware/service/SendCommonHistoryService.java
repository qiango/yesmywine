package com.yesmywine.ware.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.ware.entity.SendCommonHistory;

/**
 * Created by SJQ on 2017/4/17.
 */
public interface SendCommonHistoryService extends BaseService<SendCommonHistory, Integer> {
    String sentCommonInventory(Integer warehouseChannelId, Integer[] channelIds, Integer[] counts) throws Exception;
}
