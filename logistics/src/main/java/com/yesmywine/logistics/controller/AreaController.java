package com.yesmywine.logistics.controller;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.logistics.dao.AreaDao;
import com.yesmywine.logistics.entity.Area;
import com.yesmywine.logistics.service.AreaService;
import com.yesmywine.util.basic.MapUtil;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wangdiandian on 2017/4/13.
 */
@RestController
@RequestMapping("/logistics/area")
public class AreaController {
    @Autowired
    private AreaService areaService;
    @Autowired
    private AreaDao areaDao;
    @RequestMapping(value = "all",method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params,Integer pageNo,Integer pageSize,Integer id) throws  Exception{//分页查询

        MapUtil.cleanNull(params);
        if(id!=null){
            return ValueUtil.toJson(HttpStatus.SC_OK, areaService.findOne(id));
        }
        if(null!=params.get("all")&&params.get("all").toString().equals("true")){
            return ValueUtil.toJson(areaService.findAll());
        }else if(null!=params.get("all")){
            params.remove(params.remove("all").toString());
        }
        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }
        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());
        pageModel.addCondition(params);
        pageModel = areaService.findAll(pageModel);
        return ValueUtil.toJson(HttpStatus.SC_OK,pageModel);
        }

    @RequestMapping(method = RequestMethod.POST)
    public String create(Integer areaNo,String cityName, Integer parentId) {//新增城市
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, areaService.createArea(areaNo,cityName,parentId));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }

    }

    @RequestMapping(method = RequestMethod.DELETE)
    public String delete(Integer id) {//删除城市
        try {
            return ValueUtil.toError(HttpStatus.SC_NO_CONTENT,areaService.delete(id));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(Integer id,Integer areaNo,String cityName, Integer parentId) {//修改保存城市
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, areaService.updateSave(id,areaNo, cityName,parentId));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(value = "secondLevel",method = RequestMethod.GET)
    public String showSecondLevel() throws yesmywineException {//查看城市二级
        try {
            System.out.println("开始查询    "+new Date());
            List<Area> list=areaService.showArea();
            System.out.println("查询完成    "+new Date());
            System.out.println("开始组合数据    "+new Date());

            com.alibaba.fastjson.JSONArray jsonArray2 = new com.alibaba.fastjson.JSONArray();
            for(int i=0;i<list.size();i++){
                Area area = list.get(i);
                List<Area> list1=areaDao.findByParentId(area.getAreaNo());
                com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                com.alibaba.fastjson.JSONArray jsonArray3 = new com.alibaba.fastjson.JSONArray();
                for(int j=0;j<list1.size();j++){
                    Area areaCate = list1.get(j);
                    com.alibaba.fastjson.JSONObject jsonObject2 = new com.alibaba.fastjson.JSONObject();
                    jsonObject2.put("value",areaCate.getAreaNo());
                    jsonObject2.put("label",areaCate.getCityName());
                    jsonArray3.add(jsonObject2);
                }
                jsonObject.put("value",list.get(i).getAreaNo()) ;
                jsonObject.put("label",list.get(i).getCityName());
                if(jsonArray3.size()>0){
                    jsonObject.put("children",jsonArray3);
                }
                jsonArray2.add(jsonObject);
            }
            System.out.println("组合数据完成    "+new Date());
            return ValueUtil.toJson(HttpStatus.SC_OK,jsonArray2);
        } catch (yesmywineException e) {
            return ValueUtil.toError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Erro");
        }

        }

    @RequestMapping( method = RequestMethod.GET)
    public String showCatogory() throws yesmywineException {//查看城市三级
        try {
            System.out.println("开始查询    "+new Date());
            List<Area> list=areaService.showArea();
            System.out.println("查询完成    "+new Date());
            System.out.println("开始组合数据    "+new Date());

            com.alibaba.fastjson.JSONArray jsonArray2 = new com.alibaba.fastjson.JSONArray();
            for(int i=0;i<list.size();i++){
                Area area = list.get(i);
                List<Area> list1=areaDao.findByParentId(area.getAreaNo());
                com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                com.alibaba.fastjson.JSONArray jsonArray3 = new com.alibaba.fastjson.JSONArray();
                for(int j=0;j<list1.size();j++){
                    Area areaCate = list1.get(j);
//                    Area area2 = list1.get(i);
                    List<Area> list2=areaDao.findByParentId(areaCate.getAreaNo());
                    com.alibaba.fastjson.JSONArray jsonArray4 = new com.alibaba.fastjson.JSONArray();
                    for(int k=0;k<list2.size();k++) {
                        Area areaCate1 = list2.get(k);
                        com.alibaba.fastjson.JSONObject jsonObject3 = new com.alibaba.fastjson.JSONObject();
                        jsonObject3.put("value",areaCate1.getAreaNo());
                        jsonObject3.put("label",areaCate1.getCityName());
                        jsonArray4.add(jsonObject3);
                    }
                    com.alibaba.fastjson.JSONObject jsonObject2 = new com.alibaba.fastjson.JSONObject();
                    jsonObject2.put("value",areaCate.getAreaNo());
                    jsonObject2.put("label",areaCate.getCityName());
                    jsonArray3.add(jsonObject2);
                    if(jsonArray4.size()>0){
                        jsonObject2.put("children",jsonArray4);
                    }
                }
                jsonObject.put("value",list.get(i).getAreaNo()) ;
                jsonObject.put("label",list.get(i).getCityName());
                if(jsonArray3.size()>0){
                    jsonObject.put("children",jsonArray3);
                }
                jsonArray2.add(jsonObject);
            }
            System.out.println("组合数据完成    "+new Date());
            return ValueUtil.toJson(HttpStatus.SC_OK,jsonArray2);
        } catch (yesmywineException e) {
            return ValueUtil.toError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Erro");
        }
    }
    @RequestMapping(value = "itf",method = RequestMethod.GET)
    public String query(String areaName) {//内部调用
        try {
            return ValueUtil.toJson(HttpStatus.SC_OK, areaService.query(areaName));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }

    }

}
