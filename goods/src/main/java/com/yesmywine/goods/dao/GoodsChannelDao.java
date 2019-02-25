package com.yesmywine.goods.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.goods.entity.GoodsChannel;
import com.yesmywine.goods.entity.GoodsSku;

import java.util.List;

/**
 * Created by ${shuang} on 2017/3/16.
 */
public interface GoodsChannelDao extends BaseRepository<GoodsChannel, Integer> {

    GoodsChannel findByGoodsId(Integer goodsId);
//    GoodsChannel findBySkuId(String skuId);
    GoodsChannel findByGoodsSku(List<GoodsSku> goodsSku);
    GoodsChannel findByGoodsIdAndChannelId(Integer goodsId, Integer channelId);
}
