package com.android.browser.navigation;

import android.app.Dialog;
import android.content.Context;

import com.android.browser.R;

/**
 * 
 * 说明：自定义对话框<br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
public class LimitVisitDialog extends Dialog {

	public static class Builder {

		private Context context;

		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * 创建自定义对话框
		 */
		public LimitVisitDialog create() {
			final LimitVisitDialog dialog = new LimitVisitDialog(context, R.style.LimitVisitDialog);

			return dialog;
		}
	}

	public LimitVisitDialog(Context context) {
		super(context);
	}

	public LimitVisitDialog(Context context, int theme) {
		super(context, theme);
	}
}
