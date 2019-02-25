package com.yesmywine.util.basic;

import com.yesmywine.util.error.yesmywineException;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by by on 2017/8/4.
 */
public class UserUtil {

    public static String getUserId(HttpServletRequest request) throws yesmywineException {
        String userId = (String) request.getAttribute("userId");
        if(userId==null){
            ValueUtil.isError("非法用户");
        }
        return userId;
    }
}
