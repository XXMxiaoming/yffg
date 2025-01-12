package com.yfwl.yfgp.schedule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.yfwl.yfgp.model.FuturesShares;
import com.yfwl.yfgp.model.Revenue;
import com.yfwl.yfgp.model.Everydayincome;
import com.yfwl.yfgp.service.EverydayincomeService;
import com.yfwl.yfgp.service.FuturesSharesService;
import com.yfwl.yfgp.service.RevenueService;
import com.yfwl.yfgp.utils.AccountUtil;

public class RevenueSchedule {
	private static final double INIT_TOTAL = 1000000.f; // 设置初始金额
	// private Object lock = new Object();
	@Autowired
	RevenueService revenueService;
	@Autowired
	EverydayincomeService everydayincomeService;
	@Autowired
	FuturesSharesService futuresSharesService;

	public void bindSchedule() {
		re3();
	}

	public void re() {
		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");

		Date datenow = new Date();
		String dateString = dateSdf.format(datenow);
		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		int week = cl.get(Calendar.DAY_OF_WEEK) - 1;
		if (!(week == 6 || week == 0 || AccountUtil.HOLIDAY_STRING
				.contains(dateString))) {
			// 获取不同用户的离现在时间最近的一天的一条记录的集合
			List<Everydayincome> everydayincomeList = everydayincomeService
					.getDisEverydayincome();
			if (!everydayincomeList.isEmpty()) {
				Double bond = null;
				Double cash = null;
				Double futures = null;
				Double shares = null;
				Double totalassets = null;
				Integer userid = null;
				// 获取最后一条传入的revenue;
				Revenue re = revenueService.selectRevenue2();
				if (re == null) {
					return;
				} else {
					// Everydayincome evd = null;
					SimpleDateFormat dateSdf2 = new SimpleDateFormat(
							"yyyy-MM-dd");
					String dateString2 = dateSdf2.format(datenow);
					for (Everydayincome everydayincome : everydayincomeList) {
						String date = dateSdf2.format(everydayincome.getDate())
								.toString();
						date = date.substring(0, 10);
						if (date.equals(dateString2.substring(0, 10))) {
							continue;
						} else {
							// synchronized (lock) {
							bond = everydayincome.getBond()
									+ everydayincome.getBond() * re.getEd();
							cash = everydayincome.getCash()
									+ everydayincome.getCash() * re.getEc();
							futures = everydayincome.getFutures()
									+ everydayincome.getFutures() * re.getEf();
							shares = everydayincome.getShares()
									+ everydayincome.getShares() * re.getEs();
							totalassets = bond + cash + futures + shares;
							userid = everydayincome.getUserid();
							Everydayincome evd = new Everydayincome();
							evd.setBond(bond);
							evd.setCash(cash);
							evd.setFutures(futures);
							evd.setShares(shares);
							evd.setTotalassets(totalassets);
							evd.setUserid(userid);
							everydayincomeService.insertEverydayincome(evd);
							// }
						}

					}
				}

			}

		} else {

		}
	}

	public void re2() {
		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");

		Date datenow = new Date();
		String dateString = dateSdf.format(datenow);
		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		int week = cl.get(Calendar.DAY_OF_WEEK) - 1;
		if (!(week == 6 || week == 0 || AccountUtil.HOLIDAY_STRING
				.contains(dateString))) {
			// 获取不同用户的离现在时间最近的一天的一条记录的集合
			List<Everydayincome> everydayincomeList = everydayincomeService
					.getDisEverydayincome();
			if (!everydayincomeList.isEmpty()) {
				Double bond = null;
				Double cash = null;
				Double futures = null;
				Double shares = null;
				Double totalassets = null;
				Integer userid = null;
				// 获取最后一条传入的revenue;
				Revenue re = revenueService.selectRevenue2();

				if (re == null) {
					return;
				} else {
					// Everydayincome evd = null;
					SimpleDateFormat dateSdf2 = new SimpleDateFormat(
							"yyyy-MM-dd");
					String dateString2 = dateSdf2.format(datenow);
					// 如果当天没有输入revenue收益表，不执行每天收益everydayincome;
					if (dateSdf2.format(re.getDate()).toString().substring(0,
							10) != dateString2) {
						return;
					}
					for (Everydayincome everydayincome : everydayincomeList) {
						String way=everydayincome.getWay();
						String date = dateSdf2.format(everydayincome.getDate())
								.toString();
						date = date.substring(0, 10);
						// 之前本地和服务器同时启动结果重复执行了，加了这句防止本地和服务器同时执行
						if (date.equals(dateString2.substring(0, 10))) {
							continue;
						} else {
							List<FuturesShares> futuresSharesList = futuresSharesService
									.getAllShares();
							List<FuturesShares> futuresSharesList2 = futuresSharesService
									.getAllFutures();
							double sharesPosicale = 0.00;
							double futuresPosicale = 0.00;
							if (!futuresSharesList.isEmpty()) {
								for (FuturesShares futuresShares : futuresSharesList) {
									sharesPosicale += futuresShares
											.getPosiscale();
								}
							}
							if (!futuresSharesList2.isEmpty()) {
								for (FuturesShares futuresShares : futuresSharesList2) {
									futuresPosicale += futuresShares
											.getPosiscale();
								}
							}
							bond = 0.00;
							futures = everydayincome.getTotalassets()
									* 0.3
									* futuresPosicale
									* re.getEf()
									+ (everydayincome.getTotalassets() * 0.3 * futuresPosicale);
							shares = (everydayincome.getTotalassets() / 2 * sharesPosicale)
									* re.getEs()
									+ (everydayincome.getTotalassets() / 2 * sharesPosicale);
							cash = everydayincome.getTotalassets()
									- (everydayincome.getTotalassets() * 0.3 * futuresPosicale)
									- (everydayincome.getTotalassets() / 2 * sharesPosicale);
							totalassets = bond + cash + futures + shares;
							userid = everydayincome.getUserid();
							Everydayincome evd = new Everydayincome();
							evd.setBond(bond);
							evd.setCash(cash);
							evd.setFutures(futures);
							evd.setShares(shares);
							evd.setTotalassets(totalassets);
							evd.setUserid(userid);
							evd.setWay(way);
							everydayincomeService.insertEverydayincome(evd);

						}

					}
				}

			}

		} else {

		}
	}

