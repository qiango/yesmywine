package com.yesmywine.dictionary.entity;


import com.yesmywine.util.enums.Active;

import javax.persistence.*;

/**
 * Created by WANG, RUIQING on 12/1/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
@Entity
@Table(name = "parameter")
public class Parameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "paramCode", length = 20)
    private String code;
    @Column(name = "paramValue")
    private String value;
    //	@Column(name = "iban", length = 34, nullable = false)
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Active active;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Active getActive() {
        return active;
    }

    public void setActive(Active active) {
        this.active = active;
    }
}
