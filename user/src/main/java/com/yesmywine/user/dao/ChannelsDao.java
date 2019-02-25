package com.yesmywine.user.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.user.entity.Channels;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

;

/**
 * Created by hz on 3/28/17.
 */
@Repository
public interface ChannelsDao extends BaseRepository<Channels,Integer> {

    Channels findByChannelCode(String channelCode);
}
