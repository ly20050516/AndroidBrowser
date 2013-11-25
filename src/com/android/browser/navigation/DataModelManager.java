
package com.android.browser.navigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Browser;
import android.provider.Browser.BookmarkColumns;
import android.text.TextUtils;
import android.util.Log;

import com.android.browser.R;
import com.android.browser.navigation.PagerContentProvider.GreenSites;
import com.android.browser.navigation.PagerContentProvider.UpdatePeriod;

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

	static final int update_period = 2 * 24 * 60 * 60 * 1000;

	private DataModeManagerListener listener;

	/** 所有站点 */
	private List< BaseObject> mAllSites;

	ContentResolver mContentResolver;

	Context mContext;

	/** 我的网站 */
	private List< BaseObject> mMySitesObjects;

	/** 网址导航 */
	private LinkedHashMap< String , List< BaseObject>> mSiteNavigationObjects;

	public DataModelManager ( Context context ) {

		super();
		//
		this.mContext = context;
		this.mContentResolver = context.getContentResolver();
	}

	public void registerListener ( DataModeManagerListener l ) {

		listener = l;
	}

	/**
	 * 更新删除实体类 { 仅限制删除 家长添加 网站，其他网站不能删除 }
	 * 
	 * @param baseObject
	 */
	private void deleteEntry ( BaseObject baseObject ) {

		if ( mAllSites != null && mAllSites.size() > 0 ) {
			mAllSites.remove(baseObject);
		}

		if ( mSiteNavigationObjects != null && mSiteNavigationObjects.size() > 0 ) {
			String keyString = mContext.getString(R.string.newsiteTitle);

			if ( mSiteNavigationObjects.containsKey(keyString) ) {
				mSiteNavigationObjects.get(keyString).remove(baseObject);
			}
		}
	}

	/**
	 * 删除网站
	 */
	public void deleteSite ( final BaseObject baseObject ) {

		new Thread(new Runnable() {

			@Override
			public void run ( ) {

				String select = GreenSites.URL + "=? and " + GreenSites.FLAG + "=?";
				String[] selectArg = { baseObject.getUrl(), String.valueOf(com.android.browser.Browser.SJ_FLAG) };
				mContentResolver.delete(GREENSITE_URI, select, selectArg);

				deleteEntry(baseObject);
			}
		}).start();
	}

	/**
	 * 得到分类导航站点
	 * 
	 * @return LinkedHashMap<String, List<BaseObject>>
	 */
	public LinkedHashMap< String , List< BaseObject>> getCatalogSiteNavigation ( ) {

		if ( mSiteNavigationObjects != null && mSiteNavigationObjects.size() > 0 ) {
			Log.i(LOG_TAG, "--------------");

			return mSiteNavigationObjects;
		}

		mSiteNavigationObjects = new LinkedHashMap< String , List< BaseObject>>();
		List< BaseObject> mBaseObjects;
		String catalogString;
		for ( BaseObject baseObject : mAllSites ) {
			Log.i("---------", baseObject.toString());

			catalogString = baseObject.getCatalog();
			if ( mSiteNavigationObjects.containsKey(catalogString) ) {
				mBaseObjects = mSiteNavigationObjects.get(catalogString);
				mBaseObjects.add(baseObject);
			} else {
				mBaseObjects = new ArrayList< BaseObject>();
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
	public List< BaseObject> getFrequentSites ( int maxFrequentsites ) {

		List< BaseObject> mList = getVisitDescSort();
		if ( null == mList ) {

			return null;
		}
		if ( mList.size() > maxFrequentsites ) {
			return mList.subList(0, maxFrequentsites);
		} else {
			return mList.subList(0, mList.size());
		}
	}

	/**
	 * 得到我的站点
	 * 
	 * @return List<BaseObject>
	 */
	public List< BaseObject> getMySites ( ) {

		mMySitesObjects = new ArrayList< BaseObject>();

		ContentResolver cr = mContext.getContentResolver();
		String[] project = { BookmarkColumns.TITLE, BookmarkColumns.URL, BookmarkColumns.BOOKMARK, BookmarkColumns._ID };
		Cursor cursor = cr.query(Browser.BOOKMARKS_URI, project, null, null, null);

		if ( null == cursor ) {

			return mMySitesObjects;
		}

		while ( cursor.moveToNext() ) {

			if ( cursor.getString(2).equals("1") ) {
				String title = cursor.getString(0);
				String url = cursor.getString(1);
				long id = cursor.getLong(3);

				BaseObject object = new BaseObject();
				object.setName(title);
				object.setUrl(url);
				object.set_ID(id);

				if ( url != null ) {

					mMySitesObjects.add(object);
				}
			}
		}

		cursor.close();

		return mMySitesObjects;
	}

	/**
	 * 得到家长新增站点
	 * 
	 * @return List<BaseObject>
	 */
	public List< BaseObject> getNewSites ( ) {

		if ( mSiteNavigationObjects != null && mSiteNavigationObjects.size() > 0 ) {
			String keyString = mContext.getString(R.string.newsiteTitle);
			if ( mSiteNavigationObjects.containsKey(keyString) ) {
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
	public BaseObject getNextFrequentSite ( int maxFrequentsites ) {

		List< BaseObject> mList = getVisitDescSort();

		if ( mList.size() > maxFrequentsites ) {
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
	public List< BaseObject> getNextCountFrequentSite ( int couunt , int maxFrequentsites ) {

		List< BaseObject> mList = getVisitDescSort();

		if ( mList.size() > maxFrequentsites + couunt ) {

			return mList.subList(maxFrequentsites, maxFrequentsites + couunt);
		}
		return null;
	}

	/**
	 * 按访问次数降序排列
	 */
	private List< BaseObject> getVisitDescSort ( ) {

		if ( !mAllSites.isEmpty() ) {
			List< BaseObject> tmpList = new ArrayList< BaseObject>();
			tmpList.addAll(mAllSites);

			Collections.sort(tmpList, new Comparator< BaseObject>() {

				@Override
				public int compare ( BaseObject lhs , BaseObject rhs ) {

					if ( rhs.getVisits() < lhs.getVisits() ) {
						return -1;
					} else if ( rhs.getVisits() == lhs.getVisits() ) {
						return 0;
					} else if ( rhs.getVisits() > lhs.getVisits() ) {
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
	 * 加载所有站点
	 */
	public void loadAllSites ( ) {

		new AsyncTask< Object , Object , Object>() {

			@Override
			protected Object doInBackground ( Object ... arg0 ) {

				String select = "flag=?";
				String[] selectArgs = { String.valueOf(com.android.browser.Browser.SJ_FLAG) };
				Cursor c = mContentResolver.query(UpdatePeriod.UPDATEPERIOD_URI, null, select, selectArgs, null);
				c.moveToFirst();
				long last_time = c.getLong(c.getColumnIndex(UpdatePeriod.TIME));
				c.close();
				long curt_time = System.currentTimeMillis();

				if ( curt_time - last_time >= update_period ) {

					return accquireWebSite();

				}
				return null;
			}

			@SuppressWarnings ( "unchecked" )
			protected void onPostExecute ( Object result ) {

				List< BaseObject> listb = (List< BaseObject>) result;
				if ( listb != null && listb.size() != 0 ) {

					updatePeriodTime(System.currentTimeMillis());
					accquireAllSites();
					deleteAllSites();
					wrtieDataToSites(listb);
					updateAllSites();
				}

				if ( listener != null ) {

					listener.loadFinished();
				}
			};
		}.execute();

	}

	public void loadAllSitesFromDatabase ( ) {

		mSiteNavigationObjects = new LinkedHashMap< String , List< BaseObject>>();
		List< BaseObject> mBaseObjects;
		String catalogString;

		mAllSites = new ArrayList< BaseObject>();
		BaseObject baseObject;
		Cursor cursor = null;
		try {
			String select = "flag=?";
			String[] selectArgs = { String.valueOf(com.android.browser.Browser.SJ_FLAG) };
			cursor = mContentResolver.query(GREENSITE_URI, null, select, selectArgs, null);

			int catalogCol = cursor.getColumnIndex(GreenSites.CATALOG);
			int titleCol = cursor.getColumnIndex(GreenSites.TITLE);
			int urlCol = cursor.getColumnIndex(GreenSites.URL);
			int logoCol = cursor.getColumnIndex(GreenSites.LOGO);
			int visitsCol = cursor.getColumnIndex(GreenSites.VISITS);
			int ismeCol = cursor.getColumnIndex(GreenSites.ISME);
			int presetCol = cursor.getColumnIndex(GreenSites.ISPRESET);

			while ( cursor.moveToNext() ) {
				baseObject = new BaseObject();
				baseObject.setCatalog(cursor.getString(catalogCol));
				baseObject.setName(cursor.getString(titleCol));
				baseObject.setUrl(cursor.getString(urlCol));

				String logo = cursor.getString(logoCol);
				if ( !TextUtils.isEmpty(logo) ) {
					baseObject.setLogo(LOGO_URL + logo);
				}

				baseObject.setVisits(cursor.getInt(visitsCol));
				baseObject.setIsme(cursor.getInt(ismeCol));
				baseObject.setPreset(cursor.getInt(presetCol));
				localizable(baseObject);

				mAllSites.add(baseObject);

				catalogString = baseObject.getCatalog();
				if ( mSiteNavigationObjects.containsKey(catalogString) ) {
					mBaseObjects = mSiteNavigationObjects.get(catalogString);
					mBaseObjects.add(baseObject);
				} else {
					mBaseObjects = new ArrayList< BaseObject>();
					mBaseObjects.add(baseObject);
					mSiteNavigationObjects.put(catalogString, mBaseObjects);
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( cursor != null )
				cursor.close();
		}
	}

	/**
	 * 本地化
	 * 
	 * @param baseObject
	 */
	private void localizable ( BaseObject baseObject ) {

		if ( baseObject.getName().equals(mContext.getString(R.string.logo_whys)) ) {
			baseObject.setResId(R.drawable.logo_whys);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_aoshu)) ) {
			baseObject.setResId(R.drawable.logo_aoshu);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_jyeoo)) ) {
			baseObject.setResId(R.drawable.logo_jyeoo);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_china_education_online)) ) {
			baseObject.setResId(R.drawable.logo_china_education_online);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_kid_qq)) ) {
			baseObject.setResId(R.drawable.logo_kid_qq);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_children_english_online)) ) {
			baseObject.setResId(R.drawable.logo_children_english_online);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_xxyw)) ) {
			baseObject.setResId(R.drawable.logo_xxyw);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_116online)) ) {
			baseObject.setResId(R.drawable.logo_116online);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_eebbk)) ) {
			baseObject.setResId(R.drawable.logo_eebbk);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_education_qq)) ) {
			baseObject.setResId(R.drawable.logo_education_qq);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_crazy_english)) ) {
			baseObject.setResId(R.drawable.logo_crazy_english);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_middle_school_chinese_online)) ) {
			baseObject.setResId(R.drawable.logo_middle_school_chinese_online);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_global_english)) ) {
			baseObject.setResId(R.drawable.logo_global_english);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_ebigear)) ) {
			baseObject.setResId(R.drawable.logo_ebigear);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_sxydycom)) ) {
			baseObject.setResId(R.drawable.logo_sxydycom);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_52ektcn)) ) {
			baseObject.setResId(R.drawable.logo_52ektcn);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_kekenet)) ) {
			baseObject.setResId(R.drawable.logo_kekenet);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_ychxwnet)) ) {
			baseObject.setResId(R.drawable.logo_ychxwnet);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_education_sina)) ) {
			baseObject.setResId(R.drawable.logo_education_sina);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_disney)) ) {
			baseObject.setResId(R.drawable.logo_disney);
		} else if ( baseObject.getName().equals(mContext.getString(R.string.logo_xue999)) ) {
			baseObject.setResId(R.drawable.logo_xue999);
		}
	}

	/**
	 * 更新访问次数、我的网站
	 * 
	 * @param baseObject
	 */
	public void updateSite ( final BaseObject baseObject ) {

		new Thread(new Runnable() {

			@Override
			public void run ( ) {

				ContentValues values = new ContentValues();
				values.put(GreenSites.VISITS, baseObject.getVisits());
				String select = GreenSites.URL + "=? and " + GreenSites.FLAG + "=?";
				String[] selectArg = { baseObject.getUrl(), String.valueOf(com.android.browser.Browser.SJ_FLAG) };
				mContentResolver.update(GREENSITE_URI, values, select, selectArg);
			}
		}).start();
	}

	public List< BaseObject> accquireWebSite ( ) {

		long start = System.currentTimeMillis();
		Connect connect = new Connect();
		List< BaseObject> b = (List< BaseObject>) connect.connect(0, String.valueOf(com.android.browser.Browser.SJ_FLAG));
		if ( b != null ) {

			for ( int i = 0 ; i < b.size() ; i++ ) {

				Log.d("Liu Test", " URL = " + b.get(i).getName());
			}
		}
		long duration = System.currentTimeMillis() - start;
		Log.i(LOG_TAG, "loadAllSites操作耗时：" + duration);

		return b;
	}

	private void updatePeriodTime ( long t ) {

		ContentValues cv = new ContentValues();
		cv.put(UpdatePeriod.TIME, t);
		String select = "flag=?";
		String[] selectArgs = { String.valueOf(com.android.browser.Browser.SJ_FLAG) };
		mContentResolver.update(UpdatePeriod.UPDATEPERIOD_URI, cv, select, selectArgs);

	}

	private void accquireAllSites ( ) {

		mAllSites = new ArrayList< BaseObject>();
		BaseObject baseObject;
		Cursor cursor = null;
		try {
			String[] project = { GreenSites.URL, GreenSites.VISITS };
			String select = "flag=? and preset=0";
			String[] selectArgs = { String.valueOf(com.android.browser.Browser.SJ_FLAG) };
			cursor = mContentResolver.query(GREENSITE_URI, project, select, selectArgs, null);

			int urlCol = cursor.getColumnIndex(GreenSites.URL);

			int visitsCol = cursor.getColumnIndex(GreenSites.VISITS);

			while ( cursor != null && cursor.moveToNext() ) {

				baseObject = new BaseObject();
				baseObject.setUrl(cursor.getString(urlCol));
				baseObject.setVisits(cursor.getInt(visitsCol));
				mAllSites.add(baseObject);

			}
			cursor.close();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	private void deleteAllSites ( ) {

		String select = "flag=? and preset=0";
		String[] selectArgs = { String.valueOf(com.android.browser.Browser.SJ_FLAG) };
		mContentResolver.delete(GreenSites.GREENSITE_URI, select, selectArgs);
	}

	private void wrtieDataToSites ( List< BaseObject> b ) {

		for ( int i = 0 ; i < b.size() ; i++ ) {

			BaseObject bo = b.get(i);
			ContentValues cv = new ContentValues();

			cv.put(GreenSites.CATALOGSN, bo.getCatalogsn());
			cv.put(GreenSites.CATALOG, bo.getCatalog());
			cv.put(GreenSites.TITLE, bo.getName());
			cv.put(GreenSites.URL, bo.getUrl());
			cv.put(GreenSites.LOGO, bo.getLogo());
			cv.put(GreenSites.VISITS, bo.getVisits());
			cv.put(GreenSites.ISME, 0);
			cv.put(GreenSites.ISPRESET, 0);
			cv.put(GreenSites.FLAG, bo.getSuitableCrowd());

			mContentResolver.insert(GreenSites.GREENSITE_URI, cv);
		}
	}

	private void updateAllSites ( ) {

		for ( int i = 0 ; i < mAllSites.size() ; i++ ) {

			updateSite(mAllSites.get(i));
		}
	}

	public interface DataModeManagerListener {

		public void loadFinished ( );
	}
}
