package com.yfwl.yfgp.controller;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.yfwl.yfgp.utils.ControllerTest;
import com.yfwl.yfgp.utils.MD5Util;
import com.yfwl.yfgp.utils.SortByMap;

@Controller
@RequestMapping("/gentou")
public class GentouController extends BaseController {

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/AllMethod", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> AllMethod(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		Map<String, Object> map = new HashMap<String, Object>();
		HashMap<String, String> map2 = new HashMap<String, String>();
		Enumeration enu = request.getParameterNames();
		String token=null;
		String fage_id = null;
		while (enu.hasMoreElements()) {
			String paraName = (String) enu.nextElement();
			if (("fage_id").equals(paraName)) {
				fage_id = request.getParameter("fage_id");
			}
			if (("token").equals(paraName)) {
				token=request.getParameter("token");
				continue;
			}
			map2.put(paraName, request.getParameter(paraName));
		}
		if (fage_id!=null&&(!fage_id.equals(""))&&token!=null&&(!token.equals(""))) {
			if (validateToken(Integer.parseInt(fage_id), token)) {
				String time = String.valueOf(new Date().getTime()).substring(0,10);
				map2.put("ctl", "api");
				map2.put("time", time);
				map2.put("signature",
						MD5Util.getDigest(SortByMap.sort(map2)
								+ "Yx3V27g4SckNJ1Zk"));
				
				String res = ControllerTest.sentPost(map2);
				Map<String, Object> maps = (Map<String, Object>) JSON
						.parseObject(res);
				for (Object map3 : maps.entrySet()) {
					map.put(((Map.Entry) map3).getKey().toString(),
							((Map.Entry) map3).getValue());
				}
				
			} else {
				map = rspFormat("", WRONG_TOKEN);
			}
		} else if(fage_id==null&&token==null){
			String time = String.valueOf(new Date().getTime()).substring(0, 10);
			map2.put("ctl", "api");
			map2.put("time", time);
			System.out.println(SortByMap.sort(map2)+"Yx3V27g4SckNJ1Zk");
			System.out.println(MD5Util.string2MD5(SortByMap.sort(map2)+ "Yx3V27g4SckNJ1Zk")+"fjdsokf");
			//System.out.println(SortByMap.sort(map2)+"gdf;lgkfd");
			map2.put("signature",
					MD5Util.getDigest(SortByMap.sort(map2)
							+ "Yx3V27g4SckNJ1Zk"));
			
			String res = ControllerTest.sentPost(map2);
			Map<String, Object> maps = (Map<String, Object>) JSON
					.parseObject(res);
			for (Object map3 : maps.entrySet()) {
				map.put(((Map.Entry) map3).getKey().toString(),
						((Map.Entry) map3).getValue());
			}
			
		}else if(fage_id==null&&token!=null){
			String time = String.valueOf(new Date().getTime()).substring(0, 10);
			map2.put("ctl", "api");
			map2.put("time", time);
			System.out.println(SortByMap.sort(map2)+"Yx3V27g4SckNJ1Zk");
			System.out.println(MD5Util.string2MD5(SortByMap.sort(map2)+ "Yx3V27g4SckNJ1Zk")+"fjdsokf");
			//System.out.println(SortByMap.sort(map2)+"gdf;lgkfd");
			map2.put("signature",
					MD5Util.getDigest(SortByMap.sort(map2)
							+ "Yx3V27g4SckNJ1Zk"));
			
			String res = ControllerTest.sentPost(map2);
		
			Map<String, Object> maps = (Map<String, Object>) JSON
					.parseObject(res);
			for (Object map3 : maps.entrySet()) {
				map.put(((Map.Entry) map3).getKey().toString(),
						((Map.Entry) map3).getValue());
			}
			
		}else if(fage_id!=null&&token==null){
			map=rspFormat("", WRONG_TOKEN);
		}
		return map;

		// Map<String, Object> map = new HashMap<String, Object>();;
		// HashMap<String, String> map2 = new HashMap<String, String>();
		// Enumeration enu = request.getParameterNames();
		//
		// while (enu.hasMoreElements()) {
		// String paraName = (String) enu.nextElement();
		// map2.put(paraName, request.getParameter(paraName));
		// }
		// String time = String.valueOf(new Date().getTime()).substring(0, 10);
		// map2.put("ctl", "api");
		// map2.put("time", time);
		// map2.put("signature",
		// MD5Util.string2MD5(SortByMap.sort(map2) + "Yx3V27g4SckNJ1Zk"));
		// String res=ControllerTest.sentPost(map2);
		// List<Map<String,Object>> arr=new ArrayList<Map<String,Object>>();
		// Map<String,Object> maps=(Map<String, Object>) JSON.parseObject(res);
		// for(Object map1:maps.entrySet()){
		// map.put( ((Map.Entry)map1).getKey().toString(),
		// ((Map.Entry)map1).getValue());
		// }
		// return map;
	}

}