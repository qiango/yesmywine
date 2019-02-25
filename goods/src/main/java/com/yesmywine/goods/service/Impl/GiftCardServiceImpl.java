package com.yesmywine.goods.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.goods.common.SynchronizeGiftCard;
import com.yesmywine.goods.dao.GiftCardHistoryDao;
import com.yesmywine.goods.dao.SkuDao;
import com.yesmywine.goods.entity.GiftCard;
import com.yesmywine.goods.dao.GiftCardDao;
import com.yesmywine.goods.entity.GiftCardHistory;
import com.yesmywine.goods.entity.Sku;
import com.yesmywine.goods.entity.SkuProp;
import com.yesmywine.goods.service.GiftCardService;
import com.yesmywine.httpclient.bean.HttpBean;
import com.yesmywine.util.basic.*;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.date.DateUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.httpclient.bean.RequestMethod;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by SJQ on 2017/2/13.
 */
@Service
@Transactional
public class GiftCardServiceImpl extends BaseServiceImpl<GiftCard, Long> implements GiftCardService {
    @Autowired
    private GiftCardDao giftCardDao;
    @Autowired
    private GiftCardHistoryDao giftCardHistoryDao;
    @Autowired
    private SkuDao skuDao;

    public  Map<String, Object> updateLoad(Long id) throws yesmywineException {//加载显示礼品卡
        ValueUtil.verify(id, "idNull");
        GiftCard giftCard = giftCardDao.findOne(id);
        Map<String, Object> map = new HashMap<>();
        map.put("giftCard",giftCard);
        if(giftCard.getSkuId()!=null){
            List<Sku> sku=new ArrayList<>();
            Sku sku1=skuDao.findOne(giftCard.getSkuId());
            sku.add(sku1);
            map.put("sku",sku);
        }
        return map;



    }

    public String bound(String jsonData) throws yesmywineException {//pass接收礼品卡绑定信息接口
        JSONArray adjustArray = JSON.parseArray(jsonData);
        for (int i = 0; i < adjustArray.size(); i++) {
            JSONObject adjustCommand = (JSONObject) adjustArray.get(i);
            String phoneNumber = adjustCommand.getString("phoneNumber");
            String cardNumber = adjustCommand.getString("cardNumber");
            String password = adjustCommand.getString("password");

            //HttpBean httpRequest = new HttpBean(ConstantData.userService+ "/userService/userInfomation/showone", RequestMethod.post);
            HttpBean httpRequest = new HttpBean(Dictionary.PAAS_HOST + "/user/userInfo/showOne/itf", RequestMethod.get);
            httpRequest.addParameter("phoneNumber", phoneNumber);
            httpRequest.run();
            String temp = httpRequest.getResponseContent();
            String userId = ValueUtil.getFromJson(temp, "data", "userId");
            String userName = ValueUtil.getFromJson(temp, "data", "userName");
            GiftCard giftCard = giftCardDao.findByCardNumberAndPassword(cardNumber, password);
            giftCard.setBoundTime(new Date());
            giftCard.setBoundStatus(1);//绑定状态 1已绑定
            giftCard.setUserId(Integer.valueOf(userId));
            giftCard.setUserName(userName);
            giftCardDao.save(giftCard);

     //商城或门店把礼品卡绑定到个人账户下后，将通过此接口把相关绑定信息同步给 PAAS，并自动同步给商城。
            String  result= SynchronizeGiftCard.boundGiftCard(ValueUtil.toJson(HttpStatus.SC_CREATED, giftCard));
            if(result==null||!result.equals("201")) {
                TransactionAspectSupport.currentTransactionStatus().isRollbackOnly();
                ValueUtil.isError("同步失败");
            }
        }
        return "success";
    }

