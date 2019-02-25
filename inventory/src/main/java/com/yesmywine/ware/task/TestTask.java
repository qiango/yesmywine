package com.yesmywine.ware.task;

import com.yesmywine.httpclient.bean.HttpBean;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.httpclient.bean.RequestMethod;
import org.springframework.stereotype.Component;

/**
 * Created by SJQ on 2017/5/9.
 */
@Component
public class TestTask {

    //    @Scheduled(cron = "0 0/1 * * * ?")
    public void excuted() {
        HttpBean httpBean = new HttpBean("http://localhost:8080/dic/url_paas", RequestMethod.get);
        httpBean.run();
        String result = httpBean.getResponseContent();
        System.out.println(result);
        String message = ValueUtil.getFromJson(result, "data", "entityValue");
//        Dictionary.MALL_HOST = message;

    }

//    public static void main(String[] args) {
//        JSONArray array = new JSONArray();
//        JSONObject object1 = new JSONObject();
//        object1.put("user","张三");
//        array.add(object1);
//        JSONObject object2 = new JSONObject();
//        object2.put("user","李四");
//        array.add(object2);
//        JSONObject object3 = new JSONObject();
//        object3.put("user","张三");
//        object3.put("sex","男");
//
//
//        System.out.println(array.contains(object3));
//    }
}
