package com.yesmywine.ware.Utils;

import com.yesmywine.httpclient.bean.HttpBean;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.ValueUtil;

/**
 * Created by Administrator on 2017/4/23 0023.
 */
public class SKUUtils {
    public static String getResult(String host, String url, RequestMethod method, String paramKey, String paramValue) {
        int i = 0;
        String result = null;
        while (i < 2) {
            try {
                i++;
                HttpBean httpBean = new HttpBean(host + url, method);
                httpBean.addParameter(paramKey, paramValue);
                httpBean.run();
                result = httpBean.getResponseContent();
                if (result != null) {
                    String code = ValueUtil.getFromJson(result, "code");
                    if (code != null) {
                        break;
                    }
                }
            } catch (Exception e) {
                continue;
            }

        }
        return result;
    }
}
