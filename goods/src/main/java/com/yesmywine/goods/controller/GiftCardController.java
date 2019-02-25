package com.yesmywine.goods.controller;


import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.goods.service.GiftCardHistoryService;
import com.yesmywine.goods.service.GiftCardService;
import com.yesmywine.util.basic.MapUtil;
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
 * Created by admin on 2016/12/22.
 */
@RestController
@RequestMapping("/goods/giftCard")
public class GiftCardController {
    @Autowired
    private GiftCardService giftCardService;
    @Autowired
    private GiftCardHistoryService giftCardHistoryService;
    @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize, Long id) throws  Exception{
        MapUtil.cleanNull(params);
        if(id!=null){//查看礼品卡详情
            return ValueUtil.toJson(HttpStatus.SC_OK, giftCardService.updateLoad(id));
        }

        if(null!=params.get("all")&&params.get("all").toString().equals("true")){
            return ValueUtil.toJson(giftCardService.findAll());
        }else if(null!=params.get("all")){
            params.remove(params.remove("all").toString());
        }
        //查看礼品卡列表
        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }
        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());
        pageModel.addCondition(params);
        pageModel = giftCardService.findAll(pageModel);
        return ValueUtil.toJson(HttpStatus.SC_OK,pageModel);
    }

    @RequestMapping(value = "/giftCard",method = RequestMethod.GET)
    public String show(String jsonData) throws  Exception {//礼品卡查询接口
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, giftCardService.showGiftCard(jsonData));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }

    }

    @RequestMapping(value = "/bound",method = RequestMethod.PUT)
    public String bound(String jsonData) {//pass接收礼品卡绑定信息接口
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, giftCardService.bound(jsonData));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }

    }

    @RequestMapping(value = "/spend",method = RequestMethod.PUT)
    public String spend(String jsonData) {//pass接收礼品卡消费信息接口
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, giftCardService.spend(jsonData));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    //以下是商城调用接口
    @RequestMapping(value = "/synGiftCard/malls/itf",method = RequestMethod.POST)
    public String synchronizeGiftCard(String jsonData) {//商城创建礼品卡后同步接口
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, giftCardService.synchronizeGiftCard(jsonData));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }
    @RequestMapping(value = "/spendGiftCard/malls/itf",method = RequestMethod.PUT)
    public String synchronizeSpend(String jsonData) {//商城礼品卡消费后同步给pass接口
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, giftCardService.spendGiftCard(jsonData));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }


    @RequestMapping(value = "/synchronizeHistory/malls/itf",method = RequestMethod.POST)
    public String synchronizeCardHistory(String jsonData) {//商城礼品卡消费后记录同步给pass接口
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, giftCardService.giftCardHistory(jsonData));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(value = "boundGiftCard/malls/itf",method = RequestMethod.PUT)
    public String synchronizeBound(String jsonData) {//商城礼品卡绑定同步到pass接口
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, giftCardService.boundGiftCard(jsonData));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }
    @RequestMapping(value = "/buyGiftCard/malls/itf",method = RequestMethod.PUT)
    public String buyGiftCard(String jsonData) {//商城礼品卡购买后同步到pass接口
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, giftCardService.buyGiftCard(jsonData));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }



    @RequestMapping(value="history" ,method= RequestMethod.GET)
    public String indexHistory(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize) throws  Exception{
        MapUtil.cleanNull(params);
        String giftCardId=params.get("id").toString();
//        params.put("deleteEnum", 0);
        params.remove(params.remove("id").toString());
        params.put("giftCardId",giftCardId);
        if(null!=params.get("all")&&params.get("all").toString().equals("true")){
            return ValueUtil.toJson(giftCardHistoryService.findAll());
        }else if(null!=params.get("all")){
            params.remove(params.remove("all").toString());
        }
        //查看礼品卡消费列表
        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }
        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());
        pageModel.addCondition(params);
        pageModel = giftCardHistoryService.findAll(pageModel);
        return ValueUtil.toJson(HttpStatus.SC_OK,pageModel);
    }
}
