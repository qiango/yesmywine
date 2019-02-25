package com.yesmywine.goods.entity;


import com.yesmywine.base.record.entity.BaseEntity;

import javax.persistence.*;

/**
 * Created by WANG, RUIQING on 12/7/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
@Entity
@Table(name = "goodsSku")
public class GoodsSku extends BaseEntity<Integer> {
    @Column(columnDefinition = "int(11) COMMENT 'skuId'")
    private Integer skuId;
    @Column(columnDefinition = "int(11) COMMENT '数量'")
    private Integer count;

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}


