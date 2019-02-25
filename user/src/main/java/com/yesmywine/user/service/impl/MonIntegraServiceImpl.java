package com.yesmywine.user.service.impl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.user.dao.ChannelsDao;
import com.yesmywine.user.dao.MonIntegraDao;
import com.yesmywine.user.entity.Channels;
import com.yesmywine.user.entity.MonIntegra;
import com.yesmywine.user.service.MonIntegraService;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * Created by hz on 3/27/17.
 */
@Service
public class MonIntegraServiceImpl extends BaseServiceImpl<MonIntegra,Integer> implements MonIntegraService {

    @Autowired
    private MonIntegraDao monIntegraDao;
    @Autowired
    private ChannelsDao channelsDao;

    public String create(Map<String, String> parm) throws yesmywineException {
        String channelCode=parm.get("channelCode");
        String prop=parm.get("proportion");
        Channels channels=channelsDao.findByChannelCode(channelCode);
        MonIntegra monIntegra1= monIntegraDao.findByChannels(channels);
        if(ValueUtil.notEmpity(monIntegra1)){
            ValueUtil.isError("该渠道已设置规则");
        }
        MonIntegra monIntegra=new MonIntegra();
        monIntegra.setProportion(prop);
        monIntegra.setChannels(channels);
        monIntegra.setCreateTime(new Date());
        monIntegra.setUpdateTime(new Date());
        monIntegraDao.save(monIntegra);
        return "success";
    }

    public String updateSave(Integer monIntralId, String proportion) throws yesmywineException {
        MonIntegra monIntegra=monIntegraDao.findOne(monIntralId);
        monIntegra.setProportion(proportion);
        monIntegra.setUpdateTime(new Date());
        monIntegraDao.save(monIntegra);
        return "success";
    }

}
