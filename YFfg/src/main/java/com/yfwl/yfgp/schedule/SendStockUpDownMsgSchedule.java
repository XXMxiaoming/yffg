package com.yfwl.yfgp.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.ibatis.mapping.Environment;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easemob.server.method.SendMessageMethods;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tencent.xinge.ClickAction;
import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.Style;
import com.tencent.xinge.TimeInterval;
import com.tencent.xinge.XingeApp;
import com.yfwl.yfgp.model.OwnStock;
import com.yfwl.yfgp.model.StockXml;
import com.yfwl.yfgp.model.Stockinfo;
import com.yfwl.yfgp.model.Stocksend;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.service.OwnStockService;
import com.yfwl.yfgp.service.StockInfoService;
import com.yfwl.yfgp.service.StocksendService;
import com.yfwl.yfgp.service.UserService;
import com.yfwl.yfgp.utils.AccountUtil;
import com.yfwl.yfgp.utils.GetHSTokenUtils;
import com.yfwl.yfgp.utils.PropertiesUtils;
import com.yfwl.yfgp.utils.StockUpDownUtils;
import com.yfwl.yfgp.utils.Time;

@Component
public class SendStockUpDownMsgSchedule {
	@Autowired
	StocksendService stocksendService;
	@Autowired
	UserService userService;
	@Autowired
	StockInfoService stockInfoService;
	@Autowired
	OwnStockService ownStockService;
	private static final JsonNodeFactory factory = new JsonNodeFactory(false);
	private static Logger logger = OrderBookSchedule
			.getLogger("d:/logs/yfgp/schedule/OptiSchedule_debug_");

	public void bindSchedule() throws ParseException {
		// sendMsg();
		sendMsg3();
		sendToAllUser1();
		sendToAllUser2();
		sendToAllUser3();

	}

	public void sendMsg() {

		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");
		Date datenow = new Date();
		String dateString = dateSdf.format(datenow);
		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		int week = cl.get(Calendar.DAY_OF_WEEK) - 1;
		// 周一到周五执行
		if (!(week == 6 || week == 0 || AccountUtil.HOLIDAY_STRING
				.contains(dateString))) {
			List<StockXml> list = StockUpDownUtils.analyzeUpDownXml();
			if (list.size() > 0) {
				for (StockXml stockXml : list) {
					String stockName = stockXml.getZqjc();
					// String stockCjj = stockXml.getDay_Cjj();
					String upDown = stockXml.getUp_Down();
					// String info = stockXml.getInfo();
					String stockCode = stockXml.getZqdm();
					String operate = stockXml.getOperate();
					String market;
					Stockinfo stockinfo = null;
					try {
						stockinfo = stockInfoService.getStock(stockCode);
					} catch (Exception e) {
						// TODO: handle exception
					}
					if (stockinfo != null && !stockinfo.equals("")) {
						Integer marketNum = stockinfo.getMarket();

						if (marketNum == 1) {
							market = ".SS";
						} else {
							market = ".SZ";
						}

						List<String> easemobIdList = userService
								.getStockAttUser(stockCode);
						if (easemobIdList.size() > 0) {
							String targetType = "users";
							String from = "lbh3zyi";
							String stock = " $" + stockName + "(" + stockCode
									+ market + ")$";
							ObjectNode ext = factory.objectNode();
							String msg = "您关注的" + stock + "，已出现中期" + upDown
									+ "信号，请及时" + operate + "。";
							SendMessageMethods.sendTxtMsg(targetType,
									easemobIdList, from, ext, msg);

						}
					} else {

					}
				}
			}
		}

	}

