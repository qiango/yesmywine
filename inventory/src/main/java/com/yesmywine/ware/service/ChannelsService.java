package com.yesmywine.ware.service;


import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.entity.Channels;

import java.util.List;
import java.util.Map;

/**
 * Created by SJQ on 2017/1/9.
 *
 * @Description:
 */
public interface ChannelsService extends BaseService<Channels, Integer> {
    String deleteChannel(Integer channelId) throws yesmywineException;

    Boolean checkNameRepeat(String name);

    List<Channels> findByParentChannelIsNull();

    Channels create(Channels channels, Map<String, String> params) throws yesmywineException;

    Channels update(Channels channels, Map<String, String> params, Integer channelId) throws yesmywineException;

    List<Channels> findByType(Integer type);

    Channels findByChannelCode(String channelCode);
}
