package com.yfwl.yfgp.schedule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.yfwl.yfgp.model.AccessToken;
import com.yfwl.yfgp.model.Stockinfo;
import com.yfwl.yfgp.model.Token;
import com.yfwl.yfgp.service.StockInfoService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.utils.AccountUtil;
import com.yfwl.yfgp.utils.GetHSTokenUtils;
import com.yfwl.yfgp.utils.Host;
import com.yfwl.yfgp.utils.JacksonUtils;
import com.yfwl.yfgp.utils.PropertiesUtils;

public class MarketInformationDetailSchedule extends Host {
	@Autowired
	TokenService tokenService;

	@Autowired
	StockInfoService stockInfoService;

	public void bindSchedule() {
		Market();
		Market2();
	}

	public void Market() {
		String token = getDefaultToken();

		Map<String, String> map = new HashMap<String, String>();
		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");
		Date datenow = new Date();
		String dateString = dateSdf.format(datenow);
		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		int week = cl.get(Calendar.DAY_OF_WEEK) - 1;
		if (!(week == 6 || week == 0 || AccountUtil.HOLIDAY_STRING
				.contains(dateString))) {
			try {
				System.out.println("gfdskjghkjfs");
				String url = host+"quote/v1/market/detail";
				map.put("finance_mic", "SZ");
				String real = GetHSTokenUtils.sendGet(url, map, "UTF-8", null,
						"Bearer " + token, "");
				JSONObject json = new JSONObject(real);
				int k=0;
				while(json.toString().contains("访问令牌无效或已过期!")){
					k++;
					token = updateToken(Integer.parseInt(PropertiesUtils.getServerUserString()));
					real =  GetHSTokenUtils.sendGet(url, map, "UTF-8", null,
							"Bearer " + token, "");
					json = new JSONObject(real);
					if(k>3){
						break;
					}
				 }
				Stockinfo stockinfo2 = null;
				JSONObject data = json.getJSONObject("data");
				String financeName = data.getString("finance_name");
				JSONArray market = data.getJSONArray("market_detail_prod_grp");
				// System.out.println(market+"hdkjdhgfsdjgksdf------------------------------");
				for (int i = 0; i < market.length(); i++) {
					JSONObject js = market.getJSONObject(i);
					String prodcode = js.getString("prod_code");
					if (prodcode.startsWith("00")|| prodcode.startsWith("30")||prodcode.startsWith("399001")||prodcode.startsWith("399006")) {
						String stockid = prodcode;
						String name = js.getString("prod_name");
						Stockinfo stockinfo = new Stockinfo();
						stockinfo.setStockid(stockid);
						stockinfo.setName(name);
						stockinfo.setMarket(2);
						stockinfo.setCreatetime(datenow);
						stockinfo2 = stockInfoService.getStockinfo(stockinfo);
						if (stockinfo2 == null) {
							stockInfoService.insertStockinfo(stockinfo);
						} else {

						}
					} else {

					}

				}

			} catch (Exception e) {
				// TODO: handle exception
			}
		} else {

		}

	}

	public void Market2() {
		String token = getDefaultToken();

		Map<String, String> map = new HashMap<String, String>();
		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");
		Date datenow = new Date();
		String dateString = dateSdf.format(datenow);
		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		int week = cl.get(Calendar.DAY_OF_WEEK) - 1;
		if (!(week == 6 || week == 0 || AccountUtil.HOLIDAY_STRING
				.contains(dateString))) {
			try {
				String url = "http://sandbox.hscloud.cn/quote/v1/market/detail";
				map.put("finance_mic", "SS");
				String real = GetHSTokenUtils.sendGet(url, map, "UTF-8", null,
						"Bearer " + token, "");
				JSONObject json = new JSONObject(real);
				Stockinfo stockinfo2 = null;
				JSONObject data = json.getJSONObject("data");
				String financeName = data.getString("finance_name");
				JSONArray market = data.getJSONArray("market_detail_prod_grp");
				// System.out.println(market+"hdkjdhgfsdjgksdf------------------------------");
				for (int i = 0; i < market.length(); i++) {
					JSONObject js = market.getJSONObject(i);
					String prodcode = js.getString("prod_code");

					if (prodcode.startsWith("60")) {
						String stockid = prodcode;
						String name = js.getString("prod_name");
						Stockinfo stockinfo = new Stockinfo();
						stockinfo.setStockid(stockid);
						stockinfo.setName(name);
						stockinfo.setMarket(1);
						stockinfo.setCreatetime(datenow);
						stockinfo2 = stockInfoService.getStockinfo(stockinfo);
						if (stockinfo2 == null) {
							stockInfoService.insertStockinfo(stockinfo);
						} else {

						}
					} else {

					}

				}

			} catch (Exception e) {
				// TODO: handle exception
			}
		} else {

		}

	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public String getDefaultToken() {
		Integer userId = Integer
				.parseInt(PropertiesUtils.getServerUserString());
		String tokenString;
		Token token = tokenService.selectTokenByUserId(userId);
		Date expiresTime = token.getExpiresTime();
		Date nowDate = new Date();
		if (nowDate.before(expiresTime)) {
			// 当前时间在过期时间前面（token还未过期）
			tokenString = token.getAccessToken();
		} else {
			tokenString = updateToken(userId);
		}
		return tokenString;
	}

	public String updateToken(Integer userId) {
		String url = "http://sandbox.hscloud.cn/oauth2/oauth2/token";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("grant_type", "client_credentials");
		paramMap.put("open_id", userId.toString());
		String tokenResult = GetHSTokenUtils.sendPost(url, paramMap,
				GetHSTokenUtils.CHARSET, GetHSTokenUtils.CHARSET, null,
				GetHSTokenUtils.BASIC, "获取令牌");
		// 新的token
		AccessToken accesstoken = JacksonUtils.json2Object(tokenResult,
				AccessToken.class);
		Token token = new Token();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, Integer.parseInt(accesstoken.getExpires_in()));
		Date expiresTime = c.getTime();// 计算出过期时间
		token.setExpiresTime(expiresTime);
		token.setAccessToken(accesstoken.getAccess_token());
		token.setTokenType(accesstoken.getToken_type());
		token.setRefreshToken(accesstoken.getRefresh_token());
		token.setExpiresIn(accesstoken.getExpires_in());
		token.setUserId(userId);
		tokenService.updateTokenLoginOn(token);
		return accesstoken.getAccess_token();
	}

}