	public void sendMsg3() throws NumberFormatException, ParseException {
		Stocksend stocksend = null;
		XingeApp push = new XingeApp(XingeApp.IOS_ID, XingeApp.IOS_MYKEY);
		XingeApp push2 = new XingeApp(XingeApp.ANDRIOD_MAX_ID,
				XingeApp.ANDRIOD_MYKEY);
		// 昨天日期
		Calendar ac = Calendar.getInstance();
		ac.add(Calendar.DATE, -1);
		String yesterday = new SimpleDateFormat("yyyyMMdd")
				.format(ac.getTime());
		// 今天日期
		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");
		Date datenow = new Date();
		String dateString = dateSdf.format(datenow);

		// 判断今天是星期几
		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		int week = cl.get(Calendar.DAY_OF_WEEK) - 1;

		// 推送给信鸽的时间
		SimpleDateFormat xgsdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String xgtime = xgsdf.format(datenow);

		// 周一到周五执行
		if (!(week == 6 || week == 0 || AccountUtil.HOLIDAY_STRING
				.contains(dateString))) {
			List<Stockinfo> stockinfoList = stockInfoService.getAllStockinfo();
			Integer date = null;
			String text = null;
			Integer market = null;
			String stockid = null;
			for (Stockinfo stockinfo : stockinfoList) {
				date = stockinfo.getDate();
				if (date != null) {

					if (week == 1
							&& (Time.timeToDate3(date.toString(),yesterday)) < 3) {
						date = Integer.parseInt(yesterday);
					}

					if (date.toString().equals(yesterday)) {
						int bspoint = stockinfo.getBspoint();
						market = stockinfo.getMarket();
						if (market != null && market == 2) {
							stockid = stockinfo.getStockid() + ".SZ";
						} else if (market != null && market == 1) {
							stockid = stockinfo.getStockid() + ".SS";
						} else {
							continue;
						}

						String name = stockinfo.getName();
						List<OwnStock> OwnStockList = ownStockService
								.getOwnStock2(stockid);
						if (OwnStockList != null && !OwnStockList.isEmpty()) {
							String StockCode = null;
							String userid = null;
							for (OwnStock ownStock : OwnStockList) {
								userid = ownStock.getUserId().toString();
								if (!userid.equals("")) {
									StockCode = ownStock.getStockCode();
									// if(StockCode.toString().contains("000001.SS")){
									// continue;
									// }
									stocksend = new Stocksend();
									stocksend.setUserid(ownStock.getUserId());
									stocksend.setStockid(stockid);
									stocksend.setBspoint(bspoint);
									stocksend.setName(name);
									// stocksend.setStatus(0);
									stocksend.setStockCode(StockCode);
									stocksend.setTime(xgtime);
									stocksend.setType("1");
									Integer count = stocksendService
											.insertStocksend(stocksend);
									Integer id = stocksend.getId();

									MessageIOS iosMess = new MessageIOS();
									Map<String, Object> map = new HashMap<String, Object>();
									map.put("stockid", stockid);// 股票的stockid 如
																// 000023
									map.put("stockCode", StockCode);// 股票的stock_code
																	// 如
																	// 000023.SZ
									map.put("bspoint", bspoint);// bspoint
																// 1为买点，2为卖点。
									map.put("type", "1");// 1表示自选股
									map.put("time", xgtime);
									map.put("name", name);
									map.put("id", id);
									iosMess.setAlert("您有一条新消息");
									iosMess.setCustom(map);
									push.pushSingleAccount(0, userid, iosMess,
											XingeApp.IOSENV_PROD);

									Message mess = new Message();
									mess.setTitle("自选股");
									mess.setStyle(new Style(0, 1, 1, 0, 0));
									mess.setType(Message.TYPE_NOTIFICATION);
									mess.setContent("您有一条新消息");
									ClickAction action = new ClickAction();
									action.setActivity("com.yfnetwork.yffg.xgpush.PushMsgActivity");
									mess.setAction(action);
									Map<String, Object> map2 = new HashMap<String, Object>();
									mess.setCustom(map);
									push2.pushSingleAccount(0, userid, mess);
								} else {
									continue;
								}
							}

						}

					} else {
						continue;
					}
				} else {
					continue;
				}

			}

		} else {

		}

	}

