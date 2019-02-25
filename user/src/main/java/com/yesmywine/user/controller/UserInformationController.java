package com.yesmywine.user.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.user.dao.DeliveryAddressDao;
import com.yesmywine.user.dao.UserInformationDao;
import com.yesmywine.user.dao.VipRuleDao;
import com.yesmywine.user.entity.DeliveryAddress;
import com.yesmywine.user.entity.UserInformation;
import com.yesmywine.user.entity.VipRule;
import com.yesmywine.user.service.UserInformationService;
import com.yesmywine.user.service.VipRuleService;
import com.yesmywine.util.basic.*;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.util.number.DoubleUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by SJQ on 2017/4/20.
 */
@RestController
@RequestMapping("/user/userInfo")
public class UserInformationController{

    @Autowired
    private UserInformationService userInformationService;
    @Autowired
    private VipRuleService vipRuleService;
    @Autowired
    private VipRuleDao vipRuleDao;
    @Autowired
    private DeliveryAddressDao addressDao;
    @Autowired
    private UserInformationDao userDao;

    /*
    *@Author Gavin
    *@Description 查询用户列表及详情
    *@Date 2017/4/20 16:54
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize,Integer id){
        MapUtil.cleanNull(params);
        if(ValueUtil.notEmpity(id)){
            UserInformation userInformation = userInformationService.findOne(id);
            Integer userId = userInformation.getUserId();
            JSONObject jsonObject =ValueUtil.toJsonObject(userInformation);
            List<DeliveryAddress> list = addressDao.findByUserId(userId);
            jsonObject.put("receivingAddress",list);
            return ValueUtil.toJson(HttpStatus.SC_OK,jsonObject);
        }
        if (null != params.get("all") && params.get("all").toString().equals("true")) {
            return ValueUtil.toJson(userInformationService.findAll());
        } else if (null != params.get("all")) {
            params.remove(params.remove("all").toString());
        }
        if (params.get("levelId") != null ) {
            Integer levelId = Integer.valueOf(params.get("levelId").toString());
            VipRule vipRule = new VipRule();
            vipRule.setId(levelId);
            params.remove(params.remove("levelId").toString());
            params.put("vipRule", vipRule);
        }
        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }
        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());
        pageModel.addCondition(params);
        pageModel = userInformationService.findAll(pageModel);
        return ValueUtil.toJson(pageModel);
    }

    /*
    *@Author Gavin
    *@Description 门店创建用户
    *@Date 2017/4/20 16:58
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/doRegister/itf",method = RequestMethod.POST)
    public String storesRegeist(String jsonData){
        try {
            JSONObject userJson = JSON.parseObject(jsonData);
            String phone = userJson.getString("phoneNumber");
            UserInformation userInformation = userInformationService.findByPhoneNumber(phone);
            if(userInformation!=null){
                ValueUtil.isError("该手机号的用户已存在！");
            }
            UserInformation nameUser = userInformationService.findByUserName(phone);
            if(nameUser!=null){
                ValueUtil.isError("该手机号的用户名重复！");
            }

            userInformation = new UserInformation();
            userInformation.setUserName(phone);
            userInformation.setPhoneNumber(phone);
            userInformation.setBean(0.0);
            userInformation.setRegisterChannel("stores");
            userInformation.setGrowthValue(0);
            userInformation.setRemainingSum(0.0);
            VipRule vipRule = vipRuleService.findOne(1);
            userInformation.setVipRule(vipRule);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            userInformation.setVoluntarily(sdf.format(new Date()));
            userInformation.setBindEmailFlag(false);
            userInformation.setBindPhoneFlag(true);
            userInformation.setChannelType(0);
            userInformationService.save(userInformation);

            //向商城同步用户信息
            String mall_result = SynchronizeUtils.getResult(Dictionary.MALL_HOST,"/userService/userInfomation/synchronization", com.yesmywine.httpclient.bean.RequestMethod.post,ValueUtil.toJson(HttpStatus.SC_CREATED,"save",userInformation));
            if(mall_result!=null){
                JSONObject jsonObject = JSON.parseObject(mall_result);
                JSONObject user = jsonObject.getJSONObject("data");
                if(user!=null){
                    String userId = user.getString("id");
                    userInformation.setUserId(Integer.valueOf(userId));
                    userInformationService.save(userInformation);
                }else{
                    userInformation.setSynStatus(0);
                    userInformationService.save(userInformation);
                }
            }else{
                userInformation.setSynStatus(0);
                userInformationService.save(userInformation);
            }

            return ValueUtil.toJson(HttpStatus.SC_CREATED,userInformation);
        }catch (yesmywineException e){
            Threads.createExceptionFile("user",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }

    }

    /*
    *@Description 接收商城的同步信息
    */
    @RequestMapping(value = "/syn",method = RequestMethod.POST)
    public String syn(String jsonData){
        JSONObject jsonObject = JSON.parseObject(jsonData);
        JSONObject userJson = jsonObject.getJSONObject("data");
        String userId = userJson.getString("id");
        String userName = userJson.getString("userName");
        String password=userJson.getString("password");
        String paymentPassword=userJson.getString("paymentPassword");
        String phoneNumber = userJson.getString("phoneNumber");
        String bindPhoneFlag = userJson.getString("bindPhoneFlag");
        String nickName = userJson.getString("nickName");
        String email = userJson.getString("email");
        String bindEmailFlag = userJson.getString("bindEmailFlag");
        String IDCardNum = userJson.getString("IDCardNum");
        String bean = userJson.getString("bean");
        String registerChannel = userJson.getString("registerChannel");
        String growthValue = userJson.getString("growthValue");
        String remainingSum = userJson.getString("remainingSum");
        String voluntarily = userJson.getString("voluntarily");
        String channelType = userJson.getString("channelType");
        JSONObject vipRuleObject = userJson.getJSONObject("v");
        String levelId = vipRuleObject.getString("id");
        VipRule vipRule = vipRuleDao.findByMallId(Integer.valueOf(levelId));

        UserInformation userInformation = userInformationService.findByUserId(Integer.parseInt(userId));

        if(userInformation==null){
            userInformation = new UserInformation();
            userInformation.setUserId(Integer.valueOf(userId));
            userInformation.setUserName(phoneNumber);
            userInformation.setPhoneNumber(phoneNumber);
            userInformation.setBindPhoneFlag(Boolean.valueOf(bindPhoneFlag));
            userInformation.setNickName(nickName);
            userInformation.setEmail(email);
            userInformation.setPaymentPassword(paymentPassword);
            userInformation.setPassword(password);
            userInformation.setBindEmailFlag(Boolean.valueOf(bindEmailFlag));
            userInformation.setIDCardNum(IDCardNum);
            userInformation.setBean(Double.valueOf(bean));
            userInformation.setRegisterChannel(registerChannel);
            userInformation.setGrowthValue(Integer.valueOf(growthValue));
            userInformation.setRemainingSum(Double.valueOf(remainingSum));
            userInformation.setVoluntarily(voluntarily);
            userInformation.setVipRule(vipRule);
            userInformation.setChannelType(Integer.valueOf(channelType));
        }else{
            userInformation.setUserName(userName);
            userInformation.setPhoneNumber(phoneNumber);
            userInformation.setBindPhoneFlag(Boolean.valueOf(bindPhoneFlag));
            userInformation.setNickName(nickName);
            userInformation.setPaymentPassword(paymentPassword);
            userInformation.setPassword(password);
            userInformation.setEmail(email);
            userInformation.setBindEmailFlag(Boolean.valueOf(bindEmailFlag));
            userInformation.setIDCardNum(IDCardNum);
            userInformation.setBean(Double.valueOf(bean));
            userInformation.setRegisterChannel(registerChannel);
            userInformation.setGrowthValue(Integer.valueOf(growthValue));
            userInformation.setRemainingSum(Double.valueOf(remainingSum));
            userInformation.setVoluntarily(voluntarily);
            userInformation.setVipRule(vipRule);
            userInformation.setChannelType(Integer.valueOf(channelType));
        }

        userInformationService.save(userInformation);

        return ValueUtil.toJson(HttpStatus.SC_CREATED,userInformation);
    }

