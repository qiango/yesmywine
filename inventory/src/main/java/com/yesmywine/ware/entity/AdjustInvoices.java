package com.yesmywine.ware.entity;

import javax.persistence.*;

/**
 * Created by SJQ on 2017/4/1.
 * 库存调整记录表
 */
@Entity
@Table(name = "adjustInvoices")
public class AdjustInvoices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "int(10) COMMENT 'skuId'")
    private Integer skuId;
    @Column(columnDefinition = "varchar(10) COMMENT 'skucode'")
    private String skuCode;
    @Column(columnDefinition = "varchar(100) COMMENT 'sku名称'")
    private String skuName;
    @Column(columnDefinition = "int(10) COMMENT '数量'")
    private Integer count;
    @Column(columnDefinition = "varchar(10) COMMENT '任务Id'")
    private String taskId;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "channelId")
    private Channels channel;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "warehouseId")
    private Warehouses warehouse;
    @Column(columnDefinition = "int(1) COMMENT '调整状态 0-待处理  1-已处理'")
    private Integer status;// 调整状态 0-待处理  1-已处理
    @Column(columnDefinition = "varchar(10) COMMENT '类别  wms-wms   store-门店'")
    private String type;//  类别  wms-wms   store-门店
    @Column(columnDefinition = "int(1) COMMENT '同步状态  0-同步失败，1-同步完成'")
    private Integer synStatus; //同步状态  0-同步失败，1-同步完成

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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Channels getChannel() {
        return channel;
    }

    public void setChannel(Channels channel) {
        this.channel = channel;
    }

    public Warehouses getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouses warehouse) {
        this.warehouse = warehouse;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
