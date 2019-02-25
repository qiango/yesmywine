package com.yesmywine.user.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.user.entity.DeliveryAddress;

import java.util.List;

/**
 * Created by SJQ on 2017/4/20.
 */
public interface DeliveryAddressDao extends BaseRepository<DeliveryAddress,Integer> {

    List<DeliveryAddress> findByUserId(Integer userId);
}
