package com.yesmywine.logistics.entity;

import com.yesmywine.base.record.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by wangdiandian on 2017/3/27.
 */
@Entity
@Table(name="shippers")
public class Shippers extends BaseEntity<Integer> {
    @Column(columnDefinition = "varchar(50) COMMENT '承运商名称'")
    private String shipperName;//
    @Column(columnDefinition = "varchar(50) COMMENT '承运商编码'")
    private String shipperCode;//
    @Column(columnDefinition = "varchar(255) COMMENT '简短描述'")
    private String depict;//
    @Column(columnDefinition = "int(2) COMMENT '承运商类型（0快递、1物流）'")
    private Integer shipperType;//
    @Column(columnDefinition = "double COMMENT '代收费率'")
    private Double collectingRate;//
    @Column(columnDefinition = "double COMMENT '最低代收费'")
    private Double lowestCollecting;//
    @Column(columnDefinition = "double COMMENT 'POS机费率'")
    private Double posRate;//
    @Column(columnDefinition = "double COMMENT '开始保价费'")
    private Double initialPremium;//
    @Column(columnDefinition = "double COMMENT '保价费率'")
    private Double insuredRate;//
    @Column(columnDefinition = "double COMMENT '最低保价费率'")
    private Double lowestInsuredRate;//
    @Column(columnDefinition = "double COMMENT '最低收费（承运商类型为物流时才需要）'")
    private Double minimumCharge;//
    @Column(columnDefinition = "int(2) COMMENT '状态（１启用、０停用）'")
    private Integer status; //
    @Column(columnDefinition = "int(2) COMMENT '是否删除  0-删除  1-未删除'")
    private Integer deleteEnum;  //

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public String getShipperCode() {
        return shipperCode;
    }

    public void setShipperCode(String shipperCode) {
        this.shipperCode = shipperCode;
    }

    public String getDepict() {
        return depict;
    }

    public void setDepict(String depict) {
        this.depict = depict;
    }

    public Integer getShipperType() {
        return shipperType;
    }

    public void setShipperType(Integer shipperType) {
        this.shipperType = shipperType;
    }

    public Double getCollectingRate() {
        return collectingRate;
    }

    public void setCollectingRate(Double collectingRate) {
        this.collectingRate = collectingRate;
    }

    public Double getLowestCollecting() {
        return lowestCollecting;
    }

    public void setLowestCollecting(Double lowestCollecting) {
        this.lowestCollecting = lowestCollecting;
    }

    public Double getPosRate() {
        return posRate;
    }

    public void setPosRate(Double posRate) {
        this.posRate = posRate;
    }

    public Double getInitialPremium() {
        return initialPremium;
    }

    public void setInitialPremium(Double initialPremium) {
        this.initialPremium = initialPremium;
    }

    public Double getInsuredRate() {
        return insuredRate;
    }

    public void setInsuredRate(Double insuredRate) {
        this.insuredRate = insuredRate;
    }

    public Double getLowestInsuredRate() {
        return lowestInsuredRate;
    }

    public void setLowestInsuredRate(Double lowestInsuredRate) {
        this.lowestInsuredRate = lowestInsuredRate;
    }

    public Double getMinimumCharge() {
        return minimumCharge;
    }

    public void setMinimumCharge(Double minimumCharge) {
        this.minimumCharge = minimumCharge;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDeleteEnum() {
        return deleteEnum;
    }

    public void setDeleteEnum(Integer deleteEnum) {
        this.deleteEnum = deleteEnum;
    }
}