package com.yesmywine.goods.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.goods.entity.GiftCard;
import com.yesmywine.util.error.yesmywineException;

import java.util.Map;

/**
 * Created by admin on 2016/12/22.
 */

public interface GiftCardService extends BaseService<GiftCard, Long> {
    Map<String, Object> updateLoad(Long id) throws yesmywineException;//查看礼品卡详情

    String bound(String jsonData)throws yesmywineException;//pass接收礼品卡绑定信息接口

    String spend(String jsonData)throws yesmywineException;//pass接收礼品卡消费信息接口

    GiftCard showGiftCard(String jsonData)throws yesmywineException;//礼品卡查询接口

    String synchronizeGiftCard(String jsonDatas) throws yesmywineException;//商城创建礼品卡后同步接口

    String spendGiftCard(String jsonDatas) throws yesmywineException;//商城礼品卡消费后同步给pass接口

    String giftCardHistory(String jsonDatas) throws yesmywineException;//商城礼品卡绑定同步到pass接口

    String boundGiftCard(String jsonDatas) throws yesmywineException;//商城礼品卡绑定同步到pass接口

    String buyGiftCard(String jsonDatas) throws yesmywineException;//商城礼品卡购买同步到pass接口


}
