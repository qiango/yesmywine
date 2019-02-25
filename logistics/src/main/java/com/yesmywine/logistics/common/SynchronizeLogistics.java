package com.yesmywine.logistics.common;

import com.yesmywine.httpclient.bean.HttpBean;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.ValueUtil;

/**
 * Created by wangdiandian on 2017/4/25.
 */
public class SynchronizeLogistics {

    public static String create(String jsonData) {
//        jsonData = UriEncoder.encode(jsonData);
        int i = 0;
        String code = null;
        while (i < 2) {
            try {
                i++;
                HttpBean httpBean = new HttpBean( Dictionary.MALL_HOST+ "/logistics/shippers/itf", RequestMethod.post);
                httpBean.addParameter("jsonData", jsonData);
                httpBean.run();
                String result = httpBean.getResponseContent();
                if (result != null) {
                    code = ValueUtil.getFromJson(result, "code");
                    break;
                }
            } catch (Exception e) {
                continue;
            }

        }
        return code;
    }
    public static String delete(String jsonData) {
//        jsonData = UriEncoder.encode(jsonData);
        int i = 0;
        String code = null;
        while (i < 2) {
            try {
                i++;
                HttpBean httpBean = new HttpBean( Dictionary.MALL_HOST+ "/logistics/shippers/delete/itf", RequestMethod.post);
                httpBean.addParameter("jsonData", jsonData);
                httpBean.run();
                String result = httpBean.getResponseContent();
                if (result != null) {
                    code = ValueUtil.getFromJson(result, "code");
                    break;
                }
            } catch (Exception e) {
                continue;
            }

        }
        return code;
    }
}
