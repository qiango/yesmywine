package com.yesmywine.sms.entity;

import com.yesmywine.base.record.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by wangdiandian on 2017/5/8.
 */
@Entity
@Table(name = "configure")
public class Configure extends BaseEntity<Integer> {
    @Column(columnDefinition = "varchar(50) COMMENT '账号' ")
    private String account;
    @Column(columnDefinition = "varchar(50) COMMENT '密码' ")
    private String password;
    @Column(columnDefinition = "varchar(50) COMMENT '短信签名' ")
    private String sign;
    @Column(columnDefinition = "varchar(50) COMMENT '短信签名对应字码' ")
    private String subcode;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSubcode() {
        return subcode;
    }

    public void setSubcode(String subcode) {
        this.subcode = subcode;
    }
}
