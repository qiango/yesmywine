package com.yesmywine.email.entity;

import com.yesmywine.base.record.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by wangdiandian on 2017/5/16.
 */
@Entity
@Table(name = "theme")
public class Theme extends BaseEntity<Integer> {
    @Column(columnDefinition = "varchar(255) COMMENT '标题'")
    private String title;//主题名称
    @Column(columnDefinition = "varchar(50) COMMENT '编码'")
    private String code;//编码
    @Column(columnDefinition = "LONGTEXT COMMENT '文本'")
    private   String themeTemplate;
    @Column(columnDefinition = "varchar(50) COMMENT '类型'")
    private   String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getThemeTemplate() {
        return themeTemplate;
    }

    public void setThemeTemplate(String themeTemplate) {
        this.themeTemplate = themeTemplate;
    }
}
