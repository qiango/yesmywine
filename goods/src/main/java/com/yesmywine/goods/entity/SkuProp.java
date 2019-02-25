package com.yesmywine.goods.entity;


import com.yesmywine.base.record.entity.BaseEntity;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;

/**
 * Created by WANG, RUIQING on 12/7/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
@Entity
@Table(name = "SkuProp")
public class SkuProp extends BaseEntity<Integer> {
    @Column(columnDefinition = "int(11) COMMENT '属性id'")
    private Integer propertiesId;
    @Column(columnDefinition = "int(11) COMMENT '属性值id'")
    private Integer propValue;
    @Ignore
    @Transient
    private String propName;
    @Ignore
    @Transient
    private String propValueName;

    public Integer getPropertiesId() {
        return propertiesId;
    }

    public void setPropertiesId(Integer propertiesId) {
        this.propertiesId = propertiesId;
    }


    public Integer getPropValue() {
        return propValue;
    }

    public void setPropValue(Integer propValue) {
        this.propValue = propValue;
    }

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }

    public String getPropValueName() {
        return propValueName;
    }

    public void setPropValueName(String propValueName) {
        this.propValueName = propValueName;
    }
}


