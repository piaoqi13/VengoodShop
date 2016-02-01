package com.vengood.dialog;

import com.vengood.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 *类名：LoadingDialog.java
 *注释：加载对话框
 *日期：2016年1月31日
 *作者：王超
 */
public class LoadingDialog extends Dialog implements OnDismissListener {
    private static final int MSG_WHAT = 0xaa;
    private Context mContext = null;
    private AnimationDrawable mAnimationDrawable = null;
    private ImageView mIvLoading = null;
    private TextView mTvLoading = null;
    private long mTimeout = 30 * 1000;
    private String mToast = null;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isShowing()) {
                dismiss();
                if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
                    mAnimationDrawable.stop();
                }
                if (!TextUtils.isEmpty(mToast)) {
                    Toast.makeText(mContext, mToast, Toast.LENGTH_SHORT).show();
                }
            }
            super.handleMessage(msg);
        }
    };

    public LoadingDialog(Context context) {
        super(context, R.style.LoadingDialog);
        this.mContext = context;
    }

    public void showDialog(String text) {
        setContentView(R.layout.loading_dialog_layout);
        getWindow().setWindowAnimations(R.style.LoadingDialogAnimationFade);
        mIvLoading = (ImageView) findViewById(R.id.iv_loading);
        mTvLoading = (TextView) findViewById(R.id.tv_loading);
        mTvLoading.setText(text);
        setOnDismissListener(this);
        showAnimaction();
        show();
    }

    public void showDialog(String text, Long timeout, String toast) {
        setContentView(R.layout.loading_dialog_layout);
        getWindow().setWindowAnimations(R.style.LoadingDialogAnimationFade);
        mIvLoading = (ImageView) findViewById(R.id.iv_loading);
        mTvLoading = (TextView) findViewById(R.id.tv_loading);
        setOnDismissListener(this);
        showAnimaction();
        sendMsg(text, timeout, toast);
        show();
    }

    public void refreshText(String text) {
        mTvLoading.setText(text);
    }

    private void sendMsg(String text, Long time, String toast) {
        this.mToast = toast;
        mTvLoading.setText(text);
        if (time == null || time <= 0) {
            time = mTimeout;
        }
        mHandler.sendEmptyMessageDelayed(MSG_WHAT, time);
    }

    private void showAnimaction() {
        mAnimationDrawable = (AnimationDrawable) mContext.getResources().getDrawable(R.drawable.loading_frame);
        mIvLoading.setImageDrawable(mAnimationDrawable);
        mAnimationDrawable.start();
    }

    @Override
    public void dismiss() {
        if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        try {
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mHandler.removeMessages(MSG_WHAT);
    }
}
