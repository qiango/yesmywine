package com.yesmywine.user.entity;

import com.yesmywine.base.record.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by SJQ on 2017/4/20.
 */
@Entity
@Table(name = "vipRule")
public class VipRule extends BaseEntity<Integer> {
    @Column(unique = true,columnDefinition = "int(11) COMMENT '商城对应Id'")
    private Integer mallId;
    @Column(columnDefinition = "varchar（50） COMMENT '等级名称'")
    private String vipName;
    @Column(columnDefinition = "int（10） COMMENT '要求成长值'")
    private Integer requireValue;
    @Column(columnDefinition = "int（10） COMMENT '保级所需最低值'")
    private Integer keep; //保级所需最低值
    @Column(columnDefinition = "varchar（255） COMMENT '图片url'")
    private String url;//图片url
    @Column(columnDefinition = "int（5） COMMENT '免费存酒天数'")
    private Integer keepDays;//免费存酒天数
    @Column(columnDefinition = "double COMMENT '折扣'")
    private Double discount;//折扣

    public Integer getMallId() {
        return mallId;
    }

    public void setMallId(Integer mallId) {
        this.mallId = mallId;
    }

    public String getVipName() {
        return vipName;
    }

    public void setVipName(String vipName) {
        this.vipName = vipName;
    }

    public Integer getRequireValue() {
        return requireValue;
    }

    public void setRequireValue(Integer requireValue) {
        this.requireValue = requireValue;
    }

    public Integer getKeep() {
        return keep;
    }

    public void setKeep(Integer keep) {
        this.keep = keep;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getKeepDays() {
        return keepDays;
    }

    public void setKeepDays(Integer keepDays) {
        this.keepDays = keepDays;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }
}
