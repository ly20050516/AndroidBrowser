
package com.android.browser.navigation;

import java.net.URL;
import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint ( "DefaultLocale" )
public class Utils {

	/**
	 * 是否加载
	 * 
	 * @param url
	 *            {@link10.0.0.0 - 10.255.255.255 172.16.0.0 - 172.31.255.255
	 *            192.168.0.0 - 192.168.255.255}
	 * @param enterUrl
	 * @return boolean
	 */
	public static boolean canVisited ( String url , String enterUrl ) {

		String host;
		String enterHost;

		Log.d("Liu Test", "url: " + url + " enterUrl: " + enterUrl);
		if ( null == url || null == enterUrl ) {

			return false;
		}
		try {

			host = new URL(url).getHost().toLowerCase();
			enterHost = new URL(enterUrl).getHost().toLowerCase();

			Log.d("Liu Test", "host: " + host + " enterHost: " + enterHost + " isIpAddress " + isIpAddress(host));

			if ( isIpAddress(host) || host.indexOf("eebbk") != -1 || host.indexOf("xue999") != -1 || host.indexOf("haohaoxue") != -1 || enterHost.indexOf(host) != -1
					|| host.indexOf(enterHost) != -1 ) {
				return true;
			}

			String[] enterArray = enterHost.split("\\.");
			if ( enterArray != null ) {
				if ( enterArray.length == 2 ) {
					if ( host.indexOf(enterArray[ 0 ]) != -1 ) {
						return true;
					}
				} else if ( enterArray.length >= 3 ) {
					if ( host.indexOf(enterArray[ 1 ]) != -1 ) {
						return true;
					}
				}
			}

		} catch ( Exception e ) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	private static boolean isIpAddress ( String value ) {

		Log.d("Liu Test", "isIpAddress value is " + value);
		int start = 0;
		int end = value.indexOf('.');
		int numBlocks = 0;
		while ( start < value.length() ) {
			if ( end == -1 ) {
				end = value.length();
			}

			try {
				int block = Integer.parseInt(value.substring(start, end));
				Log.d("Liu Test", "isIpAddress block is " + block);
				if ( (block > 255) || (block < 0) ) {
					return false;
				}
			} catch ( NumberFormatException e ) {
				return false;
			}

			numBlocks++;

			start = end + 1;
			end = value.indexOf('.', start);
		}

		return numBlocks == 4;
	}
}
