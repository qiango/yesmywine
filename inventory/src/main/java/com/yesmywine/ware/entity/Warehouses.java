package com.yesmywine.ware.entity;

import javax.persistence.*;

/**
 * Created by SJQ on 2017/1/5.
 *
 * @Description:仓库表
 */
@Entity
@Table(name = "warehouse")
public class Warehouses extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "varchar(50) COMMENT '酒库编码'")
    private String warehouseCode;
    @Column(columnDefinition = "varchar(50) COMMENT '酒库名称'")
    private String warehouseName;
    @Column(columnDefinition = "varchar(50) COMMENT '酒库省份'")
    private String warehouseProvince;
    @Column(columnDefinition = "varchar(50) COMMENT '酒库省份Id'")
    private String warehouseProvinceId;
    @Column(columnDefinition = "varchar(50) COMMENT '酒库城市Id'")
    private String warehouseCityId;
    @Column(columnDefinition = "varchar(50) COMMENT '酒库城市'")
    private String warehouseCity;
    @Column(columnDefinition = "varchar(50) COMMENT '酒库区Id'")
    private String warehouseRegionId;
    @Column(columnDefinition = "varchar(50) COMMENT '酒库区'")
    private String warehouseRegion;
    @Column(columnDefinition = "varchar(50) COMMENT '酒库地址'")
    private String warehouseAddress;
    @Column(columnDefinition = "int(1) COMMENT '类别  0-门店仓、1-实体仓、2-未清关仓库、3-已清关仓'")
    private Integer type;  //类别  0-门店仓、1-实体仓、2-未清关仓库、3-已清关仓
    @Column(columnDefinition = "varchar(50) COMMENT '联系人名称'")
    private String contactName;//联系人名称
    @Column(columnDefinition = "varchar(50) COMMENT '电话'")
    private String telephone;
    @Column(columnDefinition = "varchar(50) COMMENT '电话'")
    private String phone;
    @Column(columnDefinition = "varchar(50) COMMENT '传真'")
    private String fax;
    @Column(columnDefinition = "varchar(50) COMMENT '邮箱'")
    private String email;
    @Column(columnDefinition = "varchar(50) COMMENT '文本'")
    private String comment;
    @Column(columnDefinition = "varchar(50) COMMENT '状态'")
    private String status;
    @Column(columnDefinition = "BIT(1) COMMENT '是否能删除'")
    private Boolean canDelete;
    @Column(columnDefinition = "varchar(50) COMMENT '已清关与未清关关联编码'")
    private String relationCode;

    public String getRelationCode() {
        return relationCode;
    }

    public void setRelationCode(String relationCode) {
        this.relationCode = relationCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getWarehouseProvince() {
        return warehouseProvince;
    }

    public void setWarehouseProvince(String warehouseProvince) {
        this.warehouseProvince = warehouseProvince;
    }

    public String getWarehouseCity() {
        return warehouseCity;
    }

    public void setWarehouseCity(String warehouseCity) {
        this.warehouseCity = warehouseCity;
    }

    public String getWarehouseAddress() {
        return warehouseAddress;
    }

    public void setWarehouseAddress(String warehouseAddress) {
        this.warehouseAddress = warehouseAddress;
    }

    public String getWarehouseRegion() {
        return warehouseRegion;
    }

    public void setWarehouseRegion(String warehouseRegion) {
        this.warehouseRegion = warehouseRegion;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getWarehouseProvinceId() {
        return warehouseProvinceId;
    }

    public void setWarehouseProvinceId(String warehouseProvinceId) {
        this.warehouseProvinceId = warehouseProvinceId;
    }

    public String getWarehouseCityId() {
        return warehouseCityId;
    }

    public void setWarehouseCityId(String warehouseCityId) {
        this.warehouseCityId = warehouseCityId;
    }

    public String getWarehouseRegionId() {
        return warehouseRegionId;
    }

    public void setWarehouseRegionId(String warehouseRegionId) {
        this.warehouseRegionId = warehouseRegionId;
    }

    public Boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(Boolean canDelete) {
        this.canDelete = canDelete;
    }
}
