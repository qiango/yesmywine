package com.yesmywine.ware.controller;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.util.basic.MapUtil;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.entity.CostPriceRecord;
import com.yesmywine.ware.service.CostPriceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by SJQ on 2017/4/17.
 * 成本价
 */
@RestController
@RequestMapping("/inventory/costPriceRecord")
public class CostPriceRecordController {
    @Autowired
    private CostPriceRecordService costPriceRecordService;

    /*
    *@Author SJQ
    *@Description 成本价记录表
    *@CreateTime
    *@Params
    */
    @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize, Integer id) {
        try {
            MapUtil.cleanNull(params);

            if (id != null) {
                CostPriceRecord costPriceRecord = costPriceRecordService.findOne(id);
                ValueUtil.verifyNotExist(costPriceRecord, "无此sku的成本价");
                return ValueUtil.toJson(costPriceRecord);
            }

            if (null != params.get("all") && params.get("all").toString().equals("true")) {
                return ValueUtil.toJson(costPriceRecordService.findAll());
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
            pageModel = costPriceRecordService.findAll(pageModel);
            return ValueUtil.toJson(pageModel);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }
}
