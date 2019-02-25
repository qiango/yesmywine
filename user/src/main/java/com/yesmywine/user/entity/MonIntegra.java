package com.yesmywine.user.entity;


import com.yesmywine.base.record.entity.BaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hz on 3/27/17.
 */
@Entity
@Table(name = "monIntegra")
public class MonIntegra extends BaseEntity<Integer> {

    @Column(columnDefinition = "varchar(50) COMMENT '人民币：积分'")
    private String proportion;//人民币：积分
    @Transient
    private String pro;
    private Date updateTime;
    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "channelId")
    private Channels channels;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProportion() {
        return proportion;
    }

    public void setProportion(String proportion) {
        this.proportion = proportion;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Channels getChannels() {
        return channels;
    }

    public void setChannels(Channels channels) {
        this.channels = channels;
    }

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }
}
