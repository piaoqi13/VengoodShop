package com.vengood.alipay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.alipay.sdk.app.PayTask;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

/**
*  类名：AlipayManager.java
 * 注释：支付宝支付管理
 * 日期：2016年4月14日
 * 作者：王超
 */
public class AlipayManager {
	private static final String URL_ASYNC_NOTIFY = "http://xxx.com";
    private static final String SELLER_ID = "12121212121";
    private static final String SELLER_ACCOUNT = "15542597801@qq.com";
    
 	public static final String RSA_PRIVATE = "";// 商户私钥pkcs8格式
 	public static final String RSA_PUBLIC = "";// 支付宝公钥
	
	public static void startPay(final Activity activity, String orderId, String subject, String body, String price,
			final AliPayResultCallback callback) {
		String orderInfo = getOrderInfo(SELLER_ID, SELLER_ACCOUNT, orderId, subject, body, price, URL_ASYNC_NOTIFY);
		String sign = SignUtils.sign(orderInfo, RSA_PRIVATE);
		try {
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + "sign_type=\"RSA\"";
		pay(activity, payInfo, callback);
	}
    
	public static void pay(final Activity activity, final String payInfo, final AliPayResultCallback callback) {
		new Thread() {
			public void run() {
				String state = new PayResult(new PayTask(activity).pay(payInfo, true)).getResultStatus();
				if ("9000".equals(state)) {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						public void run() {
							callback.onSuccess();
						}
					});
				} else if ("8000".equals(state)) {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						public void run() {
							callback.onWait();
						}
					});
				} else {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						public void run() {
							callback.onError();
						}
					});
				}
			}
		}.start();
	}
	
	public static String getOrderInfo(String id, String account, String orderId, String subject, String body, String price, 
			String asycNotifyUrl) {
		String orderInfo = "partner=" + "\"" + id + "\"";
		orderInfo += "&seller_id=" + "\"" + account + "\"";
		orderInfo += "&out_trade_no=" + "\"" + orderId + "\"";
		orderInfo += "&subject=" + "\"" + subject + "\"";
		orderInfo += "&body=" + "\"" + body + "\"";
		orderInfo += "&total_fee=" + "\"" + price + "\"";
		orderInfo += "&notify_url=" + "\"" + asycNotifyUrl + "\"";
		orderInfo += "&service=\"mobile.securitypay.pay\"";
		orderInfo += "&payment_type=\"1\"";
		orderInfo += "&_input_charset=\"utf-8\"";
		orderInfo += "&it_b_pay=\"30m\"";
		orderInfo += "&return_url=\"m.alipay.com\"";
		return orderInfo;
	}
	
	public interface AliPayResultCallback {
        void onSuccess();
        void onWait();
        void onError();
    }
}
