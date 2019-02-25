package com.yesmywine.goods.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.goods.entity.GiftCard;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hz on 12/12/16.
 */
@Repository
public interface GiftCardDao extends BaseRepository<GiftCard, Long> {
//    GiftCard findByCardNo(String cardNo);

    GiftCard findByCardNumberAndPassword(String cardNumber,String password);
    GiftCard findByCardNumber(String cardNumber);
    List findByBatchNumber(String batchNumber);
}
