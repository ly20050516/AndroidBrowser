package com.android.browser.navigation;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.util.Log;

/**
 * 
 * 说明：图片下载者<br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
public class ImageDownloader {

	/*
	 * An InputStream that skips the exact number of bytes provided, unless it
	 * reaches EOF.
	 */
	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	private static final String LOG_TAG = "ImageDownloader";

	/**
	 * 下载BITMAP图形
	 * 
	 * @param url
	 * @return Bitmap
	 */
	public static Bitmap downloadBitmap(String url) {

		final HttpClient client = AndroidHttpClient.newInstance("Android");
		final HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					// return BitmapFactory.decodeStream(inputStream);
					// Bug on slow connections, fixed in future release.
					FilterInputStream fit = new FlushedInputStream(inputStream);
					return BitmapFactory.decodeStream(fit);
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (IOException e) {
			getRequest.abort();
			Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
		} catch (IllegalStateException e) {
			getRequest.abort();
			Log.w(LOG_TAG, "Incorrect URL: " + url);
		} catch (Exception e) {
			getRequest.abort();
			Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
		} finally {
			if ((client instanceof AndroidHttpClient)) {
				((AndroidHttpClient) client).close();
			}
		}
		return null;
	}
}
