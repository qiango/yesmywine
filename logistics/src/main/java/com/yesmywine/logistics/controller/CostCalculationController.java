package com.yesmywine.logistics.controller;

import com.yesmywine.logistics.service.CostCalculationService;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wangdiandian on 2017/3/31.
 */
@RestController
@RequestMapping("/logistics/costCalculation/itf")
public class CostCalculationController {
    @Autowired
    private CostCalculationService costCalculationService;
    @RequestMapping( method = RequestMethod.POST)
    public String costCalculation(String jsonData) {//物流费计算
        try {
            return ValueUtil.toJson(HttpStatus.SC_OK, costCalculationService.costCalculation(jsonData));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

}
