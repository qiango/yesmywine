package com.yesmywine.ware.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.entity.SendChannelHistory;
import com.yesmywine.ware.entity.SendChannelHistoryDetails;

import java.util.List;

/**
 * Created by SJQ on 2017/4/17.
 */
public interface SendChannelHistoryService extends BaseService<SendChannelHistory, Integer> {
    String sentChannelInventory(Integer mainChannelId, Integer skuId, Integer count) throws yesmywineException;

    List<SendChannelHistoryDetails> historyDetails(Integer id) throws yesmywineException;
}
