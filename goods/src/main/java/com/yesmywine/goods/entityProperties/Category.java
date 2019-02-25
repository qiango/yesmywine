package com.yesmywine.goods.entityProperties;

import com.alibaba.fastjson.JSONArray;
import com.yesmywine.base.record.entity.BaseEntity;
import com.yesmywine.goods.bean.DeleteEnum;
import com.yesmywine.goods.bean.IsShow;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;

/**
 * Created by WANG, RUIQING on 12/7/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
@Entity
@Table(name = "category")
public class Category extends BaseEntity<Integer> {
    @Column(columnDefinition = "varchar(20) COMMENT '分类名称'")
    private String categoryName;
    @Column(columnDefinition = "varchar(20) COMMENT '编码'")
    private String code;//编码
//    private Integer parentId;
    @Enumerated(EnumType.ORDINAL)
    private DeleteEnum deleteEnum;
    @Enumerated(EnumType.ORDINAL)
    private IsShow isShow;
    @Lob
    @Column(columnDefinition="TEXT")
    private String image;
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "parentId")
    private Category parentName;

    @Ignore
    @Transient
    private JSONArray propertyInfo;
    @Column(columnDefinition = "int(3) COMMENT '分类级别１２３'")
    private Integer level; //分类级别１２３

    public IsShow getIsShow() {
        return isShow;
    }

    public void setIsShow(IsShow isShow) {
        this.isShow = isShow;
    }

    public Category() {
        this.deleteEnum = DeleteEnum.NOT_DELETE;
    }


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public DeleteEnum getDeleteEnum() {
        return deleteEnum;
    }

    public void setDeleteEnum(DeleteEnum deleteEnum) {
        this.deleteEnum = deleteEnum;
    }

    public Category getParentName() {
        return parentName;
    }

    public void setParentName(Category parentName) {
        this.parentName = parentName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public JSONArray getPropertyInfo() {
        return propertyInfo;
    }

    public void setPropertyInfo(JSONArray propertyInfo) {
        this.propertyInfo = propertyInfo;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

}
