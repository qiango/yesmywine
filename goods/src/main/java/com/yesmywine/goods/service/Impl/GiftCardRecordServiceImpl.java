
package com.yesmywine.goods.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.goods.IdUtil;
import com.yesmywine.goods.bean.DeleteEnum;
import com.yesmywine.goods.common.SynchronizeGiftCard;
import com.yesmywine.goods.dao.GiftCardDao;
import com.yesmywine.goods.dao.GiftCardRecordDao;
import com.yesmywine.goods.dao.SkuDao;
import com.yesmywine.goods.entity.GiftCard;
import com.yesmywine.goods.entity.GiftCardRecord;
import com.yesmywine.goods.entity.Sku;
import com.yesmywine.goods.service.GiftCardRecordService;
import com.yesmywine.httpclient.bean.HttpBean;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.date.DateUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by SJQ on 2017/2/13.
 */

@Service
@Transactional
public class GiftCardRecordServiceImpl extends BaseServiceImpl<GiftCardRecord, Long> implements GiftCardRecordService {

    @Autowired
    private GiftCardRecordDao giftCardRecordDao;
    @Autowired
    private GiftCardDao giftCardDao;
    @Autowired
    private SkuDao skuDao;


    public String addGiftCard(Map<String, String> param) throws yesmywineException {//新增礼品卡生

        String latestTime= param.get("latestTime");
        String df = " 00:00:00";
        latestTime = latestTime+ df;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date d2 = null;
        try {
            d2 = sdf.parse(latestTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
       Date date=new Date();
       if(d2.getTime()<date.getTime()){
           ValueUtil.isError("最迟激活时间必须大于当前时间！");
       }
        GiftCardRecord giftCardRecord = new GiftCardRecord();
        giftCardRecord.setCardName(param.get("cardName"));//礼品卡名称
        Integer type = Integer.valueOf(param.get("type"));
        giftCardRecord.setType(type);//礼品卡类型（0,电子/1,实体）
        if (type.equals(0)) {
            giftCardRecord.setSkuId(Integer.valueOf(param.get("skuId")));
            giftCardRecord.setCode(param.get("code"));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddssHHmmss");
        String format = dateFormat.format(new Date());//批次编号生成 生成规则：当天日期[16位]，如：2017041821103921
        giftCardRecord.setBatchNumber(format);
        giftCardRecord.setAmounts(Double.valueOf(param.get("amounts")));
        giftCardRecord.setNumber(Integer.valueOf(param.get("number")));
        giftCardRecord.setLatestTime(d2);
        giftCardRecord.setInDate(Integer.valueOf(param.get("inDate")));
        giftCardRecord.setStatus(0);
        giftCardRecord.setDeleteEnum(DeleteEnum.NOT_DELETE);
        giftCardRecordDao.save(giftCardRecord);
        return "success";
    }

    public  Map<String, Object>  updateLoad(Long id) throws yesmywineException {//加载显示礼品卡
        ValueUtil.verify(id, "idNull");
        GiftCardRecord giftCardRecord= giftCardRecordDao.findOne(id);
        Map<String, Object> map = new HashMap<>();
        map.put("giftCardRecord",giftCardRecord);

        if(giftCardRecord.getSkuId()!=null){
            List<Sku> sku=new ArrayList<>();
            Sku sku1=skuDao.findOne(giftCardRecord.getSkuId());
            sku.add(sku1);
            map.put("sku",sku);
        }

        return map;
    }

    public String delete(Long id) throws yesmywineException {//删除礼品卡
        ValueUtil.verify(id, "idNull");
        //礼品卡生成记录保存后默认为待审核状态，可以修改或删除，审核后不可修改或删除。
        GiftCardRecord giftCardRecord = giftCardRecordDao.findOne(id);
        if (giftCardRecord.getStatus() == 1) {
            ValueUtil.isError("审核后不可删除！");
        }
        giftCardRecord.setDeleteEnum(DeleteEnum.DELETED);
        giftCardRecordDao.save(giftCardRecord);
        return "success";
    }

    public String updateSave(Map<String, String> param) throws yesmywineException {//修改保存礼品卡
        //礼品卡生成记录保存后默认为待审核状态，可以修改或删除，审核后不可修改或删除。
        Long giftCardId =Long.valueOf(param.get("id"));
        GiftCardRecord giftCardRecord = giftCardRecordDao.findOne(giftCardId);
        if (giftCardRecord.getStatus() == 1) {
            ValueUtil.isError("审核后不可修改！");
        }
        if (giftCardRecord.getStatus() == 2) {
            ValueUtil.isError("审核不通过不可修改！");
        }
        String latestTime= param.get("latestTime");
        String df = " 00:00:00";
        latestTime = latestTime+ df;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d2 = null;
        try {
            d2 = sdf.parse(latestTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date=new Date();
        if(d2.getTime()<date.getTime()){
            ValueUtil.isError("最迟激活时间必须大于当前时间！");
        }

        if (giftCardRecord.getType().equals(0)) {
            giftCardRecord.setSkuId(Integer.valueOf(param.get("skuId")));
        } else {
            giftCardRecord.setSkuId(null);
        }
        giftCardRecord.setCardName(param.get("cardName"));//礼品卡名称
        giftCardRecord.setType(Integer.valueOf(param.get("type")));//礼品卡类型（0,电子/1,实体）
        giftCardRecord.setAmounts(Double.valueOf(param.get("amounts")));
        giftCardRecord.setNumber(Integer.valueOf(param.get("number")));
        giftCardRecord.setLatestTime(d2);
        giftCardRecord.setInDate(Integer.valueOf(param.get("inDate")));
        giftCardRecordDao.save(giftCardRecord);
        return "success";
    }

    public String audit(Long id,String reason,Integer status) throws yesmywineException {//礼品卡生成记录审核
        GiftCardRecord giftCardRecord = giftCardRecordDao.findOne(id);
        if(giftCardRecord.getStatus()==1){
            ValueUtil.isError("已审核");
        }
        if(giftCardRecord.getStatus()==2){
            ValueUtil.isError("审核不通过的不能再审核");
        }
        Date date=new Date();
        if(giftCardRecord.getLatestTime().getTime()<date.getTime()){
            ValueUtil.isError("最迟激活时间必须大于当前时间！");
        }
        if(status==1) {//审核通过
            List<GiftCard> giftCardList = new ArrayList<>();
            Integer number = giftCardRecord.getNumber();
            for (int i = 0; i < number; i++) {
                GiftCard giftCard = new GiftCard();
                giftCard.setCardName(giftCardRecord.getCardName());//礼品卡名称
                giftCard.setType(giftCardRecord.getType());//礼品卡类型（0,电子/1,实体）
                giftCard.setBatchNumber(giftCardRecord.getBatchNumber());//批次编号
                Long cardNumber = IdUtil.genId("yyMMdd1{s}{s}{s}{r}{r}{s}{s}{r}{r}", giftCardRecord.getId(), 5);
                System.out.print(cardNumber);//卡号
                giftCard.setCardNumber(cardNumber.toString());//卡号
                String password = getData();
                giftCard.setPassword(password);//密码
                giftCard.setAmounts(giftCardRecord.getAmounts());//礼品卡面值
                giftCard.setRemainingSum(giftCardRecord.getAmounts());//礼品卡余额
                giftCard.setLatestTime(giftCardRecord.getLatestTime());
                giftCard.setInDate(giftCardRecord.getInDate());
                giftCard.setGiftCardRecordId(giftCardRecord.getId());//生成记录id
                giftCard.setStatus(0);//0待激活
                giftCard.setBoundStatus(0);//0未绑定
                giftCard.setIfBuy(0);//是否购买(0否/1是)
                giftCard.setSkuId(giftCardRecord.getSkuId());
                giftCardList.add(giftCard);
            }
            giftCardRecord.setStatus(1);
            giftCardRecord.setReason(reason);
            giftCardRecord.setAuditTime(new Date());

            if (giftCardRecord.getType() == 0) {
                //电子礼品卡在审核通过后自动入库，所入仓库和所属渠道根据「电子礼品卡入库设置」来。
                HttpBean httpBean = new HttpBean(Dictionary.DIC_HOST + "/dic/sysCode/itf", RequestMethod.get);
                httpBean.addParameter("sysCode", "giftCardCode");
                httpBean.run();
                String temp1 = httpBean.getResponseContent();
                String data = ValueUtil.getFromJson(temp1, "data");
                JSONArray jsonArray1 = JSONArray.parseArray(data);
                String channelCode = null;
                String warehouseCode = null;
                for (int j = 0; j < jsonArray1.size(); j++) {
                    JSONObject jsonObject = jsonArray1.getJSONObject(j);
                    if (jsonObject.get("entityCode").equals("channelCode")) {
                        channelCode = jsonObject.get("entityValue").toString();
                    }
                    if (jsonObject.get("entityCode").equals("warehouseCode")) {
                        warehouseCode = jsonObject.get("entityValue").toString();
                    }
                }
                giftCardRecord.getSkuId();
                HttpBean httpRequest = new HttpBean("http://api.hzbuvi.com/paas/inventory/oms/stock", RequestMethod.post);
                com.alibaba.fastjson.JSONArray jsonArray = new com.alibaba.fastjson.JSONArray();
                com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                jsonObject.put("certificateNum", giftCardRecord.getBatchNumber());//
                jsonObject.put("skuCode", giftCardRecord.getCode());
                jsonObject.put("warehouseCode", warehouseCode);
                jsonObject.put("channelCode", channelCode);
                jsonObject.put("orderNum", giftCardRecord.getBatchNumber());
                jsonObject.put("orderType", "gw");
                jsonObject.put("count", giftCardRecord.getNumber());
                jsonObject.put("price", 0);//价格暂定0
                jsonArray.add(jsonObject);
                String json = ValueUtil.toJson(jsonArray);
                httpRequest.addParameter("jsonData", ValueUtil.getFromJson(json, "data"));
                httpRequest.run();
                String temp = httpRequest.getResponseContent();
                String code = ValueUtil.getFromJson(temp, "code");
                if (!"201".equals(code) || code == null) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    ValueUtil.isError("电子卡入库失败！");
                }
            }
            //礼品卡生成记录审核通过后，PAAS将自动把礼品卡明细同步给商城，供商城使用。
            String result = SynchronizeGiftCard.create(ValueUtil.toJson(HttpStatus.SC_CREATED, giftCardList));
            if (result == null || !result.equals("201")) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("同步失败");
            }
            giftCardRecordDao.save(giftCardRecord);
            giftCardDao.save(giftCardList);
        }else {
            giftCardRecord.setStatus(2);
            giftCardRecord.setReason(reason);
            giftCardRecord.setAuditTime(new Date());
            giftCardRecordDao.save(giftCardRecord);
        }
        return "success";
    }

    private String getData() {
        Random r = new Random();
        String str = "";
        for (int i = 0; i < 10; i++) { // 循环10次
            Integer x = r.nextInt(10); // 0-9的随机数
            str += x.toString(); // 拼成10位数 因为int类型只能存放200000000+的数据，所以只能用字符串拼接
        }
        return str;
    }

}