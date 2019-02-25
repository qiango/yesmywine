
package com.yesmywine.user.service.impl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.user.dao.ChannelsDao;
import com.yesmywine.user.entity.Channels;
import com.yesmywine.user.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by hz on 1/6/17.
 */
@Service
@Transactional
public class ChannelServiceImpl extends BaseServiceImpl<Channels, Integer> implements ChannelService {

    @Autowired
    private ChannelsDao channelDao;


    public Boolean synchronous(Integer id, String name, Integer synchronous, String code) {
        Boolean resutl = false;
        if(0 == synchronous || 1 == synchronous){
            resutl = this.save(id, name,code);
        }else {
            resutl = this.delete(id);
        }
        return resutl;
    }

    public Boolean save(Integer id, String name, String code){
        try {
            Channels channel = new Channels();
            channel.setId(id);
            channel.setChannelName(name);
            channel.setChannelCode(code);
            this.channelDao.save(channel);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public Boolean delete(Integer id){
        try {
            this.channelDao.delete(id);
        }catch (Exception e){
            return false;
        }
        return true;
    }

}
