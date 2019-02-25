package com.yesmywine.goods.common;

import com.yesmywine.httpclient.bean.HttpBean;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.ValueUtil;

/**
 * Created by wangdiandian on 2017/4/21.
 */
public class SynchronizeGiftCard {

    public static String create(String jsonData) {
//        jsonData = UriEncoder.encode(jsonData);
        int i = 0;
        String code = null;
        while (i < 2) {
            try {
                i++;
                HttpBean httpBean = new HttpBean(Dictionary.MALL_HOST + "/goods/giftCard/giftCard/itf", RequestMethod.post);
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
    public static String boundGiftCard(String jsonData) {
//        jsonData = UriEncoder.encode(jsonData);
        int i = 0;
        String code = null;
        while (i < 2) {
            try {
                i++;
                HttpBean httpBean = new HttpBean(Dictionary.MALL_HOST + "/goods/giftCard/boundGiftCard/itf", RequestMethod.put);
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
    public static String spendGiftCard(String jsonData) {
//        jsonData = UriEncoder.encode(jsonData);
        int i = 0;
        String code = null;
        while (i < 2) {
            try {
                i++;
                HttpBean httpBean = new HttpBean(Dictionary.MALL_HOST + "/goods/giftCard/spendGiftCard", RequestMethod.put);
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
    public static String synchronizeHistory(String jsonData) {
//        jsonData = UriEncoder.encode(jsonData);
        int i = 0;
        String code = null;
        while (i < 2) {
            try {
                i++;
                HttpBean httpBean = new HttpBean(Dictionary.MALL_HOST+ "/goods/giftCard/synchronizeHistory/itf", RequestMethod.post);
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

//    public static String storage(String jsonData) {//电子卡入库
////        jsonData = UriEncoder.encode(jsonData);
//        int i = 0;
//        String code = null;
//        while (i < 2) {
//            try {
//                i++;
//                HttpBean httpBean = new HttpBean("http://api.hzbuvi.com/inventory/oms/stock", RequestMethod.post);
//                httpBean.addParameter("jsonData", jsonData);
//                httpBean.run();
//                String result = httpBean.getResponseContent();
//                if (result != null) {
//                    code = ValueUtil.getFromJson(result, "code");
//                    break;
//                }
//            } catch (Exception e) {
//                continue;
//            }
//
//        }
//        return code;
//    }

}
