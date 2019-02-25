package com.yesmywine.ware.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.SynchronizeUtils;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.dao.ChannelsDao;
import com.yesmywine.ware.entity.Channels;
import com.yesmywine.ware.entity.ChannelsInventory;
import com.yesmywine.ware.service.ChannelsInventoryService;
import com.yesmywine.ware.service.ChannelsService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by SJQ on 2017/2/10.
 */
@Service
@Transactional
public class ChannelsServiceImpl extends BaseServiceImpl<Channels, Integer>
        implements ChannelsService {
    @Autowired
    private ChannelsDao channelsDao;

    @Autowired
    private ChannelsInventoryService channelsInventoryService;

    @Override
    public Boolean checkNameRepeat(String name) {
        List<Channels> list = channelsDao.findByChannelName(name);
        if (list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Channels> findByParentChannelIsNull() {
        return channelsDao.findByParentChannelIsNull();
    }

    @Override
    public Channels create(Channels channels, Map<String, String> params) throws yesmywineException {
        ValueUtil.verify(params, new String[]{"channelName"});
        if (checkNameRepeat(params.get("channelName"))) {
            ValueUtil.isError("名称重复");
        }
        channels.setChannelCode("C" + String.valueOf(new Date().getTime()));
        String strParentId = params.get("parentChannelId");
        if (null != strParentId && !strParentId.equals("null") && !strParentId.equals("")) {
            Integer parentId = Integer.valueOf(strParentId);
            Channels parentChannel = channelsDao.findOne(parentId);
            ValueUtil.verifyNotExist(parentChannel, "无此父级渠道");
            channels.setParentChannel(parentChannel);
            channels.setType(parentChannel.getType());
        }
        channels.setCanDelete(true);
        channelsDao.save(channels);
        //向OMS与商城库存、PAAS商品服务、商城商品服务同步渠道
        sendToOmsChannel(channels, 0);
        String mall_result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channels/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "save", channels), RequestMethod.post);
        String paas_goods_result = SynchronizeUtils.getCode(Dictionary.PAAS_HOST, "/goods/channel/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "0", channels), RequestMethod.post);
        String mall_goods_result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/goods/channel/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "0", channels), RequestMethod.post);
        String paas_user_result = SynchronizeUtils.getCode(Dictionary.PAAS_HOST, "/user/synchro/channel", ValueUtil.toJson(HttpStatus.SC_CREATED, "0", channels), RequestMethod.post);
        String mall_user_result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/user/synchro/channel", ValueUtil.toJson(HttpStatus.SC_CREATED, "0", channels), RequestMethod.post);
        if (mall_result == null || !mall_result.equals("201")) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            //回滚oms系统中的渠道信息
            sendToOmsChannel(channels, 2);
            ValueUtil.isError("渠道创建失败：无法同步在商城中创建新渠道！");
        }
        if (paas_goods_result == null || !paas_goods_result.equals("201")) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            //回滚oms、商城渠道服务中的渠道信息
            sendToOmsChannel(channels, 2);
            SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channels/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "delete", channels.getId()), RequestMethod.post);
            ValueUtil.isError("渠道创建失败：无法同步在PAAS商品服务中创建新渠道！");
        }
        if (mall_goods_result == null || !mall_goods_result.equals("201")) {
            //回滚oms、商城渠道服务、paas商品服务中的渠道信息
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            sendToOmsChannel(channels, 2);
            SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channels/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "delete", channels.getId()), RequestMethod.post);
            SynchronizeUtils.getCode(Dictionary.PAAS_HOST, "/goods/channel/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "2", channels), RequestMethod.post);
            ValueUtil.isError("渠道创建失败：无法同步在商城商品服务中创建新渠道！");
        }
        
        return channels;
    }

    private void sendToOmsChannel(Channels channels, Integer status) throws yesmywineException {//status 0-增  1-改  2-删
        JSONObject requestJson = new JSONObject();
        requestJson.put("function", status);
        JSONObject dataJson = new JSONObject();
//        dataJson.put("channelCode",channels.getChannelCode());
        dataJson.put("channelCode", channels.getChannelCode());
        dataJson.put("channelName", channels.getChannelName());
        Integer type = channels.getType();
        switch (type) {
            case 0:
                dataJson.put("channelType", "实渠道");
                break;
            case 1:
                dataJson.put("channelType", "门店分公司");
                break;
            case 2:
                dataJson.put("channelType", "客服系统");
                break;
            case 3:
                dataJson.put("channelType", "通用");
                break;
            case 4:
                dataJson.put("channelType", "海淘");
                break;
        }
        if (channels.getIfSale()) {
            dataJson.put("ifSale", "是");
        } else {
            dataJson.put("ifSale", "否");
        }
        if (channels.getIfProcurement()) {
            dataJson.put("ifProcurement", "是");
        } else {
            dataJson.put("ifProcurement", "否");
        }
        if (channels.getIfInventory()) {
            dataJson.put("ifInventory", "是");
        } else {
            dataJson.put("ifInventory", "否");
        }
        dataJson.put("parentChannelCode", channels.getParentChannel() == null ? "" : channels.getParentChannel().getChannelCode());
        requestJson.put("data", dataJson);
        //向oms同步渠道信息
        String result = SynchronizeUtils.getOmsResult(Dictionary.OMS_HOST, "/updateBaseCustomerChannel", RequestMethod.post, "", requestJson.toJSONString());
        if (result != null) {
            String respStatus = ValueUtil.getFromJson(result, "status");
            String respMessage = ValueUtil.getFromJson(result, "message");
            if (!respStatus.equals("success")) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("向OMS同步渠道失败,原因："+respMessage);
            }
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("向OMS同步渠道失败链接OMS服务失败");
        }
    }

    @Override
    public Channels update(Channels channels, Map<String, String> params, Integer channelId) throws yesmywineException {
        ValueUtil.verify(channelId);
        ValueUtil.verify(params, new String[]{"id", "channelName", "type"});
        Channels oldChannel = channelsDao.findOne(channelId);
        Boolean canDelete = oldChannel.getCanDelete();
        if (!canDelete) {
            ValueUtil.isError("此数据无法修改！");
        }
        if (!params.get("channelName").equals(oldChannel.getChannelName())) {
            if (checkNameRepeat(params.get("channelName"))) {
                ValueUtil.isError("名称重复");
            }
        }
        if (!channels.getChannelCode().equals(oldChannel.getChannelCode())) {
            ValueUtil.isError("渠道编码不可修改");
        }
        String strParentId = params.get("parentChannelId");
        if (null != strParentId && !strParentId.equals("") && !strParentId.equals("null")) {
            Integer parentId = Integer.valueOf(strParentId);
            Channels parentChannel = channelsDao.findOne(parentId);
            channels.setParentChannel(parentChannel);
        }
        channelsDao.save(channels);

        //向OMS与商城库存、PAAS商品服务、商城商品服务同步渠道
        sendToOmsChannel(channels, 1);
        String mall_result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channels/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "update", channels), RequestMethod.post);
        String paas_goods_result = SynchronizeUtils.getCode(Dictionary.PAAS_HOST, "/goods/channel/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "1", channels), RequestMethod.post);
        String mall_goods_result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/goods/channel/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "1", channels), RequestMethod.post);
        String paas_user_result = SynchronizeUtils.getCode(Dictionary.PAAS_HOST, "/user/synchro/channel", ValueUtil.toJson(HttpStatus.SC_CREATED, "1", channels), RequestMethod.post);
        String mall_user_result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/user/synchro/channel", ValueUtil.toJson(HttpStatus.SC_CREATED, "1", channels), RequestMethod.post);

        if (mall_result == null || !mall_result.equals("201")) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            //回滚oms渠道信息
            sendToOmsChannel(channelsDao.findOne(channelId), 1);
            ValueUtil.isError("渠道修改失败：无法同步在商城中修改渠道！");
        }
        if (paas_goods_result == null || !paas_goods_result.equals("201")) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            //回滚oms、商城渠道服务，渠道信息
            Channels channel = channelsDao.findOne(channelId);
            sendToOmsChannel(channel, 1);
            SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channels/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "update", channel), RequestMethod.post);
            ValueUtil.isError("渠道修改失败：无法同步在PAAS商品服务中修改渠道！");
        }
        if (mall_goods_result == null || !mall_goods_result.equals("201")) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            //回滚oms、商城渠道服务、paas商品服务，渠道信息
            Channels channel = channelsDao.findOne(channelId);
            sendToOmsChannel(channel, 1);
            SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channels/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "update", channel), RequestMethod.post);
            SynchronizeUtils.getCode(Dictionary.PAAS_HOST, "/goods/channel/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "1", channels), RequestMethod.post);
            ValueUtil.isError("渠道修改失败：无法同步在商城商品服务中修改渠道！");
        }
       
        return channels;
    }

    @Override
    public List<Channels> findByType(Integer type) {
        return channelsDao.findByType(type);
    }

    @Override
    public Channels findByChannelCode(String channelCode) {
        return channelsDao.findByChannelCode(channelCode);
    }

    @Override
    public String deleteChannel(Integer channelId) throws yesmywineException {
        ValueUtil.verify(channelId);
        Channels channels = channelsDao.findOne(channelId);
        if (channels == null) {
            ValueUtil.isError("无此渠道！");
        }
        if (channels.getCanDelete() != null && !channels.getCanDelete()) {
            ValueUtil.isError("该渠道禁止删除！");
        }
        //查看渠道是否在用，在channelInevntoru中查看是否有sku
        List<ChannelsInventory> list = channelsInventoryService.findByChannelId(channelId);
        if (list.size() > 0) {
            ValueUtil.isError("已使用，无法删除");
        }
        channelsDao.delete(channelId);

        //向OMS与商城库存、PAAS商品服务、商城商品服务同步渠道
        sendToOmsChannel(channels, 2);
        String mall_result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channels/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "delete", channelId), RequestMethod.post);
