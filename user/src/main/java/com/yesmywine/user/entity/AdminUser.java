package com.yesmywine.user.entity;

import com.yesmywine.base.record.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by ${shuang} on 2017/6/29.
 */
@Entity
@Table(name = "adminUser")
public class AdminUser extends BaseEntity<Integer> {
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
