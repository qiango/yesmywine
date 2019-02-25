package com.yesmywine.dictionary.dao;

import com.yesmywine.dictionary.entity.DicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by WANG, RUIQING on 12/1/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
@Repository
public interface DicEntityDao extends JpaRepository<DicEntity, Integer> {

    List<DicEntity> findByEntityCode(String entityCode);

    List<DicEntity> findBySysCode(String sysCode);
}
