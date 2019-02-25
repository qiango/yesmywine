package com.yesmywine.cache.service.serviceImpl;

import com.yesmywine.cache.bean.ConstantData;
import com.yesmywine.cache.service.InventoryService;
import com.yesmywine.db.base.biz.RedisCache;
import com.yesmywine.httpclient.bean.HttpBean;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.httpclient.bean.RequestMethod;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by light on 2017/3/7.
 */
@Service
public class InventoryServiceImpl implements InventoryService{
    @Override
    public String setSeckill(Integer goodsSkuID, Integer count) {
        Integer num = 0;
        if(goodsSkuID < 1000){
            num = 0;
        }else if(goodsSkuID >= 1000 && goodsSkuID < 2000){
            num = 1;
        }else if(goodsSkuID >= 2000 && goodsSkuID < 3000){
            num = 2;
        }else if(goodsSkuID >= 3000 && goodsSkuID < 4000){
            num = 3;
        }else if(goodsSkuID >= 4000 && goodsSkuID < 5000){
            num = 4;
        }else if(goodsSkuID >= 5000 && goodsSkuID < 6000){
            num = 5;
        }else if(goodsSkuID >= 6000 && goodsSkuID < 7000){
            num = 6;
        }else if(goodsSkuID >= 7000 && goodsSkuID < 8000){
            num = 7;
        }else if(goodsSkuID >= 8000 && goodsSkuID < 9000){
            num = 8;
        }else if(goodsSkuID >= 9000 && goodsSkuID < 10000){
            num = 9;
        }

//        RedisCache.selectDatabase(num);

        try {
            RedisCache.set(goodsSkuID.toString(), count, num);
            HttpBean bean = new HttpBean(ConstantData.urlOrders + "/init/start", RequestMethod.get);
            bean.addParameter("skuId", goodsSkuID.toString());
            bean.addParameter("count", count);
            bean.run();
        }catch (Exception e){
            return ValueUtil.toJson("失败");
        }
        return ValueUtil.toJson("success");

    }

    @Override
    public void setActivity(List<Integer> ids) throws Exception {
        for (Integer id: ids) {

        }
    }
}
