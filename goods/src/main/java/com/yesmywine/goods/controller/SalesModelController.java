package com.yesmywine.goods.controller;

import com.yesmywine.goods.service.SalesModelService;
import com.yesmywine.util.basic.Threads;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hz on 2016/12/9.
 * 选择销售模式接口
 */
@RestController
@RequestMapping("/goods/model")
public class SalesModelController {

    @Autowired
    private SalesModelService salesModelService;

    @RequestMapping(method = RequestMethod.GET)
    public String choose(Integer goodsId, Integer salesModelCode){

        try {
            ValueUtil.verify(goodsId);
            ValueUtil.verify(salesModelCode);
            return ValueUtil.toJson("content", this.salesModelService.choose(goodsId, salesModelCode));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }


}
