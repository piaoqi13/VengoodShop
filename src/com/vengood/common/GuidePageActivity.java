package com.vengood.common;

import java.util.ArrayList;
import java.util.List;

import com.vengood.R;
import com.vengood.util.EasyLogger;
import com.vengood.util.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 *类名：GuidePageActivity.java
 *注释：引导页
 *日期：2016年1月2日
 *作者：王超
 */
@SuppressLint("InflateParams")
public class GuidePageActivity extends Activity implements OnPageChangeListener {
	private Context mContext = null;
	private ViewPager mVpContent = null;
	private LinearLayout mLlPointView = null;
	private View mVSelectedPoint = null;
	
	private List<View> mViews = new ArrayList<View>();
	private int mWidth = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_page_activity);
		mContext = this;
		initView();
		initData();
		initListener();
	}
	
	private void initView() {
		mVpContent = (ViewPager) findViewById(R.id.vp_start_page);
		mLlPointView = (LinearLayout) findViewById(R.id.ll_point_view);
		mVSelectedPoint = findViewById(R.id.v_point_selected);
	}
	
	private void initData() {
		View view1 = getLayoutInflater().inflate(R.layout.activity_start1, null);
		View view2 = getLayoutInflater().inflate(R.layout.activity_start2, null);
		View view3 = getLayoutInflater().inflate(R.layout.activity_start3, null);
		mViews.add(view1);
		mViews.add(view2);
		mViews.add(view3);
		// 转载适配器
		mVpContent.setAdapter(new ViewPagerAdapter(mViews));
		// 初始化三个点
		for (int i = 0; i < mViews.size(); i++) {
			View view = new View(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(18, 18);
			params.rightMargin = 15;
			params.leftMargin = 15;
			params.gravity = Gravity.CENTER_VERTICAL;
			view.setLayoutParams(params);
			view.setBackgroundResource(R.drawable.vw_point_normal);
			mLlPointView.addView(view);
		}
		// 初始化选中点
		mVSelectedPoint.setBackgroundResource(R.drawable.vw_point_normal);
	}
	
	private void initListener() {
		mVpContent.setOnPageChangeListener(this);
	}

	public void startExperiment(View view) {
		Intent intent = new Intent(mContext, StartPageActivity.class);
        Utils.toLeftAnim(mContext, intent, true);
	}
	
	@Override
	public void onPageScrollStateChanged(int state) {
		//ToDo
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		EasyLogger.i("CollinWang", "onPageScrolled is run");
		EasyLogger.i("CollinWang", "width=" + mWidth);
		if (mWidth == -1) {
			mWidth = mLlPointView.getChildAt(1).getLeft() - mLlPointView.getChildAt(0).getLeft();
		}
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(18, 18);
		params.leftMargin = (int) (position * mWidth + positionOffset * mWidth) + 15;
		mVSelectedPoint.setBackgroundResource(R.drawable.vw_point_selected);
		mVSelectedPoint.setLayoutParams(params);
	}

	@Override
	public void onPageSelected(int position) {
		//ToDo
	}
}
