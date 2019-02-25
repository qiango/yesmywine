package com.yesmywine.test;


import com.yesmywine.goods.entity.Goods;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * Created by hz on 12/8/16.
 */
public class TimerTest02 {
    @Test
    public void get() {
        Date date = new Date();
//        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String time=format.format(date);
        System.out.print(date);
    }

    @Test
    public void getGoodsNumber() {
//        String bigCategoryName=ValueUtil.getFromJson(Json,bigCategory);
//        String bigCategory=ValueUtil.getFromJson(Json,category);
//        String goodsNumber=bigCategory+bigCategoryName;
    }

    @Test
    public void refelt() {
        Goods goods = new Goods();
//        goods.setId(22l);
        goods.setGoodsName("test");

        try {
            Field field = goods.getClass().getDeclaredField("id");
            field.setAccessible(true);
            Object obj = field.get(goods);
            System.out.println(obj);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }


}





