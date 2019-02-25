package com.yesmywine.user.controller;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.user.dao.UserInformationDao;
import com.yesmywine.user.entity.Channels;
import com.yesmywine.user.entity.UserInformation;
import com.yesmywine.user.service.BeanCeterService;
import com.yesmywine.user.service.BeansUserService;
import com.yesmywine.util.basic.*;
import com.yesmywine.util.error.yesmywineException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by ${shuang} on 2017/3/28.
 */
@RestController
@RequestMapping("/user/beans")
public class BeansController {
    @Autowired
    private BeansUserService beansUserService;
    @Autowired
    private BeanCeterService beanCeterService;
    @Autowired
    private UserInformationDao userInformationDao;


    @RequestMapping(value = "/local", method = RequestMethod.POST)
    public String localGenerate(@RequestParam Map<String, String> params) {//酒豆生成
        boolean isNunicodeDigits = StringUtils.isNumeric(params.get("point"));
        if (isNunicodeDigits == false) {
            return ValueUtil.toJson(HttpStatus.SC_INTERNAL_SERVER_ERROR, "积分格式不对", "point:" + params.get("point"));
        }
        String phoneNumber = params.get("phoneNumber");
        String orderNumber = params.get("orderNumber");
        Integer point = Integer.valueOf(params.get("point"));
        String userName = params.get("userName");
        String storeCode = params.get("storeCode");
        String channelCode = null;
        if (storeCode != null) {
            String result = SynchronizeUtils.getResult(Dictionary.PAAS_HOST, "/inventory/stores/code?storeCode=" + storeCode, com.yesmywine.httpclient.bean.RequestMethod.get, null);
            if (result != null) {
                channelCode = ValueUtil.getFromJson(result, "data", "channel", "channelCode");
            } else {
                try {
                    ValueUtil.isError("无法根据门店编码找到相应的渠道");
                } catch (yesmywineException e) {
                    Threads.createExceptionFile("user",e.getMessage());
                    return ValueUtil.toError(e.getCode(),e.getMessage());
                }
            }
        }
        try {
            ValueUtil.verify(phoneNumber);
            ValueUtil.verify(point);
            ValueUtil.verify(channelCode);
            Double bean = beansUserService.beansCreate(userName, phoneNumber, orderNumber, point, channelCode);
            /*HashMap<String,Double> map=new HashMap<String, Double>();
            map.put("generate",bean);*/
            return ValueUtil.toJson(HttpStatus.SC_CREATED, bean);
        } catch (yesmywineException e) {
            Threads.createExceptionFile("user",e.getMessage());

            return ValueUtil.toError(e.getCode(), "转换失败，格式不对");
        }

    }

    @RequestMapping(value = "/local/itf", method = RequestMethod.POST)
    public String storeGenerate(@RequestParam Map<String, String> params) {//门店新增积分
        boolean isNunicodeDigits = StringUtils.isNumeric(params.get("point"));
        if (isNunicodeDigits == false) {
            return ValueUtil.toJson(HttpStatus.SC_INTERNAL_SERVER_ERROR, "积分格式不对", "point:" + params.get("point"));
        }
        String phoneNumber = params.get("phoneNumber");
        String orderNumber = params.get("orderNumber");
        Integer point = Integer.valueOf(params.get("point"));
        String userName = params.get("userName");
        String storeCode = params.get("storeCode");
        String channelCode = null;
        if (storeCode != null) {
            String result = SynchronizeUtils.getResult(Dictionary.PAAS_HOST, "/inventory/stores/code?storeCode=" + storeCode, com.yesmywine.httpclient.bean.RequestMethod.get, null);
            if (result != null) {
                channelCode = ValueUtil.getFromJson(result, "data", "channel", "channelCode");
            } else {
                try {
                    ValueUtil.isError("无法根据门店编码找到相应的渠道");
                } catch (yesmywineException e) {
                    Threads.createExceptionFile("user",e.getMessage());
                    return ValueUtil.toError(e.getCode(),e.getMessage());
                }
            }
        }
        try {
            ValueUtil.verify(phoneNumber);
            ValueUtil.verify(point);
            ValueUtil.verify(channelCode);
            Double bean = beansUserService.beansCreate(userName, phoneNumber, orderNumber, point, channelCode);
            /*HashMap<String,Double> map=new HashMap<String, Double>();
            map.put("generate",bean);*/
            return ValueUtil.toJson(HttpStatus.SC_CREATED, bean);
        } catch (yesmywineException e) {
            Threads.createExceptionFile("user",e.getMessage());

            return ValueUtil.toError(e.getCode(), "转换失败，格式不对");
        }

    }

