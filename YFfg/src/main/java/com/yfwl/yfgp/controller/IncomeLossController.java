package com.yfwl.yfgp.controller;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import sun.misc.BASE64Encoder;

import com.tencent.xinge.ClickAction;
import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.Style;
import com.tencent.xinge.XingeApp;
import com.yfwl.yfgp.model.Everydayincome;
import com.yfwl.yfgp.model.FuturesShares;
import com.yfwl.yfgp.model.IncomeLoss;
import com.yfwl.yfgp.model.OwnStock;
import com.yfwl.yfgp.model.Revenue;
import com.yfwl.yfgp.model.Stockinfo;
import com.yfwl.yfgp.model.Stocksend;
import com.yfwl.yfgp.service.EverydayincomeService;
import com.yfwl.yfgp.service.FuturesSharesService;
import com.yfwl.yfgp.service.IncomeLossService;
import com.yfwl.yfgp.service.OwnStockService;
import com.yfwl.yfgp.service.RevenueService;
import com.yfwl.yfgp.service.StockInfoService;
import com.yfwl.yfgp.service.StocksendService;
import com.yfwl.yfgp.utils.AccountUtil;
import com.yfwl.yfgp.utils.ControllerTest;
import com.yfwl.yfgp.utils.MD5Util;
import com.yfwl.yfgp.utils.SortByMap;

@Controller
@RequestMapping("/incomeLoss")
public class IncomeLossController extends BaseController {
	private static final double INIT_TOTAL = 1000000.f; // 设置初始金额
	@Autowired
	IncomeLossService incomeLossService;
	@Autowired
	EverydayincomeService everydayincomeService;
	@Autowired
	RevenueService revenueService;
	@Autowired
	StockInfoService stockInfoService;
	@Autowired
	OwnStockService ownStockService;
	@Autowired
	StocksendService stocksendService;
	@Autowired
	FuturesSharesService futuresSharesService;

