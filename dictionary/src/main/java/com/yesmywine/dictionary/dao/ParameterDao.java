package com.yesmywine.dictionary.dao;

import com.yesmywine.dictionary.entity.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by WANG, RUIQING on 12/1/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
@Repository
public interface ParameterDao extends JpaRepository<Parameter, Integer> {
}
