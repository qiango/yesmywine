
package com.yesmywine.goods.controller;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.goods.entityProperties.PropertiesValue;
import com.yesmywine.goods.service.ProperValueService;
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

import java.util.List;
import java.util.Map;

/**
 * Created by hz on 1/6/17.
 */
@RestController
@RequestMapping("/goods/propertiesValue")
public class PropValueController {

    @Autowired
    private ProperValueService properValueService;


    @RequestMapping(method = RequestMethod.GET)
    public String page(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize) {   //查看
        MapUtil.cleanNull(params);
        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }
        if (null != params.get("all") && params.get("all").toString().equals("true")&& null != params.get("propertiesId")) {
            List<PropertiesValue> propertiesId = properValueService.findByPropertiesId(Integer.valueOf(params.get("propertiesId").toString()));
            return ValueUtil.toJson(propertiesId);
        } else if (null != params.get("all")) {
            params.remove(params.remove("all").toString());
        }

        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());
        pageModel.addCondition(params);
        return ValueUtil.toJson(HttpStatus.SC_OK,properValueService.findAll(pageModel));
    }


    @RequestMapping(method = RequestMethod.POST)
    public String create(@RequestParam Map<String, String> param) {   //增加属性值
        try {
            ValueUtil.verify(param.get("propertiesId"), "propertiesId");
            ValueUtil.verify(param.get("valueJson"), "valueJson");
            String result = properValueService.addPrpoValue(param);
            if("success".equals(result)){
                return ValueUtil.toJson(HttpStatus.SC_CREATED,"success");
            }
            return ValueUtil.toError("500", result);
            //同步到商城
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }



    @RequestMapping(method = RequestMethod.DELETE)
    public String delete( String id) {       //删除属性
        try {
            ValueUtil.verify(id, "id");
            String s = properValueService.deletePropValue(id);
            if("success".equals(s)){
                return ValueUtil.toJson(HttpStatus.SC_NO_CONTENT, "success");
            }else {
                return ValueUtil.toError("500", s);
            }
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
        //同步到商城
    }

}