	public void re3() {
		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");
		Date datenow = new Date();
		String dateString = dateSdf.format(datenow);
		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		int week = cl.get(Calendar.DAY_OF_WEEK) - 1;
		if (!(week == 6 || week == 0 || AccountUtil.HOLIDAY_STRING
				.contains(dateString))) {
			// 获取不同用户的离现在时间最近的一天的一条记录的集合
			List<Everydayincome> everydayincomeList = everydayincomeService
					.getDisEverydayincome();
			if (!everydayincomeList.isEmpty()) {
				Double bond = null;
				Double cash = null;
				Double futures = null;
				Double shares = null;
				Double totalassets = null;
				Integer userid = null;
				// 获取最后一条传入的revenue;
				Revenue re = revenueService.selectRevenue2();

				if (re == null) {
					return;
				} else {
					// Everydayincome evd = null;
					SimpleDateFormat dateSdf2 = new SimpleDateFormat(
							"yyyy-MM-dd");
					String dateString2 = dateSdf2.format(datenow);
					
					// 如果当天没有输入revenue收益表，每天收益everydayincome和前一天的相等;
					if (!dateSdf2.format(re.getDate()).toString().substring(0,
							10) .equals(dateString2) ) {
						for (Everydayincome everydayincome : everydayincomeList) {
							String way=everydayincome.getWay();
							String date = dateSdf2.format(
									everydayincome.getDate()).toString();
							date = date.substring(0, 10);
							// 之前本地和服务器同时启动结果重复执行了，加了这句防止本地和服务器同时执行
							if (date.equals(dateString2.substring(0, 10))) {
								continue;
							} else {
								bond = everydayincome.getBond();
								cash = everydayincome.getCash();
								futures = everydayincome.getFutures();
								shares = everydayincome.getShares();
								totalassets = everydayincome.getTotalassets();
								userid = everydayincome.getUserid();
								Everydayincome evd = new Everydayincome();
								evd.setBond(bond);
								evd.setCash(cash);
								evd.setFutures(futures);
								evd.setShares(shares);
								evd.setTotalassets(totalassets);
								evd.setUserid(userid);
								evd.setWay(way);
								everydayincomeService.insertEverydayincome(evd);
								

							}

						}
						return;
					}
					for (Everydayincome everydayincome : everydayincomeList) {
						String way=everydayincome.getWay();
						String date = dateSdf2.format(everydayincome.getDate())
								.toString();
						date = date.substring(0, 10);
						// 之前本地和服务器同时启动结果重复执行了，加了这句防止本地和服务器同时执行
						if (date.equals(dateString2.substring(0, 10))) {
							continue;
						} else {
							List<FuturesShares> futuresSharesList = futuresSharesService
									.getAllShares();
							List<FuturesShares> futuresSharesList2 = futuresSharesService
									.getAllFutures();
							double sharesPosicale = 0.00;
							double futuresPosicale = 0.00;
							if (!futuresSharesList.isEmpty()) {
								for (FuturesShares futuresShares : futuresSharesList) {
									sharesPosicale += futuresShares
											.getPosiscale();
								}
							}
							if (!futuresSharesList2.isEmpty()) {
								for (FuturesShares futuresShares : futuresSharesList2) {
									futuresPosicale += futuresShares
											.getPosiscale();
								}
							}
							bond = 0.00;
							futures = everydayincome.getTotalassets()
									* 0.3
									* futuresPosicale
									* re.getEf()
									+ (everydayincome.getTotalassets() * 0.3 * futuresPosicale);
							shares = (everydayincome.getTotalassets() / 2 * sharesPosicale)
									* re.getEs()
									+ (everydayincome.getTotalassets() / 2 * sharesPosicale);
							cash = everydayincome.getTotalassets()
									- (everydayincome.getTotalassets() * 0.3 * futuresPosicale)
									- (everydayincome.getTotalassets() / 2 * sharesPosicale);
							totalassets = bond + cash + futures + shares;
							userid = everydayincome.getUserid();
							Everydayincome evd = new Everydayincome();
							evd.setBond(bond);
							evd.setCash(cash);
							evd.setFutures(futures);
							evd.setShares(shares);
							evd.setTotalassets(totalassets);
							evd.setUserid(userid);
							evd.setWay(way);
							everydayincomeService.insertEverydayincome(evd);

						}

					}
				}

			}

		} else {

		}
	}
}
