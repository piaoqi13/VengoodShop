package com.vengood.common;

import com.vengood.R;

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
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.start_page_activity);
        mHandler.sendEmptyMessageDelayed(1, 1 * 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
