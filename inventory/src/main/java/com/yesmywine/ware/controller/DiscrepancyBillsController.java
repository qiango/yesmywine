package com.yesmywine.ware.controller;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.util.basic.MapUtil;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.entity.DiscrepancyBills;
import com.yesmywine.ware.service.DiscrepancyBillsService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by SJQ on 2017/6/9.
 */
@RestController
@RequestMapping("/inventory/discrepancy")
public class DiscrepancyBillsController {
    @Autowired
    private DiscrepancyBillsService discrepancyBillsService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize, Integer id) {
        MapUtil.cleanNull(params);
        if (id != null) {
            DiscrepancyBills discrepancyBills = discrepancyBillsService.findOne(id);
            try {
                ValueUtil.verifyNotExist(discrepancyBills, "无此差异单");
            } catch (yesmywineException e) {
                return ValueUtil.toError(e.getCode(), e.getMessage());
            }
            return ValueUtil.toJson(discrepancyBills);
        }

        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }
        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());
        pageModel.addCondition(params);
        pageModel = discrepancyBillsService.findAll(pageModel);
        return ValueUtil.toJson(pageModel);
    }


    @RequestMapping(method = RequestMethod.PUT)
    public String audit(Integer id, String comment) {
        DiscrepancyBills discrepancyBills = discrepancyBillsService.findOne(id);
        discrepancyBills.setComment(comment);
        discrepancyBills.setStatus("1");
        discrepancyBillsService.save(discrepancyBills);
        return ValueUtil.toJson(HttpStatus.SC_CREATED, discrepancyBills);
    }

}
