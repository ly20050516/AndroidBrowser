
package com.android.browser.navigation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Connect {

	private String httpUrl = null;

	private String json = null;

	private HttpEntity httpEntity = null;

	private DefaultHttpClient httpClient = null;

	public Connect ( ) {

	}

	/**
	 * 
	 * @param type
	 */
	public Object connect ( int type , String gradestage ) {

		List< NameValuePair> params = new ArrayList< NameValuePair>();
		params.add(new BasicNameValuePair("suitablecrowd", gradestage));

		return executeConnect(type, params);

	}

	protected void addMachineInfo2Params ( List< NameValuePair> params ) {

		params.add(new BasicNameValuePair(Global.MACHINE_ID, com.android.browser.Browser.BBKSN));
		Log.d("Liu Test", Global.MACHINE_ID + " " + com.android.browser.Browser.BBKSN);
		params.add(new BasicNameValuePair(Global.DEVICE_OS_VERSION, com.android.browser.Browser.DEVICE_OS_VERSION));
		params.add(new BasicNameValuePair(Global.DEVICE_MODEL, com.android.browser.Browser.DEVICE_MODEL));
		params.add(new BasicNameValuePair(Global.VERSION_NAME, com.android.browser.Browser.VERSION_NAME));

	}

	protected Object executeConnect ( int type , List< NameValuePair> params ) {

		addMachineInfo2Params(params);

		if ( initHttpUrl(type) && initHttpEntity(params) ) {

			return doBackground(type);
		}
		return null;
	}

	protected boolean initHttpUrl ( int type ) {

		httpUrl = "http://172.28.14.137:8080/h600s/giAgreeWeb/getListByDate";
		return httpUrl != null;
	}

	protected boolean initHttpEntity ( List< NameValuePair> params ) {

		try {

			httpEntity = new UrlEncodedFormEntity(params, "UTF-8");

		} catch ( UnsupportedEncodingException e ) {

			httpEntity = null;
			e.printStackTrace();
		}

		return httpEntity != null;
	}

	protected void libextraString ( ) {

		json = null;
		if ( httpClient != null && httpClient.getConnectionManager() != null ) {

			httpClient.getConnectionManager().shutdown();
		}
	}

	public String readData ( InputStream inSream , String charsetName ) throws Exception {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[ 1024 ];
		int len = -1;
		while ( (len = inSream.read(buffer)) != -1 ) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inSream.close();
		return new String(data, charsetName);
	}

	protected String fromInputString ( InputStream inputStream ) {

		String string = null;
		if ( null == inputStream ) {

			return null;
		}

		try {

			Log.d("Liu Test", getClass().getSimpleName() + " inputStream size " + inputStream.available());
			string = readData(inputStream, "UTF-8");
			Log.d("Liu Test", getClass().getSimpleName() + " string size " + string.length());

		} catch ( Exception e ) {
			e.printStackTrace();

		} finally {

		}

		return string;
	}

	protected Object makeJsonObject ( int type ) {

		if ( TextUtils.isEmpty(json) ) {

			return null;
		}
		Type mapType = new TypeToken< HashMap< String , List< BaseObject>>>() {
		}.getType();
		;

		Gson gson = new Gson();
		HashMap< String , List< BaseObject>> hasMap = gson.fromJson(json, mapType);
		List< BaseObject> b = hasMap.get("AGREEDWEBSITE");

		return b;

	}

	protected Object doBackground ( int type ) {

		Object obj = null;
		HttpPost httpPost = new HttpPost(httpUrl);

		httpPost.setEntity(httpEntity);

		// 设置超时限制
		httpClient = new DefaultHttpClient();
		httpClient.setHttpRequestRetryHandler(myRetryHandler);
		HttpParams httpParams = httpClient.getParams();
		// 连接超时时间
		HttpConnectionParams.setConnectionTimeout(httpParams, 40000);
		// 通信数据时间
		HttpConnectionParams.setSoTimeout(httpParams, 40000);

		HttpResponse httpResponse = null;

		try {

			Log.w("Liu Test", "httpClient execute start");
			httpResponse = httpClient.execute(httpPost);

			Log.d("Liu Test", "httpClient execute end");
			int ackcode = httpResponse.getStatusLine().getStatusCode();

			Log.w("Liu Test", "ackcode is " + ackcode);

			if ( ackcode == HttpStatus.SC_OK ) {

				InputStream inputStream = httpResponse.getEntity().getContent();

				json = fromInputString(inputStream);

			} else if ( ackcode == HttpStatus.SC_REQUEST_TIMEOUT || ackcode == HttpStatus.SC_REQUEST_URI_TOO_LONG ) {

				Log.e("Liu test", "HttpStatus.NETWORK_TIMEOUT");

			} else {

				Log.e("Liu test", "HttpStatus.NETWORK_ERROR");
			}

		} catch ( ClientProtocolException e ) {

			libextraString();

			e.printStackTrace();
			Log.e("Liu test", "HttpStatus.NETWORK_ERROR + ClientProtocolException");

		} catch ( IOException e ) {

			libextraString();

			e.printStackTrace();
			Log.e("Liu test", "HttpStatus.NETWORK_ERROR + IOException");

		} catch ( Exception e ) {

			e.printStackTrace();

		} finally {

			httpClient.getConnectionManager().shutdown();

		}

		obj = makeJsonObject(type);
		return obj;
	}

	private HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {

		@Override
		public boolean retryRequest ( IOException exception , int executionCount , HttpContext context ) {

			Log.d("Liu Test", "HttpRequestRetryHandler retryRequest executionCount = " + executionCount);
			if ( executionCount >= 3 ) {
				return false;
			}
			return true;
		}
	};

}
