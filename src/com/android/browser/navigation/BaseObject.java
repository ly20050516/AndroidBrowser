package com.android.browser.navigation;

import java.io.Serializable;

import com.android.browser.R;

/**
 * 
 * 说明：基础站点对象模型<br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
public class BaseObject implements Serializable {

	/**  */
	private static final long serialVersionUID = -5830796273305594685L;

	/*
	 * id
	 * */
	private long _ID;
	/**
	 * 网站分类
	 */
	private String catalog;

	/**
	 * 网站分类排序
	 */
	private String catalogsn;

	/**
	 * 标记中、小学
	 */
	private int suitablecrowd;

	/**
	 * 标记是否为我的网站 我的网站标记为1 否则为0
	 */
	private int isme;

	/**
	 * 网站LOGO网上资源
	 */
	private String logo;

	/**
	 * 网站的名称
	 */
	private String name;

	/**
	 * 匀许加入到我的网站
	 */
	private boolean permitadd = true;

	/**
	 * 是否预置
	 */
	private int preset;

	/**
	 * 网站LOGO本地资源 缺省值 ：R.drawable.logo_default
	 */
	private int resId = R.drawable.logo_default;

	/**
	 * 更新日期
	 */
	private String uploadDate;

	/**
	 * 网站地址
	 */
	private String url;

	/**
	 * 访问次数
	 */
	private int visits;

	public String getCatalog() {
		return catalog;
	}

	public String getCatalogsn() {
		return catalogsn;
	}

	public int getSuitableCrowd() {
		return suitablecrowd;
	}

	public int getIsme() {
		return isme;
	}

	public String getLogo() {
		return logo;
	}

	public String getName() {
		return name;
	}

	public int getPreset() {
		return preset;
	}

	public int getResId() {
		return resId;
	}

	public String getUploadDate() {
		return uploadDate;
	}

	public String getUrl() {
		return url;
	}

	public int getVisits() {
		return visits;
	}

	public boolean isPermitadd() {
		return permitadd;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public void setCatalogsn(String catalogsn) {
		this.catalogsn = catalogsn;
	}

	public void setSuitableCrowd(int suitablecrowd) {
		this.suitablecrowd = suitablecrowd;
	}

	public void setIsme(int isme) {
		this.isme = isme;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPermitadd(boolean permitadd) {
		this.permitadd = permitadd;
	}

	public void setPreset(int preset) {
		this.preset = preset;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setVisits(int visits) {
		this.visits = visits;
	}

	public long get_ID ( ) {

		return _ID;
	}

	public void set_ID ( long _ID ) {

		this._ID = _ID;
	}

}
