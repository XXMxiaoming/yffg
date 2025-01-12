package com.yfwl.yfgp.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yfwl.yfgp.hs.demo.HttpMarketDemoUtils;

public class GetHSTokenUtils {

	private static Logger log = LoggerFactory
			.getLogger(HttpMarketDemoUtils.class);
	/**
	 * 编码格式.
	 */
	public static final String CHARSET = "UTF-8";

	/**
	 * HTTP HEADER字段 Authorization应填充字符串Bearer
	 */
	public static final String BEARER = "Bearer ";

	/**
	 * HTTP HEADER字段 Authorization应填充字符串BASIC
	 */
	// public static final String BASIC =
	// "Basic YTE1ZTZhMTEtNTk5My00MTA5LWI0N2MtMzUyOGFhNzA3ZmYwOjMxNjAwNDdlLTBhNWEtNDc1OS05ZDQ1LWM5N2ZkY2E3OGU5MQ==";
	// 宜发
	public static final String BASIC = "Basic MzY5MjVjYTktNjc2OC00N2Y3LTgxMjQtZmEyYTlhOGEyYzdkOmQzOTJkZmY3LTE0ZjUtNDQzNy1hMGIyLTUzODZmYTcyMTYyZg==";

	private static CloseableHttpClient httpClient = null;
	/** 连接超时时间 */
	public final static int connectTimeout = 15000;

	/** socket连接超时时间 */
	public final static int socketTimeout = 20000;

	/** 发送请求相应时间 */
	public final static int requestTimeout = 15000;
	public static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
	/**
	 * 最大连接数
	 */
	public final static int MAX_TOTAL_CONNECTIONS = 500;
	/**
	 * 每个路由最大连接数 访问每个目标机器 算一个路由 默认 2个
	 */
	public final static int MAX_ROUTE_CONNECTIONS = 80;

