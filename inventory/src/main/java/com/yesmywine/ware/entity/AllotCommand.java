package com.yesmywine.ware.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by by on 2017/7/20.
 *
 * @Description:调拨指令表
 */
@Entity
@Table(name = "allotCommand")
public class AllotCommand extends com.yesmywine.base.record.entity.BaseEntity<Integer> {
    @Column(columnDefinition = "varchar(50) COMMENT '调拨指令编码'")
    private String allotCode;
    @Column(columnDefinition = "varchar(100) COMMENT '指令制作人'")
    private String producer;
    @Column(columnDefinition = "varchar(100) COMMENT '审核人'")
    private String auditor;
    @Column(columnDefinition = "datetime COMMENT '审核时间'")
    private Date auditTime;
    @Column(columnDefinition = "varchar(2) COMMENT '指令状态  0-待审核 ， 1-出库中， -1-驳回，2-入库中，3-完成'")
    private Integer status;
    @Column(columnDefinition = "varchar(255) COMMENT '备注'")
    private String comment;
    @Column(columnDefinition = "int(2) COMMENT '同步状态 2-通知wms入库失败  1-通知wms出库失败'")
    private Integer synStatus;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "allotCode",referencedColumnName = "allotCode")
    private Set<AllotDetail> detailSet = new HashSet<>();

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "channelId")
    private Channels channel;
    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "tarWarehouseId")
    private Warehouses tarWarehouse;//目标仓库
    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "allotWarehouseId")
    private Warehouses allotwarehouse;//调拨仓库
    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "outWarehouseId")
    private WarehousesHistory outWarehouse;  //出库单
    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "inWarehouseId")
    private WarehousesHistory inWarehouse;   //入库单

    @Column(columnDefinition = "varchar(10) COMMENT 'store-门店  oms-oms  omsClean - oms清关  local-直接调拨'")
    private String type;

    public AllotCommand() {
    }

    public AllotCommand(String allotCode, String producer, String auditor, Integer status, Set<AllotDetail> detailSet, Channels channel, Warehouses tarWarehouse, Warehouses allotwarehouse, WarehousesHistory outWarehouse, WarehousesHistory inWarehouse, String type) {
        this.allotCode = allotCode;
        this.producer = producer;
        this.auditor = auditor;
        this.status = status;
        this.detailSet = detailSet;
        this.channel = channel;
        this.tarWarehouse = tarWarehouse;
        this.allotwarehouse = allotwarehouse;
        this.outWarehouse = outWarehouse;
        this.inWarehouse = inWarehouse;
        this.type = type;
    }

    public void addDetails(AllotDetail detail) {
        Set<AllotDetail> detailSet = getDetailSet();
        if (detailSet == null) {
            detailSet = new HashSet<>();
            detailSet.add(detail);
        }
        detailSet.add(detail);
    }

    public Integer getSynStatus() {
        return synStatus;
    }

    public void setSynStatus(Integer synStatus) {
        this.synStatus = synStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getAllotCode() {
        return allotCode;
    }

    public void setAllotCode(String allotCode) {
        this.allotCode = allotCode;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Set<AllotDetail> getDetailSet() {
        return detailSet;
    }

    public void setDetailSet(Set<AllotDetail> detailSet) {
        this.detailSet = detailSet;
    }

    public Channels getChannel() {
        return channel;
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public void setChannel(Channels channel) {
        this.channel = channel;
    }

    public Warehouses getAllotwarehouse() {
        return allotwarehouse;
    }

    public void setAllotwarehouse(Warehouses allotwarehouse) {
        this.allotwarehouse = allotwarehouse;
    }

    public WarehousesHistory getOutWarehouse() {
        return outWarehouse;
    }

    public void setOutWarehouse(WarehousesHistory outWarehouse) {
        this.outWarehouse = outWarehouse;
    }

    public WarehousesHistory getInWarehouse() {
        return inWarehouse;
    }

    public void setInWarehouse(WarehousesHistory inWarehouse) {
        this.inWarehouse = inWarehouse;
    }

    public Warehouses getTarWarehouse() {
        return tarWarehouse;
    }

    public void setTarWarehouse(Warehouses tarWarehouse) {
        this.tarWarehouse = tarWarehouse;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
