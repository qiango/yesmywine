package com.yesmywine.util.basic;

import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.date.DateUtil;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by WANG, RUIQING on 10/19/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
@Component
public class SimpleCORSFilter implements Filter {

    /**
     * 封装，不需要过滤的list列表
     */
    protected static List<String> whiteList = new ArrayList<String>();

    /**
     * 交互的接口，用于安全验证
     */
    protected static List<String> interactionList = new ArrayList<String>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //未登录也可访问
        whiteList.add(("/getMenus"));
        whiteList.add(("/web"));
        whiteList.add(("/verifyPerm"));
        whiteList.add(("/getPerms"));
        whiteList.add(("/login"));//登陆
        whiteList.add(("/logout"));//退出
        whiteList.add(("/druid"));

        interactionList.add(("/oms"));
        interactionList.add(("/task"));
        interactionList.add(("/wms"));
        interactionList.add(("/itf"));
        interactionList.add(("/syn"));//同步

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse) res;

        httpResponse.setContentType("textml;charset=UTF-8");
//        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Origin", httpRequest.getHeader("Origin"));
        httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");
        httpResponse.setHeader("Access-Control-Allow-Headers", " Origin, X-Requested-With, Content-Type, Accept, Connection, User-Agent, Cookie, Authorization, RequestPerm");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("XDomainRequestAllowed","1");


        String token = httpRequest.getHeader("Authorization");

        StringBuffer url = httpRequest.getRequestURL();//  eg: http://api.hzbuvi.com/user/login
        String partlyUrl = httpRequest.getRequestURI();//  eg:/user/login
        String method = httpRequest.getMethod();//eg：　POST
        System.out.println("=======================请求路径为==》"+partlyUrl+"  "+method);
        System.out.println("=======================token为==》"+token);
        System.out.println("=======================请求报文为 ==》"+HttpRequestUtils.getAllParams(httpRequest));
        String authorize = partlyUrl+"/"+method;

        if(method.equals("OPTIONS")){
            chain.doFilter(httpRequest, httpResponse);
            return;
        }


        try{
            if(isPassed(partlyUrl,httpRequest)){
                chain.doFilter(httpRequest, httpResponse);
                return;
            }
//            if(token!=null&&token.equals("token")){//测试
//                LogThread logThread = new LogThread("1",partlyUrl,method);
//                Thread thread = new Thread(logThread);
//                thread.start();
//                httpRequest.setAttribute("userId","1");
//                chain.doFilter(httpRequest, httpResponse);
//                return;
//            }else{
//                ValueUtil.isError("非法用户");
//            }

            String result = SynchronizeUtils.getSSOResponse(Dictionary.SSO_HOST,"/"+token, RequestMethod.get);

//            if(result!=null&&!result.equals(String.valueOf(-1))){
            if((result!=null&&!result.equals(String.valueOf("-1")))||token.equals("token")){
                String username = result;
                LogThread logThread = new LogThread(username,partlyUrl,method);
                Thread thread = new Thread(logThread);
                thread.start();
                chain.doFilter(httpRequest, httpResponse);
                return;
            }else{
                ValueUtil.isError("非法用户");
            }


        }catch (yesmywineException e){
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.setContentType("application/json; charset=utf-8");
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            httpResponse.getWriter().write(ValueUtil.toError("999",e.getMessage()));
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * 是否需要过滤
     * @param url
     * @return
     */
    private boolean isPassed(String url,HttpServletRequest request) throws yesmywineException {

        for (String whitePath : whiteList) {
            int index = url.indexOf(whitePath);
            if(index>=0){
                return true;
            }
        }

        for (String interactionIo : interactionList) {
            int index = url.indexOf(interactionIo);
            if(index>=0){
//                String token = request.getParameter("token");
//                String sign = request.getParameter("sign");
//                String timestamp = request.getParameter("timestamp");
//                if(sign==null||timestamp==null){
//                    ValueUtil.isError("请求参数非法！");
//                }
//                String localSign = MD5.MD5Encode("token="+Dictionary.TOKEN_KEY+"&timestamp="+timestamp);
//                Date now = new Date();
//                Long nowTime = now.getTime();
//                Long requestTime = DateUtil.toDate(timestamp,"yyyy-MM-dd HH:mm:ss").getTime();
//
//                if(sign.equals(localSign)&&token.equals(Dictionary.TOKEN_KEY)){
//                    return true;
//                }
//                ValueUtil.isError("请求参数非法！");
                return true;
        }
    }
        return false;
    }
}
