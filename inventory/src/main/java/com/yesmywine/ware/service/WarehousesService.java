package com.yesmywine.ware.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.entity.Warehouses;
import com.yesmywine.ware.entity.WarehousesChannel;

import java.util.List;
import java.util.Map;

/**
 * Created by SJQ on 2017/1/9.
 *
 * @Description:
 */
public interface WarehousesService extends BaseService<Warehouses, Integer> {
    void delete(Integer warehouseId) throws yesmywineException;

    List getGoodInWhichswarehouse(String skuId);

    List<WarehousesChannel> getGoodsInWarehouse(String warehouseCode);

    Boolean findByWarehouseName(String warehouseName);

    Boolean checkIsNull(Integer warehouseId);

    Warehouses create(Warehouses warehouse, Map<String, String> params) throws yesmywineException;

    Warehouses update(Warehouses warehouse, Map<String, String> params) throws yesmywineException;

    Warehouses findByWarehouseCode(String warehouseCode);
}
