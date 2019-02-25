package com.yesmywine.ware.entity;

import javax.persistence.*;

/**
 * Created by SJQ on 2017/1/5.
 *
 * @Description:仓库进货历史表
 */
@Entity
@Table(name = "warehouseHistory")
public class WarehousesHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "int(10) COMMENT 'skuId'")
    private Integer skuId;
    @Column(columnDefinition = "varchar(50) COMMENT 'skuCode'")
    private String skuCode;
    @Column(columnDefinition = "varchar(100) COMMENT 'sku名称'")
    private String skuName;
    @Column(columnDefinition = "varchar(50) COMMENT '出入库凭证编码'")
    private String certificateNum;  //出入库凭证编码
    @Column(columnDefinition = "varchar(50) COMMENT '订单编号'")
    private String orderNum;    //订单编号
    @Column(columnDefinition = "varchar(50) COMMENT '订单类型'")
    private String orderType;   //订单类型
    @Column(columnDefinition = "int(10) COMMENT '数量'")
    private Integer count;
    @Column(columnDefinition = "varchar(50) COMMENT '文本'")
    private String comment;
    @Column(columnDefinition = "varchar(50) COMMENT 'in-入库，out-出库'")
    private String type;//in-入库，out-出库，
    @Column(columnDefinition = "int(1) COMMENT '同步状态   0-未成功，1成功'")
    private Integer synStatus; //同步状态   0-未成功，1成功


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "channelId")
    private Channels channel;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "warehouseId")
    private Warehouses warehouse;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCertificateNum() {
        return certificateNum;
    }

    public void setCertificateNum(String certificateNum) {
        this.certificateNum = certificateNum;
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

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
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
