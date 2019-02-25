package com.yesmywine.logistics.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.logistics.entity.ExpressRule;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangdiandian on 2017/3/28.
 */
@Repository
public interface ExpressRuleDao extends BaseRepository<ExpressRule,Integer> {
    List<ExpressRule> findByShipperIdAndDeleteEnum(Integer id,Integer deleteId);
    ExpressRule findByAreaNameContainingAndDeleteEnumAndWarehouseIdAndTypeAndShipperId(String distributionArea,Integer deleteEnum,Integer warehouseId,Integer type ,Integer shipperId);
    List<ExpressRule> findByWarehouseIdAndDeleteEnumAndType(Integer warehouseId,Integer deleteEnum,Integer type);
    List<ExpressRule> findByWarehouseIdAndDeleteEnumAndTypeAndIdNot(Integer warehouseId,Integer deleteEnum,Integer id,Integer type);
    List<ExpressRule> findByWarehouseIdAndDeleteEnumAndTypeAndShipperId(Integer warehouseId,Integer deleteEnum,Integer type,Integer shipperId);

    ExpressRule findByAreaNameContainingAndDeleteEnumAndWarehouseCodeAndTypeAndShipperId(String distributionArea, int i, String warehouseCode, int i1, Integer id);
}
