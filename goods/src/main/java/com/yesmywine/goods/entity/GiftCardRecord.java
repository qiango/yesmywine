package com.yesmywine.goods.entity;

import com.yesmywine.base.record.entity.BaseEntity;
import com.yesmywine.goods.bean.DeleteEnum;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by sjq on 2016/12/22.
 * 礼品卡生成记录表
 */
@Entity
@Table(name = "giftCardRecord")
public class GiftCardRecord extends BaseEntity<Long>{
    @Column(columnDefinition = "varchar(255) COMMENT '礼品卡名称'")
    private String cardName;//礼品卡名称
    @Column(columnDefinition = "int(11) COMMENT '礼品卡类型（0,电子/1,实体）'")
    private Integer type;//礼品卡类型（0,电子/1,实体）
    @Column(columnDefinition = "int(11) COMMENT 'skuId'")
    private Integer skuId;
    @Column(columnDefinition = "varchar(255) COMMENT 'SKU编码'")
    private String code;
//    @Column(columnDefinition = "varchar(255) COMMENT 'sku名字'")
//    private String skuName;
    @Column(columnDefinition = "varchar(255) COMMENT '批次编号'")
    private String batchNumber;//批次编号
    @Column(columnDefinition = "varchar(255) COMMENT '礼品卡面值'")
    private Double amounts;//礼品卡面值
    @Column(columnDefinition = "varchar(255) COMMENT '数量'")
    private Integer number;//数量
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private Date latestTime;//最迟激活时间;
    @Column(columnDefinition = "varchar(255) COMMENT '有效期（单位：天）'")
    private Integer inDate;//有效期（单位：天）
    @Column(columnDefinition = "varchar(255) COMMENT '审核时间'")
    private Date auditTime;//审核时间
    @Column(columnDefinition = "varchar(255) COMMENT '状态（0待审核/1已审核/2审核未通过)'")
    private Integer status;//状态（0待审核/1已审核)
    @Enumerated(EnumType.ORDINAL)
    private DeleteEnum deleteEnum;//ordinal枚举存数字
    private String reason;//审核原因


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Double getAmounts() {
        return amounts;
    }

    public void setAmounts(Double amounts) {
        this.amounts = amounts;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Date getLatestTime() {
        return latestTime;
    }

    public void setLatestTime(Date latestTime) {
        this.latestTime = latestTime;
    }

    public Integer getInDate() {
        return inDate;
    }

    public void setInDate(Integer inDate) {
        this.inDate = inDate;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public DeleteEnum getDeleteEnum() {
        return deleteEnum;
    }

    public void setDeleteEnum(DeleteEnum deleteEnum) {
        this.deleteEnum = deleteEnum;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

//    public String getSkuName() {
//        return skuName;
//    }
//
//    public void setSkuName(String skuName) {
//        this.skuName = skuName;
//    }
}
