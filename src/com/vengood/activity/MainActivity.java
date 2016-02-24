package com.vengood.activity;

import java.io.File;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.update.UmengUpdateAgent;
import com.vengood.R;
import com.vengood.application.VSApplication;
import com.vengood.dialog.TipDialog;
import com.vengood.http.HttpEvent;
import com.vengood.http.HttpReqListener;
import com.vengood.http.manage.NetWorkUtil;
import com.vengood.util.AMapLocationUtil;
import com.vengood.util.Constants;
import com.vengood.util.EasyLogger;
import com.vengood.util.Settings;
import com.vengood.util.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 *类名：MainActivity.java
 *注释：商城首页
 *日期：2015年12月31日
 *作者：王超
 */
public class MainActivity extends Activity implements OnClickListener, HttpReqListener {
	private final String mPageName = "MainActivity";
	private Context mContext = null;
	private TipDialog mTipDialog = null;
	private WebView mWvContent = null;
	private long mExitTime = 0;
	
	private ConnectivityManager mConnectManager = null;
	private NetworkInfo mNetworkInfo = null;
	
	private boolean isLoadImageAuto = true;
    private boolean isJavaScriptEnabled = true;
    private boolean isJavaScriptCanOpenWindowAuto = false;
    private boolean isRememberPassword = true;
    private boolean isSaveFormData = true;
    private boolean isLoadPageInOverviewMode = true;
    private boolean isUseWideViewPort = true;
    private boolean isLightTouch = false;
    private int minimumFontSize = 8;
    private int minimumLogicalFontSize = 8;
    private int defaultFontSize = 16;
    private int defaultFixedFontSize = 13;
    
    private String mCacheDatabase = null;
    private IWXAPI mIWXapi = null;
    private String mCachePath = null;
    
