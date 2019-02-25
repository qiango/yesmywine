package com.yesmywine.user.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.user.entity.AdminUser;

/**
 * Created by ${shuang} on 2017/6/29.
 */
public interface AdminUserDao  extends BaseRepository<AdminUser,Integer> {
    AdminUser findByUid(String uid);
}
