
package com.yesmywine.goods.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.goods.bean.DeleteEnum;
import com.yesmywine.goods.bean.IsShow;
import com.yesmywine.goods.bean.IsSku;
import com.yesmywine.goods.dao.*;
import com.yesmywine.goods.entity.CategoryProperty;
import com.yesmywine.goods.entity.Sku;
import com.yesmywine.goods.entity.SkuCommonProp;
import com.yesmywine.goods.entityProperties.Category;
import com.yesmywine.goods.entityProperties.Properties;
import com.yesmywine.goods.entityProperties.PropertiesValue;
import com.yesmywine.goods.service.CategoryService;
import com.yesmywine.goods.service.CommonService;
import com.yesmywine.httpclient.bean.HttpBean;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.*;
import com.yesmywine.util.basic.Dictionary;

import com.yesmywine.util.error.yesmywineException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.transaction.Transactional;
import java.util.*;


/**
 * Created by wangdiandian on 2016/12/9.
 */
@Service
@Transactional
public class CategoryServiceImpl extends BaseServiceImpl<Category, Integer> implements CategoryService {
    @Autowired
    private CategoryDao categoryRepository;
    @Autowired
    private ProperValueDao properValueDao;
    @Autowired
    private PropertiesDao propertiesDao;
    @Autowired
    private CategoryPropertyDao categoryPropertyDao;
    @Autowired
    private CommonService<Category> commonService;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private SkuCommonDao skuCommonDao;

    public JSONArray getOne(Integer categoryId) {
        List<CategoryProperty> cpList = categoryPropertyDao.findByCategoryIdOrderByPropertyId(categoryId);
        Collections.sort(cpList, new Comparator<CategoryProperty>() {
            @Override
            public int compare(CategoryProperty o1, CategoryProperty o2) {
                return o1.getPropertyId().compareTo(o2.getPropertyId());
            }
        });
        JSONArray resultJSON = new JSONArray();
        JSONArray valueJson = new JSONArray();
        Integer propertyId = 0;
        int i = 0;
        JSONObject jsonObject = new JSONObject();
        int j = 0;
        for (CategoryProperty cp : cpList) {
            Integer newPropertyId = cp.getPropertyId();
            PropertiesValue propertiesValue = cp.getPropertyValue();
            if (!propertyId.equals(newPropertyId)) {
                if (i > 0) {
                    jsonObject.put("values", valueJson);
                    resultJSON.add(jsonObject);
                    jsonObject = new JSONObject();
                    valueJson = new JSONArray();
                }
                propertyId = cp.getPropertyId();
                Integer type = cp.getType();
                Properties property = propertiesDao.findOne(propertyId);
                jsonObject.put("property", property);
                jsonObject.put("type", type);
                valueJson.add(propertiesValue);
                i = 0;
            } else {
                valueJson.add(propertiesValue);
            }
            i++;
            j++;
            if (j == cpList.size()) {
                jsonObject.put("values", valueJson);
                resultJSON.add(jsonObject);
            }
        }
        return resultJSON;
    }

    private void updateChildCategoryProperty(Category category, JSONArray delPJ)throws yesmywineException {
        List<Category> childCategoryList = categoryRepository.findByParentName(category);
        if (childCategoryList != null && delPJ != null) {
            for (int i = 0; i < delPJ.size(); i++) {
                JSONObject jsonObject = (JSONObject) delPJ.get(i);
                Integer propertyId = jsonObject.getInteger("propertyId");
                String valueIds = jsonObject.getString("valueIds");
                if (valueIds == null || valueIds.equals("")) {//如果爲空删除直接根据属性ID删除关联
                    for (Category childCategory : childCategoryList) {
                        categoryPropertyDao.deleteByCategoryIdAndPropertyId(childCategory.getId(), propertyId);
                        List<Category> grandchildCategoryList = categoryRepository.findByParentName(category);
                        if (grandchildCategoryList != null) {
                            for (Category grandchild : grandchildCategoryList) {
                                categoryPropertyDao.deleteByCategoryIdAndPropertyId(grandchild.getId(), propertyId);
                            }
                        }
                    }
                } else {//否则根据属性ID及属性值ID
                    String[] valueArr = valueIds.split(";");
                    for (String valueId : valueArr) {
                        for (Category childCategory : childCategoryList) {
                            PropertiesValue propertiesValue = null;
                            if(valueId!=null&&!valueId.equals("")){
                                propertiesValue = properValueDao.findOne(Integer.valueOf(valueId));
                                if(propertiesValue==null){
                                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                    ValueUtil.isError("包含不存在的属性值");
                                }
                            }
                            categoryPropertyDao.deleteByCategoryIdAndPropertyIdAndPropertyValue(childCategory.getId(), propertyId, propertiesValue);

                            List<Category> grandchildCategoryList = categoryRepository.findByParentName(childCategory);
                            if (grandchildCategoryList != null) {
                                for (Category grandchild : grandchildCategoryList) {
                                    PropertiesValue propertiesValueChild = null;
                                    if(valueId!=null&&!valueId.equals("")){
                                        propertiesValueChild = properValueDao.findOne(Integer.valueOf(valueId));
                                        if(propertiesValue==null){
                                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                            ValueUtil.isError("包含不存在的属性值");
                                        }
                                    }
                                    categoryPropertyDao.deleteByCategoryIdAndPropertyIdAndPropertyValue(grandchild.getId(), propertyId, propertiesValueChild);
                                }
                            }
                        }
                    }
                }
            }
        }


    }

