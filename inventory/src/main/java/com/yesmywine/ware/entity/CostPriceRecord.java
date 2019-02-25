package com.yesmywine.ware.entity;

import javax.persistence.*;

/**
 * Created by SJQ on 2017/4/17.
 * sku 每月成本价表
 */
@Entity
@Table(name = "costPriceRecord")
public class CostPriceRecord extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "int(10) COMMENT 'skuId'")
    private Integer skuId;
    @Column(columnDefinition = "varchar(50) COMMENT 'skuCode'")
    private String skuCode;
    @Column(columnDefinition = "varchar(100) COMMENT 'sku名称'")
    private String skuName;
    @Column(columnDefinition = "int(5) COMMENT '年'")
    private Integer year;
    @Column(columnDefinition = "int(5) COMMENT '月'")
    private Integer mounth;
    @Column(columnDefinition = "int(10) COMMENT '月初数量'")
    private Integer mounthInitCount;//月初数量
    @Column(columnDefinition = "double COMMENT '本月成本价'")
    private Double costPrice;//本月成本价
    @Column(columnDefinition = "int(10) COMMENT '本月进货总数量'")
    private Integer totalCount;//本月进货总数量
    @Column(columnDefinition = "double COMMENT '本月进货总金额'")
    private Double totalPrice;//本月进货总金额
    @Column(columnDefinition = "int(1) COMMENT '同步状况  0-需要同步'")
    private Integer synStatus;//同步状况  0-需要同步

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
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

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMounth() {
        return mounth;
    }

    public void setMounth(Integer mounth) {
        this.mounth = mounth;
    }

    public Integer getMounthInitCount() {
        return mounthInitCount;
    }

    public void setMounthInitCount(Integer mounthInitCount) {
        this.mounthInitCount = mounthInitCount;
    }

    public Integer getSynStatus() {
        return synStatus;
    }

    public void setSynStatus(Integer synStatus) {
        this.synStatus = synStatus;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }
}
