package com.android.browser.navigation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * 
 * 说明：图片文件缓存<br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
public class ImageFileCache {

	/**
	 * 
	 * 说明：根据文件的最后修改时间进行排序<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	private class FileLastModifSort implements Comparator<File> {

		@Override
		public int compare(File arg0, File arg1) {
			if (arg0.lastModified() > arg1.lastModified()) {
				return 1;
			} else if (arg0.lastModified() == arg1.lastModified()) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	private static final int CACHE_SIZE = 10;

	private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 10;

	private static final String LOG_TAG = "ImageFileCache";

	/** 保存期限 */
	private static final long mTimeDiff = 3 * 24 * 60 * 60 * 1000;

	/** 临时文件后缀 */
	private static final String WHOLESALE_CONV = ".bbk";

	private Context context;

	private int MB = 1024 * 1024;

	public ImageFileCache(Context context) {
		this.context = context;
		// 清理文件缓存
		removeCache(getCacheDir());
	}

	/**
	 * 将url转成文件名
	 * 
	 * @param url
	 * @return String
	 */
	private String convertUrlToFileName(String url) {
		String[] strs = url.split("/");
		return strs[strs.length - 1] + WHOLESALE_CONV;
	}

	/**
	 * 计算sdcard上的剩余空间
	 * 
	 * @return int
	 */
	private int freeSpaceOnSd() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
				.getBlockSize()) / MB;
		return (int) sdFreeMB;
	}

	/**
	 * 获取缓存目录
	 * 
	 * @return String
	 */
	public String getCacheDir() {

		// boolean sdCardExist = Environment.getExternalStorageState().equals(
		// Environment.MEDIA_MOUNTED);
		//
		// if (sdCardExist) {
		// return context.getExternalCacheDir().getPath();
		// }

		return context.getCacheDir().getPath();
	}

	/**
	 * 得到指定的BITMAP
	 * 
	 * @param url
	 * @return Bitmap
	 */
	public Bitmap getImage(final String url) {
		final String path = getCacheDir() + "/" + convertUrlToFileName(url);
		File file = new File(path);
		if (file.exists()) {
			Bitmap bmp = BitmapFactory.decodeFile(path);
			if (bmp == null) {
				file.delete();
			} else {
				updateFileTime(path);
				return bmp;
			}
		}
		return null;
	}

	/**
	 * 计算存储目录下的文件大小，
	 * 当文件总大小大于规定的CACHE_SIZE或者sdcard剩余空间小于FREE_SD_SPACE_NEEDED_TO_CACHE的规定
	 * 那么删除40%最近没有被使用的文件
	 * 
	 * @param dirPath
	 * @return boolean
	 */
	private boolean removeCache(String dirPath) {
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null || files.length == 0) {
			return true;
		}
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return false;
		}

		int dirSize = 0;
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().contains(WHOLESALE_CONV)) {
				dirSize += files[i].length();
			}
		}

		if (dirSize > CACHE_SIZE * MB
				|| FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			int removeFactor = (int) ((0.4 * files.length) + 1);

			Arrays.sort(files, new FileLastModifSort());

			for (int i = 0; i < removeFactor; i++) {

				if (files != null && files.length > removeFactor) {
					if (files[i].getName().contains(WHOLESALE_CONV)) {
						files[i].delete();
					}
				}
			}
		}

		if (freeSpaceOnSd() <= CACHE_SIZE) {
			return false;
		}
		return true;
	}

	/**
	 * 删除过期文件
	 * 
	 * @param dirPath
	 * @param filename
	 */
	public void removeExpiredCache(String dirPath, String filename) {

		File file = new File(dirPath, filename);

		if (System.currentTimeMillis() - file.lastModified() > mTimeDiff) {

			Log.i(LOG_TAG, "Clear some expiredcache files ");

			file.delete();
		}

	}

	/**
	 * 保存BMP图形到SD卡
	 * 
	 * @param bm
	 * @param url
	 */
	public void saveBmpToSd(Bitmap bm, String url) {
		if (bm == null) {
			return;
		}
		// 判断sdcard上的空间
		if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			return;
		}
		String filename = convertUrlToFileName(url);
		String dir = getCacheDir();

		File path = new File(dir);
		if (path.exists()) {
			path.mkdirs();
		}

		File file = new File(dir + "/" + filename);
		try {
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
			outStream.flush();
			outStream.close();

		} catch (FileNotFoundException e) {
			Log.w(LOG_TAG, "FileNotFoundException");
		} catch (IOException e) {
			Log.w(LOG_TAG, "IOException");
		}
	}

	/**
	 * 修改文件的最后修改时间
	 * 
	 * @param path
	 */
	public void updateFileTime(String path) {
		File file = new File(path);
		long newModifiedTime = System.currentTimeMillis();
		file.setLastModified(newModifiedTime);
	}
}