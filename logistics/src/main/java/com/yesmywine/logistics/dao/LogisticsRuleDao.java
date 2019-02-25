package com.yesmywine.logistics.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.logistics.entity.LogisticsRule;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangdiandian on 2017/3/28.
 */
@Repository
public interface LogisticsRuleDao  extends BaseRepository<LogisticsRule,Integer> {
//    List<LogisticsRule> findByShipperIdAndDeleteEnum(Integer id);
    LogisticsRule findByAreaNameContainingAndDeleteEnumAndShipperId(String distributionArea,Integer deleteEnum,Integer shipperId);
    List<LogisticsRule> findByShipperIdAndDeleteEnum(Integer shipperId,Integer deleteEnum);
    List<LogisticsRule> findByShipperIdAndDeleteEnumAndIdNot(Integer shipperId,Integer deleteEnum,Integer id);
}
