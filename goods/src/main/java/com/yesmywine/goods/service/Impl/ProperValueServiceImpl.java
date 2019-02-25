package com.yesmywine.goods.service.Impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.goods.dao.ProperValueDao;
import com.yesmywine.goods.dao.PropertiesDao;
import com.yesmywine.goods.entityProperties.Properties;
import com.yesmywine.goods.entityProperties.PropertiesValue;
import com.yesmywine.goods.service.CategoryService;
import com.yesmywine.goods.service.CommonService;
import com.yesmywine.goods.service.ProperValueService;
import com.yesmywine.httpclient.bean.HttpBean;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.ValueUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hz on 2017/4/26.
 */
@Service
@Transactional
public class ProperValueServiceImpl extends BaseServiceImpl<PropertiesValue, Integer> implements ProperValueService {

    @Autowired
    private PropertiesDao propertiesDao;
    @Autowired
    private ProperValueDao properValueDao;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CommonService<PropertiesValue> commonServiceValue;
    @Override
    public String addPrpoValue(Map param) {

        Integer propertiesId = Integer.valueOf(param.get("propertiesId").toString());
        Properties properties = this.propertiesDao.findOne(propertiesId);
        if(ValueUtil.isEmpity(properties)){
            return "没有此属性";
        }

        String valueJson=  param.get("valueJson").toString();
        JsonParser jsonParser = new JsonParser();
        JsonArray arr;
        try {
            arr = jsonParser.parse(valueJson).getAsJsonArray();
        }catch (Exception e){
            return "json格式错误";
        }
        List<PropertiesValue> pvList = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            PropertiesValue propertiesValue = new PropertiesValue();
            propertiesValue.setPropertiesId(propertiesId);
//            propertiesValue.setCnName(properties.getCnName());

            String value = arr.get(i).getAsJsonObject().get("value").getAsString();
            String code = arr.get(i).getAsJsonObject().get("code").getAsString();

            List<PropertiesValue> byPropertiesId = this.properValueDao.findByPropertiesId(propertiesId);
            if (ValueUtil.notEmpity(byPropertiesId) && byPropertiesId.size() > 0) {
                for (PropertiesValue pro : byPropertiesId) {
                    Boolean flag = true;
                    if (value.equals(pro.getCnValue())) {
                        flag = false;
                        return value + ":此属性值已存在";
                    }
                    if (flag) {
                        propertiesValue.setCode(code);
                        propertiesValue.setCnValue(value);
                    }
                }
                pvList.add(propertiesValue);

            } else {
                propertiesValue.setCode(code);
                propertiesValue.setCnValue(value);
                pvList.add(propertiesValue);
            }
        }
        try{
            properValueDao.save(pvList);
        }catch (Exception e){
            return "jdbc erro";
        }

        HttpBean httpBean = new HttpBean(Dictionary.MALL_HOST + "/goods/properValue/syncreate", RequestMethod.post);
            httpBean.addParameter("jsonData", ValueUtil.toJson(pvList));
            httpBean.run();
            String result = httpBean.getResponseContent();
            if (result!=null) {
                String codes = ValueUtil.getFromJson(result, "code");
                if (codes == null || !codes.equals("201")) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return "同步失败";
                }
            }
        return "success";
    }

    @Override
    public String addPrpoValue(Integer propertiesId, String value, String code) {
        Properties properties = this.propertiesDao.findOne(propertiesId);
        if(ValueUtil.isEmpity(properties)){
            return "没有此属性";
        }


        List<PropertiesValue> pvList = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            PropertiesValue propertiesValue = new PropertiesValue();
            propertiesValue.setPropertiesId(propertiesId);

            List<PropertiesValue> byPropertiesId = this.properValueDao.findByPropertiesId(propertiesId);
            if (ValueUtil.notEmpity(byPropertiesId) && byPropertiesId.size() > 0) {
                for (PropertiesValue pro : byPropertiesId) {
                    Boolean flag = true;
                    if (value.equals(pro.getCnValue())) {
                        flag = false;
                        return value + ":此属性值已存在";
                    }
                    if (flag) {
                        propertiesValue.setCode(code);
                        propertiesValue.setCnValue(value);
                    }
                }
                pvList.add(propertiesValue);

            } else {
                propertiesValue.setCode(code);
                propertiesValue.setCnValue(value);
                pvList.add(propertiesValue);
            }
        }
        try{
            properValueDao.save(pvList);
        }catch (Exception e){
            return "jdbc erro";
        }

        HttpBean httpBean = new HttpBean(Dictionary.MALL_HOST + "/goods/properValue/itf", RequestMethod.post);
        httpBean.addParameter("jsonData", ValueUtil.toJson(pvList));
        httpBean.run();
        String result = httpBean.getResponseContent();
        if (result==null) {
            String codes = ValueUtil.getFromJson(result, "code");
            if (codes == null || !codes.equals("201")) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return "同步失败";
            }
        }
        return "success";
    }

    @Override
    public String deletePropValue(String id) {
        String[] strs = id.split(";");
        try {
        List<PropertiesValue> propertiesValues = new ArrayList<>();
        for (int i = 0; i < strs.length; i++) {
            Integer id1 = Integer.valueOf(strs[i]);
            Boolean propertyValueUsed = this.categoryService.isPropertyValueUsed(id1);
            if (propertyValueUsed) {
                return "属性值已被使用，不能删除";
            } else {
                PropertiesValue propertiesValue = properValueDao.findOne(id1);
                propertiesValues.add(propertiesValue);
            }
        }
        properValueDao.delete(propertiesValues);
            //删除后商城同步
            HttpBean httpBean = new HttpBean(Dictionary.MALL_HOST + "/goods/properValue/syndelete", RequestMethod.post);
            httpBean.addParameter("id", id);
            httpBean.run();
            String result = httpBean.getResponseContent();
            if (result==null) {
                String codes = ValueUtil.getFromJson(result, "code");
                if (codes == null || !codes.equals("201")) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return "同步失败";
                }
            }

        } catch (Exception e) {
                return "jdbc erro";
        }
        return "success";
    }

    @Override
    public Boolean findByCnValueAndPropertiesId(String cnValue, Integer propId) {
        PropertiesValue byCnValueAndPropertiesId = this.properValueDao.findByCnValueAndPropertiesId(cnValue, propId);
        if(ValueUtil.notEmpity(byCnValueAndPropertiesId)){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public List<PropertiesValue> findByPropertiesId(Integer propId) {
        return properValueDao.findByPropertiesId(propId);
    }
}
