package com.yesmywine.goods.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.goods.entity.GiftCardRecord;
import org.springframework.stereotype.Repository;

/**
 * Created by hz on 12/12/16.
 */
@Repository
public interface GiftCardRecordDao extends BaseRepository<GiftCardRecord, Long> {
//    GiftCardRecord findBatchNumber(String batchNumber);
//    List<GiftCardRecord> findByCardName(String cardName);
}
