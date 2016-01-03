package com.vengood.dialog;

import com.vengood.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 *类名：TipDialog.java
 *注释：提示对话框
 *日期：2016年1月3日
 *作者：王超
 */
public class TipDialog extends Dialog {
	private String mTip = null;
	private View mView = null;

	private TextView mTvTitle = null;
	private TextView mTvConfirm = null;
	private TextView mTvCancle = null;

	public TipDialog(Context context, String tip) {
		super(context, R.style.tip_dialog_style);
		this.mTip = tip;
	}

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView = LayoutInflater.from(getContext()).inflate(R.layout.tip_dialog_layout, null);
		mTvTitle = (TextView) mView.findViewById(R.id.tv_tip_title);
		mTvTitle.setText(mTip);
		mTvConfirm = (TextView) mView.findViewById(R.id.tv_tip_confirm);
		mTvCancle = (TextView) mView.findViewById(R.id.tv_tip_cancel);
		// 设置属性宽度全屏
		Window window = this.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;
		window.setAttributes(lp);
		// 点击旁白不消失
		// this.setCancelable(false);
		this.setContentView(mView);
	}

	public void setListener(View.OnClickListener listener) {
		mTvConfirm.setOnClickListener(listener);
		mTvCancle.setOnClickListener(listener);
	}

}
