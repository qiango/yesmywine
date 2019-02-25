package com.yesmywine.logistics.service.Impl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.logistics.dao.AreaDao;
import com.yesmywine.logistics.entity.Area;
import com.yesmywine.logistics.service.AreaService;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

/**
 * Created by wangdiandian on 2017/4/13.
 */
@Service
@Transactional

public class AreaImpl  extends BaseServiceImpl<Area, Integer> implements AreaService {

    @Autowired
    private AreaDao areaDao;

    public String createArea(Integer areaNo,String cityName, Integer parentId) throws yesmywineException {//新增城市
        Area areaNos = areaDao.findByAreaNo(areaNo);
        if(areaNos!=null){
                ValueUtil.isError("此地区编码已存在");
        }
        Area area = new Area();
        area.setAreaNo(areaNo);
        area.setCityName(cityName);
        if (parentId != null) {
            Area parent = areaDao.findByAreaNo(parentId);
            if (parent == null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("无此父地区！");
            }
            area.setParentName(parent);
        }
        areaDao.save(area);
        return "success";
    }
    public String delete(Integer id) throws yesmywineException {//删除城市
        ValueUtil.verify(id, "idNull");
        Area area = areaDao.findOne(id);
        areaDao.delete(area);
        return "success";
    }

    public String updateSave(Integer id,Integer areaNo,String cityName, Integer parentId) throws yesmywineException {//跟新城市
        ValueUtil.verify(id, "idNull");
        Area area = areaDao.findOne(id);
        Area areaNos = areaDao.findByAreaNo(areaNo);
        if(areaNos!=null&&areaNos.getId()!=id){
            ValueUtil.isError("此行政区划代码已存在");
        }

        area.setAreaNo(areaNo);
        area.setCityName(cityName);
        if (parentId != null) {
            Area parent = areaDao.findByAreaNo(parentId);
            if (parent == null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("无此父地区！");
            }
            area.setParentName(parent);
        }
        areaDao.save(area);
        return "success";

    }

    public List<Area> showArea(){
        return areaDao.findByParentName(null);
    }
    public String query(String areaName)throws yesmywineException {
        ValueUtil.verify(areaName, "idNull");
        Area area = areaDao.findByCityName(areaName);
        if (area == null) {
            ValueUtil.isError("没有该地区");
        }
        Area cityId = areaDao.findByAreaNo(area.getParentName().getAreaNo());
        Area province=null;
        if(cityId.getParentName()!=null){
            province= areaDao.findByAreaNo(cityId.getParentName().getAreaNo());
        }
        StringBuffer sb = new StringBuffer();
        if (province == null) {
            sb.append(cityId.getAreaNo() + "," + area.getAreaNo());
        } else {
            sb.append(province.getAreaNo() + "," + cityId.getAreaNo() + "," + area.getAreaNo());
        }
        return sb.toString();
    }
}
