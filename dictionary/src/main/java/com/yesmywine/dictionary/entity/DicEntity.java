package com.yesmywine.dictionary.entity;

import javax.persistence.*;

/**
 * Created by WANG, RUIQING on 12/1/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
@Entity
@Table(name = "dicentity")
public class DicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String entityCode;
    private String entityValue;
    private String sysCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public String getEntityValue() {
        return entityValue;
    }

    public void setEntityValue(String entityValue) {
        this.entityValue = entityValue;
    }

    public String getSysCode() {
        return sysCode;
    }

    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }
}
