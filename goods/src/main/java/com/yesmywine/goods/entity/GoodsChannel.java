package com.yesmywine.goods.entity;

import com.yesmywine.base.record.entity.BaseEntity;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;
import java.util.List;

/**
 * Created by ${shuang} on 2017/3/16.
 */

@Entity
@Table(name = "goodsChannel")
public class GoodsChannel extends BaseEntity<Integer> {
    @Column(columnDefinition = "int(11) COMMENT '商品id'")
    private  Integer goodsId;
    @Column(columnDefinition = "varchar(50) COMMENT '商品名称'")
    private  String goodsName;
//    @Transient
//    private  String skuId;
    @OneToMany(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "goodsSku")
    private List<GoodsSku> goodsSku;
    @Column(columnDefinition = "varchar(50) COMMENT '商品编码'")
    private String goodsCode;
    @Column(columnDefinition = "varchar(200) COMMENT '第三方编码'")
    private String thirdCode;
    @Column(columnDefinition = "int(11) COMMENT '渠道id'")
    private  Integer  channelId;
    @Column(columnDefinition = "varchar(255) COMMENT '渠道编码'")
    private  String   channelCode;
    @Column(columnDefinition = "varchar(255) COMMENT '渠道名称'")
    private String channelName;
    @Column(columnDefinition = "varchar(50) COMMENT '商品价格'")
    private  String price;
    @Column(columnDefinition = "int(50) COMMENT '商品状态1预售,0在售'")
    private  Integer operate;//1预售,0在售
    private   String item;
    @Ignore
    @Transient
    private Integer inventory;//库存

    public GoodsChannel(){
        this.operate=1;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    @Transient
    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getOperate() {
        return operate;
    }

    public void setOperate(Integer operate) {
        this.operate = operate;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public List<GoodsSku> getGoodsSku() {
        return goodsSku;
    }

    public void setGoodsSku(List<GoodsSku> goodsSku) {
        this.goodsSku = goodsSku;
    }

    public String getThirdCode() {
        return thirdCode;
    }

    public void setThirdCode(String thirdCode) {
        this.thirdCode = thirdCode;
    }
}
