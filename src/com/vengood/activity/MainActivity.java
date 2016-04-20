package com.vengood.activity;

import java.io.File;
import java.util.Map;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.umeng.update.UmengUpdateAgent;
import com.vengood.R;
import com.vengood.alipay.AlipayManager.AliPayResultCallback;
import com.vengood.application.VSApplication;
import com.vengood.dialog.TipDialog;
import com.vengood.http.HttpEvent;
import com.vengood.http.HttpReqListener;
import com.vengood.http.manage.NetWorkUtil;
import com.vengood.util.BaiduMapUtil;
import com.vengood.util.BaiduTraceUtil;
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
import android.os.Handler;
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
public class MainActivity extends Activity implements OnClickListener, HttpReqListener, AliPayResultCallback {
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
    
    private UMShareAPI mShareAPI = null;
    private final SHARE_MEDIA[] mDisplayList = new SHARE_MEDIA[] { 
    		SHARE_MEDIA.WEIXIN, 
    		SHARE_MEDIA.WEIXIN_CIRCLE,
			//SHARE_MEDIA.SINA, 
			SHARE_MEDIA.QQ, 
			SHARE_MEDIA.QZONE};
     
    private UMImage mUMengImage = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_main);
		VSApplication.getInstance().addActivity(this);
		mPayResult = getIntent().getStringExtra("Result_Url");
		UmengUpdateAgent.update(this);
		mShareAPI = UMShareAPI.get(this);
		// 推送
		PushAgent mPushAgent = PushAgent.getInstance(this);
		mPushAgent.enable();
		// 推送统计
		PushAgent.getInstance(this).onAppStart();
		// 拿到测试设备ID
		mPushAgent.enable(new IUmengRegisterCallback() {
			@Override
			public void onRegistered(final String registrationId) {
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						// onRegistered方法的参数registrationId即是device_token
						EasyLogger.d("CollinWang", "device_token="+ registrationId);
					}
				});
			}
		});
		// 百度定位走起
		BaiduMapUtil.getInstance().startLocation(mContext);
		// 百度鹰眼走起
		String is_trace = OnlineConfigAgent.getInstance().getConfigParams(mContext, "is_trace");
		EasyLogger.i("CollinWang", "is_trace=" + is_trace);
		if (is_trace.equals("true")) {
			BaiduTraceUtil.getInstance().startTrace(mContext);
		} else if (is_trace.equals("false")) {
			BaiduTraceUtil.getInstance().stopTrace(mContext);
		}
		// 拿到在线参数
		mIndexUrl = OnlineConfigAgent.getInstance().getConfigParams(mContext, "url");
		mIndexUrl = "http://test.vengood.com/mobile.php?act=module&dzdid=0&name=bj_qmxk&do=list&weid=3";
		Log.i("CollinWang", "online param=" + mIndexUrl);
		mIWXapi = WXAPIFactory.createWXAPI(this, null);
		mIWXapi.registerApp(Constants.APP_ID);
		//AMapLocationUtil.getSingleInstance().startLocation(mContext);
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
	
	public void clickUMengSocialization(int id) {
		SHARE_MEDIA platform = null;
		/*if (id == R.id.app_auth_sina) {
			platform = SHARE_MEDIA.SINA;
		} else if (id == R.id.app_auth_qq) {
			platform = SHARE_MEDIA.QQ;
		} else if (id == R.id.app_auth_weixin) {
			platform = SHARE_MEDIA.WEIXIN;
		}*/
		platform = SHARE_MEDIA.QQ;
        mShareAPI.doOauthVerify(this, platform, umAuthListener);
        //mShareAPI.isInstall(this, platform);// 安装
        //mShareAPI.getPlatformInfo(this, platform, umAuthListener);// 信息
	}
	
	public void deleteUMengSocialization(int id) {
		SHARE_MEDIA platform = null;
		/*if (id == R.id.app_auth_sina) {
			platform = SHARE_MEDIA.SINA;
		} else if (id == R.id.app_auth_qq) {
			platform = SHARE_MEDIA.QQ;
		} else if (id == R.id.app_auth_weixin) {
			platform = SHARE_MEDIA.WEIXIN;
		}*/
        mShareAPI.deleteOauth(this, platform, umdelAuthListener);
	}
	
	public void startShared() {
		// 分享
        mUMengImage = new UMImage(MainActivity.this, "http://www.umeng.com/images/pic/social/integrated_3.png");
		new ShareAction(MainActivity.this).setDisplayList(mDisplayList)
				.withText("飘奇工作室")
				.withTitle("title")
				.withTargetUrl("http://www.baidu.com")
				.withMedia(mUMengImage)
				.setListenerList(umShareListener, umShareListener).setShareboardclickCallback(shareBoardlistener)
				.open();
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
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
                //view.loadUrl(url);
                //return true;
                return false;
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
		
		@JavascriptInterface
		public void startLocation() {
			final double latitude = VSApplication.getInstance().mLatitude;// 纬度
			final double longitude = VSApplication.getInstance().mLongitude;// 经度
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mWvContent.loadUrl("javascript:getLocationFromClient('"+ longitude +", "+ latitude +"')");  
				}
			});
			EasyLogger.i("CollinWang", "startLocation run：longitude=" + longitude + "；latitude=" + latitude);
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
    	//AMapLocationUtil.getSingleInstance().stopLocation();
    	BaiduMapUtil.getInstance().stopLocation();
    	mIWXapi.unregisterApp();
    }
    
    @Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (mWvContent.canGoBack()) { //&& !VSApplication.getInstance().isWeiXinResult
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
			//startShared();
			//clickUMengSocialization(4);
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
	
	/** 注册友盟分享结果回调 **/
	private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Toast.makeText(getApplicationContext(), "Authorize succeed", Toast.LENGTH_SHORT).show();
        }
        
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText( getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText( getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };
    
    /** 注销友盟分享结果回调 **/
    private UMAuthListener umdelAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Toast.makeText(getApplicationContext(), "delete Authorize succeed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText( getApplicationContext(), "delete Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText( getApplicationContext(), "delete Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };
	
    /** 友盟分享结果回调 **/
	private UMShareListener umShareListener = new UMShareListener() {
		@Override
		public void onResult(SHARE_MEDIA platform) {
			Log.d("plat", "platform" + platform);
			Toast.makeText(MainActivity.this, platform + " 分享成功", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			Toast.makeText(MainActivity.this, platform + " 分享失败", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onCancel(SHARE_MEDIA platform) {
			Toast.makeText(MainActivity.this, platform + " 分享取消", Toast.LENGTH_SHORT).show();
		}
	};

	private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {
		@Override
		public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
			new ShareAction(MainActivity.this).setPlatform(share_media).setCallback(umShareListener).withText("多平台分享").share();
		}
	};

	@Override
	public void onSuccess() {
		// 支付成功
	}

	@Override
	public void onWait() {
		// 支付等待
	}

	@Override
	public void onError() {
		// 支付错误
	}
    
}
