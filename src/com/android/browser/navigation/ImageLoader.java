package com.android.browser.navigation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;



/**
 * 
 * 说明：图形文件加载<br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
@SuppressLint("HandlerLeak")
public class ImageLoader {

	/**
	 * 
	 * 说明：完成消息Handler<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	private class TaskHandler extends Handler {
		MySiteItem img;
		String url;

		public TaskHandler(String url, MySiteItem img) {
			this.url = url;
			this.img = img;
		}

		@Override
		public void handleMessage(Message msg) {
			/*** 查看需要显示的图片是否被改变 ***/
			if (((BaseObject) img.getTag()).getLogo().equals(url)) {
				if (msg.obj != null) {
					Bitmap bitmap = (Bitmap) msg.obj;
					// img.setImageBitmap(bitmap);
					img.setTextDrawable(new BitmapDrawable(resources, bitmap));
				}
			}
		}
	}

	/**
	 * 
	 * 说明：子线程任务<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	private class TaskWithResult implements Callable<String> {
		private Handler handler;
		private String url;

		public TaskWithResult(Handler handler, String url) {
			this.url = url;
			this.handler = handler;
		}

		@Override
		public String call() throws Exception {
			Message msg = new Message();
			msg.obj = getBitmap(url);
			if (msg.obj != null) {
				handler.sendMessage(msg);
			}
			return url;
		}
	}

	private ExecutorService executorService; // 线程池
	private ImageFileCache fileCache; // 文件缓存
	private ImageMemoryCache memoryCache; // 内存缓存

	private Resources resources;

	private Map<String, MySiteItem> taskMap; // 存放任务

	public ImageLoader(Context context) {
		executorService = Executors.newFixedThreadPool(10);
		memoryCache = new ImageMemoryCache();
		fileCache = new ImageFileCache(context);
		taskMap = new HashMap<String, MySiteItem>();
		resources = context.getResources();
	}

	/**
	 * 添加一个任务到队列中
	 * 
	 * @param object
	 * @param img
	 */
	public void addTask(BaseObject object, MySiteItem img) {
		if (TextUtils.isEmpty(object.getLogo())) {
			return;
		}
		Bitmap bitmap = memoryCache.getBitmapFromCache(object.getLogo());
		if (bitmap != null) {
			img.setTextDrawable(new BitmapDrawable(resources, bitmap));
		} else {
			synchronized (taskMap) {
				taskMap.put(Integer.toString(img.hashCode()), img);
			}
		}
	}

	/**
	 * 开始执行任务队列
	 */
	public void doTask() {
		synchronized (taskMap) {
			Collection<MySiteItem> con = taskMap.values();
			for (MySiteItem i : con) {
				if (i != null) {
					if (i.getTag() != null) {
						loadImage(((BaseObject) i.getTag()).getLogo(), i);
					}
				}
			}
			taskMap.clear();
		}
	}

	/**
	 * 获得一个图片,从三个地方获取,首先是内存缓存,然后是文件缓存,最后从网络获取
	 * 
	 * @param url
	 * @return Bitmap
	 */
	private Bitmap getBitmap(String url) {
		Bitmap result;
		// 从内存缓存中获取图片
		result = memoryCache.getBitmapFromCache(url);
		if (result == null) {
			// 文件缓存中获取
			result = fileCache.getImage(url);
			if (result == null) {
				// 从网络获取
				result = ImageDownloader.downloadBitmap(url);
				if (result != null) {
					memoryCache.addBitmapToCache(url, result);
					fileCache.saveBmpToSd(result, url);
				}
			} else {
				// 添加到内存缓存
				memoryCache.addBitmapToCache(url, result);
			}
		}
		return result;
	}

	/**
	 * 加载图片
	 * 
	 * @param url
	 * @param img
	 */
	public void loadImage(String url, MySiteItem img) {
		executorService.submit(new TaskWithResult(new TaskHandler(url, img),
				url));
	}
}
