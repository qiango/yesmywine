package com.yesmywine.user.entity;

import com.yesmywine.base.record.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by ${shuang} on 2017/3/29.
 */
@Entity
@Table(name = "beanCenterFlow")
public class BeanCenterFlow extends BaseEntity<Integer> {
    @Column(unique = true,columnDefinition = "varchar(50) COMMENT '用户名'")
    private  String userName;
    @Column(unique = true,columnDefinition = " DECIMAL(19,2) COMMENT '用户名'")
    private BigDecimal beans;
    @Column(columnDefinition = "int(10) COMMENT '渠道Id'")
    private Integer channelId;
    @Column(unique = true,columnDefinition = "varchar(10) COMMENT '渠道名称'")
    private  String channelName;
    @Column(columnDefinition = "varchar(10) COMMENT 'g生成，退还，消费'")
    private  String status;
    @Column(columnDefinition = "varchar(10) COMMENT '付款方'")
    private String payer;
    @Column(columnDefinition = "varchar(10) COMMENT '收款方'")
    private String payee;
    @Column(columnDefinition = "int(1) COMMENT '是否需要同步'")
    private Integer mallSynStatus;//向商城的同步状况

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public BigDecimal getBeans() {
        return beans;
    }

    public void setBeans(BigDecimal beans) {
        this.beans = beans;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getMallSynStatus() {
        return mallSynStatus;
    }

    public void setMallSynStatus(Integer mallSynStatus) {
        this.mallSynStatus = mallSynStatus;
    }

}
