package com.yesmywine.util.basic;

import com.yesmywine.httpclient.bean.RequestMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by by on 2017/7/3.
 */
public class LogThread implements Runnable{

    private String username;

    private String operation;

    private String method;
    public LogThread(String username, String operation, String method){
        this.username = username;
        this.operation = operation;
        this.method = method;
    }

    public void run() {
        OperateLogger.doLog(username,operation);
    }
}