    /*
    *@Author Gavin
    *@Description 重新同步用户信息
    *@Date 2017/4/20 17:06
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/reSyn",method = RequestMethod.POST)
    public String reSyn(Integer id){
        try {
            ValueUtil.verify(id,"id");
            UserInformation userInformation = userInformationService.findOne(id);
            //向商城同步用户信息
            String mall_result = SynchronizeUtils.getResult(Dictionary.MALL_HOST,"/userService/userInfomation/synchronization", com.yesmywine.httpclient.bean.RequestMethod.post,ValueUtil.toJson(HttpStatus.SC_CREATED,userInformation));
            if(mall_result!=null){
                JSONObject jsonObject = JSON.parseObject(mall_result);
                JSONObject user = jsonObject.getJSONObject("data");
                if(user!=null){
                    String userId = user.getString("id");
                    userInformation.setUserId(Integer.valueOf(userId));
                    userInformationService.save(userInformation);
                    return ValueUtil.toJson(HttpStatus.SC_CREATED,userInformation);
                }else{
                    ValueUtil.isError("同步失败！");
                }
            }
            return null;
        }catch (yesmywineException e){
            Threads.createExceptionFile("user",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    /*
    *@Author Gavin
    *@Description 修改用户信息
    *@Date 2017/4/20 17:06
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(method = RequestMethod.PUT)
    public String update(@RequestParam Map<String,String> map){
        try {
            ValueUtil.verify(map,new String[]{"phoneNumber","nickName","email","id"});
            String phoneNumber= map.get("phoneNumber");
            String nickName= map.get("nickName");
            String email= map.get("email");
            Integer id= Integer.valueOf(map.get("id"));
            List<UserInformation> userInformations = userDao.findByNickNameOrPhoneNumberOrEmail(nickName,phoneNumber,email);
            if(userInformations.size()>1){
                for (int i = 0; i <userInformations.size() ; i++) {
                    UserInformation userInformation= userInformations.get(i);
                    if(userInformation.getId()!=id){
                        String phoneNumberOld=userInformation.getPhoneNumber() ;
                        String nickNameOld= userInformation.getNickName();
                        String emailOld= userInformation.getEmail();
                        if(phoneNumber.equals(phoneNumberOld)){
                            return ValueUtil.toJson(HttpStatus.SC_INTERNAL_SERVER_ERROR,"手机号已被使用","erro");
                        }else if(nickName.equals(nickNameOld)){
                            return ValueUtil.toJson(HttpStatus.SC_INTERNAL_SERVER_ERROR,"昵称已被使用","erro");
                        }else if(email.equals(emailOld)){
                            return ValueUtil.toJson(HttpStatus.SC_INTERNAL_SERVER_ERROR,"邮箱已被使用","erro");
                        }
                    }
                }
            }
            UserInformation userInformation = userInformationService.findOne(Integer.valueOf(map.get("id")));
                userInformation.setPhoneNumber(phoneNumber);
                userInformation.setBindPhoneFlag(true);
                userInformation.setEmail(email);
                userInformation.setBindEmailFlag(true);
            userInformation.setNickName(nickName);
            userInformationService.save(userInformation);

            //向商城同步用户信息
            String mall_result = SynchronizeUtils.getCode(Dictionary.MALL_HOST,"/userservice/userInfomation/synchronization", ValueUtil.toJson(HttpStatus.SC_CREATED,userInformation), com.yesmywine.httpclient.bean.RequestMethod.post);

            if(mall_result==null||!mall_result.equals("201")){
                userInformation.setSynStatus(1);
                userInformationService.save(userInformation);
                //发送站内信
            }
            return ValueUtil.toJson(HttpStatus.SC_CREATED,"success");
        }catch (yesmywineException e){
            Threads.createExceptionFile("user",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    /*
    *@Description 修改用户积分或酒豆
    */
    @RequestMapping(value = "/changeBeansAndGrowthValue",method = RequestMethod.POST)
    public String changeBeansAndGrowthValue(String jsonData){
        try {
            JSONObject json = JSON.parseObject(jsonData);
            String userId = json.getString("userId");
            String growthValue = json.getString("growthValue");
            String beans = json.getString("beans");
            String type = json.getString("type");

            ValueUtil.verify(userId,"userId");
            UserInformation user = userInformationService.findByUserId(Integer.valueOf(userId));

            if(user==null){
                ValueUtil.isError("无此用户！");
            }

            if(growthValue!=null&& NumberUtils.isNumber(growthValue)){
                user.setGrowthValue(user.getGrowthValue()+Integer.valueOf(growthValue));
            }
            if (beans!=null && NumberUtils.isNumber(beans)){
                user.setBean(DoubleUtils.add(user.getBean(),Double.valueOf(beans)) );
            }
            userInformationService.save(user);

            if (type!=null&&type.equals("mall")){
                return ValueUtil.toJson(HttpStatus.SC_CREATED,"SUCCESS");
                //向商城同步用户信息
            }

//            String mall_result = SynchronizeUtils.getCode(Dictionary.MALL_HOST,"/userService/userInfomation/synchronization",ValueUtil.toJson(HttpStatus.SC_CREATED,user), RequestMethod.post);
//            if(mall_result==null||!mall_result.equals("201")){
//                user.setSynStatus(0);
//                userInformationService.save(user);
//                //发送站内信
//            }
            return ValueUtil.toJson(HttpStatus.SC_CREATED,user);
        }catch (yesmywineException e){
            Threads.createExceptionFile("user",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(value = "/getByPhone",  method = RequestMethod.GET)
    public String getByPhone(String phoneNumber) {

        return ValueUtil.toJson(userInformationService.findByUserNameOrPhoneNumber(phoneNumber,phoneNumber));
    }

    @RequestMapping(value = "/getByPhone/itf",  method = RequestMethod.POST)
    public String storesGetByPhone(String phoneNumber) {

        return ValueUtil.toJson(userInformationService.findByUserNameOrPhoneNumber(phoneNumber,phoneNumber));
    }

    @RequestMapping(value = "/showOne",  method = RequestMethod.GET)
    public String showOne(String phoneNumber,Integer userId) {
        if(ValueUtil.isEmpity(userId)){
            return ValueUtil.toJson(userInformationService.findByPhoneNumber(phoneNumber));
        }else {
            return ValueUtil.toJson(userInformationService.findByUserId(userId));
        }

    }

    @RequestMapping(value = "/showOne/itf",  method = RequestMethod.GET)
    public String showOneItf(String phoneNumber,Integer userId) {
        if(ValueUtil.isEmpity(userId)){
            return ValueUtil.toJson(userInformationService.findByPhoneNumber(phoneNumber));
        }else {
            return ValueUtil.toJson(userInformationService.findByUserId(userId));
        }

    }

    @RequestMapping(value = "/sysIntegral",  method = RequestMethod.POST)
    public String sysIntegral(@RequestParam Map<String,String> params,Integer userId) {
        try {
            ValueUtil.verify(params.get("userId"));
            ValueUtil.verify(params.get("consumeBean"));
            ValueUtil.verify(params.get("channelCode"));
            ValueUtil.verify(params.get("orderNumber"));
            ValueUtil.verify(userId);
            return userInformationService.localConsume(params,userId);
        } catch (yesmywineException e) {
            Threads.createExceptionFile("user",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }


    }
}
