package com.yesmywine.ware.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.ware.entity.CostPriceRecord;
import com.yesmywine.ware.service.ChannelsInventoryService;
import com.yesmywine.ware.service.CostPriceRecordService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SJQ on 2017/4/20.
 */
@RestController
@RequestMapping(value = "/inventory/costPriceInit")
public class CostPriceInit {
    @Autowired
    private CostPriceRecordService costPriceRecordService;
    @Autowired
    private ChannelsInventoryService channelsInventoryService;

    @RequestMapping(method = RequestMethod.POST)
    public String costPriceinit(String jsonData) {
        JSONArray jsonArray = JSON.parseArray(jsonData);
        List<CostPriceRecord> list = new ArrayList<>(jsonArray.size());
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            String year = jsonObject.getString("year");
            String mounth = jsonObject.getString("mounth");
            String price = jsonObject.getString("costPrice");
            String skuId = jsonObject.getString("skuId");
            String skuCode = jsonObject.getString("skuCode");
            String skuName = jsonObject.getString("skuName");

            CostPriceRecord costPriceRecord = new CostPriceRecord();
            costPriceRecord.setSkuId(Integer.valueOf(skuId));
            costPriceRecord.setSkuCode(skuCode);
            costPriceRecord.setSkuName(skuName);
            costPriceRecord.setYear(Integer.valueOf(year));
            costPriceRecord.setMounth(Integer.valueOf(mounth));
            costPriceRecord.setCostPrice(Double.valueOf(price));
            costPriceRecord.setTotalCount(0);
            costPriceRecord.setTotalPrice(Double.valueOf(0));
            costPriceRecord.setMounthInitCount(0);

            list.add(costPriceRecord);
            if (list.size() % 300 == 0) {
                costPriceRecordService.save(list);
                list.clear();
            }
        }
        costPriceRecordService.save(list);
        return ValueUtil.toJson(HttpStatus.SC_CREATED, "SUCCESS");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String inventoryInit() {
        PageModel pageModel = new PageModel(1, 1);
        pageModel = costPriceRecordService.findAll(pageModel);
        Integer totalCount = Integer.valueOf(pageModel.getTotalRows().toString());

        Integer manyPart = totalCount / 1000;
        Integer remainder = totalCount % 1000;

        if (remainder > 0) {
            manyPart = manyPart + 1;
        }
        for (int i = 0; i < manyPart; i++) {
            InventoryInitThread inventoryInitThread = new InventoryInitThread(costPriceRecordService, channelsInventoryService, i + 1);
            Thread thread = new Thread(inventoryInitThread);
            thread.start();
        }


        return ValueUtil.toJson(HttpStatus.SC_CREATED, "SUCCESS");
    }

}
