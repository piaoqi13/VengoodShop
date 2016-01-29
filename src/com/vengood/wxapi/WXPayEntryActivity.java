package com.vengood.wxapi;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.vengood.R;
import com.vengood.util.Constants;
import com.vengood.util.EasyLogger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

/**
 *类名：WXPayEntryActivity.java
 *注释：微信支付结果
 *日期：2016年1月26日
 *作者：王超
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	private IWXAPI mIWXAPI = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_result);
		mIWXAPI = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        mIWXAPI.handleIntent(getIntent(), this);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        mIWXAPI.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		//ToDo
	}

	@Override
	public void onResp(BaseResp resp) {
		EasyLogger.i("CollinWang", "errCode=" + resp.errCode);
		EasyLogger.i("CollinWang", "errStr=" + resp.errStr);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("温馨提示");
			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
			builder.show();
		}
	}
	
}