	static {
		cm.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);// 设置最大路由数
		cm.setMaxTotal(MAX_TOTAL_CONNECTIONS);// 最大连接数
		SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true)
				.build();
		cm.setDefaultSocketConfig(socketConfig);
		RequestConfig defaultRequestConfig = RequestConfig.custom()
				.setCookieSpec(CookieSpecs.BEST_MATCH)
				.setExpectContinueEnabled(true)
				.setStaleConnectionCheckEnabled(true).setRedirectsEnabled(true)
				.build();
		ConnectionConfig connectionConfig = ConnectionConfig.custom()
				.setCharset(Consts.UTF_8)
				.setMalformedInputAction(CodingErrorAction.IGNORE)
				.setUnmappableInputAction(CodingErrorAction.IGNORE).build();
		httpClient = HttpClients.custom().setConnectionManager(cm)
				.setDefaultRequestConfig(defaultRequestConfig)
				.setDefaultConnectionConfig(connectionConfig).build();
		/*
		 * try { Base64(); } catch (UnsupportedEncodingException e) {
		 * e.printStackTrace(); }
		 */
	}

	/**
	 * post请求
	 * 
	 * @param url
	 * @param params
	 * @param charSet
	 * @param charsetReturn
	 * @param proxy
	 * @param authorization
	 * @param interfacename
	 * @return
	 */
	public static String sendPost(String url, Map<String, String> params,
			String charSet, String charsetReturn, HttpHost proxy,
			String authorization, String interfacename) {
		try {
			HttpPost post = new HttpPost(url);
			Builder builder = RequestConfig.custom();
			if (proxy != null) {
				builder.setProxy(proxy);
				RequestConfig requestConfig = builder
						.setSocketTimeout(socketTimeout)
						.setConnectTimeout(connectTimeout)
						.setConnectionRequestTimeout(requestTimeout)
						.setExpectContinueEnabled(false)
						.setRedirectsEnabled(true).build();
				post.setConfig(requestConfig);
			}

			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setHeader("Authorization", authorization);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			StringBuffer sb = new StringBuffer();
			if (params != null) {
				int n = 0;
				for (Entry<String, String> set : params.entrySet()) {
					if (n == 0) {
						n++;
						sb.append(set.getKey() + "=" + set.getValue());
					} else {
						sb.append("&" + set.getKey() + "=" + set.getValue());
					}
					nvps.add(new BasicNameValuePair(set.getKey(), set
							.getValue()));
				}
			}
			post.setEntity(new UrlEncodedFormEntity(nvps, charSet));
			LogUtils.log("post  url = ["
					+ (url.endsWith("?") ? url : url + "?") + sb.toString()
					+ "]", log);
			HttpResponse response = httpClient.execute(post);
			int status = response.getStatusLine().getStatusCode();
			HttpEntity entity = null;
			try {
				entity = response.getEntity();
				if (entity != null) {
					String result = EntityUtils.toString(entity, charsetReturn);
					LogUtils.log(interfacename + "result = " + result, log);
					return result;
				}
			} catch (Exception e) {
				LogUtils.log("HttpClient   请求 http状态码 status = [" + status
						+ "]  获取HttpEntity ", e, log);
			} finally {
				if (entity != null) {
					entity.getContent().close();
				}
			}
		} catch (ClientProtocolException e) {
			LogUtils.log("HttpClient   请求  ClientProtocolException ", e, log);
		} catch (IOException e) {
			LogUtils.log("HttpClient   请求  IOException ", e, log);
		}
		return null;
	}

	/**
	 * get请求
	 * 
	 * @param url
	 * @param params
	 * @param charSet
	 * @return
	 */
	public static String sendGet(String url, Map<String, String> params,
			String charSet, HttpHost proxy, String authorization,
			String interfacename) {
		try {
			StringBuffer urlbuf = new StringBuffer(url);
			if (params != null) {
				int n = 0;
				for (Entry<String, String> set : params.entrySet()) {
					if (!urlbuf.toString().contains("?")) {
						urlbuf.append("?");
					}
					if (n != 0) {
						urlbuf.append("&");
					}
					urlbuf.append(set.getKey()).append("=")
							.append(set.getValue());
					n++;
				}
			}
			LogUtils.log("get = " + urlbuf.toString(), log);
			HttpGet get = new HttpGet(urlbuf.toString());
			get.setHeader("Content-Type", "application/x-www-form-urlencoded");
			get.setHeader("Authorization", authorization);
			// HttpUriRequest get = new HttpGet(urlbuf.toString());
			Builder builder = RequestConfig.custom();
			if (proxy != null) {
				builder.setProxy(proxy);
			}

			RequestConfig defaultConfig = builder
					.setSocketTimeout(socketTimeout)
					.setConnectTimeout(connectTimeout)
					.setConnectionRequestTimeout(requestTimeout)
					.setExpectContinueEnabled(false).setRedirectsEnabled(true)
					.build();
			get.setConfig(defaultConfig);

			HttpResponse response = httpClient.execute(get);

			int status = response.getStatusLine().getStatusCode();
			HttpEntity entity = null;
			try {
				entity = response.getEntity();
				if (entity != null) {
					String result = EntityUtils.toString(entity, charSet);
					LogUtils.log(interfacename + "result = " + result, log);
					return result;
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogUtils.log("HttpClient   请求 http状态码 status = [" + status
						+ "]  ", e, log);
			} finally {
				if (entity != null) {
					entity.getContent().close();
				}
			}
		} catch (ClientProtocolException e) {
			LogUtils.log("HttpClient   请求  ClientProtocolException ", e, log);
		} catch (IOException e) {
			LogUtils.log("HttpClient   请求  IOException ", e, log);
		}
		return null;
	}

	/**
	 * cifangf 是对"App Key:App Secret"进行 Base64 编码后的字符串（区分大小写，包含冒号，但不包含双引号,采用
	 * UTF-8 编码）。 例如: Authorization: Basic eHh4LUtleS14eHg6eHh4LXNlY3JldC14eHg=
	 * 其中App Key和App Secret可在开放平台上创建应用后获取。
	 */
	public static String Base64() throws UnsupportedEncodingException {
		// String str = "App Key:App Secret";
		// key:35d43bc7-68e6-4359-bda3-544fe5994943
		// secret:173a3319-7fb7-42d8-a860-c0c2030bec02
		String str = "35d43bc7-68e6-4359-bda3-544fe5994943:173a3319-7fb7-42d8-a860-c0c2030bec02";

		byte[] encodeBase64 = Base64.encodeBase64(str
				.getBytes(HttpMarketDemoUtils.CHARSET));

		System.out.println("RESULT: " + new String(encodeBase64));
		return new String(encodeBase64);
	}

	public static class LogUtils {

		public static void log(String msg, Logger log) {
			//System.out.println(msg);
			//log.info(msg);
		}

		public static void log(String msg, Exception e, Logger log) {
			//System.out.println(msg + " 异常 message = [" + e.getMessage() + "]");
			//log.info(msg + " 异常 message = [" + e.getMessage() + "]", e);
		}

		public static void error(String msg, Exception e, Logger log) {
			//System.out.println(msg + " 异常 message = [" + e.getMessage() + "]");
			//log.error(msg + " 异常 message = [" + e.getMessage() + "]", e);
		}

	}
	
	
	public static String getReal(String stockStr, String token) {
		Map<String, String> map = new HashMap<String, String>();
		String url = "http://sandbox.hscloud.cn/quote/v1/real";
		map.put("en_prod_code",stockStr);
		map.put("fields", "last_px,preclose_px,up_px,down_px,prod_name,high_px,low_px,trade_status");
		String real = sendGet(url, map, "UTF-8", null, "Bearer "+token, "");
		return real;
	}
	
	
	
	
	public static String getReal2(String stockStr, String token) {
		Map<String, String> map = new HashMap<String, String>();
		String url = "http://sandbox.hscloud.cn/quote/v1/real";
		map.put("en_prod_code",stockStr);
		map.put("fields", "last_px,px_change_rate,px_change,prod_name,preclose_px");
		String real = sendGet(url, map, "UTF-8", null, "Bearer "+token, "");
		return real;
	}
	
	
	/**
	 * 自己的行情服务器区数据
	 * @param stockStr
	 * @param token
	 * @return
	 */
	/*public static String getReal(String stockStr, String token) {
		Map<String, String> map = new HashMap<String, String>();
		String url = "https://open.hscloud.cn/quote/v1/real";
		map.put("prod_code", stockStr);
		map.put("fields", "last_px,preclose_px,up_px,down_px,prod_name,high_px,low_px");
		String real = sendGet(url, map, "UTF-8", null, "", "");
		return real;
	}*/
	
	
	public static String getBuySallPoint(String stockStr) {
		Map<String, String> map = new HashMap<String, String>();
		String url = "http://fage008.com:8081/quote/v1/bs";
		map.put("gettype", "offset");
		map.put("prod_code", stockStr);
		map.put("candle_period", "6");
		String real = sendGet(url, map, "UTF-8", null, "", "");
		return real;
	}

	
	public static String getExrights(String stockStr, String token) {
		Map<String, String> map = new HashMap<String, String>();
		String url = "https://open.hscloud.cn/quote/v1/exrights";
		map.put("en_prod_code", stockStr);
		String real = sendGet(url, map, "UTF-8", null, "Bearer "+token, "");
		return real;
	}
	
	public static String getBuySallPricePoint(String stockStr) {
		Map<String, String> map = new HashMap<String, String>();
		String url = "http://fage008.com:8081/quote/v1/bs_dayOpenPrice";
		map.put("gettype", "offset");
		map.put("prod_code", stockStr);
		map.put("candle_period", "6");
		String real = sendGet(url, map, "UTF-8", null, "", "");
		return real;
	}
}
