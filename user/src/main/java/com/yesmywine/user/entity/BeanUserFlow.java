package com.yesmywine.user.entity;

import com.yesmywine.base.record.entity.BaseEntity;

import javax.persistence.*;

/**
 * Created by ${shuang} on 2017/3/28.
 */
@Entity
@Table(name = "beanUserFlow")
public class BeanUserFlow  extends BaseEntity<Integer> {
//  用户id
//•	手机号码
//•	订单编号
//•	新增积分
//•	渠道编码
    @Column(columnDefinition = "int(11) COMMENT '用户Id'")
    private  Integer userId;
    @Column(unique = true,columnDefinition = "varchar(50) COMMENT '用户名'")
    private String  userName;
    @Column(columnDefinition = "double COMMENT '酒豆'")
    private Double beans;
    @Column(unique = true,columnDefinition = "varchar(50) COMMENT '手机号'")
    private String phoneNumber;
    @Column(columnDefinition = "varchar(255) COMMENT '订单号'")
    private  String orderNumber;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "channelId")
    private Channels channels;
    @Column(columnDefinition = "varchar(10) COMMENT '0需要同步，1不需要'")
    private  String synStatus;//0需要同步
    @Column(columnDefinition = "int(11) COMMENT '积分'")
    private  Integer point;
    @Column(columnDefinition = "varchar(10) COMMENT '消耗，退还'")
    private  String status;
    @Column(columnDefinition = "varchar(50) COMMENT '描述'")
    private  String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public String getSynStatus() {
        return synStatus;
    }

    public void setSynStatus(String synStatus) {
        this.synStatus = synStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getBeans() {
        return beans;
    }

    public void setBeans(Double beans) {
        this.beans = beans;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Channels getChannels() {
        return channels;
    }

    public void setChannels(Channels channels) {
        this.channels = channels;
    }
}
