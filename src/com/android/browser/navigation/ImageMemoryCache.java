package com.android.browser.navigation;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;

/**
 * 
 * 说明：内存缓存<br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
public class ImageMemoryCache {

	private static final int HARD_CACHE_CAPACITY = 30;
	private final static ConcurrentHashMap<String, SoftReference<Bitmap>> mSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			HARD_CACHE_CAPACITY / 2);
	private HashMap<String, Bitmap> mHardBitmapCache;

	public ImageMemoryCache() {
		mHardBitmapCache = new LinkedHashMap<String, Bitmap>(
				HARD_CACHE_CAPACITY / 2, 0.75f, true) {

			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(
					LinkedHashMap.Entry<String, Bitmap> eldest) {
				if (size() > HARD_CACHE_CAPACITY) {
					// Entries push-out of hard reference cache are transferred
					// to soft reference cache
					mSoftBitmapCache.put(eldest.getKey(),
							new SoftReference<Bitmap>(eldest.getValue()));
					return true;
				} else {
					return false;
				}
			}
		};

	}

	/**
	 * 添加图片到缓存
	 * 
	 * @param url
	 * @param bitmap
	 */
	public void addBitmapToCache(String url, Bitmap bitmap) {
		if (bitmap != null) {
			synchronized (mHardBitmapCache) {
				mHardBitmapCache.put(url, bitmap);
			}
		}
	}

	/**
	 * 从缓存中获取图片
	 * 
	 * @param url
	 * @return Bitmap
	 */
	public Bitmap getBitmapFromCache(String url) {
		// 先从mHardBitmapCache缓存中获取
		synchronized (mHardBitmapCache) {
			final Bitmap bitmap = mHardBitmapCache.get(url);
			if (bitmap != null) {
				// 如果找到的话，把元素移到linkedhashmap的最前面，从而保证在LRU算法中是最后被删除
				mHardBitmapCache.remove(url);
				mHardBitmapCache.put(url, bitmap);
				return bitmap;
			}
		}
		// 如果mHardBitmapCache中找不到，到mSoftBitmapCache中找
		SoftReference<Bitmap> bitmapReference = mSoftBitmapCache.get(url);
		if (bitmapReference != null) {
			final Bitmap bitmap = bitmapReference.get();
			if (bitmap != null) {
				// 将图片移回硬缓存
				mHardBitmapCache.put(url, bitmap);
				mSoftBitmapCache.remove(url);
				return bitmap;
			} else {
				mSoftBitmapCache.remove(url);
			}
		}
		return null;
	}
}