package com.vengood.application;

import java.util.LinkedList;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;
import com.vengood.http.HttpClient;
import com.vengood.util.Settings;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 *类名：VSApplication.java
 *注释：程序启动
 *日期：2015年12月31日
 *作者：王超
 */
public class VSApplication extends Application {
	private static VSApplication mVSApplication = null;
	public Context mContext = null;
	public String mLocation = null;
	
	public boolean isWeiXinResult = false;
	
	public String mProvince = null;// 当前省份
	public String mCity = null;// 当前城市
	
	public double mLatitude = 0;// 纬度
	public double mLongitude = 0;// 经度
	
	// 装载打开Act
    private LinkedList<Activity> mActList = new LinkedList<Activity>();
	public String mLogInfo = null;
	
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
        // UMengShareRegister
        PlatformConfig.setWeixin("wx967daebe835fbeac", "5bb696d9ccd75a38c8a0bfe0675559b3");
        //PlatformConfig.setSinaWeibo("3921700954","04b48b094faeb16683c32669824ebdad");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba"); 
	};
	
	// 添加Activity到集合
    public void addActivity(Activity activity) {
        if (mActList == null) {
            mActList = new LinkedList<Activity>();
        }
        mActList.add(activity);
    }

    // 结束所有打开Activity
    public void exit() {
        if (mActList == null) {
            return;
        }
        for (Activity activity : mActList) {
            if (activity != null) {
                activity.finish();
            }
        }
        mActList.clear();
        mActList = null;
    }
}
