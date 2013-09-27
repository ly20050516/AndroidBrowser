package com.android.browser.navigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import com.android.browser.R;
import com.android.browser.navigation.PagerContentProvider.GreenSites;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


/**
 * 
 * 说明：数据模型管理 <br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
public class DataModelManager {
	
	static final Uri GREENSITE_URI = GreenSites.GREENSITE_URI;
	
	static final String LOG_TAG = DataModelManager.class.getSimpleName();
	
	static final String LOGO_URL = "http://tfile.eebbk.net/h600s/greenInternet/";
	
	/** 所有站点 */
	private List<BaseObject> mAllSites;
	
	ContentResolver mContentResolver;
	
	Context mContext;
	
	/** 我的网站 */
	private List<BaseObject> mMySitesObjects;
	
	/** 网址导航 */
	private LinkedHashMap<String, List<BaseObject>> mSiteNavigationObjects;
	
	public DataModelManager(Context context) {
		super();
		//
		this.mContext = context;
		this.mContentResolver = context.getContentResolver();
	}
	
	/**
	 * 更新删除实体类 { 仅限制删除 家长添加 网站，其他网站不能删除 }
	 * 
	 * @param baseObject
	 */
	private void deleteEntry(BaseObject baseObject) {
		if (mAllSites != null && mAllSites.size() > 0) {
			mAllSites.remove(baseObject);
		}
		
		if (mSiteNavigationObjects != null && mSiteNavigationObjects.size() > 0) {
			String keyString = mContext.getString(R.string.newsiteTitle);
			
			if (mSiteNavigationObjects.containsKey(keyString)) {
				mSiteNavigationObjects.get(keyString).remove(baseObject);
			}
		}
	}
	
	/**
	 * 删除网站
	 */
	public void deleteSite(final BaseObject baseObject) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mContentResolver.delete(GREENSITE_URI, GreenSites.URL + "=?", new String[] { baseObject.getUrl() });
				
				deleteEntry(baseObject);
			}
		}).start();
	}
	
	/**
	 * 得到分类导航站点
	 * 
	 * @return LinkedHashMap<String, List<BaseObject>>
	 */
	public LinkedHashMap<String, List<BaseObject>> getCatalogSiteNavigation() {
		
		if (mSiteNavigationObjects != null && mSiteNavigationObjects.size() > 0) {
			Log.i(LOG_TAG, "--------------");
			
			return mSiteNavigationObjects;
		}
		
		mSiteNavigationObjects = new LinkedHashMap<String, List<BaseObject>>();
		List<BaseObject> mBaseObjects;
		String catalogString;
		for (BaseObject baseObject : mAllSites) {
			Log.i("---------", baseObject.toString());
			
			catalogString = baseObject.getCatalog();
			if (mSiteNavigationObjects.containsKey(catalogString)) {
				mBaseObjects = mSiteNavigationObjects.get(catalogString);
				mBaseObjects.add(baseObject);
			}
			else {
				mBaseObjects = new ArrayList<BaseObject>();
				mBaseObjects.add(baseObject);
				mSiteNavigationObjects.put(catalogString, mBaseObjects);
			}
		}
		
		return mSiteNavigationObjects;
	}
	
	/**
	 * 得到经常访问站点
	 * 
	 * @param maxFrequentsites
	 * @return List<BaseObject>
	 */
	public List<BaseObject> getFrequentSites(int maxFrequentsites) {
		List<BaseObject> mList = getVisitDescSort();
		
		if (mList.size() > maxFrequentsites) {
			return mList.subList(0, maxFrequentsites);
		}
		else {
			return mList.subList(0, mList.size());
		}
	}
	
	/**
	 * { unused }
	 * 
	 * @return
	 */
	public String getMaxDate() {
		return null;
	}
	
	/**
	 * 得到我的站点
	 * 
	 * @return List<BaseObject>
	 */
	public List<BaseObject> getMySites() {
		mMySitesObjects = new ArrayList<BaseObject>();
		for (BaseObject baseObject : mAllSites) {
			if (baseObject.getIsme() == 1 && !mMySitesObjects.contains(baseObject)) {
				
				mMySitesObjects.add(baseObject);
			}
		}
		
		return mMySitesObjects;
	}
	
	/**
	 * 得到家长新增站点
	 * 
	 * @return List<BaseObject>
	 */
	public List<BaseObject> getNewSites() {
		if (mSiteNavigationObjects != null && mSiteNavigationObjects.size() > 0) {
			String keyString = mContext.getString(R.string.newsiteTitle);
			if (mSiteNavigationObjects.containsKey(keyString)) {
				return mSiteNavigationObjects.get(keyString);
			}
		}
		return null;
	}
	
	/**
	 * 得到下一个高频访问的站点
	 * 
	 * @param maxFrequentsites
	 * @return BaseObject
	 */
	public BaseObject getNextFrequentSite(int maxFrequentsites) {
		List<BaseObject> mList = getVisitDescSort();
		
		if (mList.size() > maxFrequentsites) {
			return mList.get(maxFrequentsites);
		}
		return null;
	}
	
	/**
	 * 得到下两个高频访问的站点
	 * 
	 * @param maxFrequentsites
	 * @return List<BaseObject>
	 */
	public List<BaseObject> getNextTwoFrequentSite(int maxFrequentsites) {
		List<BaseObject> mList = getVisitDescSort();
		
		if (mList.size() > maxFrequentsites + 2) {
			return mList.subList(maxFrequentsites, maxFrequentsites + 2);
		}
		return null;
	}
	
	/**
	 * 按访问次数降序排列
	 */
	private List<BaseObject> getVisitDescSort() {
		
		if (!mAllSites.isEmpty()) {
			List<BaseObject> tmpList = new ArrayList<BaseObject>();
			tmpList.addAll(mAllSites);
			
			Collections.sort(tmpList, new Comparator<BaseObject>() {
				@Override
				public int compare(BaseObject lhs, BaseObject rhs) {
					if (rhs.getVisits() < lhs.getVisits()) {
						return -1;
					}
					else if (rhs.getVisits() == lhs.getVisits()) {
						return 0;
					}
					else if (rhs.getVisits() > lhs.getVisits()) {
						return 1;
					}
					return 0;
				}
			});
			
			return tmpList;
		}
		
		return null;
	}
	
	/**
	 * { unused }
	 * 
	 * 新增站点
	 * 
	 * @param newBaseObjects
	 */
	public void insertSite(BaseObject newObject) {
		ContentValues values = new ContentValues();
		values.put(GreenSites.TITLE, newObject.getName());
		values.put(GreenSites.URL, newObject.getUrl());
		mContentResolver.insert(GREENSITE_URI, values);
		
		updateInsertEntry(newObject);
	}
	
	/**
	 * 加载所有站点
	 */
	public synchronized void loadAllSites() {
		long start = System.currentTimeMillis();
		
		mSiteNavigationObjects = new LinkedHashMap<String, List<BaseObject>>();
		List<BaseObject> mBaseObjects;
		String catalogString;
		
		mAllSites = new ArrayList<BaseObject>();
		BaseObject baseObject;
		Cursor cursor = null;
		try {
			cursor = mContentResolver.query(GREENSITE_URI, null, null, null, null);
			
			int catalogCol = cursor.getColumnIndex(GreenSites.CATALOG);
			int titleCol = cursor.getColumnIndex(GreenSites.TITLE);
			int urlCol = cursor.getColumnIndex(GreenSites.URL);
			int logoCol = cursor.getColumnIndex(GreenSites.LOGO);
			int visitsCol = cursor.getColumnIndex(GreenSites.VISITS);
			int ismeCol = cursor.getColumnIndex(GreenSites.ISME);
			int presetCol = cursor.getColumnIndex(GreenSites.ISPRESET);
			
			while (cursor.moveToNext()) {
				baseObject = new BaseObject();
				baseObject.setCatalog(cursor.getString(catalogCol));
				baseObject.setName(cursor.getString(titleCol));
				baseObject.setUrl(cursor.getString(urlCol));
				
				String logo = cursor.getString(logoCol);
				if (!TextUtils.isEmpty(logo)) {
					baseObject.setLogo(LOGO_URL + logo);
				}
				
				baseObject.setVisits(cursor.getInt(visitsCol));
				baseObject.setIsme(cursor.getInt(ismeCol));
				baseObject.setPreset(cursor.getInt(presetCol));
				localizable(baseObject);
				
				mAllSites.add(baseObject);
				
				// if (baseObject.getIsme() == 1) {
				// mMySitesObjects.add(baseObject);
				// }
				
				catalogString = baseObject.getCatalog();
				if (mSiteNavigationObjects.containsKey(catalogString)) {
					mBaseObjects = mSiteNavigationObjects.get(catalogString);
					mBaseObjects.add(baseObject);
				}
				else {
					mBaseObjects = new ArrayList<BaseObject>();
					mBaseObjects.add(baseObject);
					mSiteNavigationObjects.put(catalogString, mBaseObjects);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (cursor != null)
				cursor.close();
		}
		
		long duration = System.currentTimeMillis() - start;
		Log.i(LOG_TAG, "loadAllSites操作耗时：" + duration);
	}
	
	/**
	 * 本地化
	 * 
	 * @param baseObject
	 */
	private void localizable(BaseObject baseObject) {
		if (baseObject.getName().equals(mContext.getString(R.string.logo_whys))) {
			baseObject.setResId(R.drawable.logo_whys);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_aoshu))) {
			baseObject.setResId(R.drawable.logo_aoshu);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_jyeoo))) {
			baseObject.setResId(R.drawable.logo_jyeoo);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_china_education_online))) {
			baseObject.setResId(R.drawable.logo_china_education_online);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_kid_qq))) {
			baseObject.setResId(R.drawable.logo_kid_qq);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_children_english_online))) {
			baseObject.setResId(R.drawable.logo_children_english_online);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_xxyw))) {
			baseObject.setResId(R.drawable.logo_xxyw);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_116online))) {
			baseObject.setResId(R.drawable.logo_116online);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_eebbk))) {
			baseObject.setResId(R.drawable.logo_eebbk);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_education_qq))) {
			baseObject.setResId(R.drawable.logo_education_qq);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_crazy_english))) {
			baseObject.setResId(R.drawable.logo_crazy_english);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_middle_school_chinese_online))) {
			baseObject.setResId(R.drawable.logo_middle_school_chinese_online);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_global_english))) {
			baseObject.setResId(R.drawable.logo_global_english);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_ebigear))) {
			baseObject.setResId(R.drawable.logo_ebigear);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_sxydycom))) {
			baseObject.setResId(R.drawable.logo_sxydycom);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_52ektcn))) {
			baseObject.setResId(R.drawable.logo_52ektcn);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_kekenet))) {
			baseObject.setResId(R.drawable.logo_kekenet);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_ychxwnet))) {
			baseObject.setResId(R.drawable.logo_ychxwnet);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_education_sina))) {
			baseObject.setResId(R.drawable.logo_education_sina);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_disney))) {
			baseObject.setResId(R.drawable.logo_disney);
		}
		else if (baseObject.getName().equals(mContext.getString(R.string.logo_xue999))) {
			baseObject.setResId(R.drawable.logo_xue999);
		}
	}
	
	/**
	 * { unused }
	 * 
	 * 
	 * @param newObject
	 */
	private void updateInsertEntry(BaseObject newObject) {
		
		if (mAllSites != null && mAllSites.size() > 0) {
			
			mAllSites.add(newObject);
		}
		
		if (mSiteNavigationObjects != null && mSiteNavigationObjects.size() > 0) {
			
			List<BaseObject> mBaseObjects;
			String catalogString = newObject.getCatalog();
			
			if (mSiteNavigationObjects.containsKey(catalogString)) {
				
				mBaseObjects = mSiteNavigationObjects.get(catalogString);
				mBaseObjects.add(newObject);
			}
			else {
				
				mBaseObjects = new ArrayList<BaseObject>();
				mBaseObjects.add(newObject);
				mSiteNavigationObjects.put(catalogString, mBaseObjects);
			}
		}
	}
	
	/**
	 * 更新访问次数、我的网站
	 * 
	 * @param baseObject
	 */
	public void updateSite(final BaseObject baseObject) {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				ContentValues values = new ContentValues();
				values.put(GreenSites.VISITS, baseObject.getVisits());
				values.put(GreenSites.ISME, baseObject.getIsme());
				mContentResolver.update(GREENSITE_URI, values, GreenSites.URL + "=?", new String[] { baseObject.getUrl() });
			}
		}).start();
	}
}
