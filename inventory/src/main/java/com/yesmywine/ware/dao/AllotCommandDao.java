package com.yesmywine.ware.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.ware.entity.AllotApply;
import com.yesmywine.ware.entity.AllotCommand;
import com.yesmywine.ware.entity.AllotDetail;

/**
 * Created by by on 2017/7/21.
 */
public interface AllotCommandDao extends BaseRepository<AllotCommand, Integer> {
    AllotCommand findByAllotCode(String allotCode);
}
