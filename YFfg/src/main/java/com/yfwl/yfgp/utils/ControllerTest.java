package com.yfwl.yfgp.utils;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.yfwl.yfgp.utils.GetHSTokenUtils.LogUtils;


public class ControllerTest {
	
	
	protected static String host = "https://www.gentou18.com/index.php";
	
	/**
	 * http post请求,ContentType类型：application/json
	 * 
	 * @param url
	 *            请求URL
	 * @param map
	 *            请求参数
	 * @return
	 */
	public String doJsonPost(String url, HashMap<String, String> map) {

		try {
			HttpPost post = new HttpPost(url);

			// 设置访问的类型为：application/json
			ContentType type = ContentType.APPLICATION_JSON;

			// 设置请求参数
			StringEntity entity = new StringEntity(new Gson().toJson(map), type);
			post.setEntity(entity);

			// 执行post请求
			return HttpUtil.execute(post, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * post请求
	 * 
	 * @param url
	 * @param map
	 * @return
	 */
	public static String doPost(String url, HashMap<String, String> map) {
		 HttpClient httpClient = new DefaultHttpClient();
		try {
			
			HttpPost post = new HttpPost(url);

			// 设置请求参数
			List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
			if (map != null) {
				Iterator<Entry<String, String>> iter = map.entrySet()
						.iterator();
				while (iter.hasNext()) {
					Entry<String, String> entry = (Entry<String, String>) iter
							.next();
					param.add(new BasicNameValuePair(entry.getKey(), entry
							.getValue()));
				}
			}
			post.setEntity(new UrlEncodedFormEntity(param, "utf-8"));

			// 执行post请求
			//System.out.println("POST前");
			return HttpUtil.execute(post, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; 
	}

	/**
	 * 自定义头部post请求
	 * 
	 * @param url
	 * @param map
	 * @param headMap
	 * @return
	 */
	public String doPost(String url, HashMap<String, String> map,
			HashMap<String, String> headMap) {
		try {
			HttpPost post = new HttpPost(url);

			// 设置头部参数
			if (headMap != null) {
				Iterator<Entry<String, String>> iter = headMap.entrySet()
						.iterator();
				while (iter.hasNext()) {
					Entry<String, String> entry = (Entry<String, String>) iter
							.next();
					post.setHeader(entry.getKey(), entry.getValue());
				}
			}

			// 设置请求参数
			List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
			if (map != null) {
				Iterator<Entry<String, String>> iter = map.entrySet()
						.iterator();
				while (iter.hasNext()) {
					Entry<String, String> entry = (Entry<String, String>) iter
							.next();
					param.add(new BasicNameValuePair(entry.getKey(), entry
							.getValue()));
				}
			}
			post.setEntity(new UrlEncodedFormEntity(param, "utf-8"));

			// 执行post请求
			return HttpUtil.execute(post, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 带文件上传post请求
	 * 
	 * @param url
	 * @param map
	 * @param fileMap
	 * @param headMap
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String doPost(String url, HashMap<String, String> map,
			HashMap<String, File> fileMap, HashMap<String, String> headMap) {

		try {
			HttpPost post = new HttpPost(url);

			// 设置头部参数
			if (headMap != null) {
				Iterator<Entry<String, String>> iter = headMap.entrySet()
						.iterator();
				while (iter.hasNext()) {
					Entry<String, String> entry = (Entry<String, String>) iter
							.next();
					post.setHeader(entry.getKey(), entry.getValue());
				}
			}

			// 设置请求参数
			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder
					.create();
			entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			entityBuilder.setCharset(Charset.forName(HTTP.UTF_8));
			if (map != null) {
				Iterator<Entry<String, String>> iter = map.entrySet()
						.iterator();
				while (iter.hasNext()) {
					Entry<String, String> entry = (Entry<String, String>) iter
							.next();
					entityBuilder.addTextBody(entry.getKey(), entry.getValue());
				}
			}

			// 设置文件
			if (fileMap != null) {
				Iterator<Entry<String, File>> iter = fileMap.entrySet()
						.iterator();
				while (iter.hasNext()) {
					Entry<String, File> entry = (Entry<String, File>) iter
							.next();
					File file = entry.getValue();
					ContentBody contentBody = new FileBody(file);
					entityBuilder.addPart(entry.getKey(), contentBody);
				}
			}
			HttpEntity entity = entityBuilder.build();
			post.setEntity(entity);
			// 执行post请求
			return HttpUtil.execute(post, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	
	
	public static String doGet(String url, HashMap<String, String> map) {
		try {
			HttpGet Get=new HttpGet(url);
		//	HttpPost post = new HttpPost(url);
			// 设置请求参数
			List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
			if (map != null) {
				Iterator<Entry<String, String>> iter = map.entrySet()
						.iterator();
				while (iter.hasNext()) {
					Entry<String, String> entry = (Entry<String, String>) iter
							.next();
					param.add(new BasicNameValuePair(entry.getKey(), entry
							.getValue()));
				}
			}
			String str = "";  
			str = EntityUtils.toString(new UrlEncodedFormEntity(param, Consts.UTF_8)); 
			HttpGet httpGet = new HttpGet(url+"?"+str);  
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
			return HttpUtil.getResponseResult(httpResponse, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; 
	}
	

	
	
	
	public static String sentPost(HashMap<String, String> map) {
		try {
			HttpPost post = new HttpPost(host);

			// 设置请求参数
			List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
			if (map != null) {
				Iterator<Entry<String, String>> iter = map.entrySet()
						.iterator();
				while (iter.hasNext()) {
					Entry<String, String> entry = (Entry<String, String>) iter
							.next();
					param.add(new BasicNameValuePair(entry.getKey(), entry
							.getValue()));
				}
			}
			post.setEntity(new UrlEncodedFormEntity(param, "utf-8"));

			// 执行post请求
			//System.out.println("POST前");
			return HttpUtil.execute(post, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; 
	}

}
