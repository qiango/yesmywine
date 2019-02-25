package com.yesmywine.user.entity;

import com.yesmywine.base.record.entity.BaseEntity;

import javax.persistence.*;

/**
 * Created by SJQ on 2017/4/20.
 */
@Entity
@Table(name = "userInformation")
public class UserInformation extends BaseEntity<Integer>{
    @Column(unique = true,columnDefinition = "int(11) COMMENT '用户Id'")
    private Integer userId;
    @Column(unique = true,columnDefinition = "varchar(50) COMMENT '用户名'")
    private String userName;
    @Column(unique = true,columnDefinition = "varchar(50) COMMENT '登录密码'")
    private String password;//登录密码
    @Column(unique = true,columnDefinition = "varchar(50) COMMENT '支付密码'")
    private String paymentPassword;//支付密码
    @Column(unique = true,columnDefinition = "varchar(50) COMMENT '手机号'")
    private String phoneNumber;
    @Column(columnDefinition = "BIT(1) COMMENT '是否绑定手机'")
    private Boolean bindPhoneFlag;
    @Column(columnDefinition = "varchar(50) COMMENT '昵称'")
    private String nickName;
    @Column(unique = true,columnDefinition = "varchar(100) COMMENT '邮箱'")
    private String email;
    @Column(columnDefinition = "BIT(1) COMMENT '是否绑定邮箱（T：是， F：否）'")
    private Boolean bindEmailFlag;
    @Column(columnDefinition = "varchar(100) COMMENT '身份证号码'")
    private String IDCardNum;//身份证号码
    @Column(columnDefinition = "double COMMENT '酒豆'")
    private Double bean;
    @Column(columnDefinition = "varchar(50) COMMENT '注册渠道'")
    private String registerChannel;//注册渠道  stores-门店   qq-QQ  ali-支付宝
    @Column(columnDefinition = "tinyint default 0")
    private Integer growthValue;//成长值
    @Column(columnDefinition = "double COMMENT '余额'")
    private Double remainingSum;//余额
    @Column(columnDefinition = "varchar(100) COMMENT '自发升降级时间'")
    private String voluntarily;//自发升降级时间
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="levelId", referencedColumnName="mallId")
    private VipRule vipRule;//会员等级
    @Column(columnDefinition = "int(1) COMMENT '0-需要同步,1-不需要'")
    private Integer synStatus;
    @Column(columnDefinition = "int(5) COMMENT '渠道分類(0-門店 ，1-官網 )'")
    private Integer channelType;//渠道分類(0-門店注册 ，1-官網注册 )

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIDCardNum() {
        return IDCardNum;
    }

    public void setIDCardNum(String IDCardNum) {
        this.IDCardNum = IDCardNum;
    }

    public Double getBean() {
        return bean;
    }

    public void setBean(Double bean) {
        this.bean = bean;
    }

    public String getRegisterChannel() {
        return registerChannel;
    }

    public void setRegisterChannel(String registerChannel) {
        this.registerChannel = registerChannel;
    }

    public Integer getGrowthValue() {
        return growthValue;
    }

    public void setGrowthValue(Integer growthValue) {
        this.growthValue = growthValue;
    }

    public String getVoluntarily() {
        return voluntarily;
    }

    public void setVoluntarily(String voluntarily) {
        this.voluntarily = voluntarily;
    }

    public VipRule getVipRule() {
        return vipRule;
    }

    public void setVipRule(VipRule vipRule) {
        this.vipRule = vipRule;
    }

    public Boolean getBindPhoneFlag() {
        return bindPhoneFlag;
    }

    public void setBindPhoneFlag(Boolean bindPhoneFlag) {
        this.bindPhoneFlag = bindPhoneFlag;
    }

    public Boolean getBindEmailFlag() {
        return bindEmailFlag;
    }

    public void setBindEmailFlag(Boolean bindEmailFlag) {
        this.bindEmailFlag = bindEmailFlag;
    }

    public Integer getSynStatus() {
        return synStatus;
    }

    public void setSynStatus(Integer synStatus) {
        this.synStatus = synStatus;
    }

    public Double getRemainingSum() {
        return remainingSum;
    }

    public void setRemainingSum(Double remainingSum) {
        this.remainingSum = remainingSum;
    }

    public Integer getChannelType() {
        return channelType;
    }

    public void setChannelType(Integer channelType) {
        this.channelType = channelType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPaymentPassword() {
        return paymentPassword;
    }

    public void setPaymentPassword(String paymentPassword) {
        this.paymentPassword = paymentPassword;
    }
}
