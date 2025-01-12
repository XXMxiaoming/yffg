package com.yfwl.yfgp.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yfwl.yfgp.model.FuturesShares;
import com.yfwl.yfgp.model.FuturesTraderec;
import com.yfwl.yfgp.model.IncomeLoss;
import com.yfwl.yfgp.service.FuturesSharesService;
import com.yfwl.yfgp.service.FuturesTraderecService;
import com.yfwl.yfgp.service.IncomeLossService;
import com.yfwl.yfgp.utils.Time;

@Controller
@RequestMapping("/FuturesTraderec")
public class FuturesTraderecController extends BaseController {
	@Autowired
	FuturesTraderecService futuresTraderecService;
	@Autowired
	FuturesSharesService futuresSharesService;
	@Autowired
	IncomeLossService incomeLossService;

	@RequestMapping(value = "/insertFuturesTraderec", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> insertFuturesTraderec(
			HttpServletRequest request, HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		String name = request.getParameter("name");
		Integer buysell = Integer.parseInt(request.getParameter("buysell"));
		Double price = Double.parseDouble(request.getParameter("price"));
		if (name != null && name != "") {
			FuturesTraderec futuresTraderec = new FuturesTraderec();
			futuresTraderec.setName(name);
			futuresTraderec.setBuysell(buysell);
			futuresTraderec.setPrice(price);
			futuresTraderecService.insertFuturesTraderec(futuresTraderec);
			map = rspFormat("", SUCCESS);
		} else {
			map = rspFormat("", FAIL);
		}
		return map;

	}



	// 查询所有期货交易记录
		@RequestMapping(value = "/getFuturesRecord", method = { RequestMethod.POST })
		@ResponseBody
		public Map<String, Object> getFuturesRecord(HttpServletRequest request,
				HttpServletResponse response) throws ParseException {
			response.addHeader("Access-Control-Allow-origin", "*");
			response.addHeader("Access-Control-Allow-Methods", "GET,POST");
			Integer userid = Integer.parseInt(request.getParameter("userid"));
			IncomeLoss income = incomeLossService.getincomeLoss(userid);
			Date incometime = income.getTime();
			SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
			String compareTime=dateSdf.format(incometime);
			
			Date date = null;
			String time = null;
			Map<String, Object> map = null;
			String p = request.getParameter("p");
			String count = null;
			count =	request.getParameter("count");
			List<FuturesTraderec> futuresTraderecList = new ArrayList<FuturesTraderec>();
			if (p != null && !p.isEmpty()) {
				if (count != null && !count.isEmpty()) {
					Integer pageNow = Integer.parseInt(p);
					Integer count2 = Integer.parseInt(count);
					Integer pageCount = (pageNow - 1) * count2;
					futuresTraderecList = futuresTraderecService
							.getAllFutures3(pageCount, count2);
				}else{
					Integer pageNow = Integer.parseInt(p);
					Integer pageCount = (pageNow - 1) * 10;
					futuresTraderecList = futuresTraderecService
							.getAllFutures2(pageCount);
				}
				
			} else {
				futuresTraderecList = futuresTraderecService.getAllFutures();
			}
			if (!futuresTraderecList.isEmpty()) {
				List<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
				for (FuturesTraderec futuresTraderec : futuresTraderecList) {
					map = new HashMap<String, Object>();
					map.put("name", futuresTraderec.getName());
					map.put("buysell", futuresTraderec.getBuysell());
					BigDecimal price = new BigDecimal(futuresTraderec.getPrice())
							.setScale(2, RoundingMode.UP);
					date = futuresTraderec.getTime();
					time = dateSdf.format(date);
					map.put("price", price);
					map.put("number", futuresTraderec.getNumber());
					map.put("time", time);
					if(Time.dateToStamp(time)>=Time.dateToStamp(compareTime)){
						arr.add(map);
					}
				}
				if(!arr.isEmpty()){
					map = rspFormat(arr, SUCCESS);
				}else{
					map=rspFormat("", traderec_record);
				}
			} else {
				map = new HashMap<String, Object>();
				map = rspFormat("", FAIL);
			}
			return map;

		}
	// 查询所有期货持仓
//	@RequestMapping(value = "/getFuturesPosition", method = { RequestMethod.POST })
//	@ResponseBody
//	public Map<String, Object> getFuturesPosition(HttpServletRequest request,
//			HttpServletResponse response) {
//		Map<String, Object> map = null;
//		Integer userid = Integer.parseInt(request.getParameter("userid"));
//		IncomeLoss	inlo = incomeLossService.getincomeLoss(userid);
//		double Ff=inlo.getIncome()*100/30;
//		double Fs=(1-Ff*0.3)/0.5;
//		List<FuturesShares> futuresSharesList = futuresSharesService
//				.getAllFutures();
//		if (!futuresSharesList.isEmpty()) {
//			List<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
//			for (FuturesShares futuresShares : futuresSharesList) {
//				map = new HashMap<String, Object>();
//				map.put("name", futuresShares.getName());
//				map.put("manyempty", futuresShares.getManyempty());
//				BigDecimal price = new BigDecimal(futuresShares.getPrice())
//						.setScale(2, RoundingMode.UP);		
//				map.put("price", price);
//				map.put("posiscale", Ff*futuresShares.getPosiscale());
//				map.put("number", futuresShares.getNumber());
//				arr.add(map);
//			}
//			map = rspFormat(arr, SUCCESS);
//		} else {
//			map = new HashMap<String, Object>();
//			map = rspFormat("", FAIL);
//		}
//		return map;
//
//	}
	
	
	// 跟投账户查询所有期货持仓
		@RequestMapping(value = "/getFuturesPositionByGT", method = { RequestMethod.POST })
		@ResponseBody
		public Map<String, Object> getFuturesPositionByGT(HttpServletRequest request,
				HttpServletResponse response) {
			response.addHeader("Access-Control-Allow-origin", "*");
			response.addHeader("Access-Control-Allow-Methods", "GET,POST");
			Map<String, Object> map = null;
			Integer userid = Integer.parseInt(request.getParameter("userid"));
			IncomeLoss	inlo = incomeLossService.getincomeLoss2(userid);
			double Ff=inlo.getIncome()*100/30;
			double Fs=(1-Ff*0.3)/0.5;

			String p = request.getParameter("p");
			String count = null;
			count =	request.getParameter("count");
			List<FuturesShares> futuresSharesList = futuresSharesService
					.getAllFutures();
			if (p != null && !p.isEmpty()) {
				if (count != null && !count.isEmpty()) {
					Integer pageNow = Integer.parseInt(p);
					Integer count2 = Integer.parseInt(count);
					Integer pageCount = (pageNow - 1) * count2;
					futuresSharesList = futuresSharesService
							.getSharesByPageOnfu(pageCount, count2);
				}else{
					Integer pageNow = Integer.parseInt(p);
					Integer pageCount = (pageNow - 1) * 10;
					futuresSharesList = futuresSharesService
							.getSharesOnlyPageOnfu(pageCount);
				}
				
			} else {
				futuresSharesList = futuresSharesService.getAllFutures();
			}
			
			
			if (!futuresSharesList.isEmpty()) {
				List<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
				for (FuturesShares futuresShares : futuresSharesList) {
					map = new HashMap<String, Object>();
					map.put("name", futuresShares.getName());
					map.put("manyempty", futuresShares.getManyempty());
					BigDecimal price = new BigDecimal(futuresShares.getPrice())
							.setScale(2, RoundingMode.UP);		
					map.put("price", price);
					map.put("posiscale", Ff*futuresShares.getPosiscale());
					map.put("number", futuresShares.getNumber());
					arr.add(map);
				}
				map = rspFormat(arr, SUCCESS);
			} else {
				map = new HashMap<String, Object>();
				map = rspFormat("", FAIL);
			}
			return map;

		}

		
		// 查询所有期货持仓
		@RequestMapping(value = "/getFuturesPosition", method = { RequestMethod.POST })
		@ResponseBody
		public Map<String, Object> getFuturesPosition(HttpServletRequest request,
				HttpServletResponse response) {
			Map<String, Object> map = null;
			Integer userid = Integer.parseInt(request.getParameter("userid"));
			IncomeLoss	inlo = incomeLossService.getincomeLoss(userid);
			double Ff=inlo.getIncome()*100/30;
			double Fs=(1-Ff*0.3)/0.5;
			
			String p = request.getParameter("p");
			String count = null;
			count =	request.getParameter("count");
			List<FuturesShares> futuresSharesList = futuresSharesService
					.getAllFutures();
			if (p != null && !p.isEmpty()) {
				if (count != null && !count.isEmpty()) {
					Integer pageNow = Integer.parseInt(p);
					Integer count2 = Integer.parseInt(count);
					Integer pageCount = (pageNow - 1) * count2;
					futuresSharesList = futuresSharesService
							.getSharesByPageOnfu(pageCount, count2);
				}else{
					Integer pageNow = Integer.parseInt(p);
					Integer pageCount = (pageNow - 1) * 10;
					futuresSharesList = futuresSharesService
							.getSharesOnlyPageOnfu(pageCount);
				}
				
			} else {
				futuresSharesList = futuresSharesService.getAllFutures();
			}
			
			if (!futuresSharesList.isEmpty()) {
				List<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
				for (FuturesShares futuresShares : futuresSharesList) {
					map = new HashMap<String, Object>();
					map.put("name", futuresShares.getName());
					map.put("manyempty", futuresShares.getManyempty());
					BigDecimal price = new BigDecimal(futuresShares.getPrice())
							.setScale(2, RoundingMode.UP);		
					map.put("price", price);
					map.put("posiscale", Ff*futuresShares.getPosiscale());
					map.put("number", futuresShares.getNumber());
					arr.add(map);
				}
				map = rspFormat(arr, SUCCESS);
			} else {
				map = new HashMap<String, Object>();
				map = rspFormat("", FAIL);
			}
			return map;

		}
		
		
		
}