    @RequestMapping(method = RequestMethod.GET)
    public String show(String userName, String phoneNumber) {//可用豆豆
        UserInformation userInformation = userInformationDao.findByUserNameOrPhoneNumber(userName, phoneNumber);
        String str = userInformation.getBean().toString();
        String[] bean = str.split("\\.");
        return ValueUtil.toJson(HttpStatus.SC_CREATED, bean[0]);
    }

    @RequestMapping(value = "/consume", method = RequestMethod.POST)
    public String consume(@RequestParam Map<String, String> params) {//消耗酒豆
        String phoneNumber = params.get("phoneNumber");
        String userName = params.get("userName");
        UserInformation userInformation = userInformationDao.findByUserNameOrPhoneNumber(userName, phoneNumber);
        String str = userInformation.getBean().toString();
        String[] bean1 = str.split("\\.");
        boolean isNunicodeDigits = StringUtils.isNumeric(params.get("bean"));
        if (isNunicodeDigits == false) {
            return ValueUtil.toJson(HttpStatus.SC_INTERNAL_SERVER_ERROR, "积分格式不对", "bean:" + params.get("bean"));
        } else if (Integer.valueOf(bean1[0]) < Integer.valueOf(params.get("bean"))) {
            return ValueUtil.toError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "酒豆不够");
        }

