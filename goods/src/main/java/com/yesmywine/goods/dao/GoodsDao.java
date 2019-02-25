package com.yesmywine.goods.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.goods.entity.Goods;
import org.springframework.stereotype.Repository;

/**
 * Created by hz on 12/8/16.
 */
@Repository
public interface GoodsDao extends BaseRepository<Goods, Integer> {
//    Goods findByItemAndSkuIdString(Item item,String skuIdString);
    Goods findByGoodsName(String goodsName);
}
