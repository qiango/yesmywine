package com.yesmywine.ware.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by SJQ on 2017/3/27.
 */
@Entity
@Table(name = "stroes")
public class Stores extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "varchar(50) COMMENT '门店名'")
    private String storeName;   //门店名
    @Column(columnDefinition = "varchar(50) COMMENT '门店编码'")
    private String storeCode;   //门店编码

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "channelId")
    private Channels channel;   //关联渠道

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "warehouseId")
    private Warehouses warehouse;    //关联仓库

    private Date synchronizationTime;   //最后同步时间

    private Integer ifConfig;// 是否配置 0-未配置  1-已配置

    public Date getSynchronizationTime() {
        return synchronizationTime;
    }

    public void setSynchronizationTime(Date synchronizationTime) {
        this.synchronizationTime = synchronizationTime;
    }

    public Integer getIfConfig() {
        return ifConfig;
    }

    public void setIfConfig(Integer ifConfig) {
        this.ifConfig = ifConfig;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
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
}
