package com.yesmywine.ware.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.ware.entity.CostPriceRecord;
import com.yesmywine.ware.service.ChannelsInventoryService;
import com.yesmywine.ware.service.CostPriceRecordService;
import org.apache.commons.collections.map.HashedMap;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by SJQ on 2017/4/17.
 */
public class CostPriceThread implements Runnable {
    private CostPriceRecordService costPriceRecordService;
    private ChannelsInventoryService channelsInventoryService;
    private Integer pageNo;
    private Integer year;
    private Integer mounth;

    public CostPriceThread(CostPriceRecordService costPriceRecordService, ChannelsInventoryService channelsInventoryService, Integer pageNo, Integer year, Integer mounth) {
        this.costPriceRecordService = costPriceRecordService;
        this.channelsInventoryService = channelsInventoryService;
        this.pageNo = pageNo;
        this.year = year;
        this.mounth = mounth;
    }

    @Override
    public void run() {
        PageModel pageModel = new PageModel(pageNo, 1000);
        Map<String, Object> parmas = new HashedMap();
        parmas.put("year", mounth == 1 ? year - 1 : year);
        parmas.put("mounth", mounth == 1 ? 12 : mounth);
        pageModel.addCondition(parmas);
        pageModel = costPriceRecordService.findAll(pageModel);
        List<CostPriceRecord> recordList = pageModel.getContent();
        List<CostPriceRecord> newRecordList = new ArrayList<>(recordList.size());
        List<CostPriceRecord> callbackList = new ArrayList<>();
        JSONArray callbackArray = new JSONArray();
        for (CostPriceRecord costPriceRecord : recordList) {
            Integer totalCount = costPriceRecord.getTotalCount();
            Double totalPrice = costPriceRecord.getTotalPrice();
            Integer mounthInitCount = costPriceRecord.getMounthInitCount();
            Double beforeCostPrice = costPriceRecord.getCostPrice();
            Integer skuId = costPriceRecord.getSkuId();

            Integer nowCount = channelsInventoryService.findBySkuIdCount(skuId);

            BigDecimal b = new BigDecimal((float) (mounthInitCount * beforeCostPrice + totalPrice) / (nowCount + totalCount));
            Double newCostPrice = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

            costPriceRecord.setId(null);
            costPriceRecord.setYear(year);
            costPriceRecord.setMounth(mounth);
            costPriceRecord.setMounthInitCount(nowCount);
            costPriceRecord.setTotalCount(0);
            costPriceRecord.setCostPrice(Double.valueOf(0));
            costPriceRecord.setCostPrice(newCostPrice);
            if (!newCostPrice.equals(beforeCostPrice)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("skuId", costPriceRecord.getSkuId());
                jsonObject.put("skuCode", costPriceRecord.getSkuCode());
                jsonObject.put("price", newCostPrice);
                callbackArray.add(jsonObject);
                callbackList.add(costPriceRecord);
            } else {
                newRecordList.add(costPriceRecord);
            }
        }
        costPriceRecordService.save(newRecordList);
        costPriceRecordService.save(callbackList);
        //将成本价同步到sku模块
        ComputeCostPriceTask computeCostPriceTask = new ComputeCostPriceTask();
        computeCostPriceTask.costPriceCallback(callbackArray.toJSONString(), callbackList, costPriceRecordService);
    }
}