    public String spend(String jsonData) throws yesmywineException {//pass礼品卡消费信息接口
        JSONArray adjustArray = JSON.parseArray(jsonData);
        for (int i = 0; i < adjustArray.size(); i++) {
            JSONObject adjustCommand = (JSONObject) adjustArray.get(i);
            String cardNumber = adjustCommand.getString("cardNumber");
            Long orderNo = Long.valueOf(adjustCommand.getString("orderNo"));
            Double usedAmount = Double.valueOf(adjustCommand.getString("usedAmount"));
            String usedTime1 = adjustCommand.getString("usedTime");
            Date usedTime = DateUtil.toDate(usedTime1, "yyyy-mm-dd hh:mm:ss");
            Integer channel = Integer.valueOf(adjustCommand.getString("channel"));

            GiftCard giftCard = giftCardDao.findByCardNumber(cardNumber);
            if (giftCard.getStatus() == 0) {
                String latestTime = giftCard.getLatestTime().toString();
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d2 = null;
                try {
                    d2 = sdf1.parse(latestTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date date=new Date();
                if(d2.getTime()>date.getTime()){
                    ValueUtil.isError("超过最迟激活时间，不可使用！");
                }
                giftCard.setStatus(1);
                giftCard.setActivationTime(new Date());
                Integer inDate = giftCard.getInDate();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, inDate);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                System.out.println(sdf.format(calendar.getTime()));
                giftCard.setActualMaturity(calendar.getTime());
            }
            GiftCardHistory giftCardHistory = new GiftCardHistory();
            giftCardHistory.setCardNumber(cardNumber);//卡号
            giftCardHistory.setOrderNo(orderNo);//订单编号
            giftCardHistory.setUsedAmount(usedAmount);//消费金额
            giftCardHistory.setUsedTime(usedTime);//消费时间
            giftCardHistory.setChannel(channel);//消费渠道（0官网、1门店）
            giftCardHistory.setGiftCardId(giftCard.getId());//礼品卡id
            giftCardHistory.setType(0);//类型：消费
            giftCardHistoryDao.save(giftCardHistory);
            Double remainingSum = giftCard.getRemainingSum();
            Double remainingSum1 = remainingSum - usedAmount;
            giftCard.setRemainingSum(remainingSum1);//剩余金额
            giftCardDao.save(giftCard);
        //口把消费信息同步给商城。
        String  result= SynchronizeGiftCard.spendGiftCard(ValueUtil.toJson(HttpStatus.SC_CREATED, giftCard));
            String  result1= SynchronizeGiftCard.synchronizeHistory(ValueUtil.toJson(HttpStatus.SC_CREATED, giftCardHistory));

            if(result==null||!result.equals("201")||result1==null||!result1.equals("201")){
                TransactionAspectSupport.currentTransactionStatus().isRollbackOnly();
                ValueUtil.isError("同步失败");
            }
    }
        return "success";
    }

    public GiftCard showGiftCard(String jsonData) throws yesmywineException {
        GiftCard giftCard=null;
        JSONArray adjustArray = JSON.parseArray(jsonData);
        for (int i = 0; i < adjustArray.size(); i++) {
            JSONObject adjustCommand = (JSONObject) adjustArray.get(i);
            String cardNumber =adjustCommand.getString("cardNumber");
            String password = adjustCommand.getString("password");
            giftCard = giftCardDao.findByCardNumberAndPassword(cardNumber, password);
            HttpBean httpRequest = new HttpBean(Dictionary.PAAS_HOST+ "/userService/userInfomation/showone", RequestMethod.get);
            httpRequest.addParameter("userId", giftCard.getUserId());
//            httpRequest.addParameter("userName", giftCard.getUserName());
            httpRequest.run();
            String temp = httpRequest.getResponseContent();
            String phone = ValueUtil.getFromJson(temp, "data", "phoneNumber");
//            giftCard.setPhone(phone);
        }
        return giftCard;
    }

    public String synchronizeGiftCard(String jsonDatas) throws yesmywineException {//商城创建礼品卡后同步接口
        GiftCard giftCard=new GiftCard();
        String cardName = ValueUtil.getFromJson(jsonDatas, "data", "cardName");
        giftCard.setCardName(cardName);//礼品卡名称
        Integer type = Integer.valueOf(ValueUtil.getFromJson(jsonDatas, "data", "type"));
        giftCard.setType(type);//礼品卡类型（0,电子/1,实体）
        String cardNumber = ValueUtil.getFromJson(jsonDatas, "data", "cardNumber");
        giftCard.setCardNumber(cardNumber);//卡号
        String password = ValueUtil.getFromJson(jsonDatas, "data", "password");
        giftCard.setPassword(password);//密码
        Double amounts = Double.valueOf(ValueUtil.getFromJson(jsonDatas, "data", "amounts"));
        giftCard.setAmounts(amounts);//礼品卡面值
        Double remainingSum = Double.valueOf(ValueUtil.getFromJson(jsonDatas, "data", "remainingSum"));
        giftCard.setRemainingSum(remainingSum);//礼品卡余额
        String latestTime = ValueUtil.getFromJson(jsonDatas, "data", "latestTime");
         if(latestTime!=null){
                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy K:m:s a", Locale.ENGLISH);
                Date d2 = null;
                try {
                    d2 = sdf.parse(latestTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(d2);
                giftCard.setLatestTime(d2);
            }
        Integer inDate = Integer.valueOf(ValueUtil.getFromJson(jsonDatas, "data", "inDate"));
        giftCard.setInDate(inDate);//有效期（单位：天）
        String actualMaturity = ValueUtil.getFromJson(jsonDatas, "data", "actualMaturity");
            if(actualMaturity!=null){
                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy K:m:s a", Locale.ENGLISH);
                Date d2 = null;
                try {
                    d2 = sdf.parse(actualMaturity);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(d2);
                giftCard.setActualMaturity(d2);
            }
        String activityId = ValueUtil.getFromJson(jsonDatas, "data", "activityId");
        giftCard.setActivityId(Integer.valueOf(activityId));//活动id
        Integer status = Integer.valueOf(ValueUtil.getFromJson(jsonDatas, "data", "status"));
        giftCard.setStatus(status);//激活状态（0待激活/1已激活）
        String activationTime = ValueUtil.getFromJson(jsonDatas, "data", "activationTime");
        if(activationTime!=null){
                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy K:m:s a", Locale.ENGLISH);
                Date d2 = null;
                try {
                    d2 = sdf.parse(activationTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(d2);
                giftCard.setActivationTime(d2);
            }
        Integer boundStatus = Integer.valueOf(ValueUtil.getFromJson(jsonDatas, "data", "boundStatus"));
        giftCard.setBoundStatus(boundStatus);//绑定状态（0未绑定/1已绑定）
        String boundTime = ValueUtil.getFromJson(jsonDatas, "data", "boundTime");
        if(boundTime!=null){
                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy K:m:s a", Locale.ENGLISH);
                Date d2 = null;
                try {
                    d2 = sdf.parse(boundTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(d2);
                giftCard.setBoundTime(d2);
            }
        String userId = ValueUtil.getFromJson(jsonDatas, "data", "userId");
        if(userId!=null){
                giftCard.setUserId(Integer.valueOf(userId));
                String userName = ValueUtil.getFromJson(jsonDatas, "data", "userName");
                giftCard.setUserName(userName);
        }
        giftCardDao.save(giftCard);
        return "success";
    }

    public String spendGiftCard(String jsonDatas) throws yesmywineException {//商城礼品卡消费后同步给pass接口
        String cardNumber = ValueUtil.getFromJson(jsonDatas, "data", "cardNumber");
        GiftCard giftCard = giftCardDao.findByCardNumber(cardNumber);
        Integer status = Integer.valueOf(ValueUtil.getFromJson(jsonDatas, "data", "status"));
        giftCard.setStatus(status);//激活状态（0待激活/1已激活）
        Double remainingSum = Double.valueOf(ValueUtil.getFromJson(jsonDatas, "data", "remainingSum"));
        giftCard.setRemainingSum(remainingSum);//礼品卡余额
        String activationTime = ValueUtil.getFromJson(jsonDatas, "data", "activationTime");
        if (giftCard.getActivationTime() == null) {
            if (activationTime != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy K:m:s a", Locale.ENGLISH);
                Date d2 = null;
                try {
                    d2 = sdf.parse(activationTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(d2);
                giftCard.setActivationTime(d2);
            }
        }
        String actualMaturity = ValueUtil.getFromJson(jsonDatas, "data", "actualMaturity");
        if (giftCard.getActualMaturity() == null) {
            if (actualMaturity != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy K:m:s a", Locale.ENGLISH);
                Date d2 = null;
                try {
                    d2 = sdf.parse(actualMaturity);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(d2);
                giftCard.setActualMaturity(d2);
            }
        }
        giftCardDao.save(giftCard);
        return "success";
    }


    public String giftCardHistory(String jsonDatas) throws yesmywineException {//商城礼品卡消费后记录同步给pass接口
        String cardNumber =ValueUtil.getFromJson(jsonDatas, "data", "cardNumber");
        Long orderNo = Long.valueOf(ValueUtil.getFromJson(jsonDatas, "data", "orderNo"));
        Double usedAmount = Double.valueOf(ValueUtil.getFromJson(jsonDatas, "data", "usedAmount"));
        String usedTime = ValueUtil.getFromJson(jsonDatas, "data", "usedTime");
        Integer channel = Integer.valueOf(ValueUtil.getFromJson(jsonDatas, "data", "channel"));
        Long giftCardId = Long.valueOf(ValueUtil.getFromJson(jsonDatas, "data", "giftCardId"));
        Integer type = Integer.valueOf(ValueUtil.getFromJson(jsonDatas, "data", "type"));

        GiftCardHistory giftCardHistory = new GiftCardHistory();
        giftCardHistory.setCardNumber(cardNumber);//卡号
        giftCardHistory.setOrderNo(orderNo);//订单编号
        giftCardHistory.setUsedAmount(usedAmount);//金额
        giftCardHistory.setGiftCardId(giftCardId);//礼品卡id
        giftCardHistory.setType(type);//类型:0消费，1退换
        if (usedTime != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy K:m:s a", Locale.ENGLISH);
            Date d2 = null;
            try {
                d2 = sdf.parse(usedTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d2);
            giftCardHistory.setUsedTime(d2);//消费时间
        }
        giftCardHistory.setChannel(channel);//消费渠道（0官网、1门店）
        giftCardHistoryDao.save(giftCardHistory);
        return "success";

    }

    public String boundGiftCard(String jsonDatas) throws yesmywineException {//商城礼品卡绑定后同步给pass接口
        String cardNumber = ValueUtil.getFromJson(jsonDatas, "data", "cardNumber");
        GiftCard giftCard = giftCardDao.findByCardNumber(cardNumber);
        Integer boundStatus = Integer.valueOf(ValueUtil.getFromJson(jsonDatas, "data", "boundStatus"));
        giftCard.setBoundStatus(boundStatus);//绑定状态（0未绑定/1已绑定）
        String boundTime = ValueUtil.getFromJson(jsonDatas, "data", "boundTime");
        if (boundTime != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy K:m:s a", Locale.ENGLISH);
            Date d2 = null;
            try {
                d2 = sdf.parse(boundTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d2);
            giftCard.setBoundTime(d2);
        }
        String userId = ValueUtil.getFromJson(jsonDatas, "data", "userId");
        if (userId != null) {
            giftCard.setUserId(Integer.valueOf(userId));
            String userName = ValueUtil.getFromJson(jsonDatas, "data", "userName");
            giftCard.setUserName(userName);
        }
        giftCardDao.save(giftCard);
        return "success";
    }
    public String buyGiftCard(String jsonDatas) throws yesmywineException {//商城礼品卡购买同步到pass接口
        String json = ValueUtil.getFromJson(jsonDatas, "data");
        JSONArray adjustArray = JSON.parseArray(json);
        List<GiftCard> giftCardList=new ArrayList<>();
        for (int i = 0; i < adjustArray.size(); i++) {
            JSONObject adjustCommand = (JSONObject) adjustArray.get(i);
            String cardNumber = adjustCommand.getString("cardNumber");
            GiftCard giftCard = giftCardDao.findByCardNumber(cardNumber);
            giftCard.setIfBuy(1);
            giftCardList.add(giftCard);
        }
        giftCardDao.save(giftCardList);
        return "success";

    }
}
