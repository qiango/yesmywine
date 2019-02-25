package com.yesmywine.user.controller;

import com.yesmywine.user.entity.DeliveryAddress;
import com.yesmywine.user.service.DeliveryAddressService;
import com.yesmywine.util.basic.Threads;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by SJQ on 2017/4/20.
 */
@RestController
@RequestMapping("/user/deliveryAddress")
public class DeliveryAddressController {

    @Autowired
    private DeliveryAddressService deliveryAddressService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Integer userId){
        try {
            ValueUtil.verify(userId,"userId");
            List<DeliveryAddress> deliveryAddress = deliveryAddressService.findByUserId(userId);
            return ValueUtil.toJson(deliveryAddress);
        }catch (yesmywineException e){
            Threads.createExceptionFile("user",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(value = "/synchronous", method = RequestMethod.POST)
    public String synchronous(@RequestParam Map<String,String> map){
        try {
            return ValueUtil.toJson(HttpStatus.SC_OK,deliveryAddressService.synchronous(map));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("user",e.getMessage());
            return ValueUtil.toError(HttpStatus.SC_NO_CONTENT,"Erro");
        }
    }


}
