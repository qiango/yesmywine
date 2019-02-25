package com.yesmywine.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.user.dao.*;
import com.yesmywine.user.entity.*;
import com.yesmywine.user.service.BeansUserService;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.SynchronizeUtils;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_HALF_DOWN;

/**
 * Created by ${shuang} on 2017/3/28.
 */
@Service
public class BeansUserServiceImpl extends BaseServiceImpl<BeanUserFlow, Integer> implements BeansUserService {
    @Autowired
    private ChannelsDao channelsDao;
    @Autowired
    private MonIntegraDao monIntegraDao;
    @Autowired
    private MoneyDao moneyDao;


    @Autowired
    private BeanUserFlowDao beanUserFlowDao;
    @Autowired
    private BeanCenterFlowDao beanCenterFlowDao;
    @Autowired
    private UserInformationDao userInformationDao;

    public Double beansCreate(String userName,String phoneNumber, String orderNumber, Integer point, String channelCode) throws yesmywineException {
        UserInformation userInformation = userInformationDao.findByUserNameOrPhoneNumber(userName,phoneNumber);
        Channels channels = channelsDao.findByChannelCode(channelCode);
        Integer channelId = channels.getId();
        Channels channels1 = new Channels();
        channels1.setId(channelId);
        MonIntegra monIntegra = monIntegraDao.findByChannels(channels1);
        String rule = monIntegra.getProportion();
        String[] rmbpoint = rule.split(":");
        BigDecimal rmb1 = new BigDecimal(Double.valueOf(rmbpoint[0]));
        BigDecimal point1 = new BigDecimal(Double.valueOf(rmbpoint[1]));
        BigDecimal point2 = new BigDecimal(Double.valueOf(point));//传入积分
        String proportion = moneyDao.find();
        String[] rmbBeans = proportion.split(":");
        BigDecimal rmb2 = new BigDecimal(Double.valueOf(rmbBeans[0]));
        BigDecimal beans = new BigDecimal(Double.valueOf(rmbBeans[1]));
        Double newRmb = (rmb1.multiply(point2)).divide(point1, 4, ROUND_HALF_DOWN).doubleValue();//兑换的人民币
        Double newBeans = (beans.multiply(BigDecimal.valueOf(newRmb))).divide(rmb2, 4, ROUND_HALF_DOWN).doubleValue();//兑换的酒豆
        BigDecimal bigDecimal = new BigDecimal(userInformation.getBean());
        BigDecimal bigDecimal1 = new BigDecimal(newBeans);
        Double newbeans = bigDecimal.add(bigDecimal1).doubleValue();//最后个人酒豆
        userInformation.setBean(newbeans);

        BeanUserFlow beanUserFlow = new BeanUserFlow();//个人酒豆生成记录
        beanUserFlow.setUserName(userInformation.getUserName());
        beanUserFlow.setOrderNumber(orderNumber);
        beanUserFlow.setPhoneNumber(phoneNumber);
        beanUserFlow.setBeans(newBeans);
        beanUserFlow.setChannels(channels);
        beanUserFlow.setPoint(point);
        beanUserFlow.setUserId(userInformation.getUserId());
        beanUserFlow.setStatus("generate");

        BeanCenterFlow beanCenterFlow = new BeanCenterFlow();//中心酒豆生成记录
        beanCenterFlow.setUserName(userInformation.getUserName());
        beanCenterFlow.setStatus("generate");
        beanCenterFlow.setChannelId(channelId);
        beanCenterFlow.setChannelName(channels.getChannelName());
        beanCenterFlow.setPayer(channels.getChannelName());
        beanCenterFlow.setPayee("酒豆中心");
        BigDecimal newBean = new BigDecimal(newBeans);
        beanCenterFlow.setBeans(newBean);
        beanCenterFlowDao.save(beanCenterFlow);

        String usercode = SynchronizeUtils.getCode(Dictionary.MALL_HOST,
                "/userservice/userInfomation/synchronization",
                ValueUtil.toJson(userInformation), RequestMethod.post);

        if(ValueUtil.notEmpity(usercode)&&usercode.equals("201")){
            userInformation.setSynStatus(1);
        }else {
            userInformation.setSynStatus(0);//需要再次同步
        }

        String flowcode = SynchronizeUtils.getCode(Dictionary.MALL_HOST,
                "/userservice/beans/synchronization",
                ValueUtil.toJson(beanUserFlow), RequestMethod.post);
        if(ValueUtil.notEmpity(flowcode)&&flowcode.equals("201")){
            beanUserFlow.setSynStatus("1");
        }else {
            beanUserFlow.setSynStatus("0");//需要再次同步
        }
        beanUserFlowDao.save(beanUserFlow);
        userInformationDao.save(userInformation);

        return newBeans;
    }

