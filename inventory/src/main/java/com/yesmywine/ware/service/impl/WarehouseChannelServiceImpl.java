package com.yesmywine.ware.service.impl;

import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.ware.dao.WarehouseChannelDao;
import com.yesmywine.ware.dao.WarehouseDao;
import com.yesmywine.ware.entity.WarehousesChannel;
import com.yesmywine.ware.service.WarehousesChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by SJQ on 2017/3/15.
 */
@Service
@Transactional
public class WarehouseChannelServiceImpl extends BaseServiceImpl<WarehousesChannel, Integer> implements WarehousesChannelService {
    @Autowired
    private WarehouseChannelDao warehouseChannelDao;
    @Autowired
    private WarehouseDao warehouseDao;

    @Override
    public WarehousesChannel findByChannelCodeAndWarehouseCodeAndSkuCode(String channelCode, String warehouseCode, String skuCode) {
        return warehouseChannelDao.findByChannelCodeAndWarehouseCodeAndSkuCode(channelCode, warehouseCode, skuCode);
    }


    @Override
    public List<WarehousesChannel> findByWarehouseCodeAndSkuCodeOrderByUseCountDesc(String warehouseCode, String skuCode) {
        return warehouseChannelDao.findByWarehouseCodeAndSkuCodeOrderByUseCountDesc(warehouseCode, skuCode);
    }
}
