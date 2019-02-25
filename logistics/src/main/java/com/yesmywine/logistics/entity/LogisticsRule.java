package com.yesmywine.logistics.entity;

import com.yesmywine.base.record.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by wangdiandian on 2017/3/27.
 */
@Entity
@Table(name="logisticsRule")
public class LogisticsRule extends BaseEntity<Integer>{
    @Column(columnDefinition = "varchar(20) COMMENT '配送区域名称'")
    private String distributionAreaName;//
    @Column(columnDefinition = "varchar(255) COMMENT '配送区域'")
    private String distributionArea;//
    @Column(columnDefinition = "varchar(10) COMMENT '配送区域显示中文'")
    private String areaName;//
    @Column(columnDefinition = "double COMMENT '送货费'")
    private Double deliveryCharge;//
    @Column(columnDefinition = "double COMMENT '费率'")
    private Double rate;//
    @Column(columnDefinition = "int(2) COMMENT '是否删除  0-删除  1-未删除'")
    private Integer deleteEnum;
    @Column(columnDefinition = "int(11) COMMENT '承运商Id'")
    private Integer shipperId;

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getDistributionAreaName() {
        return distributionAreaName;
    }

    public void setDistributionAreaName(String distributionAreaName) {
        this.distributionAreaName = distributionAreaName;
    }

    public String getDistributionArea() {
        return distributionArea;
    }

    public void setDistributionArea(String distributionArea) {
        this.distributionArea = distributionArea;
    }

    public Double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(Double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Integer getDeleteEnum() {
        return deleteEnum;
    }

    public void setDeleteEnum(Integer deleteEnum) {
        this.deleteEnum = deleteEnum;
    }

    public Integer getShipperId() {
        return shipperId;
    }

    public void setShipperId(Integer shipperId) {
        this.shipperId = shipperId;
    }
}
