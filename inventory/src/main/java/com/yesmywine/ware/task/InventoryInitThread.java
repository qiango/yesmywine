package com.yesmywine.ware.task;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.ware.entity.CostPriceRecord;
import com.yesmywine.ware.service.ChannelsInventoryService;
import com.yesmywine.ware.service.CostPriceRecordService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SJQ on 2017/4/20.
 */
public class InventoryInitThread implements Runnable {
    private CostPriceRecordService costPriceRecordService;
    private ChannelsInventoryService channelsInventoryService;
    private Integer pageNo;

    public InventoryInitThread(CostPriceRecordService costPriceRecordService, ChannelsInventoryService channelsInventoryService, Integer pageNo) {
        this.costPriceRecordService = costPriceRecordService;
        this.channelsInventoryService = channelsInventoryService;
        this.pageNo = pageNo;
    }

    @Override
    public void run() {
        PageModel pageModel = new PageModel(pageNo, 1000);
        pageModel = costPriceRecordService.findAll(pageModel);
        List<CostPriceRecord> recordList = pageModel.getContent();
        List<CostPriceRecord> newRecordList = new ArrayList<>();
        int i = 0;
        for (CostPriceRecord costPriceRecord : recordList) {
            Integer skuId = costPriceRecord.getSkuId();
            Integer nowCount = channelsInventoryService.findBySkuIdCount(skuId);

            costPriceRecord.setMounthInitCount(nowCount);
            newRecordList.add(costPriceRecord);
            i++;
            if (i % 100 == 0) {
                costPriceRecordService.save(newRecordList);
                newRecordList.clear();
                System.out.println("Thread_" + pageNo + "已完成   " + i + "  条");
            }
        }
        costPriceRecordService.save(newRecordList);
    }
}
