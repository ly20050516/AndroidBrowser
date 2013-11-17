package com.android.browser.navigation;

import com.android.browser.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * 说明：一个网站显示项<br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
@SuppressLint("NewApi")
public class MySiteItem extends LinearLayout implements OnClickListener, OnTouchListener {

	/**
	 * 定义显示模式
	 */
	public class Mode {
		/**
		 * 浏览模式
		 */
		public final static int BROWSERMODE = 0;

		/**
		 * 删除模式
		 */
		public final static int DELETEMODE = 1;
	}

	/**
	 * 说明：网址导航监听接口
	 */
	public interface OnSiteNavigationListener {
		public void onSiteNavigation(View v);
	}

	/**
	 * 说明：网址移除监听接口
	 */
	public interface OnSiteRemoveListener {
		public void onSiteRemove(View v);
	}

	/**
	 * 定义类型
	 */
	public class Type {
		/**
		 * 图标
		 */
		public final static int LOGO = 1;
		/**
		 * 文本
		 */
		public final static int TEXT = 0;
	}

	/** 缺省STYLE - TEXT风格 */
	public static final int DEFAULT_STYLE = 0;

	/** LOGO STYLE */
	public static final int LOGO_STYLE = 2;

	/** TEXT STYLE */
	public static final int TEXT_STYLE = 1;

	/** 删除标签 */
	private ImageView mImageView;

	/** 蒙板效果 */
	private TextView mLayerTextView;

	/** 网站显示模式 */
	private int mode = 0;

	/** 网站名称/LOGO图形 */
	private TextView mTextView;

	/** 网站导航监听 */
	private OnSiteNavigationListener onSiteNavigationListener;

	/** 网站移除监听 */
	private OnSiteRemoveListener onSiteRemoveListener;

	/** 网站类型 */
	private int type = 0;

	public MySiteItem(Context context) {
		this(context, null);
	}

	public MySiteItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MySiteItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MySiteItem);
		type = typedArray.getInt(R.styleable.MySiteItem_type, 0);
		mode = typedArray.getInt(R.styleable.MySiteItem_mode, 0);
		typedArray.recycle();

		switch (defStyle) {
		case LOGO_STYLE:
			LayoutInflater.from(context).inflate(R.layout.mysiteitem_logo, this);
			break;
		case TEXT_STYLE:
		default:
			LayoutInflater.from(context).inflate(R.layout.mysiteitem_text, this);
			break;
		}
		mImageView = (ImageView) findViewById(R.id.imageView);
		mTextView = (TextView) findViewById(R.id.textView);
		mLayerTextView = (TextView) findViewById(R.id.layerTextView);

		mImageView.setOnClickListener(this);

		mTextView.setMovementMethod(LinkMovementMethod.getInstance());
		mTextView.setOnClickListener(this);
		mTextView.setOnTouchListener(this);
	}

	/**
	 * 获取显示模式
	 * 
	 * @return mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * 获取类型
	 * 
	 * @return
	 */
	public int getType() {
		return type;
	}

	@Override
	public void onClick(View v) {

		if (v == mImageView) {
			
			onSiteRemoveListener.onSiteRemove(this);
			
		} else if (v == mTextView) {
			
			if (mImageView.getVisibility() != View.VISIBLE) {
				onSiteNavigationListener.onSiteNavigation(this);
			}
			
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (this.mode == Mode.BROWSERMODE) {

			switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:

				if (this.type == Type.LOGO) {

					mLayerTextView.setVisibility(View.VISIBLE);

				} else {

					mTextView.setBackgroundColor(Color.rgb(190, 194, 200));
				}

				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:

				if (this.type == Type.LOGO) {

					mLayerTextView.setVisibility(View.GONE);

				} else {

					mTextView.setBackgroundColor(0x00000000);
				}
				break;
			}
		}
		return false;
	}

	/**
	 * 设置显示模式
	 * 
	 * @param mode
	 */
	public void setMode(int mode) {
		this.mode = mode;

		switch (this.mode) {
		case Mode.DELETEMODE:
			mImageView.setVisibility(View.VISIBLE);
			break;

		default:
			mImageView.setVisibility(View.GONE);
			break;
		}
	}

	/**
	 * /** 设置网址导航监听器
	 * 
	 * @param onSiteNavigationListener
	 * 
	 */
	public void setOnSiteNavigationListener(OnSiteNavigationListener onSiteNavigationListener) {
		this.onSiteNavigationListener = onSiteNavigationListener;
	}

	/**
	 * 设置网址移除鉴听器
	 * 
	 * @param onSiteRemoveListener
	 * 
	 */
	public void setOnSiteRemoveListener(OnSiteRemoveListener onSiteRemoveListener) {
		this.onSiteRemoveListener = onSiteRemoveListener;
	}

	/**
	 * 设置网站项显示文本
	 * 
	 * @param text
	 * 
	 */
	public void setText(String text) {
		mTextView.setText(text);
	}

	/**
	 * 设置网站项背景
	 */
	public void setTextBackgroundResource(int resId) {
		mTextView.setBackgroundResource(resId);
	}

	/**
	 * 设置网站项背景
	 */
	public void setTextDrawable(Drawable drawable) {
		mTextView.setBackgroundDrawable(drawable);
		mTextView.setText(null);
	}

	/**
	 * 设置类型
	 * 
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}
}