        String orderNumber = params.get("orderNumber");
        String bean = params.get("bean");
        String storeCode = params.get("storeCode");
        String channelCode = null;
        if (storeCode != null) {
            String result = SynchronizeUtils.getResult(Dictionary.PAAS_HOST, "/inventory/stores/code?storeCode=" + storeCode, com.yesmywine.httpclient.bean.RequestMethod.get, null);
            if (result != null) {
                channelCode = ValueUtil.getFromJson(result, "data", "channel", "channelCode");
            } else {
                try {
                    ValueUtil.isError("无法根据门店编码找到相应的渠道");
                } catch (yesmywineException e) {
                    return ValueUtil.toError(e.getCode(), e.getMessage());
                }
            }
        }
        try {
            ValueUtil.verify(orderNumber);
            ValueUtil.verify(bean);
            ValueUtil.verify(channelCode);
            String beans = beansUserService.consume(userName, phoneNumber, orderNumber, Integer.valueOf(bean), channelCode);
            /*HashMap<String,Double> map=new HashMap<String,Double>();
            map.put("consume",Double.valueOf(beans));*/
            return ValueUtil.toJson(HttpStatus.SC_CREATED, beans);
        } catch (yesmywineException e) {
            Threads.createExceptionFile("user",e.getMessage());

            return ValueUtil.toError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "消耗失败，格式不对");
        }

    }

    @RequestMapping(value = "/consume/itf", method = RequestMethod.POST)
    public String storesConsume(@RequestParam Map<String, String> params) {//门店消耗酒豆
        String phoneNumber = params.get("phoneNumber");
        String userName = params.get("userName");
        UserInformation userInformation = userInformationDao.findByUserNameOrPhoneNumber(userName, phoneNumber);
        String str = userInformation.getBean().toString();
        String[] bean1 = str.split("\\.");
        boolean isNunicodeDigits = StringUtils.isNumeric(params.get("bean"));
        if (isNunicodeDigits == false) {
            return ValueUtil.toJson(HttpStatus.SC_INTERNAL_SERVER_ERROR, "积分格式不对", "bean:" + params.get("bean"));
        } else if (Integer.valueOf(bean1[0]) < Integer.valueOf(params.get("bean"))) {
            return ValueUtil.toError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "酒豆不够");
        }

        String orderNumber = params.get("orderNumber");
        String bean = params.get("bean");
        String storeCode = params.get("storeCode");
        String channelCode = null;
        if (storeCode != null) {
            String result = SynchronizeUtils.getResult(Dictionary.PAAS_HOST, "/inventory/stores/code?storeCode=" + storeCode, com.yesmywine.httpclient.bean.RequestMethod.get, null);
            if (result != null) {
                channelCode = ValueUtil.getFromJson(result, "data", "channel", "channelCode");
            } else {
                try {
                    ValueUtil.isError("无法根据门店编码找到相应的渠道");
                } catch (yesmywineException e) {
                    Threads.createExceptionFile("user",e.getMessage());
                    return ValueUtil.toError(e.getCode(),e.getMessage());
                }
            }
        }
        try {
            ValueUtil.verify(orderNumber);
            ValueUtil.verify(bean);
            ValueUtil.verify(channelCode);
            String beans = beansUserService.consume(userName, phoneNumber, orderNumber, Integer.valueOf(bean), channelCode);
            /*HashMap<String,Double> map=new HashMap<String,Double>();
            map.put("consume",Double.valueOf(beans));*/
            return ValueUtil.toJson(HttpStatus.SC_CREATED, beans);
        } catch (yesmywineException e) {
            Threads.createExceptionFile("user",e.getMessage());

            return ValueUtil.toError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "消耗失败，格式不对");
        }

    }

    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public String account(String startDate, String endDate) {//统计
        try {
            return ValueUtil.toJson(beanCeterService.settleAccounts(startDate, endDate));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("user",e.getMessage());
            return ValueUtil.toError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "服务器报错");
        }
    }

    @RequestMapping(value = "/userbeanflow", method = RequestMethod.GET)
    public String userFolwIndex(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize) {
        MapUtil.cleanNull(params);
        if (params.get("channelId") != null) {
            Integer channelId = Integer.valueOf(params.get("channelId").toString());
            Channels channels = new Channels();
            channels.setId(channelId);
            params.remove(params.remove("channelId").toString());
            params.put("channel", channels);
        } else if (params.get("channelId_ne") != null) {
            Integer channelId = Integer.valueOf(params.get("channelId_ne").toString());
            Channels channels = new Channels();
            channels.setId(channelId);
            params.remove(params.remove("channelId_ne").toString());
            params.put("channel_ne", channels);
        }
        if (null != params.get("all") && params.get("all").toString().equals("true")) {
            return ValueUtil.toJson(beansUserService.findAll());
        } else if (null != params.get("all")) {
            params.remove(params.remove("all").toString());
        }
        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }
        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());
        pageModel.addCondition(params);
        pageModel = beansUserService.findAll(pageModel);
        return ValueUtil.toJson(pageModel);

    }


    @RequestMapping(value = "/centerbeanflow", method = RequestMethod.GET)
    public String centerFolwIndex(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize) {
        MapUtil.cleanNull(params);

        if (params.get("channelId") != null) {
            Integer channelId = Integer.valueOf(params.get("channelId").toString());
            Channels channels = new Channels();
            channels.setId(channelId);
            params.remove(params.remove("channelId").toString());
            params.put("channel", channels);
        } else if (params.get("channelId_ne") != null) {
            Integer channelId = Integer.valueOf(params.get("channelId_ne").toString());
            Channels channels = new Channels();
            channels.setId(channelId);
            params.remove(params.remove("channelId_ne").toString());
            params.put("channel_ne", channels);
        }

        if (null != params.get("all") && params.get("all").toString().equals("true")) {
            return ValueUtil.toJson(beanCeterService.findAll());
        } else if (null != params.get("all")) {
            params.remove(params.remove("all").toString());
        }
        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }
        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());
        pageModel.addCondition(params);
        pageModel = beanCeterService.findAll(pageModel);
        return ValueUtil.toJson(pageModel);
    }

    @RequestMapping(value = "/synchronization", method = RequestMethod.POST)
    public String beanFlowSys(String jsonData) {
        String result = beansUserService.beanFlowSys(jsonData);
        return ValueUtil.toJson(HttpStatus.SC_CREATED, result);
    }

    @RequestMapping(value = "/sytomall", method = RequestMethod.POST)
    public String sytomall(Integer beanUserFlowId) {
        String result = beansUserService.sytomall(beanUserFlowId);
        return result;
    }
}
