package com.yesmywine.user.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by SJQ on 2017/4/20.
 */
@Entity
@Table(name = "deliveryAddress")
public class DeliveryAddress  {
    @Id
    protected Integer id;
    @Column(columnDefinition = "DATETIME COMMENT '用户名'")
    protected Date createTime;
    @Column(columnDefinition = "varchar(50) COMMENT '收货人'")
    private String receiver; //收货人
    @Column(columnDefinition = "int(10) COMMENT '省份Id'")
    private Integer provinceId;//省
    @Column(columnDefinition = "varchar(10) COMMENT '省份'")
    private String province;//省
    @Column(columnDefinition = "int(10) COMMENT '市份Id'")
    private Integer cityId;//市
    @Column(columnDefinition = "varchar(10) COMMENT '市区'")
    private String city;//市
    @Column(columnDefinition = "int(10) COMMENT '地区Id'")
    private Integer areaId;//区
    @Column(columnDefinition = "varchar(10) COMMENT '区'")
    private String area;//区
    @Column(columnDefinition = "varchar(100) COMMENT '详细地址'")
    private String detailedAddress;//详细地址
    @Column(columnDefinition = "varchar(50) COMMENT '手机号'")
    private String  phoneNumber;//手机号码
    @Column(columnDefinition = "varchar(50) COMMENT '固定电话'")
    private String fixedTelephone;//固定电话（选填）
    @Column(columnDefinition = "varchar(50) COMMENT '邮箱'")
    private String mailbox;  //邮箱（选填）
    @Column(columnDefinition = "varchar(50) COMMENT '地址别名（选填'")
    private String addressAlias;//地址别名（选填）
    @Column(columnDefinition = "int(10) COMMENT '用户Id'")
    private Integer userId;//用户Id
    @Column(columnDefinition = "int(10) COMMENT '状态：0默认地址，1不是默认地址'")
    private Integer status;//0默认地址，1不是默认地址

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDetailedAddress() {
        return detailedAddress;
    }

    public void setDetailedAddress(String detailedAddress) {
        this.detailedAddress = detailedAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFixedTelephone() {
        return fixedTelephone;
    }

    public void setFixedTelephone(String fixedTelephone) {
        this.fixedTelephone = fixedTelephone;
    }

    public String getMailbox() {
        return mailbox;
    }

    public void setMailbox(String mailbox) {
        this.mailbox = mailbox;
    }

    public String getAddressAlias() {
        return addressAlias;
    }

    public void setAddressAlias(String addressAlias) {
        this.addressAlias = addressAlias;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
