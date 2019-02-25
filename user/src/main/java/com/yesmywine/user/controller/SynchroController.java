package com.yesmywine.user.controller;


import com.yesmywine.user.dao.ChannelsDao;
import com.yesmywine.user.service.SynchroService;
import com.yesmywine.util.basic.Threads;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by Mars on 2017/6/25.
 */
@RestController
@RequestMapping("/user/synchro")
public class SynchroController {

    @Autowired
    private SynchroService synchroService;
    @Autowired
    private ChannelsDao channelsDao;

    @RequestMapping(method = RequestMethod.POST)
    public String create(@RequestParam Map<String,String> params){
        String result= null ;
        try {
            ValueUtil.verify(params.get("status"));
            ValueUtil.verify(params.get("userName"));
            ValueUtil.verify(params.get("orderNumber"));
            ValueUtil.verify(params.get("channelCode"));
            if(params.get("status").equals("generate")||params.get("status").equals("consume")){
                ValueUtil.verify(params.get("bean"));
              result = ValueUtil.toJson(HttpStatus.SC_CREATED,synchroService.beanCenter(params));
            }else if(params.get("status").equals("return")){
                ValueUtil.verify(params.get("newBeans"));
                ValueUtil.verify(params.get("returnBean"));
                ValueUtil.verify(params.get("point"));
                result = ValueUtil.toJson(HttpStatus.SC_CREATED,synchroService. returnsConsumeSys(params)) ;
            }
        } catch (yesmywineException e) {
            Threads.createExceptionFile("user",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
        return result;
    }

//    @RequestMapping(value = "/channel",method = RequestMethod.POST)
//    public String getChannel(String jsonDate)throws yesmywineException{
//        Integer synchronous=Integer.parseInt(ValueUtil.getFromJson(jsonDate,"synchronous"));
//        if(synchronous==1){
//            try{
//            Channels channels=new Channels();
//            channels.setChannelName(ValueUtil.getFromJson(jsonDate,"channelName"));
//            channels.setChannelCode(ValueUtil.getFromJson(jsonDate,"channelCode"));
//            channels.setId(Integer.parseInt(ValueUtil.getFromJson(jsonDate,"id")));
//            channelsDao.save(channels);
//        }catch (Exception e){
//            return ValueUtil.toError("500","新增失败");
//        }
//            return ValueUtil.toJson(HttpStatus.SC_OK,"success");
//        }else if(synchronous==2){
//            try{
//            channelsDao.delete(Integer.parseInt(ValueUtil.getFromJson(jsonDate,"id")));
//        }catch (Exception e){
//            return ValueUtil.toError("500","删除失败");
//        }
//            return ValueUtil.toJson(HttpStatus.SC_OK,"success");
//        }else {
//            try{
//            Channels channels=channelsDao.findOne(Integer.parseInt(ValueUtil.getFromJson(jsonDate,"id")));
//            channels.setChannelName(ValueUtil.getFromJson(jsonDate,"channelName"));
//            channels.setChannelCode(ValueUtil.getFromJson(jsonDate,"channelCode"));
//            channelsDao.save(channels);
//        }catch (Exception e){
//            return ValueUtil.toError("500","更新失败");
//        }
//            return ValueUtil.toJson(HttpStatus.SC_OK,"success");
//        }
//    }

    @RequestMapping(value = "/channel",method = RequestMethod.POST)
    public String synchronous(String jsonData) {
//        Integer id, String name, Integer synchronous
        try {
            Integer id = Integer.valueOf(ValueUtil.getFromJson(jsonData,"data","id"));
            Integer synchronous = Integer.valueOf(ValueUtil.getFromJson(jsonData,"msg"));
            String name = String.valueOf(ValueUtil.getFromJson(jsonData,"data","channelName"));
            String code = String.valueOf(ValueUtil.getFromJson(jsonData,"data","channelCode"));
            String type = String.valueOf(ValueUtil.getFromJson(jsonData,"data","type"));
            ValueUtil.verify(id,"id");
            ValueUtil.verify(synchronous,"synchronous");
            Boolean result = this.synchroService.synchronous(id, name, type,code, synchronous);
            if(result){
                return ValueUtil.toJson(HttpStatus.SC_CREATED, "success");
            }else {
                return ValueUtil.toError("500", "erro");
            }
        } catch (yesmywineException e) {
            Threads.createExceptionFile("user",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }
}
