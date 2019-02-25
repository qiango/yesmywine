package com.yesmywine.goods.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.goods.bean.IsSku;
import com.yesmywine.goods.entity.CategoryProperty;
import com.yesmywine.goods.entityProperties.PropertiesValue;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by SJQ on 2017/4/26.
 */
public interface CategoryPropertyDao extends BaseRepository<CategoryProperty,Integer> {

    List<CategoryProperty> findByPropertyId(Integer propId);

    List<CategoryProperty> findByPropertyValue(PropertiesValue propertyValue);

    List<CategoryProperty> findByCategoryIdOrderByPropertyId(Integer categoryId);

    List<CategoryProperty> findByCategoryIdAndPropertyId(Integer categoryId, Integer propertyId);

    List<CategoryProperty> findByCategoryId(Integer categoryId);

    CategoryProperty findByCategoryIdAndPropertyIdAndPropertyValue(Integer id, Integer propertyId, PropertiesValue propertyValue);

    void deleteByCategoryId(Integer id);

    void deleteByCategoryIdAndPropertyIdAndPropertyValue(Integer categoryId, Integer propertyId, PropertiesValue propertiesValue) ;

    void deleteByCategoryIdAndPropertyId(Integer id, Integer propertyId);

    @Query("select c from CategoryProperty c ,Properties p where c.propertyId=p.id and p.isSku=:isSku and c.categoryId=:categoryId")
    List<CategoryProperty> getSKUProperty(@Param("categoryId") Integer categoryId, @Param("isSku")IsSku isSku);

    @Query("select c from CategoryProperty c ,Properties p where c.propertyId=p.id and p.isSku=:isSku and c.categoryId=:categoryId")
    List<CategoryProperty> getOrdinaryProperty(@Param("categoryId") Integer categoryId, @Param("isSku")IsSku isSku);
}
