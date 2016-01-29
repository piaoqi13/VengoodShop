package com.vengood.util;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.vengood.application.VSApplication;

import android.content.Context;

/**
 *类名：AMapLocationUtil.java
 *注释：高德定位工具类
 *日期：2016年1月5日
 *作者：王超
 */
public class AMapLocationUtil implements AMapLocationListener {
	private static AMapLocationUtil mAMapLocationUtil = null;
	
	private AMapLocationClient mLocationClient = null;
	private AMapLocationClientOption mLocationOption = null;
	
	private AMapLocationUtil() {
		//ToDo
	}
	
	public static AMapLocationUtil getSingleInstance() {
		if (mAMapLocationUtil == null) {
			mAMapLocationUtil = new AMapLocationUtil();
		}
		return mAMapLocationUtil;
	}
	
	public void startLocation(Context context) {
		mLocationClient = new AMapLocationClient(context);
		mLocationOption = new AMapLocationClientOption();
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		mLocationOption.setOnceLocation(true);
		mLocationOption.setNeedAddress(true);
		mLocationClient.setLocationListener(this);
		mLocationClient.setLocationOption(mLocationOption);
		mLocationClient.startLocation();
	}
	
	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {
			VSApplication.getInstance().mLocation = location.getAddress();
			EasyLogger.i("CollinWang", "location=" + VSApplication.getInstance().mLocation);
			//EasyLogger.i("CollinWang", "location=" +location.toString());
		} else {
			EasyLogger.i("CollinWang", "location=null");
		}
	}
	
	public void stopLocation() {
		if (mLocationClient != null) {
			mLocationClient.onDestroy();
			mLocationClient = null;
			mLocationOption = null;
		}
	}
	
}
