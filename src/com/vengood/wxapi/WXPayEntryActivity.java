package com.vengood.wxapi;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.vengood.R;
import com.vengood.activity.MainActivity;
import com.vengood.application.VSApplication;
import com.vengood.dialog.LoadingDialog;
import com.vengood.http.HttpEvent;
import com.vengood.http.HttpReqListener;
import com.vengood.http.manage.NetWorkUtil;
import com.vengood.util.Constants;
import com.vengood.util.EasyLogger;
import com.vengood.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 *类名：WXPayEntryActivity.java
 *注释：微信支付结果
 *日期：2016年1月26日
 *作者：王超
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler, HttpReqListener {
	private Context mContext = null;
	private IWXAPI mIWXAPI = null;
	
	private LoadingDialog mLoading = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = WXPayEntryActivity.this;
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
			showTipDialog(String.valueOf(resp.errCode), VSApplication.getInstance().mLogInfo);
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public void onUpdate(HttpEvent event, Object obj) {
		String tip = null;
		Intent intent = null;
		switch (event) {
		case EVENT_GET_ORDER_ID_SUCCESS:
			VSApplication.getInstance().isWeiXinResult = true;
			mLoading.dismiss();
			intent = new Intent(mContext, MainActivity.class);
			intent.putExtra("Result_Url", (String)obj);
			Utils.toLeftAnim(mContext, intent, true);
			break;
		case EVENT_GET_ORDER_ID_FAIL:
			tip = (String)obj;
			EasyLogger.i("CollinWang", "getOrderId failed=" + tip);
			break;
		case EVENT_GET_SHOP_CAR_URL_SUCCESS:
			VSApplication.getInstance().isWeiXinResult = true;
			mLoading.dismiss();
			intent = new Intent(mContext, MainActivity.class);
			intent.putExtra("Result_Url", (String)obj);
			Utils.toLeftAnim(mContext, intent, true);
			break;
		case EVENT_GET_SHOP_CAR_URL_FAIL:
			tip = (String)obj;
			EasyLogger.i("CollinWang", "getShopCarUrl failed=" + tip);
			break;
		
		}
	}
	
	private void showTipDialog(String errCode, String tip) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("支付结果");
        if (errCode.equals("0")) {
        	tip = "支付成功";
        	builder.setMessage(tip);
        	builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	mLoading = new LoadingDialog(mContext);
                    mLoading.showDialog("加载中");
                	NetWorkUtil.getOrderId(WXPayEntryActivity.this);
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        } else if (errCode.equals("-1")) {
        	tip = "支付失败";
        	builder.setMessage(tip);
        	builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	mLoading = new LoadingDialog(mContext);
                    mLoading.showDialog("加载中");
                	NetWorkUtil.getShopCarUrl(WXPayEntryActivity.this);
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        } else if (errCode.equals("-2")) {
        	tip = "支付取消";
        	builder.setMessage(tip);
        	builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	mLoading = new LoadingDialog(mContext);
                    mLoading.showDialog("加载中");
                	NetWorkUtil.getShopCarUrl(WXPayEntryActivity.this);
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }
	
}
