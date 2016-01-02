package com.vengood.http;

/**
 *类名：HttpReqListener.java
 *注释：网络请求事件回调
 *日期：2016年1月2日
 *作者：王超
 */
public interface HttpReqListener {
    void onUpdate(HttpEvent event, Object obj);
}
