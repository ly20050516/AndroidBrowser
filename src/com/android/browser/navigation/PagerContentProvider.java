
package com.android.browser.navigation;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.android.browser.R;

public class PagerContentProvider extends ContentProvider {

	static class DatabaseHelper extends SQLiteOpenHelper {

		static final String DATABASE_NAME = "greenbrowser.db";

		static final int DATABASE_VERSION = 2; // 变更时增1

		
		public DatabaseHelper ( Context context ) {

			this(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public DatabaseHelper ( Context context , String name , CursorFactory factory , int version ) {

			super(context, name, factory, version);

			

			Log.d("Liu Test", getClass().getSimpleName() + "  DatabaseHelper(Context context, String name, CursorFactory factory, int version)");

		}

		private void initUpdatePeriod ( SQLiteDatabase db ) {

			String sql1 = "CREATE TABLE greensites(_id INTEGER PRIMARY KEY AUTOINCREMENT, catalog TEXT DEFAULT '自定义', title TEXT NOT NULL, url TEXT NOT NULL, logo TEXT,catalogsn INTEGER DEFAULT 0, visits INTEGER DEFAULT 0, isme INTEGER DEFAULT 0, isnew INTEGER DEFAULT 0,preset INTEGER DEFAULT 0, addtime TIMESTAMP DEFAULT(datetime('now','localtime')), flag INTEGER DEFAULT 0)";
			String sql2 = "CREATE UNIQUE INDEX [url_index] on greensites ([url],[flag])";

			db.execSQL(sql1);
			db.execSQL(sql2);
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_UPDATE_PERIOD + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,time INTEGER DEFAULT 0,flag INTEGER NOT NULL UNIQUE)");
			db.execSQL("CREATE TABLE IF NOT EXISTS "
					+ TABLE_HISTORYSITES
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL,flag INT NOT NULL, url TEXT NOT NULL UNIQUE, visittime TIMESTAMP DEFAULT(datetime('now','localtime'))) ");

			ContentValues cv = new ContentValues();
			cv.put(UpdatePeriod.FLAG, 0);
			cv.put(UpdatePeriod.TIME, 0);
			db.insert(TABLE_UPDATE_PERIOD, null, cv);

			cv = new ContentValues();
			cv.put(UpdatePeriod.FLAG, 1);
			cv.put(UpdatePeriod.TIME, 0);
			db.insert(TABLE_UPDATE_PERIOD, null, cv);
		}

		@Override
		public void onCreate ( SQLiteDatabase db ) {

			Log.d("Liu Test", getClass().getSimpleName() + "  onCreate");
			initUpdatePeriod(db);
		}

		@Override
		public void onUpgrade ( SQLiteDatabase db , int oldVersion , int newVersion ) {

			db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORYSITES);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_UPDATE_PERIOD);

