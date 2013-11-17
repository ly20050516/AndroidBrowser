/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.browser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Application;
import android.util.Log;
import android.webkit.CookieSyncManager;

public class Browser extends Application {

	private final static String LOGTAG = "browser";

	// Set to true to enable verbose logging.
	final static boolean LOGV_ENABLED = true;

	// Set to true to enable extra debug logging.
	final static boolean LOGD_ENABLED = true;

	public static int SJ_FLAG = 0;

	@Override
	public void onCreate ( ) {

		super.onCreate();

		if ( LOGV_ENABLED )
			Log.v(LOGTAG, "Browser.onCreate: this=" + this);

		accqurieSJFlag();
		// create CookieSyncManager with current Context
		CookieSyncManager.createInstance(this);
		BrowserSettings.initialize(getApplicationContext());
		Preloader.initialize(getApplicationContext());
	}

	public void accqurieSJFlag ( ) {

		SJ_FLAG = 0;

		File file = new File(getResources().getString(R.string.path_personal_control_cfg));

		if ( !file.exists() ) {
			return;
		}

		FileInputStream fileInputStream;
		try {

			fileInputStream = new FileInputStream(file);
			int length = (int) file.length();
			byte[] temp = new byte[ length ];
			fileInputStream.read(temp, 0, length);
			fileInputStream.close();
			SJ_FLAG = temp[ 0 ] - 48;

		} catch ( FileNotFoundException e ) {
			e.printStackTrace();

		} catch ( Exception e ) {

		}

	}
}
