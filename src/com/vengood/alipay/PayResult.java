package com.vengood.alipay;import android.text.TextUtils;/***  类名：PayResult.java * 注释：拷贝来自支付宝 * 日期：2016年4月14日 * 作者：王超 */public class PayResult {	private String resultStatus;	private String result;	private String memo;	public PayResult(String rawResult) {		if (TextUtils.isEmpty(rawResult))			return;		String[] resultParams = rawResult.split(";");		for (String resultParam : resultParams) {			if (resultParam.startsWith("resultStatus")) {				resultStatus = gatValue(resultParam, "resultStatus");			}			if (resultParam.startsWith("result")) {				result = gatValue(resultParam, "result");			}			if (resultParam.startsWith("memo")) {				memo = gatValue(resultParam, "memo");			}		}	}	@Override	public String toString() {		return "resultStatus={" + resultStatus + "};memo={" + memo + "};result={" + result + "}";	}	private String gatValue(String content, String key) {		String prefix = key + "={";		return content.substring(content.indexOf(prefix) + prefix.length(), content.lastIndexOf("}"));	}	public String getResultStatus() {		return resultStatus;	}	public String getMemo() {		return memo;	}	public String getResult() {		return result;	}}