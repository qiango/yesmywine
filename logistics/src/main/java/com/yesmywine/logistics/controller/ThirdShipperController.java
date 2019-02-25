package com.yesmywine.logistics.controller;

import com.alibaba.fastjson.JSONObject;
import com.yesmywine.logistics.dao.ThirdShippersDao;
import com.yesmywine.logistics.entity.ThirdShippers;
import com.yesmywine.logistics.service.ThirdShipperService;
import com.yesmywine.util.basic.MapUtil;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ${shuang} on 2017/7/21.
 */

@RestController
@RequestMapping("/logistics/thirdShippers")
public class ThirdShipperController {
    @Autowired
    private ThirdShipperService thirdShipperService;
    @Autowired
    private ThirdShippersDao thirdShippersDao;

    @RequestMapping(method = RequestMethod.POST)
    public String create(@RequestParam Map<String, String> param) {//新增承运商
        try {
            ValueUtil.verify(param.get("channelCode"));
            ValueUtil.verify(param.get("thirdShipperCode"));
            ValueUtil.verify(param.get("shippersId"));
            return ValueUtil.toJson(HttpStatus.SC_CREATED, thirdShipperService.addThirdShipper(param));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@RequestParam Map<String, String> param) {//更改
        try {
            ValueUtil.verify(param.get("id"));
            ValueUtil.verify(param.get("channelCode"));
            ValueUtil.verify(param.get("thirdShipperCode"));
            ValueUtil.verify(param.get("shippersId"));
            return ValueUtil.toJson(HttpStatus.SC_CREATED, thirdShipperService.addThirdShipper(param));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping( method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params,Integer pageNo,Integer pageSize) {//查询承运商物流规则
        MapUtil.cleanNull(params);
        if(null!=params.get("all")&&params.get("all").toString().equals("true")){
            List<ThirdShippers> list = thirdShippersDao.findByShippersId(Integer.valueOf(params.get("shippersId").toString()));
            List<Object> newList = new ArrayList<>();
            for (int i = 0; i <list.size() ; i++) {
                ThirdShippers thirdShippers =list.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("channelCode",thirdShippers.getChannelCode());
                jsonObject.put("thirdShipperCode",thirdShippers.getThirdShipperCode());
                jsonObject.put("id",thirdShippers.getId());
                newList.add(jsonObject) ;
            }
            return ValueUtil.toJson(newList);
        }else {
            Integer id = Integer.valueOf(params.get("id").toString());
            return ValueUtil.toJson(HttpStatus.SC_OK,thirdShippersDao.findOne(id));
        }

    }
}
