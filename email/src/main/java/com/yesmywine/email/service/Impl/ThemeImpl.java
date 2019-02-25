package com.yesmywine.email.service.Impl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.email.dao.ThemeDao;
import com.yesmywine.email.entity.Theme;
import com.yesmywine.email.service.ThemeService;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.encode.Encode;
import com.yesmywine.util.error.yesmywineException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by wangdiandian on 2017/5/16.
 */
@Service
public class ThemeImpl extends BaseServiceImpl<Theme,Integer> implements ThemeService {

        @Autowired
        private ThemeDao themeDao;


        public String creat(Map<String, String> param) throws yesmywineException {//新增
            ValueUtil.verify(param, new String[]{"theme","title","type"});
            String code = Encode.getSalt(10);
            Theme theme1 = themeDao.findByCode(code);
            if(ValueUtil.notEmpity(theme1)){
                return ValueUtil.toJson(HttpStatus.SC_INTERNAL_SERVER_ERROR,"编号重复");
            }
            Theme theme = new Theme();
            theme.setTitle(param.get("title"));
            theme.setCode(code);
            theme.setThemeTemplate(param.get("theme"));
            theme.setType(param.get("type"));
            themeDao.save(theme);
            return "success";
        }

        public Theme updateLoad(Integer id) throws yesmywineException {//加载
            ValueUtil.verify(id, "idNull");
            Theme theme = themeDao.findOne(id);
            return theme;
        }

        public String updateSave(Map<String, String> param) throws yesmywineException {//修改保存
            Integer id = Integer.parseInt(param.get("id"));
            Theme theme1 = themeDao.findOne(id);
            theme1.setTitle(param.get("title"));
            theme1.setType(param.get("type"));
            theme1.setThemeTemplate(param.get("themeTemplate"));
            themeDao.save(theme1);
            return "success";
        }

}