    @Override
    public String update(Integer categoryId, String propertyJson, String delPropertyJson, Integer parentId, String isShow, String categoryName, String imgIds) throws yesmywineException {
        ValueUtil.verify(categoryId, "categoryId");
        ValueUtil.verify(isShow, "isShow");
        ValueUtil.verify(categoryName, "categoryName");
        JSONArray PPVJSON = JSON.parseArray(propertyJson);
        JSONArray delPJ = JSON.parseArray(delPropertyJson);
        Category category = categoryRepository.findOne(categoryId);
        Category newParent = null;
        if (ValueUtil.notEmpity(parentId)) {
            newParent = categoryRepository.findOne(parentId);
            ValueUtil.verifyNotExist(newParent,"无此父分类");
            Category oldParent = category.getParentName();
            if(oldParent!=null&&!oldParent.getLevel().equals(newParent.getLevel())){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("请选择同级分类！");
            }
        }

        category.setParentName(newParent);

        if (isShow.equals("yes")) {
            category.setIsShow(IsShow.yes);
        } else {
            category.setIsShow(IsShow.no);
        }

        if(!category.getCategoryName().equals(categoryName)){
            if (categoryRepository.findIdByCategoryNameAndLevel(categoryName,category.getLevel()).size()>0) {
                ValueUtil.isError("同级下已有相同的分类名");
            } else{
                category.setCategoryName(categoryName);
            }
        }
        //修改图片
        if(imgIds!=null&&!imgIds.equals("")){
            category = updateCategoryImg(category,imgIds);
        }


        Map<String, String> map = new HashMap<>();
        map.put("id", categoryId.toString());
        if (ValueUtil.notEmpity(parentId)) {
            map.put("parentName", parentId.toString());
        }

        categoryPropertyDao.deleteByCategoryId(category.getId());
        updateParentCategoryProperty(category, PPVJSON);
        updateChildCategoryProperty(category, delPJ);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("propertyJson", propertyJson);
            jsonObject.put("delPropertyJson", delPropertyJson);
            jsonObject.put("category", JSONUtil.objectToJsonStr(category));
//
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("json转换错误");
        }

        //同步到商城
            String mall_code = SynchronizeUtils.getCode(com.yesmywine.util.basic.Dictionary.MALL_HOST,"/goods/categories/synchronous",ValueUtil.toJson(HttpStatus.SC_CREATED,"update",jsonObject),RequestMethod.post );
        if(mall_code==null||!mall_code.equals("201")){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("因同步失败无法修改！");
        }

        Map<String, String> map1 = new HashMap<>();
        map1.put("id", category.getId().toString());
        map1.put("name", category.getCategoryName());
        map1.put("parentId", parentId.toString());
        if(!this.commonService.synchronous(map1, Dictionary.MALL_HOST+"/cms/synchronous/category", 1)){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("因同步失败无法新增！");
        }