	public void sendToAllUser1() throws NumberFormatException, ParseException {
		Stocksend stocksend = null;
		XingeApp push = new XingeApp(XingeApp.IOS_ID, XingeApp.IOS_MYKEY);
		XingeApp push2 = new XingeApp(XingeApp.ANDRIOD_MAX_ID,
				XingeApp.ANDRIOD_MYKEY);
		// 昨天日期
		Calendar ac = Calendar.getInstance();
		ac.add(Calendar.DATE, -1);
		String yesterday = new SimpleDateFormat("yyyyMMdd")
				.format(ac.getTime());
		// 今天日期
		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");
		Date datenow = new Date();
		String dateString = dateSdf.format(datenow);

		// 判断今天是星期几
		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		int week = cl.get(Calendar.DAY_OF_WEEK) - 1;

		// 推送给信鸽的时间
		SimpleDateFormat xgsdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String xgtime = xgsdf.format(datenow);
		if (!(week == 6 || week == 0 || AccountUtil.HOLIDAY_STRING
				.contains(dateString))) {
			String stock = "000001.SS";
			String result = null;
			JSONObject jsonObject = null;
			int k = 0;
			try {
				result = GetHSTokenUtils.getBuySallPoint(stock);
				jsonObject = new JSONObject(result);
			} catch (Exception e) {
				logger.error("000001.SS获取买卖点失败");
			}
			while (jsonObject == null) {
				k++;
				result = GetHSTokenUtils.getBuySallPoint(stock);
				jsonObject = new JSONObject(result);
				if (k > 3) {
					break;
				}
			}

			JSONObject candle = jsonObject.getJSONObject("data").getJSONObject(
					"candle");
			int len = candle.getJSONArray(stock).length();
			JSONArray stockArray = candle.getJSONArray(stock).getJSONArray(
					len - 1);
			Integer time = stockArray.getInt(0); // 时间
			int buyorsall = stockArray.getInt(1); // 1是买点， 2是卖点

			if (week == 1 && (Time.timeToDate3(time.toString(),yesterday)) < 3) {
				time = Integer.parseInt(yesterday);
			}

			// 如果昨日上证指数出现买卖点 就发送消息给所有用户
			if (time.toString().equals(yesterday)) {
				List<User> userList = userService.selectAllUsers();
				for (User user : userList) {
					stocksend = new Stocksend();
					stocksend.setUserid(user.getUserId());
					stocksend.setStockid("000001");
					stocksend.setBspoint(buyorsall);
					stocksend.setName("上证指数");
					stocksend.setStockCode("000001.SS");
					stocksend.setTime(xgtime);
					stocksend.setType("1");
					Integer count = stocksendService.insertStocksend(stocksend);
					Integer id = stocksend.getId();
					MessageIOS iosMess = new MessageIOS();
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("stockid", "000001");// 股票的stockid 如 000023
					map.put("stockCode", "000001.SS");// 股票的stock_code 如
														// 000023.SZ
					map.put("bspoint", buyorsall);// bspoint 1为买点，2为卖点。
					map.put("type", "1");// 1表示自选股
					map.put("time", xgtime);
					map.put("name", "上证指数");
					map.put("id", id);
					iosMess.setAlert("您有一条新消息");
					iosMess.setCustom(map);
					push.pushSingleAccount(0, user.getUserId().toString(),
							iosMess, XingeApp.IOSENV_PROD);

					Message mess = new Message();
					mess.setTitle("自选股");
					mess.setStyle(new Style(0, 1, 1, 0, 0));
					mess.setType(Message.TYPE_NOTIFICATION);
					mess.setContent("您有一条新消息");
					ClickAction action = new ClickAction();
					action.setActivity("com.yfnetwork.yffg.xgpush.PushMsgActivity");
					mess.setAction(action);
					Map<String, Object> map2 = new HashMap<String, Object>();
					mess.setCustom(map);
					push2.pushSingleAccount(0, user.getUserId().toString(),
							mess);
				}

			}

		}
	}

