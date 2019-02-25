package com.yesmywine.ware.entity;

import javax.persistence.*;

/**
 * Created by SJQ on 2017/6/9.
 * 差异单
 */
@Entity
@Table(name = "discrepancyBills")
public class DiscrepancyBills extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "int(10) COMMENT 'skuid'")
    private Integer skuId;
    @Column(columnDefinition = "varchar(50) COMMENT 'skucode'")
    private String skuCode;
    @Column(columnDefinition = "varchar(100) COMMENT 'sku名称'")
    private String skuName;
    @Column(columnDefinition = "int(11) COMMENT '差异数量值为正，表示收货数大于发货数；值为负，表示收货数小于发货数'")
    private Integer count;//差异数量   值为正，表示收货数大于发货数；值为负，表示收货数小于发货数
    @Column(columnDefinition = "varchar(225) COMMENT '调拨指令编码'")
    private String allotCode;//调拨指令编码
    @Column(columnDefinition = "varchar(225) COMMENT '差异类型 allot-调拨差异'")
    private String type;//可分为  allot-调拨差异
    @Column(columnDefinition = "varchar(225) COMMENT '备注'")
    private String comment; //备注
    @Column(columnDefinition = " varchar(225) COMMENT '状态 0-单据未确定  1-单据已确定'")
    private String status; //状态 0-单据未确定  1-单据已确定

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getAllotCode() {
        return allotCode;
    }

    public void setAllotCode(String allotCode) {
        this.allotCode = allotCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
