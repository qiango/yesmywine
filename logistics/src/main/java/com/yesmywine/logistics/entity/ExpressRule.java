package com.yesmywine.logistics.entity;

import com.yesmywine.base.record.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by wangdiandian on 2017/3/28.
 */
@Entity
@Table(name="expressBoxRule")
public class ExpressRule extends BaseEntity<Integer> {
    @Column(columnDefinition = "int(2) COMMENT '类型（0按箱、1按重量）'")
    private Integer type;//类型（0按箱、1按重量）
    @Column(columnDefinition = "varchar(50) COMMENT '配送区域名称'")
    private String distributionAreaName;//
    @Column(columnDefinition = "int(2) COMMENT '仓库id'")
    private Integer warehouseId;//仓库
    @Column(columnDefinition = "varchar(50) COMMENT '仓库编码'")
    private String warehouseCode;//仓库
    @Column(columnDefinition = "varchar(50) COMMENT '配送区域'")
    private String distributionArea;//
    @Column(columnDefinition = "varchar(50) COMMENT '配送区域显示中文'")
    private String areaName;//
    @Column(columnDefinition = "double COMMENT '首箱费率 (元/箱)/首重费率（元）'")
    private Double firstRate;
    @Column(columnDefinition = "double COMMENT '首重重量（元）'")
    private Double firstWeight;//
    @Column(columnDefinition = "double COMMENT '次箱费率（元/箱）/续重费率（元）'")
    private Double secondRate;//
    @Column(columnDefinition = "double COMMENT '续重重量（元）'")
    private Double secondWeight;//
    @Column(columnDefinition = "double COMMENT '首箱退费率（元）/首重退费率（元）'")
    private Double firstRefundRate;//
    @Column(columnDefinition = "double COMMENT '次箱退费率（元）/续重退费率（元）'")
    private Double secondRefundRate;//
    @Column(columnDefinition = "int(2) COMMENT '是否删除  0-删除  1-未删除'")
    private Integer deleteEnum;  //
    @Column(columnDefinition = "int(11) COMMENT '承运商Id'")
    private Integer shipperId;

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDistributionAreaName() {
        return distributionAreaName;
    }

    public void setDistributionAreaName(String distributionAreaName) {
        this.distributionAreaName = distributionAreaName;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getDistributionArea() {
        return distributionArea;
    }

    public void setDistributionArea(String distributionArea) {
        this.distributionArea = distributionArea;
    }

    public Double getFirstRate() {
        return firstRate;
    }

    public void setFirstRate(Double firstRate) {
        this.firstRate = firstRate;
    }

    public Double getFirstWeight() {
        return firstWeight;
    }

    public void setFirstWeight(Double firstWeight) {
        this.firstWeight = firstWeight;
    }

    public Double getSecondRate() {
        return secondRate;
    }

    public void setSecondRate(Double secondRate) {
        this.secondRate = secondRate;
    }

    public Double getSecondWeight() {
        return secondWeight;
    }

    public void setSecondWeight(Double secondWeight) {
        this.secondWeight = secondWeight;
    }

    public Double getFirstRefundRate() {
        return firstRefundRate;
    }

    public void setFirstRefundRate(Double firstRefundRate) {
        this.firstRefundRate = firstRefundRate;
    }

    public Double getSecondRefundRate() {
        return secondRefundRate;
    }

    public void setSecondRefundRate(Double secondRefundRate) {
        this.secondRefundRate = secondRefundRate;
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

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }
}