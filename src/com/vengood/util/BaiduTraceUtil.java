package com.vengood.util;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.Trace;

import android.content.Context;
import android.os.Looper;

/**
*  类名：BaiduTraceUtil.java
 * 注释：百度鹰眼工具类
 * 日期：2016年4月15日
 * 作者：王超
 */
public class BaiduTraceUtil {
	private static BaiduTraceUtil mBaiduTraceUtil = null;
	
	protected static OnEntityListener entityListener = null;
	protected static OnTrackListener mTrackListener = null;
    protected static OnStartTraceListener mStartTraceListener = null;
    protected static OnStopTraceListener mStopTraceListener = null;
    private int mGatherInterval = 2 * 60;
    private int mPackInterval = 4 * 60;
    
	protected static Trace mTrace = null;
	protected static String mEntityName = null;
	protected static long mServiceId = 114602;
	private int mTraceType = 2;
	
	protected static LBSTraceClient mClient = null;
	
	private BaiduTraceUtil() {
		initListener();
	}
	
	public static  BaiduTraceUtil getInstance() {
		if (mBaiduTraceUtil == null) {
			mBaiduTraceUtil = new BaiduTraceUtil();
		}
		return mBaiduTraceUtil;
	}
	
	public void initTrace(Context context) {
		mClient = new LBSTraceClient(context);
		mClient.setLocationMode(LocationMode.High_Accuracy);
		mEntityName = Utils.getPhoneNumber(context) + Utils.getDeviceId(context);
		mTrace = new Trace(context, mServiceId, mEntityName, mTraceType);
		// 采集周期
		mClient.setInterval(mGatherInterval, mPackInterval);
		// 请求协议
		mClient.setProtocolType(0);
	}
	
	public void startTrace(Context context) {
		initTrace(context);
		mClient.setOnTrackListener(mTrackListener);
		mClient.startTrace(mTrace, mStartTraceListener);
	}
	
	public void stopTrace(Context context) {
		initTrace(context);
		mClient.stopTrace(mTrace, mStopTraceListener);
	}
	
	public void destroyTrace() {
		mClient.onDestroy();
	}
	
	private void initOnTrackListener() {
		mTrackListener = new OnTrackListener() {
			@Override
			public void onRequestFailedCallback(String msg) {
				Looper.prepare();
				EasyLogger.i("CollinWang", "Track请求失败回调接口消息：" + msg);
				Looper.loop();
			}

			@Override
			public void onQueryHistoryTrackCallback(String msg) {
				super.onQueryHistoryTrackCallback(msg);
			}
		};
	}
	
    private void initOnStartTraceListener() {
        mStartTraceListener = new OnStartTraceListener() {
            public void onTraceCallback(int code, String msg) {
            	EasyLogger.i("CollinWang", "开启轨迹服务回调接口消息[消息setOnTrackListener编码：" + code + "，消息内容：" + msg + "]" + Integer.valueOf(code));
            }

            public void onTracePushCallback(byte code, String msg) {
            	EasyLogger.i("CollinWang", "轨迹服务推送接口消息[消息类型：" + code + "，消息内容：" + msg + "]");
            }
        };
    }

	private void initOnStopTraceListener() {
		mStopTraceListener = new OnStopTraceListener() {
			public void onStopTraceSuccess() {
				EasyLogger.i("CollinWang", "停止百度鹰眼轨迹成功" + Integer.valueOf(1));
			}

			public void onStopTraceFailed(int code, String msg) {
				EasyLogger.i("CollinWang", "停止百度鹰眼轨迹失败 [错误编码：" + code + "，消息内容：" + msg);
			}
		};
	}
	
	private void initListener() {
		initOnTrackListener();
        initOnStartTraceListener();
        initOnStopTraceListener();
    }
	
}
