package com.yesmywine.goods.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.goods.entity.GiftCardRecord;
import com.yesmywine.util.error.yesmywineException;

import java.util.Map;

/**
 * Created by admin on 2016/12/22.
 */
public interface GiftCardRecordService extends BaseService<GiftCardRecord, Long> {
    String addGiftCard(Map<String, String> param) throws yesmywineException;//新增礼品卡

    Map<String, Object>  updateLoad(Long id) throws yesmywineException;

    String updateSave(Map<String, String> param) throws yesmywineException;

    String delete(Long id) throws yesmywineException;

    String audit(Long id,String reason,Integer status) throws yesmywineException;

}