	public void sendToAllUser2() throws ParseException {
		Stocksend stocksend = null;
		XingeApp push = new XingeApp(XingeApp.IOS_ID, XingeApp.IOS_MYKEY);
		XingeApp push2 = new XingeApp(XingeApp.ANDRIOD_MAX_ID,
				XingeApp.ANDRIOD_MYKEY);
		// 昨天日期
		Calendar ac = Calendar.getInstance();
		ac.add(Calendar.DATE, -1);
		String yesterday = new SimpleDateFormat("yyyyMMdd")
				.format(ac.getTime());
		// 今天日期
		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");
		Date datenow = new Date();
		String dateString = dateSdf.format(datenow);

		// 判断今天是星期几
		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		int week = cl.get(Calendar.DAY_OF_WEEK) - 1;

		// 推送给信鸽的时间
		SimpleDateFormat xgsdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String xgtime = xgsdf.format(datenow);
		if (!(week == 6 || week == 0 || AccountUtil.HOLIDAY_STRING
				.contains(dateString))) {
			String stock = "399001.SZ";
			String result = null;
			JSONObject jsonObject = null;
			int k = 0;
			try {
				result = GetHSTokenUtils.getBuySallPoint(stock);
				jsonObject = new JSONObject(result);
			} catch (Exception e) {
				logger.error("399001.SZ获取买卖点失败");
			}
			while (jsonObject == null) {
				k++;
				result = GetHSTokenUtils.getBuySallPoint(stock);
				jsonObject = new JSONObject(result);
				if (k > 3) {
					break;
				}
			}
			JSONObject candle = jsonObject.getJSONObject("data").getJSONObject(
					"candle");
			int len = candle.getJSONArray(stock).length();
			JSONArray stockArray = candle.getJSONArray(stock).getJSONArray(
					len - 1);
			Integer time = stockArray.getInt(0); // 时间
			int buyorsall = stockArray.getInt(1); // 1是买点， 2是卖点
			Calendar cal = Calendar.getInstance();

			if (week == 1 && (Time.timeToDate3(time.toString(),yesterday)) < 3) {
				time = Integer.parseInt(yesterday);
			}

			// 如果昨日上证指数出现买卖点 就发送消息给所有用户
			if (time.toString().equals(yesterday)) {
				List<User> userList = userService.selectAllUsers();
				for (User user : userList) {
					stocksend = new Stocksend();
					stocksend.setUserid(user.getUserId());
					stocksend.setStockid("399001.SZ");
					stocksend.setBspoint(buyorsall);
					stocksend.setName("深圳成指");
					stocksend.setStockCode("399001.SZ");
					stocksend.setTime(xgtime);
					stocksend.setType("1");
					Integer count = stocksendService.insertStocksend(stocksend);
					Integer id = stocksend.getId();
					MessageIOS iosMess = new MessageIOS();
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("stockid", "399001.SZ");// 股票的stockid 如 000023
					map.put("stockCode", "399001.SZ");// 股票的stock_code 如
														// 000023.SZ
					map.put("bspoint", buyorsall);// bspoint 1为买点，2为卖点。
					map.put("type", "1");// 1表示自选股
					map.put("time", xgtime);
					map.put("name", "深圳成指");
					map.put("id", id);
					iosMess.setAlert("您有一条新消息");
					iosMess.setCustom(map);
					push.pushSingleAccount(0, user.getUserId().toString(),
							iosMess, XingeApp.IOSENV_PROD);

					Message mess = new Message();
					mess.setTitle("自选股");
					mess.setStyle(new Style(0, 1, 1, 0, 0));
					mess.setType(Message.TYPE_NOTIFICATION);
					mess.setContent("您有一条新消息");
					ClickAction action = new ClickAction();
					action.setActivity("com.yfnetwork.yffg.xgpush.PushMsgActivity");
					mess.setAction(action);
					Map<String, Object> map2 = new HashMap<String, Object>();
					mess.setCustom(map);
					push2.pushSingleAccount(0, user.getUserId().toString(),
							mess);
				}

			}

		}
	}

