package com.yesmywine.user.service.impl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.user.dao.BeanCenterFlowDao;
import com.yesmywine.user.dao.ChannelsDao;
import com.yesmywine.user.entity.BeanCenterFlow;
import com.yesmywine.user.service.BeanCeterService;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ${shuang} on 2017/3/30.
 */
@Service
public class BeanCenterServiceImpl extends BaseServiceImpl<BeanCenterFlow,Integer> implements BeanCeterService {
    @Autowired
    private ChannelsDao channelsDao;
    @Autowired
    private BeanCenterFlowDao beanCenterFlowDao;


    public Map<String, List> settleAccounts(String startDate, String endDate) throws yesmywineException {
        List list=beanCenterFlowDao.findAccout(startDate,endDate);
        List<Map<String,String>> list1=new ArrayList<Map<String,String>>();//地方欠中心
        List<Map<String,String>> list2=new ArrayList<Map<String,String>>();//中心欠地方
        for (int i = 0; i <list.size() ; i++) {
            Map<String,String> map=new HashMap<String,String>();
            Object[] object=(Object[]) list.get(i);
            BigDecimal beans= (BigDecimal) object[0];//豆豆
            Integer channelId= (Integer) object[1];//渠道Id
            String channelName= channelsDao.findOne(channelId).getChannelName();
            map.put("channel",channelName);
            map.put("count",beans.toString());
            if(beans.doubleValue()<0){
                list1.add(map);
            }else {
                list2.add(map);
            }
        }
        Map<String,List> map= new HashMap<String,List>();
        map.put("localOweCeter",list1);
        map.put("centerOweLocal",list2);
        return map;
    }


}
