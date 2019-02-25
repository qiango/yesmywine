package com.yesmywine.ware.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.ware.entity.SendChannelHistoryDetails;

import java.util.List;

/**
 * Created by Administrator on 2017/4/16 0016.
 */
public interface SendChannelsHistoryDetailDao extends BaseRepository<SendChannelHistoryDetails, Integer> {
    List<SendChannelHistoryDetails> findByHistoryId(Integer id);
}
