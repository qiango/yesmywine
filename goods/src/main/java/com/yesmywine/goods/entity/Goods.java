package com.yesmywine.goods.entity;

import com.alibaba.fastjson.JSONArray;
import com.yesmywine.base.record.entity.BaseEntity;
import com.yesmywine.goods.bean.Item;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


/**
 * Created by WANG, RUIQING on 12/7/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
@Entity
@Table(name = "goods")
public class Goods extends BaseEntity<Integer> implements Serializable {
    @Column(columnDefinition = "varchar(255) COMMENT '商品名'")
    private String goodsName;//商品名
    @Enumerated(EnumType.STRING)
    private Item item;//单品or多品
    @Column(columnDefinition = "varchar(255) COMMENT '商品编码'")
    private String goodsCode;//商品编码
    @Column(columnDefinition = "varchar(50) COMMENT '参考价格'")
    private String price;//参考价格


    @OneToMany(cascade = CascadeType.REFRESH)
//    @JoinColumn(name = "goodsId")
    private List<GoodsSku> goodsSku;
    @Column(columnDefinition = "varchar(50) COMMENT '分类id'")
    private String categoryId;//分类id
    @Ignore
    @Transient
    private String categoryName;
    @Ignore
    @Transient
    private String categoryGroup;//分类完整id组合,用于查询
    @Ignore
    @Transient
    private JSONArray jsonArray;

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<GoodsSku> getGoodsSku() {
        return goodsSku;
    }

    public void setGoodsSku(List<GoodsSku> goodsSku) {
        this.goodsSku = goodsSku;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryGroup() {
        return categoryGroup;
    }

    public void setCategoryGroup(String categoryGroup) {
        this.categoryGroup = categoryGroup;
    }

    @Transient
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