        return ValueUtil.toJson(HttpStatus.SC_CREATED, "SUCCESS");
    }

    private Category updateCategoryImg(Category category, String imgIds)throws yesmywineException {
        JsonParser jsonParser = new JsonParser();
        JsonArray imageRe = null;
        Integer categoryId = category.getId();
        String ids = "";
        String imgs = category.getImage();
        Map[] image = MapUtil.jsonArrayToMap(imgs);
        String[] imgArr = imgIds.split(";");
        JSONArray imageEn = null;
        if (ValueUtil.notEmpity(image)) {
            for (int i = 0; i < imgArr.length; i++) {
                Boolean flag = true;
                for (int j = 0; j < image.length; j++) {
                    Map map = (Map)image[i].get("map");
                    if (!imgArr[i].equals(map.get("id").toString())) {
                        flag = true;
                    } else {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    Boolean flag1 = true;
                    if (flag1) {
                        ids = ids + imgArr[i];
                        flag1 = false;
                    } else {
                        Map map = (Map)image[i].get("map");
                        ids = ids + "," + map.get("id").toString();
                        flag1 = true;
                    }
                }
            }
        } else {
            imageEn = new JSONArray(imgArr.length);
            ids = StringUtils.join(imgArr, ",");
        }


        if (!"".equals(ids)) {
            HttpBean httpRequest = new HttpBean(Dictionary.MALL_HOST + "/fileUpload/tempToFormal/itf", RequestMethod.post);
            httpRequest.addParameter("module", "category");
            httpRequest.addParameter("mId", categoryId);
            httpRequest.addParameter("id", ids);
            httpRequest.run();
            String temp = httpRequest.getResponseContent();
            String cd = ValueUtil.getFromJson(temp, "code");
            if (!"201".equals(cd) || ValueUtil.isEmpity(cd)) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("图片修改失败");
            } else {
                imageEn = new JSONArray(imgArr.length + image.length);
                String result = ValueUtil.getFromJson(temp, "data");
                imageRe = jsonParser.parse(result).getAsJsonArray();
                for (int f = 0; f < imageRe.size(); f++) {
                    String idRe = imageRe.get(f).getAsJsonObject().get("id").getAsString();
                    String nameRe = imageRe.get(f).getAsJsonObject().get("name").getAsString();
                    JSONObject map1 = new JSONObject();
                    map1.put("id", idRe);
                    map1.put("name", nameRe);
                    imageEn.add(map1);
                }

            }
            String imgsJSON = ValueUtil.toJson(imageEn);
            String images = ValueUtil.getFromJson(imgsJSON,"data");
            category.setImage(images.replaceAll( "\"", "\'"));
        }
        return category;
    }

    @Override
    public Category physicsDelete(Category category) {
        categoryPropertyDao.deleteByCategoryId(category.getId());
        categoryRepository.delete(category.getId());
        return category;
    }

    @Override
    public Boolean isHaveChild(Integer categoryId) {
        Category category = new Category();
        category.setId(categoryId);
        List isExist = categoryRepository.findByParentName(category);
        if (isExist.size() > 0) {
            return true;
        }
        return false;
    }

    private Category saveCategoryImg(Category category, Integer[] imgIds, Integer parentId) throws yesmywineException {
        try{
            HttpBean httpRequest = new HttpBean(Dictionary.MALL_HOST + "/fileUpload/tempToFormal/itf", RequestMethod.post);
            httpRequest.addParameter("module", "category");
            httpRequest.addParameter("mId", category.getId());
            String ids = "";
            String imageIds = "";
            for (int i = 0; i < imgIds.length; i++) {
                if (i == 0) {
                    ids = ids + imgIds[i];
//                    imageIds=imageIds+imageId[i];
                } else {
                    ids = ids + "," + imgIds[i];
//                    imageIds=imageIds+":"+imageId[i];
                }
//                category.setImageId(imageIds);
                httpRequest.addParameter("id", ids);
            }
            httpRequest.run();
            String temp = httpRequest.getResponseContent();
            String cd = ValueUtil.getFromJson(temp, "code");
            if (!"201".equals(cd) || ValueUtil.isEmpity(cd)) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("图片上传失败");
            } else {
                JSONArray maps = new JSONArray(imgIds.length);
                String result = ValueUtil.getFromJson(temp, "data");
                JsonParser jsonParser = new JsonParser();
                JsonArray image = jsonParser.parse(result).getAsJsonArray();
                for (int f = 0; f < image.size(); f++) {
                    String id = image.get(f).getAsJsonObject().get("id").getAsString();
                    String name = image.get(f).getAsJsonObject().get("name").getAsString();
                    JSONObject map1 = new JSONObject();
                    map1.put("id", id);
                    map1.put("name", name);
                    maps.add(map1);
                }

                category.setImage(maps.toJSONString().replaceAll( "\"", "\'"));

                categoryRepository.save(category);

                JSONObject map = new JSONObject();
                if (ValueUtil.notEmpity(parentId)) {
                    map.put("parentName", parentId.toString());
                }
                for (int i = 0; i < maps.size(); i++) {
                    JSONObject jsonObject = (JSONObject) maps.get(i);
                    map.put("id" + i, jsonObject.getString("id"));
                    map.put("name" + i, jsonObject.getString("name"));
                }
                map.put("num", String.valueOf(maps.size()));
                return category;
            }
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("图片服务出现问题！");
        }
        return null;
    }

    private JSONArray updateParentCategoryProperty(Category category, JSONArray pJson)throws yesmywineException {
        JSONArray correlationArray = new JSONArray();

        Category parentCategory = category.getParentName();
        for (int i = 0; i < pJson.size(); i++) {
            JSONObject jsonObject = (JSONObject) pJson.get(i);
            Integer propertyId = jsonObject.getInteger("propertyId");
            String valueIds = jsonObject.getString("valueIds");
            String[] valueArr = valueIds.split(";");
            for (String valueId : valueArr) {
                JSONObject correlationObject = new JSONObject();
                CategoryProperty categoryProperty = new CategoryProperty();
                categoryProperty.setCategoryId(category.getId());
                categoryProperty.setPropertyId(propertyId);
                PropertiesValue propertiesValue = null;
                if(valueId!=null&&!valueId.equals("")){
                    propertiesValue = properValueDao.findOne(Integer.valueOf(valueId));
                    if(propertiesValue==null){
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        ValueUtil.isError("包含不存在的属性值");
                    }
                }
                categoryProperty.setPropertyValue(propertiesValue);
                categoryPropertyDao.save(categoryProperty);
                correlationObject.put("categoryId", String.valueOf(category.getId()));
                correlationObject.put("propertyId", String.valueOf(propertyId));
                correlationObject.put("propertyValueId", valueId);
                correlationArray.add(correlationObject);
                if (parentCategory != null) {
                    Category parent = categoryRepository.findOne(parentCategory.getId());
                    while (parent != null) {
                        PropertiesValue queryPV = null;
                        if(valueId!=null&&!valueId.equals("")){
                            queryPV = new PropertiesValue();
                            queryPV.setId(Integer.valueOf(valueId));
                        }
                        CategoryProperty isExist = categoryPropertyDao.findByCategoryIdAndPropertyIdAndPropertyValue(parent.getId(), propertyId, queryPV);
                        if (isExist == null) {
                            CategoryProperty saveCP = new CategoryProperty();
                            saveCP.setCategoryId(parent.getId());
                            saveCP.setPropertyId(propertyId);
                            PropertiesValue findPV = null;
                            if(valueId!=null&&!valueId.equals("")){
                                findPV = properValueDao.findOne(Integer.valueOf(valueId));
                                if(findPV==null){
                                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                    ValueUtil.isError("包含不存在的属性值");
                                }
                            }
                            saveCP.setPropertyValue(findPV);
                            categoryPropertyDao.save(saveCP);
                            correlationObject.put("categoryId", String.valueOf(category.getId()));
                            correlationObject.put("propertyId", String.valueOf(propertyId));
                            correlationObject.put("propertyValueId", valueId);
                            correlationArray.add(correlationObject);
                        }

                        Category pparent = parent.getParentName();
                        if (pparent == null) {
                            parent = null;
                        } else {
                            parent = categoryRepository.findOne(pparent.getId());
                        }
                    }
                } else {
                    PropertiesValue queryPV = null;
                    if(valueId!=null&&!valueId.equals("")){
                        queryPV = new PropertiesValue();
                        queryPV.setId(Integer.valueOf(valueId));
                    }
                    CategoryProperty isExist = categoryPropertyDao.findByCategoryIdAndPropertyIdAndPropertyValue(category.getId(), propertyId, queryPV);
                    if (isExist == null) {
                        CategoryProperty saveCP = new CategoryProperty();
                        saveCP.setCategoryId(category.getId());
                        saveCP.setPropertyId(propertyId);

                        PropertiesValue findPV = null;
                        if(valueId!=null&&!valueId.equals("")){
                            findPV = properValueDao.findOne(Integer.valueOf(valueId));
                            if(findPV==null){
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                ValueUtil.isError("包含不存在的属性值");
                            }
                        }
                        if(propertiesValue==null){
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            ValueUtil.isError("包含不存在的属性值");
                        }
                        saveCP.setPropertyValue(findPV);
                        categoryPropertyDao.save(saveCP);
                        correlationObject.put("categoryId", String.valueOf(category.getId()));
                        correlationObject.put("propertyId", String.valueOf(propertyId));
                        correlationObject.put("propertyValueId", valueId);
                        correlationArray.add(correlationObject);
                    }
                }
            }
        }

        return correlationArray;
    }

    public String insert(String categoryName, Integer parentId, String code, String isShow, Integer[] imgIds, String propertyJson) throws yesmywineException { //插入分类
        JSONArray pJson = JSON.parseArray(propertyJson);

        Category category = new Category();
        if (isShow.equals("yes")) {
            category.setIsShow(IsShow.yes);
        } else {
            category.setIsShow(IsShow.no);
        }
        category.setCategoryName(categoryName);
        category.setDeleteEnum(DeleteEnum.NOT_DELETE);
//            category.setUrl(url);
        category.setCode(code);
        Category parent = null;
        if (parentId != null) {
            parent = categoryRepository.findOne(parentId);
            if(parent==null){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("无此父分类！");
            }
            category.setLevel(parent.getLevel()+1);
        }else{
            category.setLevel(1);
        }
        if (categoryRepository.findIdByCategoryNameAndLevel(categoryName,category.getLevel()).size()>0) {
            ValueUtil.isError("同级下已有相同的分类名");
        }
        if (null != categoryRepository.findIdByCode(code)) {
            ValueUtil.isError("该编码已存在");
        }
        category.setParentName(parent);
        categoryRepository.save(category);

        updateParentCategoryProperty(category, pJson);
        Category category_afterImg = category;
        if(imgIds!=null&&!imgIds.equals("")){
            category_afterImg = saveCategoryImg(category, imgIds, parentId);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("propertyJson", propertyJson);
            jsonObject.put("category", JSONUtil.objectToJsonStr(category_afterImg));
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("json转换错误");
        }

        String mall_code = SynchronizeUtils.getCode(Dictionary.MALL_HOST,"/goods/categories/synchronous",ValueUtil.toJson(HttpStatus.SC_CREATED,"save",jsonObject),RequestMethod.post );
        if(mall_code==null||!mall_code.equals("201")){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("因同步失败无法新增！");
        }

        Map<String, String> map = new HashMap<>();
        map.put("id", category.getId().toString());
        map.put("name", category.getCategoryName());
        if(ValueUtil.notEmpity(parentId)) {
            map.put("parentId", parentId.toString());
        }
        if(!this.commonService.synchronous(map, Dictionary.MALL_HOST+"/cms/synchronous/category", 1)){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("因同步失败无法新增！");
        }
        return "SUCCESS";
    }


    public List findByDeleteEnumAndIsShow(DeleteEnum deleteEnum, IsShow isShow) {//找出未被删除可查看的分类
        return categoryRepository.findByDeleteEnumAndIsShow(deleteEnum, isShow);
    }

    public List<Category> showCategory() {
//        Category category=new Category();
//        category.setId(null);
        return categoryRepository.findByParentName(null);
    }

    public List<Category> findByDeleteEnum() {
        return categoryRepository.findByDeleteEnum(DeleteEnum.NOT_DELETE);
    }

    @Override
    public Boolean isPropertyValueUsed(Integer pvId) {
        PropertiesValue propertiesValue = new PropertiesValue();
        propertiesValue.setId(pvId);
        List<CategoryProperty> cpList = categoryPropertyDao.findByPropertyValue(propertiesValue);
        if (cpList.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public JSONArray getSKUProperty(Integer categoryId) {
        List<CategoryProperty> cpList = categoryPropertyDao.getSKUProperty(categoryId, IsSku.yes);
        Collections.sort(cpList, new Comparator<CategoryProperty>() {
            @Override
            public int compare(CategoryProperty o1, CategoryProperty o2) {
                return o1.getPropertyId().compareTo(o2.getPropertyId());
            }
        });
        JSONArray resultJSON = new JSONArray();
        JSONArray valueJson = new JSONArray();
        Integer propertyId = 0;
        int i = 0;
        JSONObject jsonObject = new JSONObject();
        int j = 0;
        for (CategoryProperty cp : cpList) {
            Integer newPropertyId = cp.getPropertyId();
            PropertiesValue propertiesValue = cp.getPropertyValue();
            if (!propertyId.equals(newPropertyId)) {
                if (i > 0) {
                    jsonObject.put("values", valueJson);
                    resultJSON.add(jsonObject);
                    jsonObject = new JSONObject();
                    valueJson = new JSONArray();
                }
                propertyId = cp.getPropertyId();
                Properties property = propertiesDao.findOne(propertyId);
                jsonObject.put("property", property);
                valueJson.add(propertiesValue);
                i = 0;
            } else {
                valueJson.add(propertiesValue);
            }
            i++;
            j++;
            if (j == cpList.size()) {
                jsonObject.put("values", valueJson);
                resultJSON.add(jsonObject);
            }
        }
        return resultJSON;
    }

    @Override
    public JSONArray getOrdinaryProperty(Integer categoryId) {
        List<CategoryProperty> cpList = categoryPropertyDao.getOrdinaryProperty(categoryId, IsSku.no);
        JSONArray resultJSON = new JSONArray();
        JSONArray valueJson = new JSONArray();
        Integer propertyId = 0;
        int i = 0;
        JSONObject jsonObject = new JSONObject();
        int j = 0;
        for (CategoryProperty cp : cpList) {

            Integer newPropertyId = cp.getPropertyId();
            PropertiesValue propertiesValue = cp.getPropertyValue();
            if (!propertyId.equals(newPropertyId)) {
                if (i > 0) {
                    jsonObject.put("values", valueJson);
                    resultJSON.add(jsonObject);
                    jsonObject = new JSONObject();
                    valueJson = new JSONArray();
                }
                propertyId = cp.getPropertyId();
                Properties property = propertiesDao.findOne(propertyId);
                jsonObject.put("property", property);
                valueJson.add(propertiesValue);
                i = 0;
            } else {
                valueJson.add(propertiesValue);
            }
            i++;
            j++;
            if (j == cpList.size()) {
                jsonObject.put("values", valueJson);
                resultJSON.add(jsonObject);
            }
        }
        return resultJSON;
    }

    @Override
    public List<Category> findByLevel(Integer level) {

        return categoryRepository.findByLevel(level);
    }

    @Override
    public JSONArray findAllChildrenByParentId(Integer parentId) {
        Category category = new Category();
        category.setId(parentId);
        List<Category> categoryList = categoryRepository.getAllChildren(parentId);
//        Map categoryList = categoryRepository.getAllChildrenStr(parentId);
//        List<Category> categoryList=categoryRepository.findByParentName(category);
        JSONArray jsonArray=new JSONArray();
        if(categoryList.size()!=0){
            for(Category c:categoryList){
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("value",c.getId());
                jsonObject.put("lable",c.getCategoryName());
                JSONArray jsonArray1=new JSONArray();
                List<Category> categoryList1=categoryRepository.findByParentName(categoryRepository.findOne(c.getId()));
                if(categoryList1.size()!=0) {
                    for (Category b : categoryList1) {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("value",b.getId());
                        jsonObject1.put("lable",b.getCategoryName());
                        jsonArray1.add(jsonObject1);
                    }
                    jsonObject.put("children",jsonArray1);
                }
                jsonArray.add(jsonObject);
            }
        }
//        System.out.println(categoryList);
        return jsonArray;
    }

    @Override
    public String updateProp(Integer categoryId, Integer propertyId, Integer type) {
        try {
            List<CategoryProperty> byCategoryIdAndPropertyId = this.categoryPropertyDao.findByCategoryIdAndPropertyId(categoryId, propertyId);
            for (CategoryProperty categoryProperty : byCategoryIdAndPropertyId) {
                categoryProperty.setType(type);
            }
            this.categoryPropertyDao.save(byCategoryIdAndPropertyId);
//            Category category = new Category();
//            category.setId(categoryId);
//            List<Sku> byCategory = this.skuDao.findByCategory(category);
//            for(Sku sku: byCategory){
//                List<SkuCommonProp> skuCommonProps = sku.getSkuCommonProp();
//                for(SkuCommonProp skuCommonProp: skuCommonProps){
//                    if(propertyId == skuCommonProp.getPropId()) {
//                        skuCommonProp.setType(type);
//                    }
//                }
//                this.skuCommonDao.save(skuCommonProps);
//            }
            Map<String, String> map = new HashMap<>();
            map.put("categoryId", categoryId.toString());
            map.put("proptiesId", propertyId.toString());
            map.put("type", type.toString());
            if(!this.commonService.synchronous(map, Dictionary.MALL_HOST+"/goods/goods/synchronousProp", 0)){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return "同步失败";
            }
            return "success";
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "erro";
        }
    }

    @Override
    public List<Category> getAllChildren(Integer parentId) {
        return categoryRepository.getAllChildren(parentId);
    }
}
