package com.yesmywine.ware.service;

import com.yesmywine.base.record.biz.BaseService;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.entity.AllotApply;
import com.yesmywine.ware.entity.Channels;
import com.yesmywine.ware.entity.Warehouses;

import java.util.List;

/**
 * Created by SJQ on 2017/1/17.
 *
 * @Description:
 */
public interface AllotApplyService extends BaseService<AllotApply, Integer> {

    String allotCommand(String jsonData, String userId) throws yesmywineException;

    AllotApply findByChannelCodeAndWarehouseCodeAndSkuId(String channelCode, String warehouseCode, Integer skuId);

    String allotApply(String jsonData) throws yesmywineException;

    AllotApply findByChannelCodeAndWarehouseCodeAndSkuIdAndStatusAndType(String channelCode, String warehouseCode, Integer skuId, Integer status,String type);

   List<AllotApply >  findByAllotCode(String allotCode);

    List<Channels> getApplyChannels();

    List<Warehouses> getApplyWarehouses(Integer channelId);

    String rirectAllotCommandRirect(Integer channelId, Integer tarWarehouseId,  Integer cwIds[], Integer counts[], Integer userId) throws yesmywineException;

    String omsCleanApply(String jsonData) throws yesmywineException;

    String omsCleanCommand(String jsonData, Integer userId)throws yesmywineException;

    String audit(Integer commandId, String comment, String userId) throws yesmywineException;

    String reject(Integer commandId, String comment, String userId) throws yesmywineException;
}
