package com.yesmywine.email.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.email.entity.Theme;

/**
 * Created by wangdiandian on 2017/5/16.
 */
public interface ThemeDao extends BaseRepository<Theme,Integer> {
    Theme findByCode(String code);
    Theme findByCodeAndIdNot(String code,Integer id);
    
}
