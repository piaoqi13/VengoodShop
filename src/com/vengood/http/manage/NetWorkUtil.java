package com.vengood.http.manage;

import org.json.JSONException;
import org.json.JSONObject;

import com.vengood.http.HttpClient;
import com.vengood.http.HttpEvent;
import com.vengood.http.HttpParam;
import com.vengood.http.HttpReqListener;
import com.vengood.http.HttpUrl;

import android.util.Log;

/**
 *类名：NetWorkUtil.java
 *注释：网络请求工具类
 *日期：2016年1月2日
 *作者：王超
 */
public class NetWorkUtil {
	public static void login(final HttpReqListener listener, final String account, final String password) {
        HttpClient.getInstance().doWork(HttpUrl.getLoginUrl(), HttpParam.getLoginParam(account, password), new HttpClient.HttpCallBack() {
            @Override
            public void succeed(int statusCode, String content) {
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    String flag = jsonObject.optString("status");
                    String tip = null;
                    if (flag.equals("1")) {
                        listener.onUpdate(HttpEvent.EVENT_LOGIN_SUCCESS, null);
                    } else {
                    	if (flag.equals("-1")) {
                    		tip = "账号为空";
                    	} else if (flag.equals("-2")) {
                    		tip = "密码为空";
                    	} else if (flag.equals("-3")) {
                    		tip = "账号或密码错误";
                    	}
                    	listener.onUpdate(HttpEvent.EVENT_LOGIN_FAIL, tip);
                    }
                } catch (JSONException e) {
                    Log.e("NetWorkUtil", "loginJsonCatch=", e);
                    listener.onUpdate(HttpEvent.EVENT_LOGIN_FAIL, e.toString());
                }
            }
            
            @Override
            public void failed(Throwable error, String content) {
                listener.onUpdate(HttpEvent.EVENT_LOGIN_FAIL, content);
            }
        });
    }
	
	public static void getOrderId(final HttpReqListener listener) {
        HttpClient.getInstance().doWork(HttpUrl.getOrderIdUrl(), HttpParam.getOrderIdParam(), new HttpClient.HttpCallBack() {
            @Override
            public void succeed(int statusCode, String content) {
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    String flag = jsonObject.optString("code");
                    if (flag.equals("0")) {
                        listener.onUpdate(HttpEvent.EVENT_GET_ORDER_ID_SUCCESS, jsonObject.optString("url"));
                    } else {
                    	listener.onUpdate(HttpEvent.EVENT_GET_ORDER_ID_FAIL, jsonObject.optString("msg"));
                    }
                } catch (JSONException e) {
                    Log.e("NetWorkUtil", "getOrderId JsonCatch=", e);
                    listener.onUpdate(HttpEvent.EVENT_GET_ORDER_ID_FAIL, e.toString());
                }
            }
            
            @Override
            public void failed(Throwable error, String content) {
                listener.onUpdate(HttpEvent.EVENT_GET_ORDER_ID_FAIL, content);
            }
        });
    }
	
	public static void getShopCarUrl(final HttpReqListener listener) {
        HttpClient.getInstance().doWork(HttpUrl.getShopCarUrl(), HttpParam.getShopCarParam(), new HttpClient.HttpCallBack() {
            @Override
            public void succeed(int statusCode, String content) {
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    String flag = jsonObject.optString("code");
                    if (flag.equals("0000")) {
                        listener.onUpdate(HttpEvent.EVENT_GET_SHOP_CAR_URL_SUCCESS, jsonObject.optString("url"));
                    } else {
                    	listener.onUpdate(HttpEvent.EVENT_GET_SHOP_CAR_URL_FAIL, jsonObject.optString("msg"));
                    }
                } catch (JSONException e) {
                    Log.e("NetWorkUtil", "getShopCarUrl JsonCatch=", e);
                    listener.onUpdate(HttpEvent.EVENT_GET_SHOP_CAR_URL_FAIL, e.toString());
                }
            }
            
            @Override
            public void failed(Throwable error, String content) {
                listener.onUpdate(HttpEvent.EVENT_GET_SHOP_CAR_URL_FAIL, content);
            }
        });
    }
}
