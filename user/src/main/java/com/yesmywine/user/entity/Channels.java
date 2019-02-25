package com.yesmywine.user.entity;

import com.yesmywine.base.record.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by hz on 3/28/17.
 */
@Entity
@Table(name = "channels")
public class Channels {

    @Id
    private Integer id;
    @Column(columnDefinition = "varchar(50) COMMENT '渠道名称'")
    private String channelName;
    @Column(columnDefinition = "varchar(50) COMMENT '渠道编码'")
    private String channelCode; //渠道编码
    private String type;        //渠道类型

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
