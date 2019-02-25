package com.yesmywine.goods.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.goods.entity.GoodsChannel;
import com.yesmywine.goods.entity.GoodsSku;
import com.yesmywine.util.error.yesmywineException;

import java.util.List;

/**
 * Created by ${shuang} on 2017/3/16.
 */

public interface GoodsChannelService extends BaseService<GoodsChannel,Integer> {
    //下发
    List setGoodsChannel(Integer goodsId, String[] params, Integer operate) throws yesmywineException;

    //更改预售
    String exchange(Integer goodsId,Integer channelId) throws yesmywineException;

    String http ( String skuId,Integer channelId);

    String http (String skuId);

    Integer inventory(String skuId, Integer channelId, String item);

    Integer inventoryGoodsSku(List<GoodsSku> goodsSkus, Integer channelId, String item);

    String updateThirdCode(Integer id,String thirdCode)throws yesmywineException;

}