			initUpdatePeriod(db);
		}
	}

	public static interface GreenSites extends BaseColumns {

		/** 网站分类 */
		public static final String CATALOG = "catalog";

		/** 网站分类排序 */
		public static final String CATALOGSN = "catalogsn";

		public static final Uri GREENSITE_CUSTOM_URI = Uri.withAppendedPath(AUTHORITY_URI, UriPath.GREENSITES_CUSTOM);

		public static final Uri GREENSITE_URI = Uri.withAppendedPath(AUTHORITY_URI, UriPath.GREENSITES);

		/** 我的网站 */
		public static final String ISME = "isme";

		/** 是否预置 */
		public static final String ISPRESET = "preset";

		/** 网站logo */
		public static final String LOGO = "logo";

		public static final String ORDER_BY_SELECT_ALL = " catalogsn, _id DESC ";

		public static final String ORDER_BY_SELECT_CUSTOM = " addtime ";

		/** 全表检索 */
		public static final String SELECT_ALL = "SELECT catalog, title, url, logo, visits, isme, preset FROM " + TABLE_GREENSITES + "WHERE flag=?";

		/** 自定义检索 */
		public static final String SELECT_CUSTOM = "SELECT title, url FROM " + TABLE_GREENSITES + " WHERE preset=1 and flag=?";

		public static final String SELECT_MAXTIME = "SELECT MAX(ADDTIME) FROM ALLSITES ";

		/** 网站标题 */
		public static final String TITLE = "title";

		/** 添加日期 */
		public static final String UPDATE = "addtime";

		/** 网站URL */
		public static final String URL = "url";

		/** 访问次数 */
		public static final String VISITS = "visits";

		public static final String FLAG = "flag";
	}

	public static interface HistorySites extends BaseColumns {

		public static final Uri HISTORYSITES_URI = Uri.withAppendedPath(AUTHORITY_URI, UriPath.HISTORYSITES);

		public static final String ORDER_BY_SELECT_HISTORY = " visittime ";

		/** 查历史记录显示 */
		public static final String SELECT_HISTORY = "SELECT title, url FROM " + TABLE_HISTORYSITES + "WHERE flag=?";

		/** 网站标题 */
		public static final String TITLE = "title";

		/** URL */
		public static final String URL = "url";

		/** 访问时间 */
		public static final String VISITDATE = "visittime";

		public static final String FLAG = "flag";

	}

	public static interface UpdatePeriod extends BaseColumns {

		public static final Uri UPDATEPERIOD_URI = Uri.withAppendedPath(AUTHORITY_URI, UriPath.UPDATE_PERIOD);

		public static final String FLAG = "flag";

		public static final String TIME = "time";
	}

	static interface UriCode {

		public static final int GREENSITES = 10;

		public static final int GREENSITES_CUSTOM = 11;

		public static final int HISTORYSITES = 100;

		public static final int UPDATE_PERIOD = 101;

	}

	public static interface UriPath {

		public static final String GREENSITES = "greensites";

		public static final String GREENSITES_CUSTOM = "greensites/custom";

		public static final String HISTORYSITES = "historysites";

		public static final String UPDATE_PERIOD = "historysites";
	}

	static final String AUTHORITY = "com.android.browser.green";

	static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

	static final UriMatcher sMatcher;

	static final String TABLE_GREENSITES = "greensites";

	static final String TABLE_GREENSITES_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.android.browser.green.greensites";

	static final String TABLE_GREENSITES_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.android.browser.green.greensites";

	static final String TABLE_HISTORYSITES = "historysites";

	static final String TABLE_HISTORYSITES_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.android.browser.green.historysites";

	static final String TABLE_HISTORYSITES_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.android.browser.green.historysites";

	static final String TABLE_UPDATE_PERIOD = "updateperiod";

	static final String TABLE_UPDATE_PERIOD_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.android.browser.green.updateperoid";

	static final String TABLE_UPDATE_PERIOD_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.android.browser.green.updateperoid";

	static {

		sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sMatcher.addURI(AUTHORITY, UriPath.GREENSITES, UriCode.GREENSITES);
		sMatcher.addURI(AUTHORITY, UriPath.GREENSITES_CUSTOM, UriCode.GREENSITES_CUSTOM);
		sMatcher.addURI(AUTHORITY, UriPath.HISTORYSITES, UriCode.HISTORYSITES);
		sMatcher.addURI(AUTHORITY, UriPath.UPDATE_PERIOD, UriCode.UPDATE_PERIOD);

	}

	DatabaseHelper mOpenHelper;

	@Override
	public int delete ( Uri uri , String whereClause , String[] whereArgs ) {

		// TODO Auto-generated method stub
		int count = 0;

		switch ( sMatcher.match(uri) ) {
		case UriCode.GREENSITES :
		case UriCode.GREENSITES_CUSTOM :
			count = mOpenHelper.getWritableDatabase().delete(TABLE_GREENSITES, whereClause, whereArgs);
			break;

		case UriCode.HISTORYSITES :
			count = mOpenHelper.getWritableDatabase().delete(TABLE_HISTORYSITES, whereClause, whereArgs);
			break;
		case UriCode.UPDATE_PERIOD :
			count = mOpenHelper.getWritableDatabase().delete(TABLE_UPDATE_PERIOD, whereClause, whereArgs);
			break;
		default :
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		if ( count > 0 ) {

			getContext().getContentResolver().notifyChange(uri, null);
		}

		return count;
	}

	@Override
	public String getType ( Uri uri ) {

		switch ( sMatcher.match(uri) ) {

		case UriCode.GREENSITES :

			return TABLE_GREENSITES_CONTENT_TYPE;
		case UriCode.GREENSITES_CUSTOM :

			return TABLE_GREENSITES_CONTENT_ITEM_TYPE;
		case UriCode.HISTORYSITES :

			return TABLE_HISTORYSITES_CONTENT_TYPE;
		case UriCode.UPDATE_PERIOD :
			return TABLE_UPDATE_PERIOD_CONTENT_ITEM_TYPE;
		default :
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert ( Uri uri , ContentValues values ) {

		String tables = null;
		Uri insert_uri = null;
		switch ( sMatcher.match(uri) ) {

		case UriCode.GREENSITES :
		case UriCode.GREENSITES_CUSTOM :
			if ( !values.containsKey(GreenSites.CATALOG) ) {

				values.put(GreenSites.CATALOG, getContext().getString(R.string.newsiteTitle));
				if ( !values.containsKey(GreenSites.CATALOGSN) ) {

					values.put(GreenSites.CATALOGSN, 1000);
				}
			}

			if ( !values.containsKey(GreenSites.ISPRESET) ) {

				values.put(GreenSites.ISPRESET, 1);
			}

			tables = TABLE_GREENSITES;
			insert_uri = GreenSites.GREENSITE_URI;

			break;

		case UriCode.HISTORYSITES :

			tables = TABLE_HISTORYSITES;
			insert_uri = HistorySites.HISTORYSITES_URI;

			break;
		case UriCode.UPDATE_PERIOD : {

			tables = TABLE_UPDATE_PERIOD;
			insert_uri = UpdatePeriod.UPDATEPERIOD_URI;
			break;
		}

		default :
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		long rowId = mOpenHelper.getWritableDatabase().insert(tables, null, values);
		if ( rowId > 0 ) {

			Uri rowUri = ContentUris.withAppendedId(insert_uri, rowId);
			getContext().getContentResolver().notifyChange(rowUri, null);
			return rowUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate ( ) {

		Log.d("------", "onCreate");
		mOpenHelper = new DatabaseHelper(getContext());

		return true;
	}

	@Override
	public Cursor query ( Uri uri , String[] columns , String selection , String[] selectionArgs , String orderBy ) {

		Log.d("------", String.valueOf(sMatcher.match(uri)));

		String table = null;

		switch ( sMatcher.match(uri) ) {
		case UriCode.GREENSITES :
			table = TABLE_GREENSITES;
			orderBy = GreenSites.ORDER_BY_SELECT_ALL;
			break;

		case UriCode.GREENSITES_CUSTOM :

			table = TABLE_GREENSITES;
			orderBy = GreenSites.ORDER_BY_SELECT_CUSTOM;
			break;

		case UriCode.HISTORYSITES :

			table = TABLE_HISTORYSITES;
			orderBy = HistorySites.ORDER_BY_SELECT_HISTORY;

			break;
		case UriCode.UPDATE_PERIOD :

			table = TABLE_UPDATE_PERIOD;
			orderBy = null;

			break;
		default :
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		Cursor c = mOpenHelper.getReadableDatabase().query(table, columns, selection, selectionArgs, null, null, orderBy);

		if ( c != null ) {
			c.setNotificationUri(getContext().getContentResolver(), uri);
		}

		return c;
	}

	@Override
	public int update ( Uri uri , ContentValues values , String whereClause , String[] whereArgs ) {

		int count = 0;
		String table = "";
		switch ( sMatcher.match(uri) ) {
		case UriCode.GREENSITES :
		case UriCode.GREENSITES_CUSTOM :
			table = TABLE_GREENSITES;
			break;
		case UriCode.HISTORYSITES :
			table = TABLE_HISTORYSITES;
			break;
		case UriCode.UPDATE_PERIOD :
			table = TABLE_UPDATE_PERIOD;
			break;
		default :
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		count = mOpenHelper.getWritableDatabase().update(table, values, whereClause, whereArgs);
		if ( count > 0 ) {

			getContext().getContentResolver().notifyChange(uri, null);
		}

		return count;
	}

}
