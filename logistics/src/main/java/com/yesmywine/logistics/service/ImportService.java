package com.yesmywine.logistics.service;

import java.util.List;
import java.util.Map;

/**
 * Created by hz on 2017/5/8.
 */
public interface ImportService {


    List importExpressRule(List<Map<String, Object>> list);

    List importLogisticsRule(List<Map<String, Object>> list);



}
