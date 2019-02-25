package com.yesmywine.ware.entity;

import com.yesmywine.base.record.entity.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by by on 2017/7/21.
 */
@Entity
@Table(name = "commandDetail")
public class AllotDetail extends com.yesmywine.base.record.entity.BaseEntity<Integer> {
    @Column(columnDefinition = "varchar(50) COMMENT '调拨指令编码'")
    private String allotCode;
    @Column(columnDefinition = "int(10) COMMENT 'skuId'")
    private Integer skuId;
    @Column(columnDefinition = "varchar(50) COMMENT 'skucode'")
    private String skuCode;
    @Column(columnDefinition = "varchar(255) COMMENT 'sku名称'")
    private String skuName;
    @Column(columnDefinition = "int(10) COMMENT '申请数量'")
    private Integer applyCount;
    @Column(columnDefinition = "int(10) COMMENT '实际调拨数量'")
    private Integer allotCount;
    @Column(columnDefinition = "int(10) COMMENT '实际接收数量'")
    private Integer receiveCount;

    public AllotDetail() {
    }

    public AllotDetail(String allotCode, Integer skuId, String skuCode, String skuName, Integer applyCount, Integer allotCount, Integer receiveCount) {
        this.allotCode = allotCode;
        this.skuId = skuId;
        this.skuCode = skuCode;
        this.skuName = skuName;
        this.applyCount = applyCount;
        this.allotCount = allotCount;
        this.receiveCount = receiveCount;
    }

    public String getAllotCode() {
        return allotCode;
    }

    public void setAllotCode(String allotCode) {
        this.allotCode = allotCode;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public Integer getApplyCount() {
        return applyCount;
    }

    public void setApplyCount(Integer applyCount) {
        this.applyCount = applyCount;
    }

    public Integer getAllotCount() {
        return allotCount;
    }

    public void setAllotCount(Integer allotCount) {
        this.allotCount = allotCount;
    }

    public Integer getReceiveCount() {
        return receiveCount;
    }

    public void setReceiveCount(Integer receiveCount) {
        this.receiveCount = receiveCount;
    }
}
