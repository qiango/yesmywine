package com.yesmywine.ware.entity;

import com.yesmywine.base.record.entity.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by by on 2017/7/21.
 */
@Entity
@Table(name = "applyDetail")
public class AllotApplyDetail extends com.yesmywine.base.record.entity.BaseEntity<Integer> {
    @Column(columnDefinition = "int(11) COMMENT '申请id'")
    private Integer applyId;
    @Column(columnDefinition = "varchar(50) COMMENT '订单编号'")
    private String orderNo;
    @Column(columnDefinition = "int(11) COMMENT '申请数量'")
    private Integer count;

    public AllotApplyDetail() {
    }

    public AllotApplyDetail(String orderNo, Integer count) {
        this.orderNo = orderNo;
        this.count = count;
    }

    public Integer getApplyId() {
        return applyId;
    }

    public void setApplyId(Integer applyId) {
        this.applyId = applyId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
