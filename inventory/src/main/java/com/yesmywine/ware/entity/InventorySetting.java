package com.yesmywine.ware.entity;

import javax.persistence.*;

/**
 * Created by SJQ on 2017/1/17.
 *
 * @Description:
 */
@Entity
@Table(name = "inventorySetting")
public class InventorySetting extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "int(1) COMMENT '0-下单扣库存  1-支付扣库存'")
    private Integer type;//0-下单扣库存  1-支付扣库存

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
