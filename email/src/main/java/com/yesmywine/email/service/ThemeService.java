package com.yesmywine.email.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.email.entity.Theme;
import com.yesmywine.util.error.yesmywineException;

import java.util.Map;

/**
 * Created by wangdiandian on 2017/5/16.
 */
public interface ThemeService extends BaseService<Theme,Integer> {

    String creat(Map<String, String> param) throws yesmywineException;
    Theme updateLoad(Integer id) throws yesmywineException;
    String updateSave(Map<String, String> param) throws yesmywineException;
}
