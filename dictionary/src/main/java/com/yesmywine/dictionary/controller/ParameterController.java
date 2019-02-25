package com.yesmywine.dictionary.controller;

import com.yesmywine.dictionary.dao.ParameterDao;
import com.yesmywine.dictionary.entity.Parameter;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.enums.Active;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by WANG, RUIQING on 12/1/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
@RestController
@RequestMapping("/param")
public class ParameterController {

    @Autowired
    private ParameterDao parameterDao;

    private static Map<String, String> cache = new HashMap<>();

    private void refresh() {
        List<Parameter> parameterList = parameterDao.findAll();
        cache.clear();
        parameterList.forEach(p -> {
            if (p.getActive().equals(Active.inActive)) {
                cache.put(p.getCode(), p.getValue());
            }
        });

    }

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        if (null == cache || cache.size() == 0) {
            refresh();
        }
        return ValueUtil.toJson(cache);
    }

    @RequestMapping(value = "/itf", method = RequestMethod.GET)
    public String indexItf() {
        if (null == cache || cache.size() == 0) {
            refresh();
        }
        return ValueUtil.toJson(cache);
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public String refreshcache() {
        refresh();
        return ValueUtil.toString(cache);
    }

    @RequestMapping(value = "/{code}", method = RequestMethod.GET)
    public String getUrl(@PathVariable("code") String code) {
        if (null == cache || cache.size() == 0) {
            refresh();
        }

        String value = cache.get(code);

        if (null == value) {
            value = "";
            Parameter parameter = new Parameter();
            parameter.setCode(code);
            parameter.setActive(Active.inActive);
            parameter.setValue("");
            parameterDao.save(parameter);
            refresh();
        }
        return value;
    }


}
