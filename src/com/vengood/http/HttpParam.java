package com.vengood.http;

import com.loopj.android.http.RequestParams;
import com.vengood.util.MD5Tool;
import com.vengood.util.Settings;

/**
 *类名：HttpParams.java
 *注释：网络请求参数工具类
 *日期：2016年1月2日
 *作者：王超
 */
public class HttpParam {
    public static RequestParams getLoginParam(String account, String pwd) {
        RequestParams params = new RequestParams();
        params.put("act", "module");
        params.put("name", "bj_qmxk");
        params.put("do", "login");
        params.put("weid", "3");
        params.put("app_login", "yes");
        params.put("account", account);
        params.put("pwd", pwd);
        return params;
    }
    
    public static RequestParams getOrderIdParam() {
        RequestParams params = new RequestParams();
        params.put("order_id", Settings.getString("order_id", "", true));
        params.put("sign", MD5Tool.string2MD5(Settings.getString("order_id", "", true) + String.valueOf(System.currentTimeMillis()/1000) + "WXPAY_F3&%^532((*43%%$7~!@*&"));
        params.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
        return params;
    }
}
