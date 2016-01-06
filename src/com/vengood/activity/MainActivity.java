package com.vengood.activity;

import java.io.File;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.update.UmengUpdateAgent;
import com.vengood.R;
import com.vengood.dialog.TipDialog;
import com.vengood.http.HttpEvent;
import com.vengood.http.HttpReqListener;
import com.vengood.http.manage.NetWorkUtil;
import com.vengood.util.AMapLocationUtil;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_main);
		UmengUpdateAgent.update(this);
		mIWXapi = WXAPIFactory.createWXAPI(this, "wxb4ba3c02aa476ea1");
		//mIWXapi.registerApp("wxb4ba3c02aa476ea1");
		AMapLocationUtil.getSingleInstance().startLocation(mContext);
		initView();
		autoLogin();
		initListener();
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
		// 缓存CollinWang2016.01.03
		webSettings.setDomStorageEnabled(true);
		webSettings.setAppCacheEnabled(true);
		webSettings.setAppCacheMaxSize(5 * 1024 * 1024);
		String cache = mContext.getDir("cache", Context.MODE_PRIVATE).getPath() + "/webcache";
		EasyLogger.i("CollinWang", "cache=" + cache);
		webSettings.setAppCachePath(cache);
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
			quotaUpdater.updateQuota(spaceNeeded * 6);
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
	
    private void initData() {
        if (!Utils.isNetworkAvailable(mContext)) {
        	mTipDialog = new TipDialog(mContext, "网络不通");
        	mTipDialog.show();
        	mTipDialog.setListener(this);
        } else {
        	clearWebViewCache();
        }
        // 加载网页
        String url = OnlineConfigAgent.getInstance().getConfigParams(mContext, "url");
        EasyLogger.i("CollinWang", "url=" + url);
        mWvContent.loadUrl(url);
    }

    private void initListener() {
    	mWvContent.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }
	
    // 给JS调用
    public void reqWinXinPay() {
    	PayReq req = new PayReq();
    	mIWXapi.sendReq(req);
    }
    
    // 给JS调用
    public void saveLoginInfo(String account, String pwd) {
    	Settings.setString("account", account, true);
    	Settings.setString("pwd", pwd, true);
    }
    
	@Override
    protected void onResume() {
        super.onResume();
        initData();// 考虑设置网络重获焦点
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(this);
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
    }
    
    @Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return doubleClickToExit();
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
	
	private void clearWebViewCache() {
		try {
			deleteDatabase("webview.db");
			deleteDatabase("webviewCache.db");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// WebView 缓存文件
		File appCacheDir = new File(getFilesDir().getAbsolutePath() + "/webcache");
		EasyLogger.i("CollinWang", "appCacheDir path=" + appCacheDir.getAbsolutePath());

		File wbCacheDir = new File(getCacheDir().getAbsolutePath() + "/webviewCache");
		EasyLogger.i("CollinWang", "webviewCacheDir path=" + wbCacheDir.getAbsolutePath());

		if (wbCacheDir.exists()) {
			deleteFile(wbCacheDir);
		}
		if (appCacheDir.exists()) {
			deleteFile(appCacheDir);
		}
	}
	
	private void deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i]);
				}
			}
			boolean isOk = file.delete();
			EasyLogger.i("CollinWang", "isOk=" + isOk);
		} else {
			EasyLogger.i("CollinWang", "no file" + file.getAbsolutePath());
		}
	}

	@Override
	public void onUpdate(HttpEvent event, Object obj) {
		switch (event) {
		case EVENT_LOGIN_SUCCESS:
			EasyLogger.i("CollinWang", "login succeed");
			break;
		case EVENT_LOGIN_FAIL:
			String tip = (String)obj;
			EasyLogger.i("CollinWang", "login failed=" + tip);
			break;
		}
	}

}
