package com.vengood.common;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.vengood.R;

import android.app.Activity;
import android.os.Bundle;

/**
 *类名：MainActivity.java
 *注释：商城首页
 *日期：2015年12月31日
 *作者：王超
 */
public class MainActivity extends Activity {
	private final String mPageName = "MainActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		UmengUpdateAgent.update(this);
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(this);
    }
}
