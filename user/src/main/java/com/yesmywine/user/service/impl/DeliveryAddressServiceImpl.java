package com.yesmywine.user.service.impl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.user.dao.DeliveryAddressDao;
import com.yesmywine.user.entity.DeliveryAddress;
import com.yesmywine.user.service.DeliveryAddressService;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by SJQ on 2017/4/20.
 */
@Service
@Transactional
public class DeliveryAddressServiceImpl extends BaseServiceImpl<DeliveryAddress,Integer> implements DeliveryAddressService {

    @Autowired
    private DeliveryAddressDao deliveryAddressDao;

    public List<DeliveryAddress> findByUserId(Integer userId) {
        return  deliveryAddressDao.findByUserId(userId);
    }

    public String synchronous(Map<String, String> param) throws yesmywineException {
        if (0 == Integer.parseInt(String.valueOf(param.get("synchronous")))) {
            save(param);
            return "update success";
        } else if (2 == Integer.parseInt(String.valueOf(param.get("synchronous")))) {
            String id = param.get("id");
            delete(Integer.valueOf(id));
            return "update success";
        } else {
            updateSave(param);
            return "update success";
        }
    }

    public String save(Map<String, String> param) throws yesmywineException {//新增收货地址

        DeliveryAddress deliveryAddress=new DeliveryAddress();
        String id=param.get("id");
        String receiver=param.get("receiver");//收货人
        String provinceId=param.get("provinceId");//省
        String province=param.get("province");//省
        String cityId=param.get("cityId");//市
        String city=param.get("city");//市
        String areaId=param.get("areaId");//区
        String area=param.get("area");//区
        String detailedAddress=param.get("detailedAddress");//详细地址
        String phoneNumber=param.get("phoneNumber");//手机号码
        String fixedTelephone=param.get("fixedTelephone");//固定电话（选填）
        String mailbox=param.get("mailbox"); //邮箱（选填）
        String addressAlias=param.get("addressAlias"); //地址别名（选填）
        String userId=param.get("userId"); //用户id
        String status=param.get("status"); //状态：0默认地址，1不是默认地址

        deliveryAddress.setId(Integer.valueOf(id));
        deliveryAddress.setReceiver(receiver);
        deliveryAddress.setProvinceId(Integer.valueOf(provinceId));
        deliveryAddress.setProvince(province);
//        deliveryAddress.setCityId(Integer.valueOf(cityId));
//        deliveryAddress.setCity(city);
//        deliveryAddress.setAreaId(Integer.valueOf(areaId));
//        deliveryAddress.setArea(area);
        if(!cityId.equals("null")){
            deliveryAddress.setCityId(Integer.valueOf(cityId));
            deliveryAddress.setCity(city);
        }
        if(!areaId.equals("null")){
            deliveryAddress.setAreaId(Integer.valueOf(areaId));
            deliveryAddress.setArea(area);
        }
        deliveryAddress.setDetailedAddress(detailedAddress);
        deliveryAddress.setPhoneNumber(phoneNumber);
        deliveryAddress.setFixedTelephone(fixedTelephone);
        deliveryAddress.setMailbox(mailbox);
        deliveryAddress.setAddressAlias(addressAlias);
        deliveryAddress.setCreateTime(new Date());
        deliveryAddress.setUserId(Integer.valueOf(userId));
        deliveryAddress.setStatus(Integer.valueOf(status));
        deliveryAddressDao.save(deliveryAddress);
        return "success";
    }

    public String delete(Integer id) throws yesmywineException {//删除收货地址
        DeliveryAddress deliveryAddress=deliveryAddressDao.findOne(id);
        if(deliveryAddress!=null){
            deliveryAddressDao.delete(deliveryAddress);
        }
        return "success";
    }

    public String updateSave(Map<String, String> param) throws yesmywineException {//修改保存收货地址

        String id=param.get("id");
        DeliveryAddress deliveryAddress=deliveryAddressDao.findOne(Integer.valueOf(id));
        String receiver=param.get("receiver");//收货人
        String provinceId=param.get("provinceId");//省
        String province=param.get("province");//省
        String cityId=param.get("cityId");//市
        String city=param.get("city");//市
        String areaId=param.get("areaId");//区
        String area=param.get("area");//区
        String detailedAddress=param.get("detailedAddress");//详细地址
        String phoneNumber=param.get("phoneNumber");//手机号码
        String fixedTelephone=param.get("fixedTelephone");//固定电话（选填）
        String mailbox=param.get("mailbox"); //邮箱（选填）
        String addressAlias=param.get("addressAlias"); //地址别名（选填）
        String userId=param.get("userId"); //用户id
        String status=param.get("status"); //状态：0默认地址，1不是默认地址

        deliveryAddress.setReceiver(receiver);
        deliveryAddress.setProvinceId(Integer.valueOf(provinceId));
        deliveryAddress.setProvince(province);
//        deliveryAddress.setCityId(Integer.valueOf(cityId));
//        deliveryAddress.setCity(city);
//        deliveryAddress.setAreaId(Integer.valueOf(areaId));
//        deliveryAddress.setArea(area);
        if(!cityId.equals("null")){
            deliveryAddress.setCityId(Integer.valueOf(cityId));
            deliveryAddress.setCity(city);
        }
        if(!areaId.equals("null")){
            deliveryAddress.setAreaId(Integer.valueOf(areaId));
            deliveryAddress.setArea(area);
        }
        deliveryAddress.setDetailedAddress(detailedAddress);
        deliveryAddress.setPhoneNumber(phoneNumber);
        deliveryAddress.setFixedTelephone(fixedTelephone);
        deliveryAddress.setMailbox(mailbox);
        deliveryAddress.setAddressAlias(addressAlias);
        deliveryAddress.setUserId(Integer.valueOf(userId));
        deliveryAddress.setStatus(Integer.valueOf(status));
        deliveryAddressDao.save(deliveryAddress);
        return "success";
    }

}
