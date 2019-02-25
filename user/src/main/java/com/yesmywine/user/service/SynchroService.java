package com.yesmywine.user.service;

import com.yesmywine.util.error.yesmywineException;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.util.Map;

/**
 * Created by Mars on 2017/6/25.
 */

@Transactional
public interface SynchroService {

    String beanCenter(@RequestParam Map<String, String> params) throws yesmywineException;
    Object returnsConsumeSys(@RequestParam Map<String, String> params) throws yesmywineException;

    Boolean synchronous(Integer id, String name, String type,String goodsCode ,Integer synchronous);

}
