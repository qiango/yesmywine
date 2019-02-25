package com.yesmywine.sms.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.sms.entity.Configure;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by wangdiandian on 2017/5/8.
 */
@Repository
public interface ConfigureDao extends BaseRepository<Configure,Integer> {
    @Query("from Configure where id = (select max(id) from Configure)")
    Configure rule();

}