    public String consume(String userName, String phoneNumber, String orderNumber, Integer bean, String channelCode) throws yesmywineException {
        BigDecimal beans = new BigDecimal(Double.valueOf(bean));//要消耗的豆豆
        Channels channels = channelsDao.findByChannelCode(channelCode);
        Integer channelId = channels.getId();
        UserInformation userInformation = userInformationDao.findByUserNameOrPhoneNumber(userName,phoneNumber);

        BigDecimal userBeans = new BigDecimal(userInformation.getBean());
        BigDecimal newUserBeans = userBeans.subtract(beans);//个人剩余

        userInformation.setBean(newUserBeans.setScale(4, ROUND_HALF_DOWN).doubleValue());


        BeanUserFlow beanUserFlow = new BeanUserFlow();//个人 酒豆消耗记录
        beanUserFlow.setBeans(-bean.doubleValue());
        beanUserFlow.setUserName(userInformation.getUserName());
        beanUserFlow.setOrderNumber(orderNumber);
        beanUserFlow.setPhoneNumber(phoneNumber);
        beanUserFlow.setChannels(channels);
        beanUserFlow.setPoint(0);
        beanUserFlow.setUserId(userInformation.getUserId());
        beanUserFlow.setStatus("consume");


        BeanCenterFlow beanCenterFlow = new BeanCenterFlow();//中心酒豆消耗记录
        beanCenterFlow.setUserName(userName);
        beanCenterFlow.setStatus("consume");
        beanCenterFlow.setChannelId(channelId);
        beanCenterFlow.setChannelName(channels.getChannelName());
        beanCenterFlow.setPayer("酒豆中心");
        beanCenterFlow.setPayee(channels.getChannelName());
        beanCenterFlow.setBeans(beans.multiply(new BigDecimal(-1)));
        beanCenterFlowDao.save(beanCenterFlow);

        String usercode = SynchronizeUtils.getCode(Dictionary.MALL_HOST,
                "/userService/userInfomation/synchronization",
                ValueUtil.toJson(userInformation), RequestMethod.post);
        if(ValueUtil.notEmpity(usercode)&&usercode.equals("201")){
            userInformation.setSynStatus(1);
        }else {
            userInformation.setSynStatus(0);//需要再次同步
        }

        String flowcode = SynchronizeUtils.getCode(Dictionary.MALL_HOST,
                "/userService/beans/synchronization",
                ValueUtil.toJson(beanUserFlow), RequestMethod.post);
        if(ValueUtil.notEmpity(flowcode)&&flowcode.equals("201")){
            beanUserFlow.setSynStatus("1");
        }else {
            beanUserFlow.setSynStatus("0");//需要再次同步
        }
        beanUserFlowDao.save(beanUserFlow);
        userInformationDao.save(userInformation);


        return bean.toString();
    }

    public String beanFlowSys(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);
        JSONObject beanFlowJson = jsonObject.getJSONObject("data");
        String userName = beanFlowJson.getString("userName");
        String userId = beanFlowJson.getString("userId");
        String beans = beanFlowJson.getString("beans");
        String point = beanFlowJson.getString("points");
        String orderNumber = beanFlowJson.getString("orderNumber");
        String channelCode = beanFlowJson.getString("channelCode");
        String status =  beanFlowJson.getString("status");
        String describe =  beanFlowJson.getString("description");

        Channels channels = channelsDao.findByChannelCode(channelCode);
        BeanUserFlow beanFlow =new BeanUserFlow();
        beanFlow.setBeans(Double.valueOf(beans));
        beanFlow.setOrderNumber(orderNumber);
        beanFlow.setUserName(userName);
        beanFlow.setPoint(Integer.parseInt(point));
        beanFlow.setChannels(channels);
        beanFlow.setUserId(Integer.parseInt(userId));
        beanFlow.setStatus(status);
        beanFlow.setDescription(describe);
        beanUserFlowDao.save(beanFlow);
        return "success";
    }

    public String sytomall(Integer beanUserFlowId) {
        BeanUserFlow beanUserFlow = beanUserFlowDao.findOne(beanUserFlowId);

        String flowcode = SynchronizeUtils.getCode(Dictionary.MALL_HOST,
                "/userService/beans/synchronization",
                ValueUtil.toJson(beanUserFlow), RequestMethod.post);
        if(ValueUtil.notEmpity(flowcode)&&flowcode.equals("201")){
            return ValueUtil.toError(HttpStatus.SC_OK,"同步成功");
        }else {
            return ValueUtil.toError(HttpStatus.SC_INTERNAL_SERVER_ERROR,"同步失败");

        }
    }


}



