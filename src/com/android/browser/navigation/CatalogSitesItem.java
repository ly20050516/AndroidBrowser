package com.android.browser.navigation;

import java.util.ArrayList;
import java.util.List;

import com.android.browser.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;



/**
 * 
 * 说明：网址导航ITEM显示项<br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
@SuppressLint("NewApi")
public class CatalogSitesItem extends RelativeLayout {

	/**
	 * 说明：折叠展开<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	class AllOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (state == State.EXPANDING) {
				state = State.COLLAPSING;
			} else {
				state = State.EXPANDING;
			}
			setShowState();
		}
	}

	/**
	 * 
	 * 说明：网址导航<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	class MySiteItemOnSiteNavigationListener implements MySiteItem.OnSiteNavigationListener {
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

	/**
	 * 定义显示状态
	 */
	public class State {

		/** 折叠 */
		public static final int COLLAPSING = 1;

		/** 展开 */
		public static final int EXPANDING = 0;
	}

	/** 单个网址固定宽度 */
	private static final int FIXEDWIDTH = 160;

	/** 显示列数 */
	private int columnCount = 3;

	private Context mContext;

	/** 内容 */
	private GridLayout mGridLayout;

	/** 状态 */
	private ImageView mImageView;

	/** 标题 */
	private TextView mTextView;

	/** 存储当前组的网站列表 */
	private List<MySiteItem> mySiteItems = new ArrayList<MySiteItem>();

	/** 网址导航监听 */
	protected OnSiteNavigationListener onSiteNavigationListener;

	/** 默认展开 */
	private int state = 0;

	/** 设置标题 */
	private String title;

	public CatalogSitesItem(Context context) {
		this(context, null);
	}

	public CatalogSitesItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;

		LayoutInflater.from(mContext).inflate(R.layout.catalogsitesitem, this);
		mTextView = (TextView) findViewById(R.id.catalogtitle);
		mGridLayout = (GridLayout) findViewById(R.id.cataloggridlayout);
		mImageView = (ImageView) findViewById(R.id.catalogimage);
		setDidScreenColumnCount();
	}

	public String getTitle() {
		return title;
	}

	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		setDidScreenResetContentView();

		Log.i(this.getClass().getSimpleName(), "onConfigurationChanged");
	}

	/**
	 * 移除一个网站
	 */
	@Override
	public synchronized void removeView(View view) {
		for (MySiteItem mSiteItem : mySiteItems) {
			if (mSiteItem.getTag().equals(view.getTag())) {
				mySiteItems.remove(mSiteItem);
				mGridLayout.removeView(mSiteItem);
				mGridLayout.requestLayout();
				break;
			}
		}

		setDidScreenResetContentView();
	}

	/**
	 * 设置内容
	 * 
	 * @param key
	 * @param value
	 */
	public void setContentView(String key, List<BaseObject> value) {
		mTextView.setText("[" + key + "]");
		setTitle(key);

		MySiteItem mSiteItem;
		for (BaseObject baseObject : value) {
			mSiteItem = new MySiteItem(mContext);
			mSiteItem.setText(baseObject.getName());
			mSiteItem.setTag(baseObject);
			mSiteItem.setOnSiteNavigationListener(new MySiteItemOnSiteNavigationListener());
			mySiteItems.add(mSiteItem);

			if (state == State.COLLAPSING && mGridLayout.getChildCount() >= columnCount) {
				mImageView.setVisibility(View.VISIBLE);
				continue;
			}

			mGridLayout.addView(mSiteItem, FIXEDWIDTH, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		if (mGridLayout.getChildCount() < columnCount) {
			mGridLayout.setColumnCount(mGridLayout.getChildCount());
		}

		if (state == State.EXPANDING) {
			mImageView.setImageResource(R.drawable.arrow_down_pressed);
		} else {
			mImageView.setImageResource(R.drawable.arrow_rights);
		}
		mImageView.setOnClickListener(new AllOnClickListener());
	}

	/**
	 * 适配横竖屏一行显示数据列数
	 */
	private void setDidScreenColumnCount() {
		
		if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			
			columnCount = 4;
			
		} else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			
			columnCount = 2;
		}

		mGridLayout.setColumnCount(columnCount);
	}

	/**
	 * 适配横竖屏重置ContentView
	 */
	private void setDidScreenResetContentView() {
		int total = mySiteItems.size();
		if (total == 0) {
			return;
		}

		mGridLayout.removeAllViews();
		setDidScreenColumnCount();
		for (int j = 0, c = 0, r = 0; j < total; j++, c++) {
			if (c == columnCount) {
				c = 0;
				r++;
			}
			GridLayout.LayoutParams param = new GridLayout.LayoutParams();
			param.width = FIXEDWIDTH;
			param.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
			param.columnSpec = GridLayout.spec(c);
			param.rowSpec = GridLayout.spec(r);

			mGridLayout.addView(mySiteItems.get(j), param);
		}

		if (mGridLayout.getChildCount() < columnCount) {
			mGridLayout.setColumnCount(mGridLayout.getChildCount());
			mImageView.setVisibility(View.INVISIBLE);
		} else {
			mImageView.setVisibility(View.VISIBLE);
		}

		mGridLayout.requestLayout();

		if (state == State.COLLAPSING) {
			setShowState();
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

	/**
	 * 设置显示状态
	 */
	private void setShowState() {
		switch (state) {
		case State.COLLAPSING:
			mImageView.setImageResource(R.drawable.arrow_rights);
			if (mGridLayout.getChildCount() > columnCount) {
				mGridLayout.removeViewsInLayout(columnCount, mGridLayout.getChildCount() - columnCount);
			}
			break;

		case State.EXPANDING:
			mImageView.setImageResource(R.drawable.arrow_down_pressed);
			int i = 0;
			for (MySiteItem mSiteItem : mySiteItems) {
				i++;
				if (i > columnCount) {
					mGridLayout.addView(mSiteItem, FIXEDWIDTH, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				}
			}
			break;
		}
		mGridLayout.requestLayout();
	}

	/**
	 * 设置显示状态
	 * 
	 * @param state
	 */
	public void setState(int state) {
		this.state = state;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