	/**
	 * @insertIncomeLoss添加一个收益亏损单
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/insertIncomeLoss", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> insertIncomeLoss(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		String token = request.getParameter("token");
		// 预期收益，预期亏损
		Double income = Double.parseDouble(request.getParameter("income"));
		Double loss = Double.parseDouble(request.getParameter("loss"));
		if (validateToken(userid, token)) {
			IncomeLoss inlo = null;
			inlo = incomeLossService.getincomeLoss(userid);
			if (inlo != null && ("1").equals(inlo.getStatus().toString())) {
				map.put("msg", "您已经创建过此表单");
				map.put("data", "");
				map.put("status", 4);
			} else if (inlo != null
					&& ("0").equals(inlo.getStatus().toString())) {

				if (income / loss > 3) {
					map.put("msg", "最大收益亏损为3：1");
					map.put("status", "1");
					map.put("data", "");
				} else {
					inlo.setUserid(userid);
					inlo.setIncome(income);
					inlo.setLoss(loss);
					inlo.setWay("fg");
					incomeLossService.updateIncomeLoss(inlo);
					Everydayincome everydayincome = new Everydayincome();
					List<FuturesShares> futuresSharesList = futuresSharesService
							.getAllShares();
					List<FuturesShares> futuresSharesList2 = futuresSharesService
							.getAllFutures();
					double sharesPosicale = 0.00;
					double futuresPosicale = 0.00;
					if (!futuresSharesList.isEmpty()) {
						for (FuturesShares futuresShares : futuresSharesList) {
							sharesPosicale += futuresShares.getPosiscale();
						}
					}
					if (!futuresSharesList2.isEmpty()) {
						for (FuturesShares futuresShares : futuresSharesList2) {
							futuresPosicale += futuresShares.getPosiscale();
						}
					}
					everydayincome.setBond(0);

					everydayincome.setFutures(INIT_TOTAL * 0.3
							* futuresPosicale);// 期货
					everydayincome.setShares(INIT_TOTAL / 2 * sharesPosicale);// 股票
					everydayincome.setCash(INIT_TOTAL
							- (INIT_TOTAL * 0.3 * futuresPosicale)
							- (INIT_TOTAL / 2 * sharesPosicale));// 现金
					everydayincome.setTotalassets(INIT_TOTAL);
					everydayincome.setUserid(userid);
					everydayincome.setWay("fg");
					everydayincomeService.insertEverydayincome(everydayincome);
					map = rspFormat("", SUCCESS);
				}
			} else {

				if (income / loss > 3) {
					map.put("msg", "最大收益亏损为3：1");
					map.put("status", "1");
					map.put("data", "");
					// map = rspFormat("", FAIL);
				} else {
					IncomeLoss inlo2 = new IncomeLoss();
					inlo2.setUserid(userid);
					inlo2.setIncome(income);
					inlo2.setLoss(loss);
					inlo2.setWay("fg");
					incomeLossService.insertIncomeLoss(inlo2);
					Everydayincome everydayincome = new Everydayincome();
					List<FuturesShares> futuresSharesList = futuresSharesService
							.getAllShares();
					List<FuturesShares> futuresSharesList2 = futuresSharesService
							.getAllFutures();
					double sharesPosicale = 0.00;
					double futuresPosicale = 0.00;
					if (!futuresSharesList.isEmpty()) {
						for (FuturesShares futuresShares : futuresSharesList) {
							sharesPosicale += futuresShares.getPosiscale();
						}
					}
					if (!futuresSharesList2.isEmpty()) {
						for (FuturesShares futuresShares : futuresSharesList2) {
							futuresPosicale += futuresShares.getPosiscale();
						}
					}
					everydayincome.setBond(0);

					everydayincome.setFutures(INIT_TOTAL * 0.3
							* futuresPosicale);// 期货
					everydayincome.setShares(INIT_TOTAL / 2 * sharesPosicale);// 股票
					everydayincome.setCash(INIT_TOTAL
							- (INIT_TOTAL * 0.3 * futuresPosicale)
							- (INIT_TOTAL / 2 * sharesPosicale));// 现金
					everydayincome.setTotalassets(INIT_TOTAL);
					everydayincome.setUserid(userid);
					everydayincome.setWay("fg");
					everydayincomeService.insertEverydayincome(everydayincome);
					map = rspFormat("", SUCCESS);
				}

			}

		} else {
			map = rspFormat("", WRONG_TOKEN);
		}

		return map;

	}

	/**
	 * @insertIncomeLoss给从跟投过来的账户添加一个收益亏损单
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/insertIncomeLossByGT", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> insertIncomeLossByGT(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "GET,POST");
		Map<String, Object> map = new HashMap<String, Object>();
		
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		// 预期收益，预期亏损
		Double income = Double.parseDouble(request.getParameter("income"));
		Double loss = Double.parseDouble(request.getParameter("loss"));
		IncomeLoss inlo = null;
		inlo = incomeLossService.getincomeLoss2(userid);
		if (inlo != null && ("1").equals(inlo.getStatus().toString())) {
			map.put("msg", "您已经创建过此表单");
			map.put("data", "");
			map.put("status", 4);
		} else if (inlo != null && ("0").equals(inlo.getStatus().toString())) {

			if (income / loss > 3) {
				map.put("msg", "最大收益亏损为3：1");
				map.put("status", "1");
				map.put("data", "");
			} else {
				inlo.setUserid(userid);
				inlo.setIncome(income);
				inlo.setLoss(loss);
				inlo.setWay("gt");
				incomeLossService.updateIncomeLoss(inlo);
				Everydayincome everydayincome = new Everydayincome();
				List<FuturesShares> futuresSharesList = futuresSharesService
						.getAllShares();
				List<FuturesShares> futuresSharesList2 = futuresSharesService
						.getAllFutures();
				double sharesPosicale = 0.00;
				double futuresPosicale = 0.00;
				if (!futuresSharesList.isEmpty()) {
					for (FuturesShares futuresShares : futuresSharesList) {
						sharesPosicale += futuresShares.getPosiscale();
					}
				}
				if (!futuresSharesList2.isEmpty()) {
					for (FuturesShares futuresShares : futuresSharesList2) {
						futuresPosicale += futuresShares.getPosiscale();
					}
				}
				everydayincome.setBond(0);

				everydayincome.setFutures(INIT_TOTAL * 0.3 * futuresPosicale);// 期货
				everydayincome.setShares(INIT_TOTAL / 2 * sharesPosicale);// 股票
				everydayincome.setCash(INIT_TOTAL
						- (INIT_TOTAL * 0.3 * futuresPosicale)
						- (INIT_TOTAL / 2 * sharesPosicale));// 现金
				everydayincome.setTotalassets(INIT_TOTAL);
				everydayincome.setUserid(userid);
				everydayincome.setWay("gt");
				everydayincomeService.insertEverydayincome(everydayincome);
				map = rspFormat("", SUCCESS);
			}
		} else {

			if (income / loss > 3) {
				map.put("msg", "最大收益亏损为3：1");
				map.put("status", "1");
				map.put("data", "");
				// map = rspFormat("", FAIL);
			} else {
			
				IncomeLoss inlo2 = new IncomeLoss();
				inlo2.setUserid(userid);
				inlo2.setIncome(income);
				inlo2.setLoss(loss);
				inlo2.setWay("gt");
				incomeLossService.insertIncomeLoss(inlo2);
				Everydayincome everydayincome = new Everydayincome();
				List<FuturesShares> futuresSharesList = futuresSharesService
						.getAllShares();
				List<FuturesShares> futuresSharesList2 = futuresSharesService
						.getAllFutures();
				double sharesPosicale = 0.00;
				double futuresPosicale = 0.00;
				if (!futuresSharesList.isEmpty()) {
					for (FuturesShares futuresShares : futuresSharesList) {
						sharesPosicale += futuresShares.getPosiscale();
					}
				}
				if (!futuresSharesList2.isEmpty()) {
					for (FuturesShares futuresShares : futuresSharesList2) {
						futuresPosicale += futuresShares.getPosiscale();
					}
				}
				everydayincome.setBond(0);

				everydayincome.setFutures(INIT_TOTAL * 0.3 * futuresPosicale);// 期货
				everydayincome.setShares(INIT_TOTAL / 2 * sharesPosicale);// 股票
				everydayincome.setCash(INIT_TOTAL
						- (INIT_TOTAL * 0.3 * futuresPosicale)
						- (INIT_TOTAL / 2 * sharesPosicale));// 现金
				everydayincome.setTotalassets(INIT_TOTAL);
				everydayincome.setUserid(userid);
				everydayincome.setWay("gt");
				everydayincomeService.insertEverydayincome(everydayincome);
				map = rspFormat("", SUCCESS);
			}

		}

		return map;

	}

	@RequestMapping(value = "/deleteIncomeLoss", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> deleteIncomeLoss(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		String token = request.getParameter("token");
		if (validateToken(userid, token)) {
			IncomeLoss inlo = null;
			inlo = incomeLossService.getincomeLoss(userid);
			if (inlo == null
					|| (inlo != null && ("0").equals(inlo.getStatus()
							.toString()))) {
				map.put("msg", "表单不存在");
				map.put("data", "");
				map.put("status", 4);
			} else {
				incomeLossService.deleteIncomeLoss(userid);// 设置status=0；
				everydayincomeService.deleteEverydayincome(userid);
				map = rspFormat("", SUCCESS);
			}

		} else {
			map = rspFormat("", WRONG_TOKEN);
		}
		return map;
	}

	/**
	 * 预期智能配置
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/expectIncomeLoss", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> expectIncomeLoss(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "GET,POST");
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();

		DecimalFormat df = new DecimalFormat("#.##");
		Double j = 0.10;
		for (int i = 1; i <= 9; i++) {
			String st = df.format(j);
			list.add(st);
			j += 0.10;
		}
		Double k = 0.05;
		for (int i = 0; i <= 5; i++) {
			String sk = df.format(k);
			list2.add(sk);
			k += 0.05;
		}
		map.put("income", list);
		map.put("loss", list2);
		return map;
	}

	/**
	 * 查询各个标的比例情况
	 * 
	 * @param request
	 * @param response
	 * @return
	 */

