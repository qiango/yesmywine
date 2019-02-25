package com.yesmywine.util.basic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yesmywine.httpclient.bean.HttpBean;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Created by SJQ on 2017/4/21.
 */
public class SynchronizeUtils {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizeUtils.class);
    private static String itf = "/itf";

    public static String getResult(String host, String url, RequestMethod method, String jsonData) {
        int i = 0;
        String result = null;
        while (i < 2) {
            try {
                i++;
                HttpBean httpBean = new HttpBean(host + url, method);
                if (jsonData != null && !jsonData.equals("")) {
                    httpBean.addParameter("jsonData", jsonData);
                    if (url.indexOf(itf) >= 0) {
                        addVeritySign(httpBean);
                    }
                    httpBean.run();
                }
                result = httpBean.getResponseContent();
                if (result != null) {
                    String code = JSON.parseObject(result).getString("code");
                    if (code != null && (code.equals("201") || code.equals("204") || code.equals("200") || code.equals("500"))) {
                        break;
                    } else {
                        result = null;
                    }
                }
            } catch (Exception e) {
                continue;
            }

        }
        return result;
    }

    public static String paramsCode(String host, String url, RequestMethod method, Map<String, Object> params) {
        int i = 0;
        String code = null;
        while (i < 2) {
            try {
                i++;
                HttpBean httpBean = new HttpBean(host + url, method);
                if (params != null && !params.equals("")) {
                    httpBean.addParameter("userName", params.get("userName"));
                    httpBean.addParameter("bean", params.get("bean"));
                    httpBean.addParameter("status", params.get("status"));
                    httpBean.addParameter("channelCode", params.get("channelCode"));
                    httpBean.addParameter("orderNumber", params.get("orderNumber"));
                    httpBean.addParameter("returnBean", params.get("returnBean"));
                    httpBean.addParameter("consumeBean", params.get("consumeBean"));
                    httpBean.addParameter("newBeans", params.get("newBeans"));
                    httpBean.addParameter("point", params.get("point"));
                    httpBean.addParameter("userId", params.get("userId"));
                    httpBean.run();
                }
                String result = httpBean.getResponseContent();
                if (result != null) {
                    code = JSON.parseObject(result).getString("code");
                    if (code != null && (code.equals("201") || code.equals("204") || code.equals("200") || code.equals("500"))) {
                        break;
                    }
                }
            } catch (Exception e) {
                continue;
            }

        }
        return code;
    }

    public static String getResult(String host, String url, RequestMethod method, Map<String, Object> paramsData, HttpServletRequest request) {
        int i = 0;
        String result = null;
        while (i < 2) {
            try {
                i++;
                HttpBean httpBean = new HttpBean(host + url, method);
                if (paramsData != null && paramsData.size() > 0) {
                    Iterator it = paramsData.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, Object> p = (Map.Entry) it.next();
                        String key = p.getKey();
                        Object value = p.getValue();
                        httpBean.addParameter(key, value);
                    }
                }
                if (request != null) {
                    Enumeration headerNames = request.getHeaderNames();
                    while (headerNames.hasMoreElements()) {
                        String key = (String) headerNames.nextElement();
                        if (key.equals("Authorization") || key.equals("RequestPerm")) {
                            String value = request.getHeader(key);
                            httpBean.addHeader(key, value);
                        }
                    }
                }
                if (url.indexOf(itf) >= 0) {
                    addVeritySign(httpBean);
                }
                httpBean.run();
                result = httpBean.getResponseContent();
                if (result != null) {
                    String code = JSON.parseObject(result).getString("code");
                    if (code != null) {
                        break;
                    } else {
                        result = null;
                    }
                }
            } catch (Exception e) {
                continue;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String getCode(String host, String url, RequestMethod method, Map<String, Object> paramsData, HttpServletRequest request) {
        int i = 0;
        String code = null;
        while (i < 2) {
            try {
                i++;
                HttpBean httpBean = new HttpBean(host + url, method);
                if (paramsData != null && paramsData.size() > 0) {
                    Iterator it = paramsData.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, Object> p = (Map.Entry) it.next();
                        String key = p.getKey();
                        Object value = p.getValue();
                        httpBean.addParameter(key, value);
                    }
                }
                if (request != null) {
                    Enumeration headerNames = request.getHeaderNames();
                    while (headerNames.hasMoreElements()) {
                        String key = (String) headerNames.nextElement();
                        if (key.equals("Authorization") || key.equals("RequestPerm")) {
                            String value = request.getHeader(key);
                            httpBean.addHeader(key, value);
                        }
                    }
                }
                if (url.indexOf(itf) >= 0) {
                    addVeritySign(httpBean);
                }
                httpBean.run();
                String result = httpBean.getResponseContent();
                if (result != null) {
                    JSONObject obj = JSON.parseObject(result);
                    code = obj.getString("code");
                }

                if (result != null) {
                    code = JSON.parseObject(result).getString("code");
                    if (code != null) {
                        break;
                    }
                }
            } catch (Exception e) {
                continue;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return code;
    }


    public static String getOmsResult(String host, String url, RequestMethod method, String paramName, String jsonData) {
        logger.info("================================调用OMS，参数为==》" + jsonData);
        DataOutputStream wr = null;
        DataInputStream rd = null;
        HttpURLConnection conn = null;
        try {
            int i = 0;
            while (i < 2) {
                URL requestUrl = new URL(host + url);
                conn = (HttpURLConnection) requestUrl.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Charset", "utf-8");
                conn.setRequestMethod("POST");
                conn.connect();
                wr = new DataOutputStream(conn.getOutputStream());
                wr.write(jsonData.getBytes("utf-8"));
                wr.flush();
                DataInputStream dis = new DataInputStream(conn.getInputStream());
                byte[] aryZlib = streamToByteArray(dis);
                if (dis != null) {
                    dis.close();
                    dis = null;
                }
                logger.info("================================调用OMS，响应报文为==》" + new String(aryZlib, "utf-8"));
                String response = new String(aryZlib, "utf-8");
                if (response != null && !response.equals("")) {
                    return response;
                }
                i++;
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rd != null) {
                try {
                    rd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                rd = null;
            }
            if (wr != null) {
                try {
                    wr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                wr = null;
            }
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        return null;
    }

    public static byte[] streamToByteArray(InputStream in) throws IOException {
        byte[] buf = new byte[100];
        byte[] dest = new byte[0];
        int len = -1;
        while ((len = in.read(buf)) != -1) {
            byte[] tmp = new byte[dest.length + len];
            System.arraycopy(dest, 0, tmp, 0, dest.length);
            System.arraycopy(buf, 0, tmp, dest.length, len);
            dest = tmp;
        }
        return dest;
    }

    public static String getCode(String host, String url, String jsonData, RequestMethod method) {
        int i = 0;
        String code = null;
        while (i < 2) {
            try {
                i++;
                HttpBean httpBean = new HttpBean(host + url, method);
                httpBean.addParameter("jsonData", jsonData);
                if (url.indexOf(itf) >= 0) {
                    addVeritySign(httpBean);
                }
                httpBean.run();
                String result = httpBean.getResponseContent();
                if (result != null) {
                    code = ValueUtil.getFromJson(result, "code");
                    if (code != null && (code.equals("201") || code.equals("204") || code.equals("200") || code.equals("500"))) {
                        break;
                    }
                }
            } catch (Exception e) {
                continue;
            }

        }
        return code;
    }

    public static String getSSOResponse(String host, String token, RequestMethod method) {
        int i = 0;
        String result = null;
        while (i < 2) {
            try {
                i++;
                HttpBean httpBean = new HttpBean(host + token, method);
                httpBean.run();
                result = httpBean.getResponseContent();
                if (result != null) {
                    break;
                }
            } catch (Exception e) {
                continue;
            }

        }
        logger.info("========================单点登录验证结果："+result);
        return result;
    }


    public static String getWmsResult(String requestUrl, String method,String messageid, String jsonData) {

        // 返回的结果
        String result = "";
        String errMsg = "";
        // 读取响应输入流
        BufferedReader in = null;
        // post参数写入流
        PrintWriter out = null;
        // 处理请求参数
        StringBuffer sb = new StringBuffer();
        // 编码之后的参数
        String params = "method="+method+"&client_customerid=FLUXWMSJSON&client_db=FLUXWMSJSONDB&messageid="+messageid+"&apptoken=80AC1A3F-F949-492C-A024-7044B28C8025&appkey=test&sign=NWY5ZDLKYZZHMJVKZTC0OTHLYZQ0YZE3YJG2YJHHMTM%3D&data=";
        try {
            logger.info("============================调用WMS，参数为==》" +requestUrl+"  " + params+jsonData);
            params += URLEncoder.encode(jsonData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int i = 0;
        while (i < 2) {
            try {
                // 编码请求参数
                URL pageUrl = new URL(requestUrl);
                HttpURLConnection httpConn = (HttpURLConnection) pageUrl.openConnection();
                httpConn.setRequestProperty("Accept", "*/*");
                httpConn.setRequestProperty("Connection", "Keep-Alive");
                httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
                // 设置POST方式
                httpConn.setDoInput(true);
                httpConn.setDoOutput(true);
            /*设置连接超时时间10秒*/
                httpConn.setConnectTimeout(10000);
            /*设置读取连接响应数据超时时间10秒*/
                httpConn.setReadTimeout(10000);
            /*打开连接*/
                httpConn.connect();
                // 获取HttpURLConnection对象对应的输出流
                out = new PrintWriter(httpConn.getOutputStream());
                // 发送请求参数
                out.write(params);
                //logger.info("params="+params);
                out.flush();
                out.close();
                if (HttpURLConnection.HTTP_OK != httpConn.getResponseCode() &&
                        HttpURLConnection.HTTP_CREATED != httpConn.getResponseCode() &&
                        HttpURLConnection.HTTP_ACCEPTED != httpConn.getResponseCode()) {
                    // 定义BufferedReader输入流来读取URL的响应，设置编码方式
                    in = new BufferedReader(new InputStreamReader(httpConn.getErrorStream(), "UTF-8"));
                } else {
                    in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
                }
                String line = "";
                // 读取返回的内容
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                in.close();
                httpConn.disconnect();
                logger.info("第 " + i + " 次调用");
                if (result != null && !result.equals("")) {
                    result = URLDecoder.decode(result, "UTF-8");
                    break;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                errMsg = e.getMessage();
            } catch (ProtocolException e) {
                e.printStackTrace();
                errMsg = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                errMsg = e.getMessage();
            }
            i++;
        }

        try {
            logger.info("============================调用WMS，响应报文为==》" + URLDecoder.decode(result, "UTF-8"));
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void addVeritySign(HttpBean httpBean) {
//        httpBean.addParameter("token", Dictionary.TOKEN_KEY);
        String timestamp = DateUtil.toString(new Date(), "yyyy-MM-ddHH:mm:ss");
        httpBean.addParameter("timestamp", timestamp);
        String sign = MD5.MD5Encode("token=" + Dictionary.TOKEN_KEY + "&timestamp=" + timestamp);
        httpBean.addParameter("sign", sign);
    }
}
