package com.yesmywine.user.service;

import com.yesmywine.util.error.yesmywineException;

/**
 * Created by hz on 3/27/17.
 */
public interface MoneyService {
    String create(String proportion) throws yesmywineException;
    String update(String proportion) throws yesmywineException;
}
