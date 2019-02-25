package com.yesmywine.logistics.controller;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.logistics.service.ThirdAreaService;
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
 * Created by wangdiandian on 2017/7/21.
 */
@RestController
@RequestMapping("/logistics/thirdArea")
public class ThirdAreaController {
    @Autowired
    private ThirdAreaService thirdAreaService;


    @RequestMapping(method = RequestMethod.POST)
    public String create(@RequestParam Map<String, String> param) {//新增第三方城市
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, thirdAreaService.createThirdArea(param));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }

    }

    @RequestMapping(method = RequestMethod.DELETE)
    public String delete(Integer id) {//删除第三方城市
        try {
            return ValueUtil.toError(HttpStatus.SC_NO_CONTENT,thirdAreaService.delete(id));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@RequestParam Map<String, String> param) {//修改保存第三方城市
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, thirdAreaService.updateSave(param));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }


    @RequestMapping( method = RequestMethod.GET)
    public String otherthirdArea(Integer areaId) throws yesmywineException {//查询第三方城市

        try {
            return ValueUtil.toJson(HttpStatus.SC_OK, thirdAreaService.query(areaId));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }
    @RequestMapping( value = "load",method = RequestMethod.GET)
    public String load(Integer id) throws yesmywineException {//加载详情第三方城市
            return ValueUtil.toJson(HttpStatus.SC_OK, thirdAreaService.findOne(id));
    }

}
