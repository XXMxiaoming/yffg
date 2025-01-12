package com.yfwl.yfgp.schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.XingeApp;
import com.yfwl.yfgp.model.Revenue;
import com.yfwl.yfgp.model.Stockinfo;
import com.yfwl.yfgp.service.RevenueService;
import com.yfwl.yfgp.service.StockInfoService;
import com.yfwl.yfgp.utils.AccountUtil;
import com.yfwl.yfgp.utils.ControllerTest;
import com.yfwl.yfgp.utils.HttpUtil;

@Component
public class stockInfoSchedule {
	@Autowired
	RevenueService revenueService;
	@Autowired
	StockInfoService stockInfoService;

	public void bindSchedule() {
		bsPoint();
		
	
	}

	public void bsPoint() {
		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");
		Date datenow = new Date();
		String dateString = dateSdf.format(datenow);
		Calendar cl = Calendar.getInstance();
		Integer bsPoint;
		Integer date;
		cl.setTime(new Date());
		int week = cl.get(Calendar.DAY_OF_WEEK) - 1;
		if (!(week == 6 || week == 0 || AccountUtil.HOLIDAY_STRING
				.contains(dateString))) {
			List<Stockinfo> stockInfoList = stockInfoService.getAllStockinfo();
			if (!stockInfoList.isEmpty()) {
				for (Stockinfo stockinfo : stockInfoList) {
					try {
					String stockid = stockinfo.getStockid();
					String stock = stockid.startsWith("6") ? stockid + ".SS"
							: stockid + ".SZ";
					String url = "http://fage008.com:8081/quote/v1/bs";
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("get_type", "offset");
					map.put("prod_code", stock);
					map.put("candle_period", "6");
					String haha = ControllerTest.doGet(url, map);
					JSONObject json=new JSONObject(haha);
					JSONObject candle =json.getJSONObject("data").getJSONObject("candle");
					JSONArray array=candle.getJSONArray(stock);
//					if(!(array.length()>0)){
//						bsPoint=1;
//						date=Integer.parseInt(dateString);
//					}
//					else{
						 Object x=array.get(array.length()-1);
						 bsPoint=Integer.parseInt(x.toString().substring(10,11));
						 date=Integer.parseInt(x.toString().substring(1,9));
//					}
					
					Stockinfo si=new Stockinfo();
					si.setDate(date);
					si.setBspoint(bsPoint);
					si.setStockid(stockid);
					si.setUpdatetime(datenow);
					stockInfoService.updateStockinfo(si);
					} catch (Exception e) { 
						// TODO: handle exception
					}
					
				}

			} else {

			}

		} else {

		}
	}
	
	
	
}
