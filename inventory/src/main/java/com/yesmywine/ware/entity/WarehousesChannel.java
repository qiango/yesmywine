package com.yesmywine.ware.entity;

import javax.persistence.*;

/**
 * Created by SJQ on 2017/1/5.
 *
 * @Description:仓库与商品关联表（多对多）
 */
@Entity
@Table(name = "warehouseChannel")
public class WarehousesChannel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "varchar(50) COMMENT 'skuCode'")
    private String warehouseCode;
    @Column(columnDefinition = "int(10) COMMENT 'skuId'")
    private Integer skuId;
    @Column(columnDefinition = "varchar(50) COMMENT 'skuCode'")
    private String skuCode;
    @Column(columnDefinition = "varchar(255) COMMENT 'sku名称'")
    private String skuName;
    @Column(columnDefinition = "varchar(50) COMMENT '渠道编码'")
    private String channelCode;
    @Column(columnDefinition = "int(10) COMMENT '总库存'")
    private Integer overall;
    @Column(columnDefinition = "int(10) COMMENT '可用'")
    private Integer useCount;
    @Column(columnDefinition = "int(10) COMMENT '冻结'")
    private Integer freezeCount;
    @Column(columnDefinition = "int(10) COMMENT '在途'")
    private Integer enRouteCount;//在途库存

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "channelId")
    private Channels channel;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "warehouseId")
    private Warehouses warehouse;

    public WarehousesChannel() {
    }

    public WarehousesChannel(String warehouseCode, Integer skuId, String skuCode, String skuName, String channelCode, Integer overall, Integer useCount, Integer freezeCount, Integer enRouteCount, Channels channel, Warehouses warehouse) {
        this.warehouseCode = warehouseCode;
        this.skuId = skuId;
        this.skuCode = skuCode;
        this.skuName = skuName;
        this.channelCode = channelCode;
        this.overall = overall;
        this.useCount = useCount;
        this.freezeCount = freezeCount;
        this.enRouteCount = enRouteCount;
        this.channel = channel;
        this.warehouse = warehouse;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

//    public Integer getWarehouseId() {
//        return warehouseId;
//    }
//
//    public void setWarehouseId(Integer warehouseId) {
//        this.warehouseId = warehouseId;
//    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
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

//    public Integer getChannelId() {
//        return channelId;
//    }
//
//    public void setChannelId(Integer channelId) {
//        this.channelId = channelId;
//    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public Integer getOverall() {
        return overall;
    }

    public void setOverall(Integer overall) {
        this.overall = overall;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public Integer getFreezeCount() {
        return freezeCount;
    }

    public void setFreezeCount(Integer freezeCount) {
        this.freezeCount = freezeCount;
    }

    public Integer getEnRouteCount() {
        return enRouteCount;
    }

    public void setEnRouteCount(Integer enRouteCount) {
        this.enRouteCount = enRouteCount;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }
}
