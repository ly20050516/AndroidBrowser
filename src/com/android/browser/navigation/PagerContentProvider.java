
package com.android.browser.navigation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.android.browser.R;

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
import android.text.TextUtils;
import android.util.Log;

public class PagerContentProvider extends ContentProvider {

	static class DatabaseHelper extends SQLiteOpenHelper {

		static final String DATABASE_NAME = "greenbrowser.db";

		static final int DATABASE_VERSION = 2; // 变更时增1

		private Context mContext;

		public DatabaseHelper ( Context context ) {

			this(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public DatabaseHelper ( Context context , String name , CursorFactory factory , int version ) {

			super(context, name, factory, version);

			mContext = context;
			initDatabase(R.raw.greenbrowser);

			Log.d("Liu Test", getClass().getSimpleName() + "  DatabaseHelper(Context context, String name, CursorFactory factory, int version)");

		}

		/**
		 * 初始化数据
		 * 
		 * @param rawid
		 */
		private void initDatabase ( int rawid ) {

			Log.d("Liu Test", getClass().getSimpleName() + " initDatabase(int rawid)");
			File dbfile = mContext.getDatabasePath(DATABASE_NAME);
			if ( dbfile.exists() ) {

				return;
			}

			InputStream is = null;
			FileOutputStream fos = null;
			try {
				dbfile.getParentFile().mkdirs();
				is = mContext.getResources().openRawResource(rawid);
				fos = new FileOutputStream(dbfile);
				byte[] buffer = new byte[ 8192 ];
				int count = 0;
				while ( (count = is.read(buffer)) > 0 ) {
					fos.write(buffer, 0, count);
				}

				fos.close();
				is.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}

		@Override
		public void onCreate ( SQLiteDatabase db ) {

			db.execSQL("CREATE TABLE IF NOT EXISTS "
					+ TABLE_HISTORYSITES
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL,flag INT NOT NULL, url TEXT NOT NULL UNIQUE, visittime TIMESTAMP DEFAULT(datetime('now','localtime'))) ");
		}

		@Override
		public void onUpgrade ( SQLiteDatabase db , int oldVersion , int newVersion ) {

			db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORYSITES);

			db.execSQL("CREATE TABLE IF NOT EXISTS "
					+ TABLE_HISTORYSITES
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL,flag INT NOT NULL, url TEXT NOT NULL UNIQUE, visittime TIMESTAMP DEFAULT(datetime('now','localtime'))) ");
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

		// + " WHERE date(visittime,'Localtime')=Date('now','Localtime') ";
	}

	static interface UriCode {

		public static final int GREENSITES = 10;

		public static final int GREENSITES_CUSTOM = 11;

		public static final int HISTORYSITES = 100;

	}

	public static interface UriPath {

		public static final String GREENSITES = "greensites";

		public static final String GREENSITES_CUSTOM = "greensites/custom";

		public static final String HISTORYSITES = "historysites";
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

	static {

		sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sMatcher.addURI(AUTHORITY, UriPath.GREENSITES, UriCode.GREENSITES);
		sMatcher.addURI(AUTHORITY, UriPath.GREENSITES_CUSTOM, UriCode.GREENSITES_CUSTOM);
		sMatcher.addURI(AUTHORITY, UriPath.HISTORYSITES, UriCode.HISTORYSITES);
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

		default :
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert ( Uri uri , ContentValues values ) {

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

			long rowId = mOpenHelper.getWritableDatabase().insert(TABLE_GREENSITES, null, values);
			if ( rowId > 0 ) {

				Uri rowUri = ContentUris.withAppendedId(GreenSites.GREENSITE_URI, rowId);
				getContext().getContentResolver().notifyChange(rowUri, null);
				return rowUri;
			}

			throw new SQLException("Failed to insert row into " + uri);

		case UriCode.HISTORYSITES :

			rowId = mOpenHelper.getWritableDatabase().insert(TABLE_HISTORYSITES, null, values);

			if ( rowId > 0 ) {

				Uri rowUri = ContentUris.withAppendedId(HistorySites.HISTORYSITES_URI, rowId);
				getContext().getContentResolver().notifyChange(rowUri, null);
				return rowUri;

			}

			throw new SQLException("Failed to insert row into " + uri);

		default :
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
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

		switch ( sMatcher.match(uri) ) {
		case UriCode.GREENSITES :
		case UriCode.GREENSITES_CUSTOM :
			count = mOpenHelper.getWritableDatabase().update(TABLE_GREENSITES, values, whereClause, whereArgs);
			break;

		case UriCode.HISTORYSITES :
			count = mOpenHelper.getWritableDatabase().update(TABLE_HISTORYSITES, values, whereClause, whereArgs);
			break;

		default :
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		if ( count > 0 ) {

			getContext().getContentResolver().notifyChange(uri, null);
		}

		return count;
	}

}
