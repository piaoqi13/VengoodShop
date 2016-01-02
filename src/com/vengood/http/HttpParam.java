package com.vengood.http;

import com.loopj.android.http.RequestParams;

/**
 *类名：HttpParams.java
 *注释：网络请求参数工具类
 *日期：2016年1月2日
 *作者：王超
 */
public class HttpParam {
    public static RequestParams getLoginParams(String bodyId) {
        RequestParams params = new RequestParams();
        params.put("bodyId", bodyId);
        return params;
    }
}
