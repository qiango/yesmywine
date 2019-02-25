package com.yesmywine.sms.entity;

import com.yesmywine.base.record.entity.BaseEntity;

import javax.persistence.*;

/**
 * Created by wangdiandian on 2017/5/8.
 */
@Entity
@Table(name = "smsTemplate")
public class SmsTemplate extends BaseEntity<Integer> {
    @Column(columnDefinition = "varchar(20) COMMENT '编码' ")
    private String code;//编码
    @Column(columnDefinition = "varchar(20) COMMENT '类型名称' ")
    private String typeName;//
    @Column(columnDefinition = "text COMMENT '内容' ")
    private String content;//


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
