package com.vengood.receiver;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.vengood.util.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *类名：AppRegister.java
 *注释：微信注册广播
 *日期：2016年1月30日
 *作者：王超
 */
public class AppRegister extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final IWXAPI msgApi = WXAPIFactory.createWXAPI(context, null);
		msgApi.registerApp(Constants.APP_ID);
	}

}
