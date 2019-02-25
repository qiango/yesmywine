package com.yesmywine.dictionary.controller;

import com.yesmywine.dictionary.dao.DicEntityDao;
import com.yesmywine.dictionary.dao.LanguageDao;
import com.yesmywine.dictionary.entity.DicEntity;
import com.yesmywine.dictionary.entity.Language;
import com.yesmywine.util.basic.ValueUtil;
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
@RequestMapping("/dic")
public class DicController {

    @Autowired
    private DicEntityDao dicEntityDao;
    @Autowired
    private LanguageDao languageDao;

    private static final String defaultOuter = "default";


    private static Map<String, String> cache = new HashMap<>();

    //               outer    sysCode
    private static Map<String, String> languageCache = new HashMap<>();



    private void refresh() {
        List<DicEntity> list = dicEntityDao.findAll();
        cache.clear();
        list.forEach(d -> {
            cache.put(d.getEntityCode() + ":" + d.getSysCode(), d.getEntityValue());
        });

        List<Language> languages = languageDao.findAll();
        languageCache.clear();
        languages.forEach(l -> {
            languageCache.put(l.getOuterCode(), l.getSysCode());
        });

        if (null == languageCache.get(defaultOuter)) {
            Language defaultLanguage = new Language();
            defaultLanguage.setOuterCode(defaultOuter);
            defaultLanguage.setSysCode(defaultOuter);
            languageDao.save(defaultLanguage);
        }
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

        if (null == cache.get(code + ":" + languageCache.get(defaultOuter))) {
            DicEntity dicEntity = new DicEntity();
            dicEntity.setSysCode(languageCache.get(defaultOuter));
            dicEntity.setEntityCode(code);
            dicEntity.setEntityValue("");
            dicEntityDao.save(dicEntity);

            refresh();
        }
        return ValueUtil.toJson(cache.get(code + ":" + languageCache.get(defaultOuter)));
    }

    @RequestMapping(value = "/entityCode", method = RequestMethod.GET)
    public String getValue(String entityCode) {
        return ValueUtil.toJson(dicEntityDao.findByEntityCode(entityCode));
    }

    @RequestMapping(value = "/sysCode", method = RequestMethod.GET)
    public String getCodeAndValue(String sysCode) {
        return ValueUtil.toJson(dicEntityDao.findBySysCode(sysCode));
    }


    @RequestMapping(value = "/sysCode/itf", method = RequestMethod.GET)
    public String getCodeAndValueItf(String sysCode) {
//        System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk:::::::::::::::::"+sysCode);
        return ValueUtil.toJson(dicEntityDao.findBySysCode(sysCode));
    }

    @RequestMapping(value = "/language", method = RequestMethod.POST)
    public String addLanguage(String outerCode, String sysCode) {
        if (null != languageCache.get(outerCode)) {
            return ValueUtil.toError("languageAlreadyExist", "languageAlreadyExist");
        } else {
            Language language = new Language();
            language.setOuterCode(outerCode);
            language.setSysCode(sysCode);
            languageDao.save(language);
            refresh();
            return ValueUtil.toJson(languageCache);
        }
    }

    @RequestMapping(value = "/{code}/{language}", method = RequestMethod.GET)
    public String find(@PathVariable("code") String code, @PathVariable("language") String language) {
        if (null == cache || cache.size() == 0) {
            refresh();
        }

        if (null == languageCache.get(language)) {
            Language newLang = new Language();
            newLang.setOuterCode(language);
            newLang.setSysCode(language);
            languageDao.save(newLang);

            DicEntity dicEntity = new DicEntity();
            dicEntity.setSysCode(language);
            dicEntity.setEntityCode(code);
            dicEntity.setEntityValue(ValueUtil.coalesce(cache.get(code + ":" + languageCache.get(defaultOuter)), ""));
            dicEntityDao.save(dicEntity);

            refresh();
        }

        return cache.get(code + ":" + languageCache.get(language));
    }


}
