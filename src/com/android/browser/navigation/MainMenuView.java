
package com.android.browser.navigation;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.browser.R;

/**
 * 
 * 主菜单选项(PopupWindow)
 * 
 * @author
 * 
 */
public class MainMenuView implements OnTouchListener {

	private PopupWindow mPopupWindow;

	private View mParentView;

	private View menuView;

	/**
	 * 主菜单
	 * 
	 * @param context
	 * @param resource
	 *            父类布局
	 * @param itemsOnClick
	 *            按键监听
	 */
	public MainMenuView ( Activity context , View parentView , OnClickListener itemsOnClick ) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		menuView = inflater.inflate(R.layout.menu_layout, null);
		menuView.setFocusableInTouchMode(true);
		mParentView = parentView;
		menuView.findViewById(R.id.menu_item_find_on_page).setOnClickListener(itemsOnClick);

		menuView.findViewById(R.id.menu_item_ua_switcher_desktop).setOnClickListener(itemsOnClick);

		menuView.findViewById(R.id.menu_item_page_info).setOnClickListener(itemsOnClick);

		menuView.findViewById(R.id.menu_item_preferences).setOnClickListener(itemsOnClick);

		menuView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey ( View v , int keyCode , KeyEvent event ) {

				if ( event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_MENU ) {
					if ( isShowiing() ) {
						dismissMainMenuView();
					}
				}
				return true;
			}
		});

		mPopupWindow = new PopupWindow(menuView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setOutsideTouchable(true);
		// mPopupWindow.setAnimationStyle(R.style.popwin_anim_style);
		mPopupWindow.setFocusable(true);
	}

	public boolean isShowiing ( ) {

		return mPopupWindow.isShowing();
	}

	/**
	 * 显示MainMenuView
	 */
	public void showMainMenuView ( ) {

		mPopupWindow.showAtLocation(mParentView, Gravity.BOTTOM, 0, 0);
	}

	/**
	 * 消失UpdateDltDataView布局
	 */
	public void dismissMainMenuView ( ) {

		mPopupWindow.dismiss();
	}

	public void setTextById(int vID,int strID){
		
		TextView text = (TextView) menuView.findViewById(vID);
		text.setText(strID);
	}
	@Override
	public boolean onTouch ( View v , MotionEvent event ) {

		if ( event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE ) {
			final View child = ((ViewGroup) v).getChildAt(0);
			child.postDelayed(new Runnable() {

				@Override
				public void run ( ) {

					child.setPressed(true);

				}
			}, 150);
		} else {
			final View child = ((ViewGroup) v).getChildAt(0);
			child.postDelayed(new Runnable() {

				@Override
				public void run ( ) {

					child.setPressed(false);

				}
			}, 150);
		}

		return false;
	}

}
