
package com.yesmywine.goods.controller;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.goods.dao.CategoryDao;
import com.yesmywine.goods.dao.SkuDao;
import com.yesmywine.goods.entityProperties.Category;
import com.yesmywine.goods.service.CategoryService;
import com.yesmywine.goods.service.CommonService;
import com.yesmywine.util.basic.*;
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
 * Created by hz on 2016/12/9.
 */
@RestController
@RequestMapping("/goods/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private CommonService<Category> commonService;

    @RequestMapping(method = RequestMethod.GET)
    public String page(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize,String isShow,String deleteEnum,Integer id,String type) {   //查看
        MapUtil.cleanNull(params);
        if(id != null&&type==null){
            Category category = categoryService.findOne(id);
            category.setPropertyInfo(categoryService.getOne(id));
            return ValueUtil.toJson(category);
        }else if(id != null&&type!=null&&type.equals("isSku")){
            return ValueUtil.toJson(categoryService.getSKUProperty(id));
        }else if(id != null&&type!=null&&type.equals("notSku")){
            return ValueUtil.toJson(categoryService.getOrdinaryProperty(id));
        }
        if (null != params.get("all") && params.get("all").toString().equals("true")) {
            return ValueUtil.toJson(categoryService.findAll());
        } else if (null != params.get("all")) {
            params.remove(params.remove("all").toString());
        }

        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }
        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());
        if(ValueUtil.notEmpity(isShow)){
            if(isShow.equals("all")){
                params.remove("isShow");
            }else {
                params.put("isShow_eq_com.yesmywine.goods.bean.IsShow",isShow);
                params.remove("isShow");
            }
        }else {
            params.remove("isShow");
        }
        if(ValueUtil.isEmpity(deleteEnum)){
            params.put("deleteEnum_eq_com.yesmywine.goods.bean.DeleteEnum","NOT_DELETE");
            params.remove("deleteEnum");
        }
        pageModel.addCondition(params);
        return ValueUtil.toJson(HttpStatus.SC_OK,categoryService.findAll(pageModel));
    }

    //根据等级查找分类
    @RequestMapping(value = "/findByLevel",method = RequestMethod.GET)
    public String findByLevel(Integer level){
        try {
            ValueUtil.verify(level,"level");
            List<Category> categoryList = categoryService.findByLevel(level);
            return ValueUtil.toJson(categoryList);
        }catch (yesmywineException e){
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }

    }

    @RequestMapping(value = "/getChildren", method = RequestMethod.GET)
    public String findAllChildren(Integer parentId) {
        try {
            return ValueUtil.toJson(HttpStatus.SC_OK,categoryService.findAllChildrenByParentId(parentId));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    /*
    *@Author:Gavin
    *@Email:gavinsjq@sina.com
    *@Date:  2017/8/8
    *@Param
    *@Description:递归查询所有子分类
    */
    @RequestMapping(value = "/getChildren/itf", method = RequestMethod.GET)
    public String getAllChildren(Integer parentId) {
        return ValueUtil.toJson(HttpStatus.SC_OK,categoryService.getAllChildren(parentId));
    }

    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public String showCatogory(String all) {   //分类树结构显示
        try {
            List<Category> list=categoryService.showCategory();
            com.alibaba.fastjson.JSONArray firstChild = new com.alibaba.fastjson.JSONArray();
            for(int i=0;i<list.size();i++){
                Integer parentId=list.get(i).getId();
                Category c=new Category();
                c.setId(parentId);
                List<Category> list1=categoryDao.findByParentName(c);
                if(list1!=null){
                    com.alibaba.fastjson.JSONObject first = new com.alibaba.fastjson.JSONObject();
                    com.alibaba.fastjson.JSONArray secondChild = new com.alibaba.fastjson.JSONArray();
                    for(int j=0;j<list1.size();j++){
                        Category childCate = list1.get(j);
                        com.alibaba.fastjson.JSONObject second = new com.alibaba.fastjson.JSONObject();
                        second.put("value",childCate.getId());
                        second.put("label",childCate.getCategoryName());
                        secondChild.add(second);

                        if(all!=null&&all.equals("y")){
                            List<Category> grandChildList = categoryDao.findByParentName(childCate);
                            if(grandChildList!=null){
                                com.alibaba.fastjson.JSONArray thildChildren = new com.alibaba.fastjson.JSONArray();
                                for(Category grandChildCate:grandChildList){
                                    com.alibaba.fastjson.JSONObject third = new com.alibaba.fastjson.JSONObject();
                                    third.put("value",grandChildCate.getId());
                                    third.put("label",grandChildCate.getCategoryName());
                                    thildChildren.add(third);
                                }
                                if(thildChildren.size()>0){
                                    second.put("children",thildChildren);
                                }
                            }
                        }

                    }
                    first.put("value",list.get(i).getId()) ;
                    first.put("label",list.get(i).getCategoryName());
                    if(secondChild.size()>0){
                        first.put("children",secondChild);
                    }
                    firstChild.add(first);
                }

            }
            return ValueUtil.toJson(HttpStatus.SC_OK,firstChild);
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(String categoryName,String propertyJson, Integer parentId, String code, String isShow,String imgIds) {   //新增
        try {
            ValueUtil.verify(categoryName,"categoryName");
            ValueUtil.verify(code,"code");
            ValueUtil.verify(isShow,"isShow");
            ValueUtil.verify(propertyJson,"propertyJson");
            String[] imgArr = imgIds.split(";");
            Integer[] arr=new Integer[imgArr.length];
            for(int i=0;i<imgArr.length;i++){
                arr[i]=Integer.parseInt(imgArr[i]);
            }
            return ValueUtil.toJson(HttpStatus.SC_CREATED, categoryService.insert(categoryName, parentId, code, isShow,arr,propertyJson));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(value = "/showOne", method = RequestMethod.GET)
    public String showOne(Integer categoryId) {
        try {
            ValueUtil.verify(categoryId,"categoryId");
            com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
            com.alibaba.fastjson.JSONObject JSONObject1 = new com.alibaba.fastjson.JSONObject();
            com.alibaba.fastjson.JSONObject JSONObject2 = new com.alibaba.fastjson.JSONObject();
            com.alibaba.fastjson.JSONArray jsonArray = new com.alibaba.fastjson.JSONArray();
            Category category=categoryDao.findOne(categoryId);
            Category category1=category.getParentName();
//            Category category2=category1.getParentName();
            if(null!=category.getParentName()){
                JSONObject1.put("id",category.getParentName().getId());
                JSONObject1.put("name",category.getParentName().getCategoryName());
                jsonArray.add(JSONObject1);
                if(null!=category1.getParentName()){
                    JSONObject2.put("id",category1.getParentName().getId());
                    JSONObject2.put("name",category1.getParentName().getCategoryName());
                    jsonArray.add(JSONObject2);
                }
            }
            jsonObject.put("id",category.getId());
            jsonObject.put("categoryName",category.getCategoryName());
            jsonObject.put("isShow",category.getIsShow());
            jsonObject.put("code",category.getCode());
            jsonObject.put("image",category.getImage());
            jsonObject.put("parent",jsonArray);
//            jsonObject.put("imageId",category.getImageId());
            return ValueUtil.toJson(HttpStatus.SC_OK,jsonObject);
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }

    }

    @RequestMapping(method = RequestMethod.PUT)
    public String updateSave(Integer categoryId,String propertyJson,String delPropertyJson,Integer parentId,String isShow,String categoryName,String imgIds) {
        try {
            return categoryService.update(categoryId,propertyJson,delPropertyJson,parentId,isShow,categoryName,imgIds);
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping( method = RequestMethod.DELETE)
    public String delete(Integer categoryId) {
        try {
            ValueUtil.verify(categoryId,"categoryId");

        Category category = categoryService.findOne(categoryId);
        if(category==null){
            ValueUtil.isError("无此分类！");
        }
        Category category1=new Category();
        category1.setId(categoryId);
        Boolean isExist = categoryService.isHaveChild(categoryId);
        if(skuDao.findByCategory(category1).size()==0&&!isExist){
            String mall_code = SynchronizeUtils.getCode(Dictionary.MALL_HOST,"/goods/categories/synchronous",ValueUtil.toJson(HttpStatus.SC_CREATED,"delete",categoryId), com.yesmywine.httpclient.bean.RequestMethod.post );
            if(mall_code==null||!mall_code.equals("201")){
                ValueUtil.isError("因同步失败无法新增！");
            }
            categoryService.physicsDelete(category);
            return ValueUtil.toJson(HttpStatus.SC_NO_CONTENT, "delete");
        }else
            return ValueUtil.toError("500","该分类已被使用,不可删除");


        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }


    @RequestMapping(value = "/prop", method = RequestMethod.PUT)
    public String updateProp(Integer categoryId, Integer propertyId, Integer type) {
        try {
            String s = categoryService.updateProp(categoryId, propertyId, type);
            if("success".equals(s)){
                return ValueUtil.toJson(HttpStatus.SC_CREATED, s);
            }
            return ValueUtil.toError("500", s);
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }


}