package com.yesmywine.logistics.controller;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.logistics.entity.Shippers;
import com.yesmywine.logistics.service.ExpressRuleService;
import com.yesmywine.logistics.service.LogisticsRuleService;
import com.yesmywine.logistics.service.ShipperService;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by wangdiandian on 2017/3/28.
 */
@RestController
@RequestMapping("/logistics/shippers")
public class ShipperController {

    @Autowired
    private ShipperService shipperService;
    @Autowired
    private ExpressRuleService expressRuleService;
    @Autowired
    private LogisticsRuleService logisticsRuleService;

    @RequestMapping(method = RequestMethod.POST)
    public String create(@RequestParam Map<String, String> param) {//新增承运商
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, shipperService.addShipper(param));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }

    }

    @RequestMapping(method = RequestMethod.DELETE)
    public String delete(Integer id) {//删除承运商
        try {
                return ValueUtil.toError(HttpStatus.SC_NO_CONTENT,shipperService.delete(id));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@RequestParam Map<String, String> param) {//修改保存承运商
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, shipperService.updateSave(param));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }


    @RequestMapping( method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params,Integer pageNo,Integer pageSize,Integer id) throws yesmywineException {//查询供应商
        if(id!=null){
                return ValueUtil.toJson(HttpStatus.SC_OK, shipperService.updateLoad(id));
        }
        params.put("deleteEnum", 0);
        if(params.get("shipperType")!=null) {
            if (ValueUtil.notEmpity(params.get("shipperType"))) {
                String shipperType = params.get("shipperType").toString();
                params.remove(params.remove("shipperType").toString());
                params.put("shipperType", shipperType);
            }
        }
        if(params.get("status")!=null) {
            if (ValueUtil.notEmpity(params.get("status"))) {
                String status = params.get("status").toString();
                params.remove(params.remove("status").toString());
                params.put("status", status);
            }
        }
        if(null!=params.get("all")&&params.get("all").toString().equals("true")){
            return ValueUtil.toJson(shipperService.findAll());
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
        pageModel = shipperService.findAll(pageModel);
        return ValueUtil.toJson(HttpStatus.SC_OK,pageModel);
        }


    @RequestMapping( value = "rule",method = RequestMethod.GET)
    public String indexRule(@RequestParam Map<String, Object> params,Integer pageNo,Integer pageSize) throws yesmywineException {//查询供应商
        String shipperId=params.get("id").toString();
        if(shipperId==null){
            return null;
        }
        Shippers shippers=shipperService.findOne(Integer.valueOf(params.get("id").toString()));
        Integer type=shippers.getShipperType();
        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);

        if(type==0){
            params.put("deleteEnum", 0);
            if (null != params.get("id")) {
                params.remove(params.remove("id").toString());
                params.put("shipperId",shipperId);
            }
            if (null != params.get("showFields")) {
                pageModel.setFields(params.remove("showFields").toString());
            }
            if (pageNo != null) params.remove(params.remove("pageNo").toString());
            if (pageSize != null) params.remove(params.remove("pageSize").toString());
            pageModel.addCondition(params);
            pageModel = expressRuleService.findAll(pageModel);
        }else{
            params.put("deleteEnum", 0);
            if (null != params.get("id")) {
                params.remove(params.remove("id").toString());
                params.put("shipperId",shipperId);
            }
            if (null != params.get("showFields")) {
                pageModel.setFields(params.remove("showFields").toString());
            }
            if (pageNo != null) params.remove(params.remove("pageNo").toString());
            if (pageSize != null) params.remove(params.remove("pageSize").toString());
            pageModel.addCondition(params);
            pageModel = logisticsRuleService.findAll(pageModel);
        }
        return ValueUtil.toJson(HttpStatus.SC_OK,pageModel);
    }

    @RequestMapping(value = "status",method = RequestMethod.PUT)
    public String updateStatus(@RequestParam Map<String, String> param) {//修改状态
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, shipperService.updateStatus(param));
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

}

