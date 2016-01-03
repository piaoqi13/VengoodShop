package com.vengood.activity;

import com.umeng.onlineconfig.OnlineConfigAgent;
import com.vengood.R;
import com.vengood.util.Settings;
import com.vengood.util.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 *类名：StartPageActivity.java
 *注释：启动页
 *日期：2016年1月2日
 *作者：王超
 */
public class StartPageActivity extends Activity {
    private Context mContext = null;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
			case 1:
				Intent intent = new Intent(mContext, MainActivity.class);
				Utils.toLeftAnim(mContext, intent, true);
				break;
			default:
				break;
			}
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        OnlineConfigAgent.getInstance().updateOnlineConfig(mContext);
        boolean isFirstTime = Settings.getBoolean("is_first_time", true, true);
        if (isFirstTime) {
        	Intent intent = new Intent(mContext, GuidePageActivity.class);
			Utils.toLeftAnim(mContext, intent, true);
        	Settings.setBoolean("is_first_time", false, true);
        } else {
        	setContentView(R.layout.start_page_activity);
        	mHandler.sendEmptyMessageDelayed(1, 1 * 1000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
