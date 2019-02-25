package com.yesmywine.ware.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.ware.entity.Warehouses;
import org.springframework.cache.annotation.CacheConfig;

import java.util.List;

/**
 * Created by SJQ on 2017/1/9.
 *
 * @Description:
 */
@CacheConfig(cacheNames = "warehouse")
public interface WarehouseDao extends BaseRepository<Warehouses, Integer> {
    Warehouses findByWarehouseCode(String exportWarehouseCode);

    Warehouses findByWarehouseName(String warehouseName);


    List<Warehouses> findByWarehouseProvinceIdAndWarehouseCityIdAndWarehouseRegionIdAndType(String a, String b, String c, Integer d);

    List<Warehouses> findByRelationCode(String relationCode);
//    @Query("select * FROM warehouse WHERE warehouseProvinceId =:a AND warehouseCityId=:b AND warehouseRegionId=:c AND type=0")
//    List<Warehouses> findByPro(@Param("a") Integer a,@Param("b") Integer b,@Param("c") Integer c);
}
