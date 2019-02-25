package com.yesmywine.ware.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by SJQ on 2017/1/17.
 *
 * @Description:调拨申请、调拨指令表
 */
@Entity
@Table(name = "allotApply")
public class AllotApply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "int(10) COMMENT 'skuId'")
    private Integer skuId;
    @Column(columnDefinition = "varchar(50) COMMENT 'skucode'")
    private String skuCode;
    @Column(columnDefinition = "varchar(255) COMMENT 'sku名称'")
    private String skuName;
    @Column(columnDefinition = "int(10) COMMENT '申请数量'")
    private Integer count;
    @Column(columnDefinition = "varchar(100) COMMENT '备注'")
    private String comment;
    @Column(columnDefinition = "varchar(10) COMMENT 'store-门店  oms-oms omsClean-清关调拨'")
    private String type;
    @Column(columnDefinition = "int(1) COMMENT '状态  0-未处理  1-处理中 2-已完成'")
    private Integer status;
    @Column(columnDefinition = "varchar(50) COMMENT '调拨指令编码'")
    private String allotCode;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "channelId")
    private Channels channel;//申请渠道
    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "warehouseId")
    private Warehouses warehouse;//申请仓库

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "applyId")
    private Set<AllotApplyDetail> detailSet = new HashSet<>();//申请单详情

    public AllotApply() {
    }

    public AllotApply(Integer skuId, String skuCode, String skuName, Integer count, String comment, String type, Integer status, String allotCode, Channels channel, Warehouses warehouse, Set<AllotApplyDetail> detailSet) {
        this.skuId = skuId;
        this.skuCode = skuCode;
        this.skuName = skuName;
        this.count = count;
        this.comment = comment;
        this.type = type;
        this.status = status;
        this.allotCode = allotCode;
        this.channel = channel;
        this.warehouse = warehouse;
        this.detailSet = detailSet;
    }

    public void addDetails(AllotApplyDetail detail) {
        Set<AllotApplyDetail> detailSet = getDetailSet();
        if (detailSet == null) {
            detailSet = new HashSet<>();
            detailSet.add(detail);
        }
        detailSet.add(detail);
    }

    public String getAllotCode() {
        return allotCode;
    }

    public void setAllotCode(String allotCode) {
        this.allotCode = allotCode;
    }

    public Warehouses getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouses warehouse) {
        this.warehouse = warehouse;
    }

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

    public Set<AllotApplyDetail> getDetailSet() {
        return detailSet;
    }

    public void setDetailSet(Set<AllotApplyDetail> detailSet) {
        this.detailSet = detailSet;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Channels getChannel() {
        return channel;
    }

    public void setChannel(Channels channel) {
        this.channel = channel;
    }

}
