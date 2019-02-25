package com.yesmywine.ware.controller;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.util.basic.MapUtil;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.ware.service.ReleaseFreezeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by SJQ on 2017/4/17.
 */
@RestController
@RequestMapping("/inventory/releaseFreezeRecord")
public class ReleaseFreezeController {
    @Autowired
    private ReleaseFreezeRecordService releaseFreezeRecordService;

    /*
    *@Author Gavin
    *@Description 释放冻结失败记录
    *@Date 2007/3/16 14:59
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize) {
        MapUtil.cleanNull(params);
        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }
        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());
        pageModel.addCondition(params);
        pageModel = releaseFreezeRecordService.findAll(pageModel);
        return ValueUtil.toJson(pageModel);
    }
}