	// @RequestMapping(value = "/getIncomeLoss", method = { RequestMethod.POST
	// })
	// @ResponseBody
	// public Map<String, Object> getIncomeLoss(HttpServletRequest request,
	// HttpServletResponse response) {
	// Map<String, Object> map = null;
	// Integer userid = Integer.parseInt(request.getParameter("userid"));
	// String token = request.getParameter("token");
	// if (validateToken(userid, token)) {
	// IncomeLoss inlo = incomeLossService.getincomeLoss(userid);
	// List<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
	// if (inlo != null && ("1").equals(inlo.getStatus().toString())) {
	// Double income = inlo.getIncome();
	// Double loss = inlo.getLoss();
	// // 股票，债券，期货，现金所占比例
	// Double ps = 0.2; // 股票
	// Double pf = 2 * loss; // 期货
	// Double pc = 0.1; // 现金
	// Double pd = 1 - ps - pf - pc; // 债券
	// if (pf > 0.7) {
	// map = rspFormat("", FAIL);
	// } else {
	// map = new HashMap<String, Object>();
	// map.put("name", "股票");
	// map.put("scale", (int) (ps * 100) + "%");
	// arr.add(map);
	//
	// map = new HashMap<String, Object>();
	// map.put("name", "债券");
	// map.put("scale", (int) (pd * 100) + "%");
	// arr.add(map);
	//
	// map = new HashMap<String, Object>();
	// map.put("name", "期货");
	// map.put("scale", (int) (pf * 100) + "%");
	// arr.add(map);
	//
	// map = new HashMap<String, Object>();
	// map.put("name", "现金");
	// map.put("scale", (int) (pc * 100) + "%");
	// arr.add(map);
	//
	// map = rspFormat(arr, SUCCESS);
	// }
	//
	// } else {
	// map = new HashMap<String, Object>();
	// map.put("msg", "表单不存在");
	// map.put("data", "");
	// map.put("status", 4);
	// }
	// } else {
	// map = new HashMap<String, Object>();
	// map=rspFormat("", WRONG_TOKEN);
	// }
	// return map;
	// }
	//