    private String mIndexUrl = null;// 商城首页
    private String mPayResult = null;// 结果地址
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_main);
		mPayResult = getIntent().getStringExtra("Result_Url");
		UmengUpdateAgent.update(this);
		mIndexUrl = OnlineConfigAgent.getInstance().getConfigParams(mContext, "url");
		Log.i("CollinWang", "online param=" + mIndexUrl);
		mIWXapi = WXAPIFactory.createWXAPI(this, null);
		mIWXapi.registerApp(Constants.APP_ID);
		AMapLocationUtil.getSingleInstance().startLocation(mContext);
		initView();
		autoLogin();
		initListener();
		if (mPayResult == null) {
			initData(mIndexUrl);
		} else {
			initData(mPayResult);
		}
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(this);
    }
	
	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		mConnectManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		mNetworkInfo = mConnectManager.getActiveNetworkInfo();
		mWvContent = (WebView) findViewById(R.id.wv_content);
        WebSettings webSettings = mWvContent.getSettings();
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUserAgentString(null);
        webSettings.setUseWideViewPort(isUseWideViewPort);
        webSettings.setLoadsImagesAutomatically(isLoadImageAuto);
        webSettings.setJavaScriptEnabled(isJavaScriptEnabled);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(isJavaScriptCanOpenWindowAuto);
        webSettings.setMinimumFontSize(minimumFontSize);
        webSettings.setMinimumLogicalFontSize(minimumLogicalFontSize);
        webSettings.setDefaultFontSize(defaultFontSize);
        webSettings.setDefaultFixedFontSize(defaultFixedFontSize);
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        webSettings.setLightTouchEnabled(isLightTouch);
        webSettings.setSaveFormData(isSaveFormData);
        webSettings.setSavePassword(isRememberPassword);
        webSettings.setLoadWithOverviewMode(isLoadPageInOverviewMode);
        webSettings.setNeedInitialFocus(false);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        // JS交互CollinWang2016.01.07
        webSettings.setUserAgentString("vengood_app_android"); //webSettings.getUserAgentString() + 
        mWvContent.addJavascriptInterface(new WebViewUtil(), "loginAndroid");
		// 缓存CollinWang2016.01.03
		webSettings.setDomStorageEnabled(true);
		webSettings.setAppCacheEnabled(true);
		webSettings.setAppCacheMaxSize(5 * 1024 * 1024);
		mCachePath = mContext.getDir("cache", Context.MODE_PRIVATE).getPath();
		EasyLogger.i("CollinWang", "cache=" + mCachePath);
		webSettings.setAppCachePath(mCachePath);
		webSettings.setAllowFileAccess(true);
		mWvContent.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		if (Utils.isNetworkAvailable(mContext)) {
			if (mNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
				EasyLogger.i("CollinWang", "移动网络");
			} else {
				webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
				EasyLogger.i("CollinWang", "wifi网络");
			}
		} else {
			webSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);
			EasyLogger.i("CollinWang", "没有网络");
		}

		webSettings.setDatabaseEnabled(true);
		mCacheDatabase = mContext.getDir("database", Context.MODE_PRIVATE).getPath();
		EasyLogger.i("CollinWang", "database=" + mCacheDatabase);
		webSettings.setDatabasePath(mCacheDatabase);
		mWvContent.setWebChromeClient(mChromeClient);
	}
	
	private WebChromeClient mChromeClient = new WebChromeClient() {
		@SuppressWarnings("deprecation")
		@Override
		public void onReachedMaxAppCacheSize(long spaceNeeded, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
			quotaUpdater.updateQuota(spaceNeeded * 2);
		}
	};
	
	private void autoLogin() {
		String account = Settings.getString("account", "", true);
		String pwd = Settings.getString("pwd", "", true);
		if (!account.equals("") && !pwd.equals("")) {
			NetWorkUtil.login(this, account, pwd);
		} else {
			EasyLogger.i("CollinWang", "autoLogin is not run");
		}
	}
	
    private void initData(String url) {
    	if (mCachePath != null && mCacheDatabase != null) {
    		File file = new File(mCachePath);
    		File file2 = new File(mCacheDatabase);
    		Log.i("CollinWang", "缓存文件有没有=" + file.exists());
    		if (Utils.isNetworkAvailable(mContext)) {//file.lastModified() + 2*60*60*1000) < System.currentTimeMillis()
    			// 清空缓存
    			clearCacheFolder(file);
    			clearCacheFolder(file2);
    			// 不使用缓存
    			mWvContent.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
    			Log.i("CollinWang", "不使用缓存");
    		} else {
    			if (!file.exists()) {
    				mTipDialog = new TipDialog(mContext, "网络不通");
    	        	mTipDialog.show();
    	        	mTipDialog.setListener(this);
    			} else {
    				// 使用缓存
    				mWvContent.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    				Log.i("CollinWang", "使用缓存");
    			}
    		}
    	} else {
    		EasyLogger.i("CollinWang", "Catch=null");
    	}
        EasyLogger.i("CollinWang", "Url=" + url);
        // 加载网页
        mWvContent.loadUrl(url);
    }

    private void initListener() {
    	mWvContent.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
            	EasyLogger.i("CollinWang", "WebUrl=" + url);
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }
	// JS交互
    public final class WebViewUtil {
		@JavascriptInterface
		public void saveLoginInfo(String account, String pwd) {
			Log.i("CollinWang","account=" + account + "；password=" + pwd);
			Settings.setString("account", account, true);
	    	Settings.setString("pwd", pwd, true);
		}
		
		@JavascriptInterface
	    public void reqWinXinPay(String param01, String param02, String param03, String param04, String param05, String param06, String param07, String param08) {
			EasyLogger.i("CollinWang", "appId=" + param01);
			EasyLogger.i("CollinWang", "partnerId=" + param02);
			EasyLogger.i("CollinWang", "prepayId=" + param03);
			EasyLogger.i("CollinWang", "nonceStr=" + param04);
			EasyLogger.i("CollinWang", "timeStamp=" + param05);
			EasyLogger.i("CollinWang", "packageValue=" + param06);
			EasyLogger.i("CollinWang", "sign=" + param07);
			EasyLogger.i("CollinWang", "order_id=" + param08);
			// 存储订单号码
			Settings.setString("order_id", param08, true);
			VSApplication.getInstance().mLogInfo = "appId:" + param01 + "\n"
					+ "partnerId:" + param02 + "\n"
					+ "prepayId:" + param03 + "\n"
					+ "nonceStr:" + param04 + "\n"
					+"timeStamp:" +  param05 + "\n"
					+ "packageValue:" + param06 + "\n"
					+ "sign:" + param07 + "\n"
					+ "order_id:" + param08;
			PayReq req = new PayReq();
			req.appId = param01;
			req.partnerId = param02;
			req.prepayId = param03;
			req.nonceStr = param04;
			req.timeStamp = param05;
			req.packageValue = param06;
			req.sign = param07;
	    	mIWXapi.sendReq(req);
	    }
	}
    
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(this);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	AMapLocationUtil.getSingleInstance().stopLocation();
    	mIWXapi.unregisterApp();
    }
    
    @Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (mWvContent.canGoBack() && !VSApplication.getInstance().isWeiXinResult) {
				mWvContent.goBack();
			} else {
				doubleClickToExit();
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
    
	private boolean doubleClickToExit() {
		if ((System.currentTimeMillis() - mExitTime) > 2000) {
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			mExitTime = System.currentTimeMillis();
		} else {
			finish();
		}
		return true;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_tip_confirm:
			mTipDialog.dismiss();
			Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
            startActivity(intent);
			break;
		case R.id.tv_tip_cancel:
			mTipDialog.dismiss();
			finish();
			break;
		}
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void onUpdate(HttpEvent event, Object obj) {
		String tip = null;
		switch (event) {
		case EVENT_LOGIN_SUCCESS:
			EasyLogger.i("CollinWang", "login succeed");
			break;
		case EVENT_LOGIN_FAIL:
			tip = (String)obj;
			EasyLogger.i("CollinWang", "login failed=" + tip);
			break;
		}
	}

	private int clearCacheFolder(File dir) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child);
					}
					if (child.delete()) {
						Log.i("CollinWang", "delete缓存" + deletedFiles);
						deletedFiles++;
					}
				}
			} catch (Exception e) {
				Log.i("CollinWang", "Catch=", e);
			}
		}
		return deletedFiles;
	}
	
}
