package com.yesmywine.logistics.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.logistics.dao.ThirdAreaDao;
import com.yesmywine.logistics.entity.Area;
import com.yesmywine.logistics.entity.Shippers;
import com.yesmywine.logistics.entity.ThirdArea;
import com.yesmywine.logistics.entity.ThirdShippers;
import com.yesmywine.logistics.service.AreaService;
import com.yesmywine.logistics.service.ThirdAreaService;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.SynchronizeUtils;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * Created by wangdiandian on 2017/7/21.
 */
@Service
@Transactional
public class ThirdAreaImpl extends BaseServiceImpl<ThirdArea,Integer> implements ThirdAreaService {
   @Autowired
    private ThirdAreaDao thirdAreaDao;
   @Autowired
    private AreaService areaService;

    public String createThirdArea(Map<String, String> param)throws yesmywineException {//新增第三方城市
        ValueUtil.verify(param, new String[]{"channelCode","areaId","thirdAreaName"});
        ThirdArea thirdArea = new ThirdArea();
        String areaId= param.get("areaId");//城市id
        String channelCode= param.get("channelCode");//渠道编码
        String thirdAreaName= param.get("thirdAreaName");//第三方城市名
        ThirdArea thirdArea1= thirdAreaDao.findByAreaIdAndThirdAreaName(Integer.valueOf(areaId),thirdAreaName);
        if(thirdArea1!=null){
            ValueUtil.isError("该城市下第三方城市名已存在");
        }

        thirdArea.setAreaId(Integer.valueOf(areaId));
        thirdArea.setChannelCode(channelCode);
        thirdArea.setThirdAreaName(thirdAreaName);
        thirdAreaDao.save(thirdArea);

        sendToOMS(areaService.findOne(Integer.valueOf(areaId)),thirdArea,0);
        return "success";
    }
    public ThirdArea updateLoad(Integer id) throws yesmywineException{//加载第三方城市
        ValueUtil.verify(id, "idNull");
        ThirdArea thirdArea = thirdAreaDao.findOne(id);
        return thirdArea;

    }
    public String updateSave(Map<String, String> param) throws yesmywineException{//跟新第三方城市
        Integer thirdAreaId = Integer.parseInt(param.get("id"));
        String areaId= param.get("areaId");//城市id
        String channelCode= param.get("channelCode");//渠道编码
        String thirdAreaName= param.get("thirdAreaName");//第三方城市名
        ThirdArea thirdArea1= thirdAreaDao.findByAreaIdAndThirdAreaName(Integer.valueOf(areaId),thirdAreaName);
        if(thirdArea1!=null&&thirdArea1.getId()!=thirdAreaId){
            ValueUtil.isError("该城市下第三方城市名已存在");
        }
        ThirdArea thirdArea = thirdAreaDao.findOne(thirdAreaId);
        thirdArea.setChannelCode(channelCode);
        thirdArea.setThirdAreaName(thirdAreaName);
        thirdAreaDao.save(thirdArea);
        sendToOMS(areaService.findOne(Integer.valueOf(areaId)),thirdArea,1);
        return "success";

    }
    public String delete(Integer id) throws yesmywineException{//删除第三方城市
        ValueUtil.verify(id, "idNull");
        ThirdArea thirdArea = thirdAreaDao.findOne(id);
        thirdAreaDao.delete(thirdArea);
        return "success";
    }


    public List<ThirdArea> query(Integer areaId)  throws yesmywineException{//查询第三方城市
        ValueUtil.verify(areaId, "idNull");
        List<ThirdArea> thirdAreaList = thirdAreaDao.findByAreaId(areaId);
        return thirdAreaList;
    }

    private void sendToOMS(Area area, ThirdArea thirdArea, Integer status) throws yesmywineException {//status 0-增  1-改  2-删

        JSONObject requestJson = new JSONObject();
        requestJson.put("function",status);
        JSONArray dataArray = new JSONArray();
        JSONObject dataJson = new JSONObject();
        dataJson.put("channelCode",thirdArea.getChannelCode());
        dataJson.put("thirdAreaName",thirdArea.getThirdAreaName());
        dataJson.put("code",area.getAreaNo());
        dataJson.put("relationId",thirdArea.getId());
        if(area.getLevel().equals("AREA_CLASS_TYPE_PROVINCE")){
            dataJson.put("type","province");
        }else if(area.getLevel().equals("AREA_CLASS_TYPE_CITY")){
            dataJson.put("type","city");
        }else{
            dataJson.put("type","district");
        }
        dataArray.add(dataJson);
        requestJson.put("data",dataArray);

        String  result = SynchronizeUtils.getOmsResult(Dictionary.OMS_HOST,"/updateBaseArea", RequestMethod.post,"",requestJson.toJSONString());
        if(result!=null) {
            String respStatus = ValueUtil.getFromJson(result,"status");
            String message = ValueUtil.getFromJson(result,"message");
            if(!respStatus.equals("success")){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("向OMS同步城市第三方关联信息失败,原因："+message);
            }
        }else{
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("向OMS同步城市第三方关联信息失败");
        }

    }

}