	// @RequestMapping(value = "/getIncomeLoss", method = { RequestMethod.POST
	// })
	// @ResponseBody
	// public Map<String, Object> getIncomeLoss(HttpServletRequest request,
	// HttpServletResponse response) {
	// Map<String, Object> map = null;
	// Integer userid = Integer.parseInt(request.getParameter("userid"));
	// String token = request.getParameter("token");
	// if (validateToken(userid, token)) {
	// IncomeLoss inlo = incomeLossService.getincomeLoss(userid);
	// List<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
	// if (inlo != null && ("1").equals(inlo.getStatus().toString())) {
	// Double income = inlo.getIncome();
	// Double loss = inlo.getLoss();
	// List<FuturesShares> futuresSharesList = futuresSharesService
	// .getAllShares();
	// Double posicale = (double) 1;
	//
	// if (!futuresSharesList.isEmpty()) {
	// posicale = (double) 0;
	// for (FuturesShares futuresShares : futuresSharesList) {
	// posicale += futuresShares.getPosiscale();
	// }
	// posicale = posicale * 5;
	// }
	//
	// // 股票，债券，期货，现金所占比例
	// Double ps1 = 0.2 * posicale; // 股票
	// Double pf1 = 2 * loss; // 期货
	// Double pc1 = 0.1 + 0.2 * (1 - posicale); // 现金
	// Double pd1 = 1 - ps1 - pf1 - pc1; // 债券
	// BigDecimal ps = new BigDecimal(ps1 * 100).setScale(2,
	// RoundingMode.DOWN);
	// BigDecimal pf = new BigDecimal(pf1 * 100).setScale(2,
	// RoundingMode.DOWN);
	// BigDecimal pc = new BigDecimal(pc1 * 100).setScale(2,
	// RoundingMode.DOWN);
	// BigDecimal pd = new BigDecimal(pd1 * 100).setScale(2,
	// RoundingMode.DOWN);
	// if (pf1 > 0.7) {
	// map = rspFormat("", FAIL);
	// } else {
	// map = new HashMap<String, Object>();
	// map.put("name", "股票");
	// map.put("scale", ps + "%");
	// arr.add(map);
	//
	// map = new HashMap<String, Object>();
	// map.put("name", "债券");
	// map.put("scale", pd + "%");
	// arr.add(map);
	//
	// map = new HashMap<String, Object>();
	// map.put("name", "期货");
	// map.put("scale", pf + "%");
	// arr.add(map);
	//
	// map = new HashMap<String, Object>();
	// map.put("name", "现金");
	// map.put("scale", pc + "%");
	// arr.add(map);
	//
	// map = rspFormat(arr, SUCCESS);
	// }
	//
	// } else {
	// map = new HashMap<String, Object>();
	// map.put("msg", "表单不存在");
	// map.put("data", "");
	// map.put("status", 4);
	// }
	// } else {
	// map = new HashMap<String, Object>();
	// map = rspFormat("", WRONG_TOKEN);
	// }
	// return map;
	// }

	// @RequestMapping(value = "/getIncomeLoss", method = { RequestMethod.POST
	// })
	// @ResponseBody
	// public Map<String, Object> getIncomeLoss(HttpServletRequest request,
	// HttpServletResponse response) {
	// Map<String, Object> map = null;
	// Integer userid = Integer.parseInt(request.getParameter("userid"));
	// String token = request.getParameter("token");
	// if (validateToken(userid, token)) {
	// IncomeLoss inlo = incomeLossService.getincomeLoss(userid);
	// List<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
	// if (inlo != null && ("1").equals(inlo.getStatus().toString())) {
	// // Double income = inlo.getIncome();
	// Double loss = inlo.getLoss();
	// List<FuturesShares> futuresSharesList = futuresSharesService
	// .getAllShares();
	// double marketvalue = 0;
	// Revenue revenue = null;
	// double All = 1;
	// double posicale = 1;
	// revenue = revenueService.selectRevenue2();
	// if (!futuresSharesList.isEmpty() && revenue != null) {
	// All = revenue.getSharesmoney();
	// for (FuturesShares futuresShares : futuresSharesList) {
	// marketvalue += futuresShares.getMarketvalue();
	// }
	// posicale = marketvalue / All;
	//
	// }
	//
	// // 股票，债券，期货，现金所占比例
	// Double ps1 = 0.2 * posicale; // 股票
	// Double pf1 = 2 * loss; // 期货
	// Double pc1 = 0.1 + 0.2 * (1 - posicale); // 现金
	// Double pd1 = 1 - ps1 - pf1 - pc1; // 债券
	// BigDecimal ps = new BigDecimal(ps1 * 100).setScale(2,
	// RoundingMode.UP);
	// BigDecimal pf = new BigDecimal(pf1 * 100).setScale(2,
	// RoundingMode.DOWN);
	// BigDecimal pc = new BigDecimal(pc1 * 100).setScale(2,
	// RoundingMode.DOWN);
	// BigDecimal pd = new BigDecimal(pd1 * 100).setScale(2,
	// RoundingMode.DOWN);
	// if (pf1 > 0.7) {
	// map = rspFormat("", FAIL);
	// } else {
	// map = new HashMap<String, Object>();
	// map.put("name", "股票");
	// map.put("scale", ps + "%");
	// arr.add(map);
	//
	// map = new HashMap<String, Object>();
	// map.put("name", "债券");
	// map.put("scale", pd + "%");
	// arr.add(map);
	//
	// map = new HashMap<String, Object>();
	// map.put("name", "期货");
	// map.put("scale", pf + "%");
	// arr.add(map);
	//
	// map = new HashMap<String, Object>();
	// map.put("name", "现金");
	// map.put("scale", pc + "%");
	// arr.add(map);
	//
	// map = rspFormat(arr, SUCCESS);
	// }
	//
	// } else {
	// map = new HashMap<String, Object>();
	// map.put("msg", "表单不存在");
	// map.put("data", "");
	// map.put("status", 4);
	// }
	// } else {
	// map = new HashMap<String, Object>();
	// map = rspFormat("", WRONG_TOKEN);
	// }
	// return map;
	// }

