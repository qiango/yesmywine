package com.yesmywine.goods.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.goods.entity.SkuCommonProp;
import org.springframework.stereotype.Repository;

/**
 * Created by hz on 7/11/17.
 */
@Repository
public interface SkuCommonDao extends BaseRepository<SkuCommonProp,Integer> {
}
