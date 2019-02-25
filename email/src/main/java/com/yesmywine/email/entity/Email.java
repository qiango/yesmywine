package com.yesmywine.email.entity;

import com.yesmywine.base.record.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by wangdiandian on 2017/5/16.
 */
@Entity
@Table(name = "email")
public class Email extends BaseEntity<Integer>{

    @Column(columnDefinition = "varchar(255) COMMENT '邮箱服务器地址'")
    private String email;//邮件
    @Column(columnDefinition = "varchar(255) COMMENT '邮箱登录名'")
    private String emailLoginName;//登录名
    @Column(columnDefinition = "varchar(255) COMMENT '密码'")
    private String emailPassword;//密码
    @Column(columnDefinition = "varchar(50) COMMENT '端口号'")
    private String port;
    @Column(columnDefinition = "varchar(50) COMMENT '加密类型no,ssl,starttls'")
    private String encryption;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailLoginName() {
        return emailLoginName;
    }

    public void setEmailLoginName(String emailLoginName) {
        this.emailLoginName = emailLoginName;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

}