	@RequestMapping(value = "/getIncomeLoss", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getIncomeLoss(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = null;
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		String token = request.getParameter("token");
		if (validateToken(userid, token)) {
			IncomeLoss inlo = incomeLossService.getincomeLoss(userid);
			List<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
			if (inlo != null && ("1").equals(inlo.getStatus().toString())) {
				Double income = inlo.getIncome();
				Double loss = inlo.getLoss();
				List<FuturesShares> futuresSharesList = futuresSharesService
						.getAllShares();// 股票持仓
				List<FuturesShares> futuresSharesList2 = futuresSharesService
						.getAllFutures();// 期货持仓
				double Ff = inlo.getIncome() * 100 / 30;// 期货算法
				double Fs = (1 - Ff * 0.3) / 0.5;// 股票算法

				Double ps2 = 0.00;

				Double pf2 = 0.00;
				if (!futuresSharesList.isEmpty()) {
					for (FuturesShares futuresShares : futuresSharesList) {
						ps2 += Fs * futuresShares.getPosiscale();
					}
				} else {
					ps2 = 0.50;
				}
				if (!futuresSharesList2.isEmpty()) {

					for (FuturesShares futuresShares : futuresSharesList2) {
						pf2 += Ff * futuresShares.getPosiscale();
					}
				} else {
					pf2 = 0.30;
				}
				// 股票，债券，期货，现金所占比例
				if (ps2 + pf2 > 1) {
					pf2 = 1 - ps2;
				}

				Double pc1 = (1 - ps2 - pf2); // 现金
				Double pd1 = 0.0; // 债券
				BigDecimal ps = new BigDecimal(ps2 * 100).setScale(2,
						RoundingMode.UP);
				BigDecimal pf = new BigDecimal(pf2 * 100).setScale(2,
						RoundingMode.UP);
				BigDecimal pc = new BigDecimal(pc1 * 100).setScale(2,
						RoundingMode.DOWN);
				BigDecimal pd = new BigDecimal(pd1 * 100).setScale(2,
						RoundingMode.DOWN);

				map = new HashMap<String, Object>();
				map.put("name", "股票");
				map.put("scale", ps + "%");
				arr.add(map);

				map = new HashMap<String, Object>();
				map.put("name", "债券");
				map.put("scale", pd + "%");
				arr.add(map);

				map = new HashMap<String, Object>();
				map.put("name", "期货");
				map.put("scale", pf + "%");
				arr.add(map);

				map = new HashMap<String, Object>();
				map.put("name", "现金");
				map.put("scale", pc + "%");
				arr.add(map);

				map = rspFormat(arr, SUCCESS);

			} else {
				map = new HashMap<String, Object>();
				map.put("msg", "表单不存在");
				map.put("data", "");
				map.put("status", 4);
			}
		} else {
			map = new HashMap<String, Object>();
			map = rspFormat("", WRONG_TOKEN);
		}
		return map;
	}

	/**
	 * 添加revenue表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/revenue", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> revenue(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		Double Ef = Double.parseDouble(request.getParameter("Ef"));
		Double Ed = Double.parseDouble(request.getParameter("Ed"));
		Double Es = Double.parseDouble(request.getParameter("Es"));
		Double Ec = Double.parseDouble(request.getParameter("Ec"));
		Double sharesmoney = Double.parseDouble(request
				.getParameter("sharesmoney"));
		Double futuresmoney = Double.parseDouble(request
				.getParameter("futuresmoney"));
		Revenue revenue = new Revenue();
		revenue.setEc(Ec);
		revenue.setEd(Ed);
		revenue.setEf(Ef);
		revenue.setEs(Es);
		revenue.setSharesmoney(sharesmoney);
		revenue.setFuturesmoney(futuresmoney);
		revenueService.insertRevenue2(revenue);
		map = rspFormat("", SUCCESS);
		return map;
	}

	/**
	 * 获取智能配置每天的总资产
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/totalassets", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> totalassets(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = null;
		List<Object> arr = new ArrayList<Object>();
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		String token = request.getParameter("token");
		if (validateToken(userid, token)) {
			List<Everydayincome> everydayincomeList = everydayincomeService
					.getAllEverydayincomeByUserid(userid);
			if (!everydayincomeList.isEmpty()) {
				List<Object> list = null;
				for (Everydayincome everydayincome : everydayincomeList) {
					// map=new HashMap<String, Object>();
					list = new ArrayList<Object>();
					list.add(everydayincome.getTotalassets());
					String s = everydayincome.getDate().toLocaleString();
					String[] arrs = s.split(" ");
					list.add(arrs[0]);
					// list.add(s.substring(0, 9));
					arr.add(list);
				}
				map = rspFormat(arr, SUCCESS);
			}
		} else {
			map = rspFormat("", WRONG_TOKEN);
		}
		return map;
	}

	/**
	 * 策略推荐
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/strategies", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> strategies(HttpServletRequest request,
			HttpServletResponse response) {

		SimpleDateFormat xgsdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date datenow = new Date();
		String xgtime = xgsdf.format(datenow);

		Map<String, Object> map = new HashMap<String, Object>();
		HashMap<String, String> map2 = new HashMap<String, String>();
		Integer deal_id = Integer.parseInt(request.getParameter("deal_id"));
		Object status = null;
		JSONObject json = null;
		// 根据策略ID进行策略推荐
		try {
			map2.put("ctl", "api");
			map2.put("act", "deal");
			map2.put("deal_id", deal_id.toString());
			String time = String.valueOf(new Date().getTime()).substring(0, 10);
			map2.put("time", time);
			map2.put("signature", MD5Util.getDigest(SortByMap.sort(map2)
					+ "Yx3V27g4SckNJ1Zk"));
			String res = ControllerTest.sentPost(map2);
			json = new JSONObject(res);
			status = json.get("status");
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (status.toString().equals("0")) {
			Object data1 = json.get("data");
			if (data1.toString().equals("")) {
				map.put("msg", "无效的产品id");
				map.put("data", "");
				map.put("status", "1");
			} else {
				JSONObject data = json.getJSONObject("data");
				JSONObject deal = data.getJSONObject("deal");
				Double rate = deal.getDouble("rate");

				String name = (String) deal.get("name");
				Map<String, Object> map3 = new HashMap<String, Object>();
				map3.put("type", "3");// 策略推荐
				map3.put("deal_id", deal_id);
				map3.put("name", name);
				map3.put("rate", rate);
				map3.put("time", xgtime);
				XingeApp.pushAllAndroid2(XingeApp.ANDRIOD_MAX_ID,
						XingeApp.ANDRIOD_MYKEY, "宜发投资机器人", "您有一条新消息", map3);
				XingeApp.pushAllIos2(XingeApp.IOS_ID, XingeApp.IOS_MYKEY,
						"您有一条新消息", map3, XingeApp.IOSENV_DEV);
				// ClickAction action = new ClickAction();
				// action.setActionType(ClickAction.TYPE_ACTIVITY);
				map = rspFormat("", SUCCESS);
			}

		} else {
			map = rspFormat("", FAIL);
		}
		return map;

	}

	// 昨天日期自选股推送
	@RequestMapping(value = "/bspoint", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> bspoint(HttpServletRequest request,
			HttpServletResponse response) {

		Stocksend stocksend = null;
		XingeApp push = new XingeApp(XingeApp.IOS_ID, XingeApp.IOS_MYKEY);
		XingeApp push2 = new XingeApp(XingeApp.ANDRIOD_MAX_ID,
				XingeApp.ANDRIOD_MYKEY);
		Map<String, Object> map10 = new HashMap<String, Object>();

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
			for (Stockinfo stockinfo : stockinfoList) {
				date = stockinfo.getDate();
				if (date != null) {
					if (stockinfo.getDate().toString().equals(yesterday)) {

						int bspoint = stockinfo.getBspoint();

						String stockid = stockinfo.getStockid();
						String name = stockinfo.getName();

						List<OwnStock> OwnStockList = ownStockService
								.getOwnStock2(stockid);
						if (OwnStockList != null && !OwnStockList.isEmpty()) {
							// System.out.println("ghfdksgks");
							// List<String> accountList = new
							// ArrayList<String>();
							String StockCode = null;
							String userid = null;
							for (OwnStock ownStock : OwnStockList) {
								userid = ownStock.getUserId().toString();
								if (!userid.equals("")) {
									StockCode = ownStock.getStockCode();
									if (StockCode.toString().contains(
											"000001.SS")) {
										continue;
									}
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
									// System.out.println(id + "ghdksjghks");

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
									// System.out.println(js+userid+"gjsdokfgjfdk");
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

						} else {
							continue;
						}

					} else {
						continue;
					}
				} else {
					continue;
				}

			}
			map10 = rspFormat("", SUCCESS);
		} else {

		}
		return map10;
	}

	// 优化组合推送
	@RequestMapping(value = "/youhua", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> youhua(HttpServletRequest request,
			HttpServletResponse response) {
		Calendar cal = Calendar.getInstance();
		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH) + 1;
		int d = cal.get(Calendar.DAY_OF_MONTH);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int mi = cal.get(Calendar.MINUTE);
		String dateStr = y + "年" + m + "月" + d + "日" + h + "时" + mi + "分";

		Map<String, Object> map10 = new HashMap<String, Object>();
		SimpleDateFormat xgsdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date datenow = new Date();
		String xgtime = xgsdf.format(datenow);

		XingeApp push = new XingeApp(XingeApp.IOS_ID, XingeApp.IOS_MYKEY);
		Map<String, Object> map2 = new HashMap<String, Object>();
		MessageIOS IosMess = new MessageIOS();
		map2.put("time", xgtime);
		map2.put("type", "2");// 2代表优化组合
		map2.put("dateStr", "");
		map2.put("gname", "");
		map2.put("gid", "");
		map2.put("stockName", "");
		map2.put("stock", "");
		map2.put("Vol", "");
		map2.put("last_px", "");
		map2.put("bgYhzh", "");
		map2.put("bgYzh", "");

		// map2.put("time", xgtime);
		// map2.put("type", "2");//2代表优化组合
		// map2.put("dateStr", dateStr);
		// map2.put("gname", mainAccounts.getGname());
		// map2.put("gid", mainAccounts.getGid().toString());
		// map2.put("stockName", stockName);
		// map2.put("stock", stock);
		// map2.put("vol", orderBook.getVol() * 100);
		// map2.put("last_px", last_px);
		// map2.put("buyOrSell", "1");
		// map2.put("bgYhzh", bgYhzh.doubleValue());
		// map2.put("bgYzh", bgYzh.doubleValue());
		// map2.put("id", id);

		List<String> accountList = new ArrayList<String>();
		IosMess.setAlert("您有一条新消息");

		IosMess.setCustom(map2);
		accountList.add("81");
		push.pushAccountList(0, accountList, IosMess, XingeApp.IOSENV_PROD);

		XingeApp push2 = new XingeApp(XingeApp.ANDRIOD_MAX_ID,
				XingeApp.ANDRIOD_MYKEY);
		Message mess = new Message();
		mess.setTitle("andriod");
		mess.setStyle(new Style(0, 1, 1, 0, 0));
		mess.setType(Message.TYPE_NOTIFICATION);
		mess.setContent("您有一条新消息");
		mess.setCustom(map2);
		push2.pushAccountList(0, accountList, mess);
		ClickAction action = new ClickAction();
		action.setActionType(ClickAction.TYPE_ACTIVITY);
		return map2;

	}

	// 昨天日期自选股单人推送
	@RequestMapping(value = "/bspoint2", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> bspoint2(HttpServletRequest request,
			HttpServletResponse response) {
		String userid = request.getParameter("userid");
		Stocksend stocksend = new Stocksend();
		XingeApp push = new XingeApp(XingeApp.IOS_ID, XingeApp.IOS_MYKEY);
		XingeApp push2 = new XingeApp(XingeApp.ANDRIOD_MAX_ID,
				XingeApp.ANDRIOD_MYKEY);
		Map<String, Object> map10 = new HashMap<String, Object>();

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

		stocksend.setBspoint(1);
		stocksend.setName("平安银行");
		stocksend.setStockCode("000001.SZ");
		stocksend.setUserid(Integer.parseInt(userid));
		stocksend.setStockid("000001");
		stocksend.setTime(xgtime);
		stocksend.setType("1");
		Integer count = stocksendService.insertStocksend(stocksend);
		Integer id = stocksend.getId();
		MessageIOS iosMess = new MessageIOS();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("stockid", "000001");// 股票的stockid 如
										// 000023
		map.put("stockCode", "000001.SZ");// 股票的stock_code
											// 如
											// 000023.SZ
		map.put("bspoint", 1);// bspoint
								// 1为买点，2为卖点。
		map.put("type", "1");// 1表示自选股
		map.put("time", xgtime);
		map.put("name", "平安银行");
		map.put("id", id);
		iosMess.setAlert("您有一条新消息");
		iosMess.setCustom(map);
		JSONObject json1 = push.pushSingleAccount(0, userid, iosMess,
				XingeApp.IOSENV_PROD);
		System.out.println(json1 + userid + "gjsdokfgjfdk");
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
		JSONObject json2 = push2.pushSingleAccount(0, userid, mess);
		// System.out.println(json2+"gfdkjgjfdk");
		// System.out.println(json1+"gfdkjgjfdk");
		map10 = rspFormat("", SUCCESS);
		return map10;
	}

	@RequestMapping(value = "/getIncomeLossByGT", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getIncomeLossByGT(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "GET,POST");
		Map<String, Object> map = null;
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		IncomeLoss inlo = incomeLossService.getincomeLoss2(userid);
		List<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
		if (inlo != null && ("1").equals(inlo.getStatus().toString())) {
			Double income = inlo.getIncome();
			Double loss = inlo.getLoss();
			List<FuturesShares> futuresSharesList = futuresSharesService
					.getAllShares();// 股票持仓
			List<FuturesShares> futuresSharesList2 = futuresSharesService
					.getAllFutures();// 期货持仓
			double Ff = inlo.getIncome() * 100 / 30;// 期货算法
			double Fs = (1 - Ff * 0.3) / 0.5;// 股票算法

			Double ps2 = 0.00;

			Double pf2 = 0.00;
			if (!futuresSharesList.isEmpty()) {
				for (FuturesShares futuresShares : futuresSharesList) {
					ps2 += Fs * futuresShares.getPosiscale();
				}
			} else {
				ps2 = 0.50;
			}
			if (!futuresSharesList2.isEmpty()) {

				for (FuturesShares futuresShares : futuresSharesList2) {
					pf2 += Ff * futuresShares.getPosiscale();
				}
			} else {
				pf2 = 0.30;
			}
			// 股票，债券，期货，现金所占比例
			if (ps2 + pf2 > 1) {
				pf2 = 1 - ps2;
			}

			Double pc1 = (1 - ps2 - pf2); // 现金
			Double pd1 = 0.0; // 债券
			BigDecimal ps = new BigDecimal(ps2 * 100).setScale(2,
					RoundingMode.UP);
			BigDecimal pf = new BigDecimal(pf2 * 100).setScale(2,
					RoundingMode.UP);
			BigDecimal pc = new BigDecimal(pc1 * 100).setScale(2,
					RoundingMode.DOWN);
			BigDecimal pd = new BigDecimal(pd1 * 100).setScale(2,
					RoundingMode.DOWN);

			map = new HashMap<String, Object>();
			map.put("name", "股票");
			map.put("scale", ps + "%");
			arr.add(map);

			map = new HashMap<String, Object>();
			map.put("name", "债券");
			map.put("scale", pd + "%");
			arr.add(map);

			map = new HashMap<String, Object>();
			map.put("name", "期货");
			map.put("scale", pf + "%");
			arr.add(map);

			map = new HashMap<String, Object>();
			map.put("name", "现金");
			map.put("scale", pc + "%");
			arr.add(map);

			map = rspFormat(arr, SUCCESS);

		} else {
			map = new HashMap<String, Object>();
			map.put("msg", "表单不存在");
			map.put("data", "");
			map.put("status", 4);
		}

		return map;
	}

	/**
	 * 跟投客户获取智能配置每天的总资产
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/totalassetsByGT", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> totalassetsByGT(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "GET,POST");
		Map<String, Object> map = null;
		List<Object> arr = new ArrayList<Object>();
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		List<Everydayincome> everydayincomeList = everydayincomeService
				.getAllEverydayincomeByUserid2(userid);
		if (!everydayincomeList.isEmpty()) {
			List<Object> list = null;
			for (Everydayincome everydayincome : everydayincomeList) {
				// map=new HashMap<String, Object>();
				list = new ArrayList<Object>();
				list.add(everydayincome.getTotalassets());
				String s = everydayincome.getDate().toLocaleString();
				String[] arrs = s.split(" ");
				list.add(arrs[0]);
				// list.add(s.substring(0, 9));
				arr.add(list);
			}
			map = rspFormat(arr, SUCCESS);
		}

		return map;
	}
	
	
	//判断跟投用户是否创建过资产配置
	
	@RequestMapping(value = "/exitByGT", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> exitByGT(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "GET,POST");
		Map<String, Object> map =  new HashMap<String, Object>();
		List<Object> arr = new ArrayList<Object>();
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		IncomeLoss inlo=null;
		inlo = incomeLossService.getincomeLoss2(userid);
		if(inlo!=null&&("1").equals(inlo.getStatus().toString())){
			map.put("msg", "您已经创建过此表单");
			map.put("data", "");
			map.put("status", 4);
		}else{
			map.put("msg", "您还未创建此表单");
			map.put("data", "");
			map.put("status", 6);
		}
		return map;
		
	}
	
}
