package com.yesmywine.logistics.entity;

import com.yesmywine.base.record.entity.BaseEntity;

import javax.persistence.*;

/**
 * Created by wangdiandian on 2017/7/21.
 */
@Entity
@Table(name = "thirdArea")
public class ThirdArea extends BaseEntity<Integer> {
    @Column(columnDefinition = "varchar(100) COMMENT '渠道编码'")
    private String channelCode;
    @Column(columnDefinition = "varchar(100) COMMENT '第三方城市名'")
    private String thirdAreaName;
    @Column(columnDefinition = "int(10)  COMMENT '城市id'")
    private Integer areaId;

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getThirdAreaName() {
        return thirdAreaName;
    }

    public void setThirdAreaName(String thirdAreaName) {
        this.thirdAreaName = thirdAreaName;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }
}
