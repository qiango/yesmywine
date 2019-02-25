package com.yesmywine.ware.entity;

import javax.persistence.*;

/**
 * Created by Administrator on 2017/4/16 0016.
 */
@Entity
@Table(name = "sendChannelHistoryDetail")
public class SendChannelHistoryDetails extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "int(10) COMMENT 'skuId'")
    private Integer skuId;
    @Column(columnDefinition = "varchar(50) COMMENT 'skuCode'")
    private String skuCode;
    @Column(columnDefinition = "varchar(100) COMMENT 'sku名称'")
    private String skuName;
    @Column(columnDefinition = "int(10) COMMENT '数量'")
    private Integer count;
    @Column(columnDefinition = "int(10) COMMENT '历史Id'")
    private Integer historyId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "channelId")
    private Channels channel;   //分配哪个渠道
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "warehouseId")
    private Warehouses warehouse; //分配到哪个渠道、哪个仓库的通用库存

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

    public Integer getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Integer historyId) {
        this.historyId = historyId;
    }

    public Channels getChannel() {
        return channel;
    }

    public void setChannel(Channels channel) {
        this.channel = channel;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public Warehouses getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouses warehouse) {
        this.warehouse = warehouse;
    }
}
