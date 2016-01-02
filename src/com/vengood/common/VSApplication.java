package com.vengood.common;

import com.umeng.analytics.MobclickAgent;
import com.vengood.http.HttpClient;
import com.vengood.util.Settings;

import android.app.Application;
import android.content.Context;

/**
 *类名：VSApplication.java
 *注释：程序启动
 *日期：2015年12月31日
 *作者：王超
 */
public class VSApplication extends Application {
	public static Context mContext = null;
    private static VSApplication mVSApplication = null;
	
    public static VSApplication getInstance() {
        return mVSApplication;
    }
    
    @Override
	public void onCreate() {
		super.onCreate();
		mVSApplication = this;
        mContext = getApplicationContext();
        // Software Storage
        Settings.initPreferences(mContext);
        // HTTP
        HttpClient.getInstance().init(this);
        // UMeng
		MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setCatchUncaughtExceptions(true);
	};
}
