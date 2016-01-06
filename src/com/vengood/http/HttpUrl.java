package com.vengood.http;

/**
 *类名：HttpUrl.java
 *注释：网络请求地址工具类
 *日期：2016年1月2日
 *作者：王超
 */
public class HttpUrl {
	public static String Server_Uri = "http://test.vengood.com/";
    public static String Login_Url = "mobile.php";

    public static String getLoginUrl() {
        return Server_Uri + Login_Url;
    }
}
