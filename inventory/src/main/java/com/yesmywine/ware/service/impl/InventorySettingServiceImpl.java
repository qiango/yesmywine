package com.yesmywine.ware.service.impl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.ware.entity.InventorySetting;
import com.yesmywine.ware.service.InventorySettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by SJQ on 2017/2/10.
 */
@Service
@Transactional
public class InventorySettingServiceImpl extends BaseServiceImpl<InventorySetting, Integer> implements InventorySettingService {
}
