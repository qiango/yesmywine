package com.yesmywine.goods.entity;

import javax.persistence.*;

/**
 * Created by WANG, RUIQING on 12/7/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
@Entity
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "varchar(255) COMMENT '标签名称'")
    private String tagName;            //标签名称
    @Column(columnDefinition = "varchar(255) COMMENT '标签描述'")
    private String tagDescription;     //标签描述
    @Column(columnDefinition = "int(11) COMMENT '父标签'")
    private Integer parentId;         //父标签

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagDescription() {
        return tagDescription;
    }

    public void setTagDescription(String tagDescription) {
        this.tagDescription = tagDescription;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}
