package com.yesmywine.logistics.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.logistics.dao.ThirdShippersDao;
import com.yesmywine.logistics.entity.Shippers;
import com.yesmywine.logistics.entity.ThirdShippers;
import com.yesmywine.logistics.service.ShipperService;
import com.yesmywine.logistics.service.ThirdShipperService;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.SynchronizeUtils;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.transaction.Transactional;
import java.util.Map;

/**
 * Created by ${shuang} on 2017/7/21.
 */

@Service
@Transactional
public class ThirdShipperServiceImpl extends BaseServiceImpl<ThirdShippers,Integer> implements ThirdShipperService {

    @Autowired
    private ThirdShippersDao thirdShippersDao;
    @Autowired
    private ShipperService shipperService;
    @Override
    public String addThirdShipper(Map<String, String> param) throws yesmywineException {
        ThirdShippers thirdShippers = null;
        Integer status = null;
        Shippers shippers = shipperService.findOne(Integer.valueOf(param.get("shippersId")));
        if(ValueUtil.isEmpity(param.get("id"))){
            thirdShippers = new ThirdShippers();
            thirdShippers.setShippersId(Integer.valueOf(param.get("shippersId")));
            thirdShippers.setChannelCode(param.get("channelCode"));
            thirdShippers.setThirdShipperCode(param.get("thirdShipperCode"));
            thirdShippersDao.save(thirdShippers);
            status = 0;
        }else {
            thirdShippers =thirdShippersDao.findOne(Integer.valueOf(param.get("id")));
            thirdShippers.setShippersId(Integer.valueOf(param.get("shippersId")));
            thirdShippers.setChannelCode(param.get("channelCode"));
            thirdShippers.setThirdShipperCode(param.get("thirdShipperCode"));
            thirdShippersDao.save(thirdShippers);
            status = 1;
        }
        //将第三方承运商同步到OMS
        sendToOMS(shippers,thirdShippers,status);
        return "success";
    }

    private void sendToOMS(Shippers shippers, ThirdShippers thirdShippers, Integer status) throws yesmywineException {//status 0-增  1-改  2-删

        JSONObject requestJson = new JSONObject();
        requestJson.put("function",status);
        JSONArray dataArray = new JSONArray();
        JSONObject dataJson = new JSONObject();
        dataJson.put("carrierCode",shippers.getShipperCode());
        dataJson.put("channelCode",thirdShippers.getChannelCode());
        dataJson.put("thirdShipperCode",thirdShippers.getThirdShipperCode());
        dataJson.put("relationId",thirdShippers.getId());
        dataArray.add(dataJson);
        requestJson.put("data",dataArray);
        String  result = SynchronizeUtils.getOmsResult(Dictionary.OMS_HOST,"/pullCarrierRelationCode", RequestMethod.post,"",requestJson.toJSONString());
        if(result!=null) {
            String respStatus = ValueUtil.getFromJson(result,"status");
            String message = ValueUtil.getFromJson(result,"message");
            if(!respStatus.equals("success")){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("向OMS同步承运商第三方关联信息失败,原因："+message);
            }
        }else{
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("向OMS同步承运商第三方关联信息失败");
        }

    }

    @Override
    public void deleteByShipperId(Integer shippersId) {
        thirdShippersDao.deleteByShippersId(shippersId);
    }
}
