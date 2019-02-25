package com.yesmywine.user.entity;

/**
 * Created by ${shuang} on 2017/3/29.
 */

public class Count {
    private Integer id;
    private Double beansAcount;
    private Integer channelId;

    public Double getBeansAcount() {
        return beansAcount;
    }

    public void setBeansAcount(Double beansAcount) {
        this.beansAcount = beansAcount;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }
}
