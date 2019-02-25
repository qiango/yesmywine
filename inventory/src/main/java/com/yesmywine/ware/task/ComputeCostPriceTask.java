package com.yesmywine.ware.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.httpclient.bean.HttpBean;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.date.DateUtil;
import com.yesmywine.ware.entity.CostPriceRecord;
import com.yesmywine.ware.service.ChannelsInventoryService;
import com.yesmywine.ware.service.CostPriceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by SJQ on 2017/4/17.
 * 计算成本价
 */
@RestController
@RequestMapping(value = "/inventory/computeCostPrice/task")
public class ComputeCostPriceTask {
    @Autowired
    private CostPriceRecordService costPriceRecordService;
    @Autowired
    private ChannelsInventoryService channelsInventoryService;

    @RequestMapping(method = org.springframework.web.bind.annotation.RequestMethod.GET)
    public String excuted() {
        if (true) {
            System.out.println("=============================每月初计算成本价定时任务 开始================================");
            return ValueUtil.toJson("SUCCESS");
        }
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        PageModel pageModel = new PageModel(1, 1);
        pageModel = costPriceRecordService.findAll(pageModel);
        Integer totalCount = Integer.valueOf(pageModel.getTotalRows().toString());

        Integer manyPart = totalCount / 1000;
        Integer remainder = totalCount % 1000;

        if (remainder > 0) {
            manyPart = manyPart + 1;
        }
        for (int i = 0; i < manyPart; i++) {
            CostPriceThread costPriceThread = new CostPriceThread(costPriceRecordService, channelsInventoryService, i + 1, year, month);
            Thread thread = new Thread(costPriceThread);
            thread.start();
        }
        System.out.println("=============================每月初计算成本价定时任务 结束================================");
        return ValueUtil.toJson("每月初计算成本价定时任务   " + DateUtil.getNowTime());
    }

    public void costPriceCallback(String jsonData, List<CostPriceRecord> list, CostPriceRecordService costPriceRecordService) {
        try {
            HttpBean httpBean = new HttpBean(Dictionary.PAAS_HOST + "/goods/sku", RequestMethod.put);
            httpBean.addParameter("jsonArray", jsonData);
            httpBean.run();
            String result = httpBean.getResponseContent();
            JSONObject resultJson = JSON.parseObject(result);
            String code = resultJson.getString("code");
            List<CostPriceRecord> newList = new ArrayList<>();
            if (code == null || !code.equals("200")) {
                for (CostPriceRecord costPriceRecord : list) {
                    costPriceRecord.setSynStatus(0);
                    newList.add(costPriceRecord);
                }
                costPriceRecordService.save(newList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    public static void main(String[] args) {
//
//        System.out.println(new java.text.DecimalFormat("#.00").format((float)7.31/3));
//        BigDecimal b = new BigDecimal((float)7.31/3);
//        Double newCostPrice = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//        System.out.println(newCostPrice);
//    }
}
