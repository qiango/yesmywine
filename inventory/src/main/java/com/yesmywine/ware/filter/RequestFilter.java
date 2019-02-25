package com.yesmywine.ware.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by SJQ on 2017/3/23.
 */
public class RequestFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            String header = request.getHeader("Authorization");
            // 获取当前页面文件名此处url为：/Gzlkh/login.jsp
            String url = request.getRequestURI();
            String method = request.getMethod();
            String user = "";

            chain.doFilter(request, response);
        } catch (Exception e) {

        }
    }

    @Override
    public void destroy() {
        System.out.println("销毁");
    }
}
