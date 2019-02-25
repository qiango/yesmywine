package com.yesmywine.goods.dao;


import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.goods.entityProperties.Properties;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by hz on 12/13/16.
 */
public interface PropertiesDao extends BaseRepository<Properties, Integer> {
    @Query("select max(id) from Properties")
    Integer findId();
    Properties findByCnName(String cnName);
    Properties findByCode(String code);
//    @Query("select id from Properties where categoryId= :categoryId and canSearch=0")
//    List<Integer> findIdByCategoryId(@Param("categoryId") Integer categoryId);

//    List<Properties>findByCategoryAndCanSearch(Category category, CanSearch canSearch);

//    Properties findByCategoryAndCnName(Category category,String cnName);

//    List<Properties> findByCategoryIdAndDeleteEnum(Integer categoryId,  DeleteEnum deleteEnum);
//    List<Properties> findByCategoryAndDeleteEnum(Category category,DeleteEnum deleteEnum);

//    List<Properties> findByDeleteEnum(DeleteEnum deleteEnum);

//    List<Properties> findByCategoryAndIsSkuAndDeleteEnum(Category category,IsSku isSku,DeleteEnum deleteEnum);

//    List<Properties> findByIsSkuAndDeleteEnum(IsSku isSku,DeleteEnum deleteEnum);
}
