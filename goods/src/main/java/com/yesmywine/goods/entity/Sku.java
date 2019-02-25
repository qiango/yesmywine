package com.yesmywine.goods.entity;


import com.yesmywine.base.record.entity.BaseEntity;
import com.yesmywine.goods.bean.IsUse;
import com.yesmywine.goods.entityProperties.Category;
import com.yesmywine.goods.entityProperties.Supplier;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;
import java.util.List;

/**
 * Created by WANG, RUIQING on 12/7/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
@Entity
@Table(name = "sku")
public class Sku extends BaseEntity<Integer> {
    @Column(columnDefinition = "varchar(100) COMMENT '编码'")
    private String code;
//    private Integer categoryId;
    @Column(columnDefinition = "varchar(255) COMMENT 'sku名称'")
    private String skuName;
//    private Integer supplierId; //供应商id
    @Enumerated(EnumType.ORDINAL)
    private IsUse isUse;
    @Column(columnDefinition = "int(2) COMMENT '0是普通商品，1是虚拟商品'")
    private Integer type;//0是普通商品，1是虚拟商品
    @Column(columnDefinition = "varchar(50) COMMENT '成本价'")
    private String costPrice;//成本价
    @Lob
    @Ignore
    @Transient
    private String property;
    @Lob
    @Column(columnDefinition="TEXT")
    private String imageId;//图片id
    @OneToMany(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "skuCommonPropId")//sku普通属性
    private List<SkuCommonProp> skuCommonProp;
    @OneToMany(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "skuProp")
    private List<SkuProp> skuProp;
    @Lob
    private String sku;
    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "supplierId")
    private Supplier supplier;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "categoryId")
    private Category category;
    @Column(columnDefinition = "int(2) COMMENT '是否是贵品,0:是,1:否'")
    private Integer isExpensive;

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


    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }


    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }


    public List<SkuProp> getSkuProp() {
        return skuProp;
    }

    public void setSkuProp(List<SkuProp> skuProp) {
        this.skuProp = skuProp;
    }

    public IsUse getIsUse() {
        return isUse;
    }

    public void setIsUse(IsUse isUse) {
        this.isUse = isUse;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(String costPrice) {
        this.costPrice = costPrice;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public Integer getIsExpensive() {
        return isExpensive;
    }

    public void setIsExpensive(Integer isExpensive) {
        this.isExpensive = isExpensive;
    }

    public List<SkuCommonProp> getSkuCommonProp() {
        return skuCommonProp;
    }

    public void setSkuCommonProp(List<SkuCommonProp> skuCommonProp) {
        this.skuCommonProp = skuCommonProp;
    }
}


