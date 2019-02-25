package com.yesmywine.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.yesmywine.user.dao.ChannelsDao;
import com.yesmywine.user.dao.MonIntegraDao;
import com.yesmywine.user.dao.MoneyDao;
import com.yesmywine.user.entity.Channels;
import com.yesmywine.user.entity.MonIntegra;
import com.yesmywine.util.basic.ValueUtil;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Mars on 2017/6/23.
 */

@RestController
@RequestMapping("/user/rule/itf")
public class GetRuleController {
//    http://api.hzbuvi.com/paas/web/user/rule?channelCode=%E5%95%86%E5%9F%8E
    @Autowired
    private ChannelsDao channelsDao;
    @Autowired
    private MonIntegraDao monIntegraDao;
    @Autowired
    private MoneyDao moneyDao;

    @RequestMapping(method = RequestMethod.GET)
    public String localGenerate(String channelCode) {//规则
        JSONObject jsonObject = new JSONObject();

        Channels channels = channelsDao.findByChannelCode(channelCode);
        Integer channelId = channels.getId();
        Channels channels1 = new Channels();
        channels1.setId(channelId);
        MonIntegra monIntegra = monIntegraDao.findByChannels(channels1);
        String rule = monIntegra.getProportion();

        String proportion = moneyDao.find();

        jsonObject.put("mopo", rule);//人民币兑换积分
        jsonObject.put("mobe", proportion);//人民币兑换酒豆

        return ValueUtil.toJson(HttpStatus.SC_OK, jsonObject);
    }
}