	public void sendToAllUser3() throws NumberFormatException, ParseException {
		Stocksend stocksend = null;
		XingeApp push = new XingeApp(XingeApp.IOS_ID, XingeApp.IOS_MYKEY);
		XingeApp push2 = new XingeApp(XingeApp.ANDRIOD_MAX_ID,
				XingeApp.ANDRIOD_MYKEY);
		// 昨天日期
		Calendar ac = Calendar.getInstance();
		ac.add(Calendar.DATE, -1);
		String yesterday = new SimpleDateFormat("yyyyMMdd")
				.format(ac.getTime());
		// 今天日期
		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");
		Date datenow = new Date();
		String dateString = dateSdf.format(datenow);

		// 判断今天是星期几
		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		int week = cl.get(Calendar.DAY_OF_WEEK) - 1;

		// 推送给信鸽的时间
		SimpleDateFormat xgsdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String xgtime = xgsdf.format(datenow);
		if (!(week == 6 || week == 0 || AccountUtil.HOLIDAY_STRING
				.contains(dateString))) {
			String stock = "399006.SZ";

			String result = null;
			JSONObject jsonObject = null;
			int k = 0;
			try {
				result = GetHSTokenUtils.getBuySallPoint(stock);
				jsonObject = new JSONObject(result);
			} catch (Exception e) {
				logger.error("399006.SZ获取买卖点失败");
			}
			while (jsonObject == null) {
				k++;
				result = GetHSTokenUtils.getBuySallPoint(stock);
				jsonObject = new JSONObject(result);
				if (k > 3) {
					break;
				}
			}

			JSONObject candle = jsonObject.getJSONObject("data").getJSONObject(
					"candle");
			int len = candle.getJSONArray(stock).length();
			JSONArray stockArray = candle.getJSONArray(stock).getJSONArray(
					len - 1);
			Integer time = stockArray.getInt(0); // 时间
			int buyorsall = stockArray.getInt(1); // 1是买点， 2是卖点

			if (week == 1 && (Time.timeToDate3(time.toString(),yesterday)) < 3) {
				time = Integer.parseInt(yesterday);
			}

			// 如果昨日上证指数出现买卖点 就发送消息给所有用户
			if (time.toString().equals(yesterday)) {
				List<User> userList = userService.selectAllUsers();
				for (User user : userList) {
					stocksend = new Stocksend();
					stocksend.setUserid(user.getUserId());
					stocksend.setStockid("399006.SZ");
					stocksend.setBspoint(buyorsall);
					stocksend.setName("创业板指");
					stocksend.setStockCode("399006.SZ");
					stocksend.setTime(xgtime);
					stocksend.setType("1");
					Integer count = stocksendService.insertStocksend(stocksend);
					Integer id = stocksend.getId();
					MessageIOS iosMess = new MessageIOS();
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("stockid", "399006.SZ");// 股票的stockid 如 000023
					map.put("stockCode", "399006.SZ");// 股票的stock_code 如
														// 000023.SZ
					map.put("bspoint", buyorsall);// bspoint 1为买点，2为卖点。
					map.put("type", "1");// 1表示自选股
					map.put("time", xgtime);
					map.put("name", "创业板指");
					map.put("id", id);
					iosMess.setAlert("您有一条新消息");
					iosMess.setCustom(map);
					push.pushSingleAccount(0, user.getUserId().toString(),
							iosMess, XingeApp.IOSENV_PROD);

					Message mess = new Message();
					mess.setTitle("自选股");
					mess.setStyle(new Style(0, 1, 1, 0, 0));
					mess.setType(Message.TYPE_NOTIFICATION);
					mess.setContent("您有一条新消息");
					ClickAction action = new ClickAction();
					action.setActivity("com.yfnetwork.yffg.xgpush.PushMsgActivity");
					mess.setAction(action);
					Map<String, Object> map2 = new HashMap<String, Object>();
					mess.setCustom(map);
					push2.pushSingleAccount(0, user.getUserId().toString(),
							mess);
				}

			}

		}
	}
}