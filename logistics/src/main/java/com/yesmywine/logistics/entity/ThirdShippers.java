package com.yesmywine.logistics.entity;

import com.yesmywine.base.record.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by ${shuang} on 2017/7/21.
 */
@Entity
@Table(name="thirdShippers")
public class ThirdShippers  extends BaseEntity<Integer> {

    @Column(columnDefinition = "int(10) COMMENT '承运商关联主键'")
    private Integer shippersId;
    @Column(columnDefinition = "varchar(50) COMMENT '第三方渠道编码'")
    private String  channelCode;
    @Column(columnDefinition = "varchar(50) COMMENT '第三方承运商编码'")
    private String thirdShipperCode;

    public Integer getShippersId() {
        return shippersId;
    }

    public void setShippersId(Integer shippersId) {
        this.shippersId = shippersId;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getThirdShipperCode() {
        return thirdShipperCode;
    }

    public void setThirdShipperCode(String thirdShipperCode) {
        this.thirdShipperCode = thirdShipperCode;
    }
}
