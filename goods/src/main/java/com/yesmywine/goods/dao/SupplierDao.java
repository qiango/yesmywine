package com.yesmywine.goods.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.goods.bean.DeleteEnum;
import com.yesmywine.goods.entityProperties.Supplier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by wangdiandian on 2017/3/15.
 */
public interface SupplierDao extends BaseRepository<Supplier, Integer> {

    List<Supplier> findByDeleteEnum(DeleteEnum deleteEnum);

    Supplier findBySupplierNameAndDeleteEnum(String name,DeleteEnum deleteEnum);

    @Query("from Supplier where supplierCode = :supplierCode  and   deleteEnum = 0 ")
    List<Supplier> findBySupplierCode(@Param("supplierCode") String code);
    Supplier findBySupplierCodeAndDeleteEnum(String supplierCode,DeleteEnum deleteEnum);
    Supplier findBySupplierCodeAndDeleteEnumAndIdNot(String supplierCode,DeleteEnum deleteEnum,Integer id);
}
