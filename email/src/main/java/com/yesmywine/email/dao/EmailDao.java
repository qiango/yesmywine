package com.yesmywine.email.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.email.entity.Email;
import org.springframework.stereotype.Repository;

/**
 * Created by wangdiandian on 2017/5/16.
 */
@Repository
public interface EmailDao extends BaseRepository<Email,Integer> {

}
