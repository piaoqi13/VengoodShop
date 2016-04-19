package com.vengood.util;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.vengood.application.VSApplication;

import android.content.Context;

/**
*  类名：BaiduMapUtil.java
 * 注释：百度定位工具类
 * 日期：2016年4月15日
 * 作者：王超
 */
public class BaiduMapUtil {
	private static BaiduMapUtil mBaiduMapUtil = null;
	private LocationClient mLocationClient = null;
	private LocationListener mLocationListener = null;
	
	private BaiduMapUtil() {
		// ToDo
	}
	
	public static  BaiduMapUtil getInstance() {
		if (mBaiduMapUtil == null) {
			mBaiduMapUtil = new BaiduMapUtil();
		}
		return mBaiduMapUtil;
	}
	
	public void startLocation(Context context) {
		mLocationClient = new LocationClient(context);
        mLocationListener = new LocationListener();
        mLocationClient.registerLocationListener(mLocationListener);
        // 设置属性
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIgnoreKillProcess(true);
        option.setEnableSimulateGps(false);
        mLocationClient.setLocOption(option);
        // 定位开始
        mLocationClient.start();
	}
	
	public class LocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
        	// 拿到当前省份城市名称
        	VSApplication.getInstance().mProvince = location.getProvince();
        	VSApplication.getInstance().mCity = location.getCity();
        	VSApplication.getInstance().mLatitude = location.getLatitude();
        	VSApplication.getInstance().mLongitude = location.getLongitude();
        	EasyLogger.i("CollinWang", "Latitude=" + location.getLatitude() + "；Longitude="+ location.getLongitude());
        	// 停止
        	stopLocation();
        }
    }
	
	public void stopLocation() {
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
	}
	
}