//        String mall_result = SynchronizeUtils.getCode("http://localhost:8081", "/inventory/channels/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "delete", channelId), RequestMethod.post);
        String paas_goods_result = SynchronizeUtils.getCode(Dictionary.PAAS_HOST, "/goods/channel/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "2", channels), RequestMethod.post);
        String mall_goods_result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/goods/channel/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "2", channels), RequestMethod.post);
        String paas_user_result = SynchronizeUtils.getCode(Dictionary.PAAS_HOST, "/goods/channel/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "2", channels), RequestMethod.post);
        String mall_user_result = SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/goods/channel/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "2", channels), RequestMethod.post);

        if (mall_result == null || !mall_result.equals("201")) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            Channels channel = channelsDao.findOne(channelId);
            //回滚oms渠道信息
            sendToOmsChannel(channel, 0);
            ValueUtil.isError("渠道刪除失败：无法同步在商城中刪除渠道！");
        }
        if (paas_goods_result == null || !paas_goods_result.equals("201")) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            //回滚oms、商城渠道服务，渠道信息
            Channels channel = channelsDao.findOne(channelId);
            sendToOmsChannel(channel, 0);
            SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channels/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "save", channel), RequestMethod.post);
            ValueUtil.isError("渠道删除失败：无法同步在PAAS商品服务中删除渠道！");
        }
        if (mall_goods_result == null || !mall_goods_result.equals("201")) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            //回滚oms、商城渠道服务、paas商品服务，渠道信息
            Channels channel = channelsDao.findOne(channelId);
            sendToOmsChannel(channel, 0);
            SynchronizeUtils.getCode(Dictionary.MALL_HOST, "/inventory/channels/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "save", channel), RequestMethod.post);
            SynchronizeUtils.getCode(Dictionary.PAAS_HOST, "/goods/channel/syn", ValueUtil.toJson(HttpStatus.SC_CREATED, "0", channels), RequestMethod.post);
            ValueUtil.isError("渠道删除失败：无法同步在商城商品服务中删除渠道！");
        }
        
        return ValueUtil.toJson(HttpStatus.SC_NO_CONTENT, "channels");

    }
}
