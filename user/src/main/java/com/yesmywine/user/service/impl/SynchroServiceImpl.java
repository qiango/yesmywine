package com.yesmywine.user.service.impl;

import com.yesmywine.user.dao.BeanCenterFlowDao;
import com.yesmywine.user.dao.BeanUserFlowDao;
import com.yesmywine.user.dao.ChannelsDao;
import com.yesmywine.user.dao.UserInformationDao;
import com.yesmywine.user.entity.BeanCenterFlow;
import com.yesmywine.user.entity.BeanUserFlow;
import com.yesmywine.user.entity.Channels;
import com.yesmywine.user.entity.UserInformation;
import com.yesmywine.user.service.SynchroService;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by Mars on 2017/6/25.
 */

@Service
public class SynchroServiceImpl implements SynchroService {

    @Autowired
    private ChannelsDao channelsDao;
    @Autowired
    private BeanUserFlowDao beanUserFlowDao;
    @Autowired
    private BeanCenterFlowDao beanCenterFlowDao;
    @Autowired
    private UserInformationDao userDao;


    public String beanCenter(@RequestParam Map<String,String> params) throws yesmywineException {

        String channelCode = params.get("channelCode");
        String userName = params.get("userName");
        String oderNumber = params.get("orderNumber");
        String bean = params.get("bean");
        String userId = params.get("userId");

        Channels channels = channelsDao.findByChannelCode(channelCode);
        Integer channelId = channels.getId();
        BeanCenterFlow beanCenterFlow = new BeanCenterFlow();//中心酒豆消耗记录
        if(params.get("status").equals("generate")){

            beanCenterFlow.setUserName(userName);
            beanCenterFlow.setStatus("generate");
            beanCenterFlow.setChannelId(channelId);
            beanCenterFlow.setChannelName(channels.getChannelName());
            beanCenterFlow.setPayer(channels.getChannelName());
            beanCenterFlow.setPayee("酒豆中心");
            BigDecimal beans = new BigDecimal(Double.valueOf(bean));
            beanCenterFlow.setBeans(beans);
        }else if(params.get("status").equals("consume")){
            beanCenterFlow.setUserName(userName);
            beanCenterFlow.setStatus("consume");
            beanCenterFlow.setChannelId(channelId);
            beanCenterFlow.setChannelName(channels.getChannelName());
            beanCenterFlow.setPayer("酒豆中心");
            beanCenterFlow.setPayee(channels.getChannelName());
            BigDecimal beans = new BigDecimal(Double.valueOf(bean));
            BigDecimal temp = new BigDecimal(Double.valueOf(-1));
            beanCenterFlow.setBeans(beans.multiply(temp));
        }

        beanCenterFlowDao.save(beanCenterFlow);


        return "success";
    }


    public Object returnsConsumeSys(@RequestParam Map<String,String> params) throws yesmywineException {

        String channelCode = params.get("channelCode");
        String userName = params.get("userName");
        String oderNumber = params.get("orderNumber");
        String newBeans = params.get("newBeans");
        String returnBean = params.get("returnBean");
        String ponit = params.get("point");
        String userId = params.get("userId");
        UserInformation userInformation = userDao.findByUserNameOrPhoneNumber(userName,null);

        Channels channels = channelsDao.findByChannelCode(channelCode);
        Integer channelId = channels.getId();
        BeanUserFlow beanUserFlow = new BeanUserFlow();//个人 酒豆退回
        beanUserFlow.setUserName(userName);
        beanUserFlow.setOrderNumber(oderNumber);
        beanUserFlow.setBeans(Double.valueOf(returnBean));
        beanUserFlow.setChannels(channels);
        beanUserFlow.setPoint(0);
        beanUserFlow.setStatus("generate");
        beanUserFlow.setUserId(Integer.valueOf(userId));
        beanUserFlowDao.save(beanUserFlow);

        BeanUserFlow beanUserFlow1 = new BeanUserFlow();//个人 酒豆收回
        beanUserFlow1.setUserName(userName);
        beanUserFlow1.setOrderNumber(oderNumber);
        beanUserFlow1.setBeans(Double.valueOf(newBeans));
        beanUserFlow1.setChannels(channels);
        beanUserFlow1.setStatus("consume");//撤回
        beanUserFlow1.setPoint(Integer.valueOf(ponit));
        beanUserFlow1.setUserId(Integer.valueOf(userId));
        beanUserFlowDao.save(beanUserFlow1);

        BeanCenterFlow beanCenterFlow = new BeanCenterFlow();//中心酒豆退回是增加
        beanCenterFlow.setUserName(userName);
        beanCenterFlow.setStatus("generate");
        beanCenterFlow.setChannelId(channelId);
        beanCenterFlow.setChannelName(channels.getChannelName());
        beanCenterFlow.setPayer(channels.getChannelName());
        beanCenterFlow.setPayee("酒豆中心");
        BigDecimal beans = new BigDecimal(Double.valueOf(returnBean));
        beanCenterFlow.setBeans(beans);
        beanCenterFlowDao.save(beanCenterFlow);
        BeanCenterFlow beanCenterFlow1 = new BeanCenterFlow();//中心酒豆消耗记录
        beanCenterFlow1.setUserName(userName);
        beanCenterFlow1.setStatus("consume");
        beanCenterFlow1.setChannelId(channelId);
        beanCenterFlow1.setChannelName(channels.getChannelName());
        beanCenterFlow1.setPayer("酒豆中心");
        beanCenterFlow1.setPayee(channels.getChannelName());
        BigDecimal newBean = new BigDecimal(Double.valueOf(newBeans));
        beanCenterFlow1.setBeans(newBean.multiply(new BigDecimal(-1)));
        beanCenterFlowDao.save(beanCenterFlow1);

        return "success";

    }


    public Boolean synchronous(Integer id, String name, String type,String goodsCode ,Integer synchronous) {
        Boolean resutl = false;
        if(0 == synchronous || 1 == synchronous){
            resutl = this.save(id, name, type,goodsCode);
        }else {
            resutl = this.delete(id);
        }
        return resutl;
    }

    public Boolean save(Integer id, String name, String type,String code){
        try {
            Channels channel = new Channels();
            channel.setId(id);
            channel.setType(type);
            channel.setChannelName(name);
            channel.setChannelCode(code);
            this.channelsDao.save(channel);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public Boolean delete(Integer id){
        try {
            this.channelsDao.delete(id);
        }catch (Exception e){
            return false;
        }
        return true;
    }


}
