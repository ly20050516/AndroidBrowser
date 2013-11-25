
package com.android.browser.navigation;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.BrowserContract;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.browser.R;
import com.android.browser.navigation.DataModelManager.DataModeManagerListener;
import com.android.browser.navigation.MySiteItem.Mode;
import com.android.browser.navigation.MySitesView.SitesType;

/**
 * 
 * 说明：主页面HOMEVIEW<br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
public class HomeView extends RelativeLayout implements DataModeManagerListener {

	/**
	 * 
	 * 说明：OnClickListener<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	class AllOnClickListener implements OnClickListener {

		@Override
		public void onClick ( View v ) {

			switch ( v.getId() ) {

			case R.id.btn_mysites :

				mViewPager.setCurrentItem(MYNAVIGATION);
				setCurrentPage(MYNAVIGATION);

				break;

			case R.id.btn_allsites :

				mViewPager.setCurrentItem(SITENAVIGATION);
				setCurrentPage(SITENAVIGATION);
				break;

			case R.id.btn_back :

				switch ( currentPage ) {

				case MYNAVIGATION :
					mViewPager.setCurrentItem(SITENAVIGATION);
					setCurrentPage(SITENAVIGATION);
					break;

				case SITENAVIGATION :
					mViewPager.setCurrentItem(MYNAVIGATION);
					setCurrentPage(MYNAVIGATION);
					break;
				}
				break;

			case R.id.btn_delete :

				setMode(mFrequentSitesSitesItem.getMode());
				break;

			}
		}
	}

	/**
	 * 
	 * 说明：异步加载类<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author
	 * @version
	 */
	class AsynLoader extends AsyncTask< Void , Void , Boolean> {

		private int mode = 0;

		public AsynLoader ( int mode ) {

			this.mode = mode;
		}

		@Override
		protected Boolean doInBackground ( Void ... params ) {

			mDataManager.loadAllSitesFromDatabase();

			return true;
		}

		@Override
		protected void onPostExecute ( Boolean result ) {

			super.onPostExecute(result);

			if ( 0 == mode ) {

				loadCatalogSites();
			}
			loadFrequentSites();
			loadNewSites();
			loadMySites();
		}

	}

	/**
	 * 
	 * 说明：数据加载者Handler<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	@SuppressWarnings ( "unchecked" )
	static class LoaderHandler extends Handler {

		WeakReference< HomeView> mHomeView;

		LoaderHandler ( HomeView mHomeView ) {

			this.mHomeView = new WeakReference< HomeView>(mHomeView);
		}

		@Override
		public void handleMessage ( Message msg ) {

			HomeView mView = mHomeView.get();
			switch ( msg.what ) {

			case LoaderMessage.LOADFREQUENTSITES :

				mView.mFrequentSitesSitesItem.setConentView(true, (List< BaseObject>) msg.obj, mView.mImageLoader);
				break;

			case LoaderMessage.LOADMYSITES :

				mView.mMySitesSitesItem.setConentView(false, (List< BaseObject>) msg.obj);
				break;

			case LoaderMessage.LOADNEWSITES :

				mView.mNewSitesSitesItem.setConentView(false, (List< BaseObject>) msg.obj);

				break;

			case LoaderMessage.LOADCATALOGSITES :

				mView.mSiteNavigationScrollView.setContentView(((LinkedHashMap< String , List< BaseObject>>) msg.obj));
				break;

			case LoaderMessage.LOADNETWORK :

				break;
			}

			super.handleMessage(msg);
		}
	}

	/**
	 * 
	 * 说明：加载数据的消息类型<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	class LoaderMessage {

		/** 加载分类站点消息 */
		static final int LOADCATALOGSITES = 0X00003;

		/** 加载高频站点消息 */
		static final int LOADFREQUENTSITES = 0X00000;

		/** 加载我的网站消息 */
		static final int LOADMYSITES = 0X000001;

		/** 加载网络数据 */
		static final int LOADNETWORK = 0X00004;

		/** 加载新网推荐消息 */
		static final int LOADNEWSITES = 0X000002;
	}

	/**
	 * 
	 * 说明：页面改变监听<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	class MyPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled ( int arg0 , float arg1 , int arg2 ) {

		}

		@Override
		public void onPageScrollStateChanged ( int arg0 ) {

		}

		@Override
		public void onPageSelected ( int arg0 ) {

			animationCursor(arg0);
			setCurrentPage(arg0);

		}
	}

	private void animationCursor ( int index ) {

		Animation animation = null;

		switch ( index ) {

		case MYNAVIGATION :

			animation = new TranslateAnimation(120, 0, 0, 0);
			break;

		case SITENAVIGATION :

			animation = new TranslateAnimation(0, 120, 0, 0);
			break;
		}

		currentPage = index;
		animation.setFillEnabled(true);
		animation.setFillAfter(true);
		animation.setDuration(300);
		mCursor.startAnimation(animation);

	}

	/**
	 * 
	 * 说明：网址导航监听类<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	class MySitesViewOnSiteNavigationListener implements MySitesView.OnSiteNavigationListener , AllSitesView.OnSiteNavigationListener {

		@Override
		public void onSiteNavigation ( final View v ) {

			updateSite(v.getTag());

			onSiteNavigationListener.onSiteNavigation(v);
		}

		/**
		 * 更新网站对象
		 * 
		 * @param object
		 */
		private void updateSite ( final Object object ) {

			new Thread(new Runnable() {

				@Override
				public void run ( ) {

					BaseObject baseObject = (BaseObject) object;
					if ( baseObject.getVisits() == -1 ) {

						baseObject.setVisits(1);
					} else {

						baseObject.setVisits(baseObject.getVisits() + 1);
					}

					mDataManager.updateSite(baseObject);
				}
			}).start();
		}
	}

	/**
	 * 
	 * 说明：网址移除监听类<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	class MySitesViewOnSiteRemoveListener implements MySitesView.OnSiteRemoveListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.android.bbkgreenbrowser.ui.MySitesView.OnSiteRemoveListener#
		 * onSiteRemove (android.view.View)
		 */
		@Override
		public void onSiteRemove ( View v ) {

			BaseObject baseObject = (BaseObject) v.getTag();
			final int type = ((MySitesView) v.getParent().getParent().getParent()).getType();
			switch ( type ) {
			case SitesType.FREQUENTSITES :
				// 移除经常访问时标记访问次数-1 以便与未访问过的网站区别保证移除项排列到尾部
				baseObject.setVisits(-1);
				mDataManager.updateSite(baseObject);
				BaseObject baseObject2 = mDataManager.getNextFrequentSite(maxFrequentsites - 1);
				if ( baseObject2.getVisits() == -1 ) {
					Toast.makeText(mContext, mContext.getText(R.string.delete_frequent), Toast.LENGTH_SHORT).show();
					break;
				}
				mFrequentSitesSitesItem.removeView(v);
				mFrequentSitesSitesItem.addView(true, baseObject2);
				break;
			case SitesType.MYSITES :

				displayRemoveBookmarkDialog(baseObject.get_ID(), baseObject.getName(), mContext, null, v);

				break;
			case SitesType.NEWSITES :
				mNewSitesSitesItem.removeView(v);
				mDataManager.deleteSite(baseObject);
				mSiteNavigationScrollView.removeView(v);
				break;
			}
		}
	}

	private void displayRemoveBookmarkDialog ( final long id , final String title , final Context context , final Message msg , final View v ) {

		new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert).setMessage(context.getString(R.string.delete_bookmark_warning, title))
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick ( DialogInterface dialog , int whichButton ) {

						if ( msg != null ) {
							msg.sendToTarget();
						}
						Runnable runnable = new Runnable() {

							@Override
							public void run ( ) {

								Uri uri = ContentUris.withAppendedId(BrowserContract.Bookmarks.CONTENT_URI, id);
								context.getContentResolver().delete(uri, null, null);
							}
						};
						new Thread(runnable).start();
						mMySitesSitesItem.removeView(v);
					}
				}).setNegativeButton(R.string.cancel, null).show();
	}

	/**
	 * 
	 * 说明：网址导航接口<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	public interface OnSiteNavigationListener {

		public void onSiteNavigation ( View v );
	}

	private static final String LOG_TAG = "HomeView";

	/** 我的导航 */
	private final static int MYNAVIGATION = 0;

	/** 网址导航 */
	private final static int SITENAVIGATION = 1;

	private final static int MAX_FREQUENTSITES_PORT = 8;

	private final static int MAX_FREQUENTSITES_LAND = 10;

	private int currentPage = 0;

	private boolean isFirst = true;

	/** 经常访问站点最大值,设置默认值8 */
	private int maxFrequentsites = MAX_FREQUENTSITES_PORT;

	private Context mContext;

	private ImageView mCursor;

	/** 数据模型 */
	private DataModelManager mDataManager;

	private ImageView mDeleteButton;

	/** 我的导航 */
	private MySitesView mFrequentSitesSitesItem;

	/** LOGO图片加载 */
	private ImageLoader mImageLoader;

	private List< View> mListViews;

	/** 数据加载Handler */
	LoaderHandler mLoaderHandler = new LoaderHandler(this);

	private Button mMyNavigationButton;

	private MySitesView mMySitesSitesItem;

	private MySitesView mNewSitesSitesItem;

	private ImageView mPointImageButton;

	private AllSitesView mSiteNavigationScrollView;

	private Button mSitesNavigationButton;

	/** ViewPager */
	private ViewPager mViewPager;

	/** 网址导航接口 */
	public OnSiteNavigationListener onSiteNavigationListener;

	public HomeView ( Context context ) {

		this(context, null);
	}

	public HomeView ( Context context , AttributeSet attrs ) {

		super(context, attrs);

		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.view_home, this);
		mViewPager = (ViewPager) findViewById(R.id.homeFrameLayout);
		mMyNavigationButton = (Button) findViewById(R.id.btn_mysites);
		mSitesNavigationButton = (Button) findViewById(R.id.btn_allsites);
		mPointImageButton = (ImageView) findViewById(R.id.btn_back);
		mCursor = (ImageView) findViewById(R.id.line_bottom_scroll);
		mMyNavigationButton.setOnClickListener(new AllOnClickListener());
		mSitesNavigationButton.setOnClickListener(new AllOnClickListener());
		mPointImageButton.setOnClickListener(new AllOnClickListener());
		mDataManager = new DataModelManager(context);
		mDataManager.registerListener(this);
		mDataManager.loadAllSites();
		mImageLoader = new ImageLoader(context);
		mListViews = new ArrayList< View>();
		mListViews.add(setMyNavigationPage());
		mListViews.add(setSitesNavigationPage());
		mViewPager.setAdapter(new ViewPagerAdapter(mListViews));
		mViewPager.setOnPageChangeListener(new MyPageChangeListener());
	}

	/**
	 * 添加至我的网站
	 * 
	 * @param baseObject
	 */
	public void addMySites ( BaseObject baseObject ) {

		mMySitesSitesItem.addView(baseObject);
		mDataManager.updateSite(baseObject);
	}

	/**
	 * 是否存在
	 * 
	 * @param baseObject
	 * @return boolean
	 */
	public boolean isExist ( BaseObject baseObject ) {

		if ( mMySitesSitesItem.isExist(baseObject) ) {
			return true;
		}
		return false;
	}

	/**
	 * 加载分类站点
	 */
	private void loadCatalogSites ( ) {

		new Thread() {

			@Override
			public void run ( ) {

				Message msg = mLoaderHandler.obtainMessage();

				msg.obj = mDataManager.getCatalogSiteNavigation();

				msg.what = LoaderMessage.LOADCATALOGSITES;

				mLoaderHandler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 加载高频站点
	 */
	private void loadFrequentSites ( ) {

		new Thread() {

			@Override
			public void run ( ) {

				Message msg = mLoaderHandler.obtainMessage();

				msg.obj = mDataManager.getFrequentSites(maxFrequentsites);

				msg.what = LoaderMessage.LOADFREQUENTSITES;

				mLoaderHandler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 加载我的站点
	 */
	private void loadMySites ( ) {

		new Thread() {

			@Override
			public void run ( ) {

				Message msg = mLoaderHandler.obtainMessage();

				msg.obj = mDataManager.getMySites();

				msg.what = LoaderMessage.LOADMYSITES;

				mLoaderHandler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 加载新网推荐
	 */
	private void loadNewSites ( ) {

		new Thread() {

			@Override
			public void run ( ) {

				Message msg = mLoaderHandler.obtainMessage();

				msg.obj = mDataManager.getNewSites();

				msg.what = LoaderMessage.LOADNEWSITES;

				mLoaderHandler.sendMessage(msg);
			}
		}.start();
	}

	@Override
	protected void onConfigurationChanged ( Configuration newConfig ) {

		super.onConfigurationChanged(newConfig);
		if ( newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ) {

			if ( maxFrequentsites == MAX_FREQUENTSITES_PORT ) {
				mFrequentSitesSitesItem.addViews(true, mDataManager.getNextCountFrequentSite(2, maxFrequentsites));
				maxFrequentsites = MAX_FREQUENTSITES_LAND;
			}
		} else if ( newConfig.orientation == Configuration.ORIENTATION_PORTRAIT ) {

			if ( maxFrequentsites == MAX_FREQUENTSITES_LAND ) {
				mFrequentSitesSitesItem.removeViewsInLayout(maxFrequentsites - 2, 2);
				maxFrequentsites = MAX_FREQUENTSITES_PORT;
			}
		}

		Log.i(LOG_TAG, "onConfigurationChanged");
	}

	public void pause ( ) {

		
	}

	/**
	 * 从我的网站移除
	 * 
	 * @param baseObject
	 */
	public void removeMySites ( BaseObject baseObject ) {

		View view = mMySitesSitesItem.findViewWithTag(baseObject);
		mMySitesSitesItem.removeView(view);
		mDataManager.updateSite(baseObject);
	}

	public void resume ( ) {

		if ( !isFirst ) {
			Log.d("Liu Test", getClass().getSimpleName() + " resume");
			new AsynLoader(1).execute();
		}
		isFirst = false;
	}

	/**
	 * 设置当前页面
	 * 
	 * @param page
	 */
	private void setCurrentPage ( int page ) {

		currentPage = page;

		switch ( page ) {

		case MYNAVIGATION :
			mPointImageButton.setImageResource(R.drawable.btn_nexts);
			mSitesNavigationButton.setTextColor(Color.rgb(126, 126, 126));
			mMyNavigationButton.setTextColor(Color.rgb(76, 160, 234));
			break;

		case SITENAVIGATION :
			mPointImageButton.setImageResource(R.drawable.btn_backs);
			mMyNavigationButton.setTextColor(Color.rgb(126, 126, 126));
			mSitesNavigationButton.setTextColor(Color.rgb(76, 160, 234));

			if ( mFrequentSitesSitesItem.getMode() == Mode.DELETEMODE ) {
				this.setMode(Mode.DELETEMODE);
			}
			break;

		}
	}

	/**
	 * 设置高频访问站点
	 */
	private void setFrequentSites ( ) {

		mFrequentSitesSitesItem.setTitle(mContext.getString(R.string.frequentTitle));
		mFrequentSitesSitesItem.setTextColor(Color.rgb(242, 87, 153));
		mFrequentSitesSitesItem.setType(SitesType.FREQUENTSITES);
		mFrequentSitesSitesItem.setSplitlineBackgroundResource(R.drawable.splitline2);
		mFrequentSitesSitesItem.setOnSiteNavigationListener(new MySitesViewOnSiteNavigationListener());
		mFrequentSitesSitesItem.setOnSiteRemoveListener(new MySitesViewOnSiteRemoveListener());
		// 横竖屏根据屏幕改变经常访问最大值
		if ( mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ) {
			maxFrequentsites = MAX_FREQUENTSITES_LAND;
		} else if ( mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ) {
			maxFrequentsites = MAX_FREQUENTSITES_PORT;
		}
	}

	/**
	 * 设置显示模式
	 * 
	 * @param mode
	 */
	private void setMode ( int mode ) {

		switch ( mode ) {

		case Mode.BROWSERMODE :

			mDeleteButton.setImageResource(R.drawable.btn_finishs);
			mFrequentSitesSitesItem.setMode(Mode.DELETEMODE);
			mMySitesSitesItem.setMode(Mode.DELETEMODE);
			mNewSitesSitesItem.setMode(Mode.DELETEMODE);
			break;

		case Mode.DELETEMODE :

			mDeleteButton.setImageResource(R.drawable.btn_deletes);
			mFrequentSitesSitesItem.setMode(Mode.BROWSERMODE);
			mMySitesSitesItem.setMode(Mode.BROWSERMODE);
			mNewSitesSitesItem.setMode(Mode.BROWSERMODE);
			break;
		}

	}

	/**
	 * 设置我的导航
	 */
	private View setMyNavigationPage ( ) {

		View view = LayoutInflater.from(mContext).inflate(R.layout.my_navigation, null);
		mDeleteButton = (ImageView) view.findViewById(R.id.btn_delete);
		mDeleteButton.setOnClickListener(new AllOnClickListener());
		mFrequentSitesSitesItem = (MySitesView) view.findViewById(R.id.frequentSitesSitesItem);
		mMySitesSitesItem = (MySitesView) view.findViewById(R.id.mySitesSitesItem);
		mNewSitesSitesItem = (MySitesView) view.findViewById(R.id.newSitesSitesItem);

		setFrequentSites();
		setMySites();
		setNewSites();

		return view;
	}

	/**
	 * 设置我的网站
	 */
	private void setMySites ( ) {

		mMySitesSitesItem.setTitle(mContext.getString(R.string.mysiteTitle));
		mMySitesSitesItem.setTextColor(Color.rgb(0xf1, 0xab, 0x47));
		mMySitesSitesItem.setType(SitesType.MYSITES);
		mMySitesSitesItem.setSplitlineBackgroundResource(R.drawable.splitline);
		mMySitesSitesItem.setOnSiteNavigationListener(new MySitesViewOnSiteNavigationListener());
		mMySitesSitesItem.setOnSiteRemoveListener(new MySitesViewOnSiteRemoveListener());
	}

	/**
	 * 设置新网推荐
	 */
	private void setNewSites ( ) {

		mNewSitesSitesItem.setTitle(mContext.getString(R.string.newsiteTitle));
		mNewSitesSitesItem.setTextColor(Color.rgb(0x4C, 0xA0, 0xEA));
		mNewSitesSitesItem.setType(SitesType.NEWSITES);
		mNewSitesSitesItem.setSplitlineBackgroundResource(R.drawable.splitline);

		mNewSitesSitesItem.setOnSiteNavigationListener(new MySitesViewOnSiteNavigationListener());
		mNewSitesSitesItem.setOnSiteRemoveListener(new MySitesViewOnSiteRemoveListener());
	}

	/**
	 * 设置网址导航监听
	 * 
	 * @param onSiteNavigationListener
	 */
	public void setOnSiteNavigationListener ( OnSiteNavigationListener onSiteNavigationListener ) {

		this.onSiteNavigationListener = onSiteNavigationListener;
	}

	/**
	 * 设置网址导航
	 */
	private AllSitesView setSitesNavigationPage ( ) {

		mSiteNavigationScrollView = new AllSitesView(mContext);
		mSiteNavigationScrollView.setOnSiteNavigation(new MySitesViewOnSiteNavigationListener());
		return mSiteNavigationScrollView;
	}

	@Override
	public void loadFinished ( ) {

		new AsynLoader(0).execute();
	}
}
