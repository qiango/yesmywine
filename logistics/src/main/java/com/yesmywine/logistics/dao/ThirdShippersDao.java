package com.yesmywine.logistics.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.logistics.entity.ThirdShippers;

import java.util.List;

/**
 * Created by ${shuang} on 2017/7/21.
 */
public interface ThirdShippersDao extends BaseRepository<ThirdShippers,Integer> {

    List findByShippersId(Object shippersId);

    void deleteByShippersId(Integer shippersId);
}
