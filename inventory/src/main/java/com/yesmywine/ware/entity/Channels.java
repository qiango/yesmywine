package com.yesmywine.ware.entity;


import javax.persistence.*;

/**
 * Created by SJQ on 2017/1/5.
 *
 * @Description:库存渠道字典表
 */
@Entity
@Table(name = "channels")
public class Channels extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "varchar(100) COMMENT '渠道名称'")
    private String channelName;
    @Column(columnDefinition = "varchar(50) COMMENT '渠道编码'")
    private String channelCode; //渠道编码
    @ManyToOne
    @JoinColumn(name = "parentChannelId")
    private Channels parentChannel; //上级渠道
    @Column(columnDefinition = "int(1) COMMENT '类别 0-实渠道  1-门店分公司渠道   2-客服系统渠道   3-通用渠道'")
    private Integer type; //类别 0-实渠道  1-门店分公司渠道   2-客服系统渠道   3-通用渠道
    @Column(columnDefinition = "varchar(50) COMMENT '备注'")
    private String comment; //备注
    @Column(columnDefinition = "BIT(1) COMMENT '是否用于销售'")
    private Boolean ifSale; //是否用于销售
    @Column(columnDefinition = "BIT(1) COMMENT '是否用于库存'")
    private Boolean ifInventory;    //是否用于库存
    @Column(columnDefinition = "BIT(1) COMMENT '是否用于采购'")
    private Boolean ifProcurement;  //是否用于采购
    @Column(columnDefinition = "BIT(1) COMMENT '是否能删除'")
    private Boolean canDelete;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public Channels getParentChannel() {
        return parentChannel;
    }

    public void setParentChannel(Channels parentChannel) {
        this.parentChannel = parentChannel;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getIfSale() {
        return ifSale;
    }

    public void setIfSale(Boolean ifSale) {
        this.ifSale = ifSale;
    }

    public Boolean getIfInventory() {
        return ifInventory;
    }

    public void setIfInventory(Boolean ifInventory) {
        this.ifInventory = ifInventory;
    }

    public Boolean getIfProcurement() {
        return ifProcurement;
    }

    public void setIfProcurement(Boolean ifProcurement) {
        this.ifProcurement = ifProcurement;
    }

    public Boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(Boolean canDelete) {
        this.canDelete = canDelete;
    }
}
