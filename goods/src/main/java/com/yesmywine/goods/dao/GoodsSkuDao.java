package com.yesmywine.goods.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.goods.entity.GoodsSku;
import org.springframework.stereotype.Repository;

/**
 * Created by hz on 12/8/16.
 */
@Repository
public interface GoodsSkuDao extends BaseRepository<GoodsSku, Integer> {
}
