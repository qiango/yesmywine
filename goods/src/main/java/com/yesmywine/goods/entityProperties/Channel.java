package com.yesmywine.goods.entityProperties;

import javax.persistence.*;

/**
 * Created by WANG, RUIQING on 12/7/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
@Entity
@Table(name = "channel")
public class Channel {
    @Id
    private Integer id;
    @Column(columnDefinition = "varchar(50) COMMENT '销售渠道名称'")
    private String channelName;        //销售渠道名称
    @Column(columnDefinition = "varchar(50) COMMENT '销售渠道名称'")
    private String type;        //渠道类型
    private String channelCode;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }
}
