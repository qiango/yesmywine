package com.yesmywine.goods.dao;

import com.yesmywine.base.record.repository.BaseRepository;
import com.yesmywine.goods.bean.DeleteEnum;
import com.yesmywine.goods.bean.IsShow;
import com.yesmywine.goods.entityProperties.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangdiandian on 2016/12/9.
 */
@Repository
public interface CategoryDao extends BaseRepository<Category, Integer> {
//    @Query("select id from Category where code = :code  and   deleteEnum = 0 ")
//    String findIdByCode(@Param("code") Object code);

    @Query("select id from Category where categoryName = :name  and   deleteEnum = 0 ")
    Integer findIdByCategoryName(@Param("name") String name);
    @Query("select id from Category where code = :code  and   deleteEnum = 0 ")
    Integer findIdByCode(@Param("code") String code);
    @Query("from Category where code = :code  and   deleteEnum = 0 ")
    Category findByCode(@Param("code") String code);

    List findByDeleteEnumAndIsShow(DeleteEnum deleteEnum, IsShow isShow);

    List<Category> findByParentName(Category category);

    List<Category> findByDeleteEnum(DeleteEnum deleteEnum);

    List<Category> findByLevel(Integer level);

    @Query(value = "select c.* from category c where FIND_IN_SET(id,queryChildrenAreaInfo(:parentId))",nativeQuery=true)
    List<Category> getAllChildren(@Param("parentId") Integer parentId);

    List<Category> findIdByCategoryNameAndLevel(String categoryName, Integer level);
}
