package com.vengood.model;

/**
*  类名：WeiXinUserInfo.java
 * 注释：请求微信接口实体类
 * 日期：2016年4月22日
 * 作者：王超
 */
public class WeiXinUserInfo {
	private String code = null;
	private String errMsg = null;
	private String user = null;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
