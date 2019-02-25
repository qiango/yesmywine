package com.yesmywine.goods.entityProperties;

import com.yesmywine.base.record.entity.BaseEntity;

import javax.persistence.*;

/**
 * Created by hz on 1/6/17.
 */
@Entity
@Table(name = "propertiesValue")
public class PropertiesValue extends BaseEntity<Integer> {
    @Column(columnDefinition = "varchar(200) COMMENT '属性值'")
    private String cnValue;   //属性值
    @Column(columnDefinition = "int(11) COMMENT '属性Id'")
    private Integer propertiesId;
    @Column(columnDefinition = "varchar(200) COMMENT '编码'")
    private String code;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCnValue() {
        return cnValue;
    }

    public void setCnValue(String cnValue) {
        this.cnValue = cnValue;
    }

    public Integer getPropertiesId() {
        return propertiesId;
    }

    public void setPropertiesId(Integer propertiesId) {
        this.propertiesId = propertiesId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
