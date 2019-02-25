package com.yesmywine.goods.controller;


import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.goods.dao.CategoryDao;
import com.yesmywine.goods.entity.Goods;
import com.yesmywine.goods.service.GoodsService;
import com.yesmywine.util.basic.MapUtil;
import com.yesmywine.util.basic.Threads;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by hz on 12/8/16.消息机制
 */
@RestController
@RequestMapping("/goods/goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private CategoryDao categoryDao;

    @RequestMapping(method = RequestMethod.POST)
    public String create(@RequestParam Map<String, String> param) {//新增商品
        try {
            if(categoryDao.findOne(Integer.parseInt(param.get("categoryId"))).getLevel()!=3){
                ValueUtil.isError("分类只可为3级");
            }
            String s = goodsService.addGoods(param);
            if("success".equals(s)) {
                return ValueUtil.toError(HttpStatus.SC_CREATED, s);
            }else {
                return ValueUtil.toError("500", s);
            }
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public String delete(Integer id) {//删除商品
        try {
            return ValueUtil.toError(HttpStatus.SC_NO_CONTENT,goodsService.delete(id));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }


    @RequestMapping(value = "/sku", method = RequestMethod.GET)
    public String skuIndex(@RequestParam Map<String, Object> params,Integer pageNo,Integer pageSize,Integer id) throws yesmywineException {//分页查询商品

//        this.goodsService.querySku(id);
        return ValueUtil.toJson(HttpStatus.SC_OK,this.goodsService.querySku(id));

//        if(null!=params.get("all")&&params.get("all").toString().equals("true")){
//            return ValueUtil.toJson(goodsService.findAll());
//        }else if(null!=params.get("all")){
//            params.remove(params.remove("all").toString());
//        }
//        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
//        if (null != params.get("showFields")) {
//            pageModel.setFields(params.remove("showFields").toString());
//        }
//        if (pageNo != null) params.remove(params.remove("pageNo").toString());
//        if (pageSize != null) params.remove(params.remove("pageSize").toString());
//
//        if(ValueUtil.notEmpity(params.get("categoryId_l"))){
//            params.put("categoryGroup_l", "L" + params.get("categoryId_l"));
//            params.remove(params.remove("categoryId_l").toString());
//        }
//
//        pageModel.addCondition(params);
//        pageModel = goodsService.findAll(pageModel);
//        List<Goods> conditions = pageModel.getContent();
//        for(int i=0; i< pageModel.getContent().size(); i ++){
//            conditions.get(i).setCategoryName(this.categoryDao.findOne( Integer.valueOf(conditions.get(i).getCategoryId().toString())).getCategoryName());
//        }
//        pageModel.setContent(conditions);
//        return ValueUtil.toJson(HttpStatus.SC_OK,pageModel);

    }

    @RequestMapping( method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params,Integer pageNo,Integer pageSize,Integer id) throws yesmywineException {//分页查询商品
        MapUtil.cleanNull(params);
        if(id!=null){
          Goods goods = goodsService.findOne(id);
          goods.setCategoryName(this.categoryDao.findOne(Integer.valueOf(goods.getCategoryId().toString())).getCategoryName());
          return ValueUtil.toJson(HttpStatus.SC_OK,goods);

      }
        if(null!=params.get("all")&&params.get("all").toString().equals("true")){
            return ValueUtil.toJson(goodsService.findAll());
        }else if(null!=params.get("all")){
            params.remove(params.remove("all").toString());
        }
        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }
        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());

        if(ValueUtil.notEmpity(params.get("categoryId_l"))){
            params.put("categoryGroup_l", "L" + params.get("categoryId_l"));
            params.remove(params.remove("categoryId_l").toString());
        }

        pageModel.addCondition(params);
        pageModel = goodsService.findAll(pageModel);
        List<Goods> conditions = pageModel.getContent();
//        List conditions2 = new ArrayList();
        for(int i=0; i< pageModel.getContent().size(); i ++){
            conditions.get(i).setCategoryName(this.categoryDao.findOne( Integer.valueOf(conditions.get(i).getCategoryId().toString())).getCategoryName());
//            Object obj = conditions.get(i);
////            Map<?, ?> map = new org.apache.commons.beanutils.BeanMap(o);
//
//            Map<String, Object> map = new HashMap<String, Object>();
//
//            Field[] declaredFields = obj.getClass().getDeclaredFields();
//            for (Field field : declaredFields) {
//                field.setAccessible(true);
//                try {
//                    map.put(field.getName(), field.get(obj));
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }


//            if(ValueUtil.notEmpity(map.get("categoryId"))){
//                map.put("categoryName", this.categoryDao.findOne( Integer.valueOf(map.get("categoryId").toString())).getCategoryName());
//            }
//            conditions2.add(i, map);
        }
        pageModel.setContent(conditions);
        return ValueUtil.toJson(HttpStatus.SC_OK,pageModel);

    }

}



