package com.yesmywine.user.dao;

import com.yesmywine.user.entity.MonBeans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hz on 3/27/17.
 */
@Repository
public interface MoneyDao extends JpaRepository<MonBeans, Integer> {

    @Query("select max(id) from MonBeans")
    Integer findId();

    @Query("SELECT proportion FROM MonBeans WHERE id=(SELECT max(id) FROM MonBeans)")
    String find();


    @Query("FROM MonBeans")
    List<MonBeans> findAlls();

}
