package com.android.browser.navigation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import com.android.browser.R;
import com.android.browser.navigation.CatalogSitesItem.State;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;



/**
 * 
 * 说明：网址导航显示VIEW<br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
public class AllSitesView extends ScrollView {

	/**
	 * 说明：网址导航<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	class CatalogSitesItemOnSiteNavigationListener implements CatalogSitesItem.OnSiteNavigationListener {

		@Override
		public void onSiteNavigation(View v) {
			onSiteNavigationListener.onSiteNavigation(v);
		}

	}

	/**
	 * 
	 * 说明：网址导航监听接口<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	public interface OnSiteNavigationListener {
		public void onSiteNavigation(View v);
	}

	private static final String LOG_TAG = "AllSitesView";

	private Context mContext;

	private LinearLayout mLinearLayout;

	/** 网址导航监听 */
	protected OnSiteNavigationListener onSiteNavigationListener;

	public AllSitesView(Context context) {
		this(context, null);
	}

	public AllSitesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		mLinearLayout = new LinearLayout(context);
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);
		this.addView(mLinearLayout, mLayoutParams);
	}

	/**
	 * 增加一条拆分线
	 */
	private void addSplitLineView() {
		View view = new View(mContext);
		android.widget.LinearLayout.LayoutParams layoutParams = new android.widget.LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1);
		int margins_allsitesview_spline = (int) mContext.getResources().getDimension(R.dimen.margins_allsitesview_spline);
		layoutParams.leftMargin = margins_allsitesview_spline;
		layoutParams.rightMargin = margins_allsitesview_spline;
		view.setBackgroundResource(R.drawable.splitline);
		mLinearLayout.addView(view, layoutParams);
	}

	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		Log.i(LOG_TAG, "onConfigurationChanged");
	}

	/**
	 * 移除一个网站
	 */
	@Override
	public void removeView(View view) {

		if (mLinearLayout != null) {
			int chileCount = mLinearLayout.getChildCount();
			CatalogSitesItem mCatalogSitesItem;
			for (int i = chileCount - 1; i > 0 && i < chileCount; i--) {
				View view2 = mLinearLayout.getChildAt(i);
				if (view2 instanceof CatalogSitesItem) {
					mCatalogSitesItem = (CatalogSitesItem) view2;
					String title = mCatalogSitesItem.getTitle();
					if (title.equals(mContext.getString(R.string.newsiteTitle))) {
						mCatalogSitesItem.removeView(view);
						break;
					}
				}
			}
		}
	}

	/**
	 * 设置ContentView
	 * 
	 * @param mLinkedHashMap
	 */
	public void setContentView(LinkedHashMap<String, List<BaseObject>> mLinkedHashMap) {
		mLinearLayout.removeAllViews();

		CatalogSitesItem mCatalogSitesItem;
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		int margins_allsitesview_itemleft = (int) mContext.getResources().getDimension(R.dimen.margins_allsitesview_itemleft);
		int margins_allsitesview_itemtop = (int) mContext.getResources().getDimension(R.dimen.margins_allsitesview_itemtop);
		mLayoutParams.setMargins(margins_allsitesview_itemleft, margins_allsitesview_itemtop, margins_allsitesview_itemleft,
				margins_allsitesview_itemtop);
		for (Entry<String, List<BaseObject>> entry : mLinkedHashMap.entrySet()) {
			String key = entry.getKey();
			List<BaseObject> value = entry.getValue();
			mCatalogSitesItem = new CatalogSitesItem(mContext);
			mCatalogSitesItem.setState(State.COLLAPSING);
			mCatalogSitesItem.setContentView(key, value);
			mCatalogSitesItem.setOnSiteNavigation(new CatalogSitesItemOnSiteNavigationListener());
			mLinearLayout.addView(mCatalogSitesItem, mLayoutParams);
			addSplitLineView();
		}
	}

	/**
	 * 设置网址导航监听器
	 * 
	 * @param onSiteNavigationListener
	 */
	public void setOnSiteNavigation(OnSiteNavigationListener onSiteNavigationListener) {
		this.onSiteNavigationListener = onSiteNavigationListener;
	}
}
