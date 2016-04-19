package com.vengood.util;

import com.vengood.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 *类名：Utils.java
 *注释：工具类
 *日期：2016年1月2日
 *作者：王超
 */
public class Utils {
    public static void toLeftAnim(Context mContext, Intent intent, boolean isFinished) {
        Activity mActivity = (Activity) mContext;
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.right_to_current, R.anim.curent_to_left);
        if (isFinished) {
            mActivity.finish();
        }
    }

    public static void toRightAnim(Context mContext, Intent intent, boolean isFinished) {
        Activity mActivity = (Activity) mContext;
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.left_to_current, R.anim.curent_to_right);
        if (isFinished) {
            mActivity.finish();
        }
    }

    public static void toRightAnim(Context mContext) {
        Activity mActivity = (Activity) mContext;
        mActivity.finish();
        mActivity.overridePendingTransition(R.anim.left_to_current, R.anim.curent_to_right);
    }
    
    // 检查网络是否可用
    public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectManage = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectManage != null) {
			NetworkInfo[] info = connectManage.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
    
    public static String getPhoneNumber(Context context) {
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyMgr.getLine1Number();
	}
    
    public static String getDeviceId(Context context) {
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyMgr.getDeviceId();
	}
}
