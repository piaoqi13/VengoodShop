package com.vengood.common;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 *类名：ViewPagerAdapter.java
 *注释：ViewPager适配器
 *日期：2016年1月2日
 *作者：王超
 */
public class ViewPagerAdapter extends PagerAdapter {
	private List<View> mViews = null;
	
	public ViewPagerAdapter(List<View> views) {
		this.mViews = views;
	}
	
	@Override
	public void destroyItem(View view, int position, Object obj) {
		((ViewPager) view).removeView(mViews.get(position));
	}
	
	@Override
	public void finishUpdate(View view) {
		// ToDo
	}
	
	@Override
	public int getCount() {
		return mViews.size();
	}
	
	@Override
	public Object instantiateItem(View view, int position) {
		((ViewPager) view).addView(mViews.get(position), 0);
		return mViews.get(position);
	}
	
	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}
	
	@Override
	public void restoreState(Parcelable par, ClassLoader cld) {
		// ToDo
	}
	
	@Override
	public Parcelable saveState() {
		return null;
	}
	
	@Override
	public void startUpdate(View view) {
		// ToDo
	}
	
}
