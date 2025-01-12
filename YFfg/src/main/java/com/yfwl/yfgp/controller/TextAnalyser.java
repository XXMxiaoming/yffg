package com.yfwl.yfgp.controller;

//文本分析器，专用于股票名称快速识别
//作者：陈建群
//初始开发日期：20160104
//最新更新日期：20160111


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yfwl.yfgp.model.AccessToken;
import com.yfwl.yfgp.model.HashInfo;
import com.yfwl.yfgp.model.TextAnalyserResult;
import com.yfwl.yfgp.model.Token;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.utils.ControllerTest;
import com.yfwl.yfgp.utils.GetHSTokenUtils;
import com.yfwl.yfgp.utils.JacksonUtils;
import com.yfwl.yfgp.utils.PropertiesUtils;

@Controller
@RequestMapping("/TextAnalyser")
public class TextAnalyser extends BaseController {
	
	int hash[];
	HashInfo info[];
	boolean hashid[];
	int infoidx;
	boolean bInited = false;
	
	@Autowired
	TokenService tokenService;

	public boolean init(){
		try{
			hash = new int[3*65536];
			info = new HashInfo[8000];
			hashid = new boolean[1000000];
			infoidx = 1;
			int id;
			InputStreamReader isr;
			BufferedReader bf;
			
			String s;
			String t[];
			char c1=0;
			char c2=0;
			char c3=0;
			char c4=0;
			int k=0;
			int j=0;
//			if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
//	                .getExternalStorageState())) {
//	            final File updateDir = new File(Environment.getExternalStorageDirectory(),
//	                    Config.saveStockFileName);
	           File file = new File("D:\\tomact\\webapps\\apk\\stocklist.txt");
				 if(file.exists()){
					 isr = new InputStreamReader(new FileInputStream(file),"UTF-8");
				 }else{
					 file.mkdirs();
					 isr = new InputStreamReader(new FileInputStream(file),"UTF-8");
				 }
//			}
//		else{
//				 isr = new InputStreamReader(DemoApplication.getInstance().getApplicationContext().getResources().openRawResource(R.raw.stocklist), "UTF-8");
//			}
			
			
			
			bf = new BufferedReader(isr);
			
			do{
				s=bf.readLine();
				if( s==null ) break;
				//System.out.println(s);
				t = s.split(",");
				if( t.length==2 ){
					if( t[1].length()<=1 ){
						continue;
					}else if( t[1].length()==2 ){
						c1=t[1].charAt(0);
						c2=t[1].charAt(1);
						k = (int)c1+(int)c2;
						
					}else if( t[1].length()==3 ){
						c1=t[1].charAt(0);
						c2=t[1].charAt(1);
						c3=t[1].charAt(2);
						k = (int)c1+(int)c2+(int)c3;
						
					}else if( t[1].length()>=4 ){
						c1=t[1].charAt(0);
						c2=t[1].charAt(1);
						c3=t[1].charAt(2);
						c4=t[1].charAt(3);
						k = (int)c1+(int)c2+(int)c3+(int)c4;
						
					}
					if( hash[k]==0 ){
						hash[k] = infoidx;
						info[infoidx] = new HashInfo();
						info[infoidx].isConfirm = true; 
						info[infoidx].num = 1;
						info[infoidx].code[0] = t[0];
						info[infoidx].name[0] = t[1];
						infoidx++;
					}else{
						j=info[hash[k]].num;
						if( j<5 ){
							info[hash[k]].code[j] = t[0];
							info[hash[k]].name[j] = t[1];
							info[hash[k]].num++;
						}else{
							System.out.println("out of range!");
						}
					}
				}
			}while(true);
			bf.close();
			isr.close();
//			if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
//	                .getExternalStorageState())) {
//	            final File updateDir = new File(Environment.getExternalStorageDirectory(),
//	                    Config.saveStockFileName);
//	           File file = new File(updateDir.getPath(),  "stocklist.txt");
//				 if(file.exists()){
//					 isr = new InputStreamReader(new FileInputStream(file),"UTF-8");
//				 }else{
//					 isr = new InputStreamReader(DemoApplication.getInstance().getApplicationContext().getResources().openRawResource(R.raw.stocklist), "UTF-8");
//				 }
//			}else{
//				 isr = new InputStreamReader(DemoApplication.getInstance().getApplicationContext().getResources().openRawResource(R.raw.stocklist), "UTF-8");
//			}
			
				final  File file2 = new File("D:\\tomact\\webapps\\apk\\stocklist.txt");
				 if(file2.exists()){
					 isr = new InputStreamReader(new FileInputStream(file),"UTF-8");
				 }else{
					 file2.mkdirs();
					 isr = new InputStreamReader(new FileInputStream(file),"UTF-8");
				 }
				 
			
		
				 
			bf = new BufferedReader(isr);
			do{
				s=bf.readLine();
				if( s==null ) break;
				//System.out.println(s);
				t = s.split(",");
				if( t.length==2 ){
					if( t[1].length()>0 ){
						//首字符映射
						c1 = t[1].charAt(0);
						k=(int)c1;
						if( hash[k]==0 ){
							hash[k] = infoidx;
							info[infoidx] = new HashInfo();
							info[infoidx].isConfirm = false; 
							info[infoidx].num = 1;
							infoidx++;
						}else{
							if( info[hash[k]]==null ){
								System.out.println("error! null pointer!");
							}
							if( info[hash[k]].isConfirm==true ){
								//System.out.println("one word duplicate,c1=" + c1 + ",stock = " + info[hash[k]].name[0]);
							}else{
								info[hash[k]].num++;
							}
						}
					}
					
					if( t[1].length()>1 ){
						//首两字符映射
						c1 = t[1].charAt(0);
						c2 = t[1].charAt(1);
						k=(int)c1+(int)c2;
						if( hash[k]==0 ){
							hash[k] = infoidx;
							info[infoidx] = new HashInfo();
							info[infoidx].isConfirm = false; 
							info[infoidx].num = 1;
							infoidx++;
						}else{
							if( info[hash[k]]==null ){
								System.out.println("error! null pointer!");
							}
							if( info[hash[k]].isConfirm==true ){
								//System.out.println("two word duplicate,word=" + c1 + c2 + ",stock = " + info[hash[k]].name[0]);
							}else{
								info[hash[k]].num++;
							}
						}
					}						

					if( t[1].length()>2 ){
						//首三字符映射
						c1 = t[1].charAt(0);
						c2 = t[1].charAt(1);
						c3 = t[1].charAt(2);
						k=(int)c1+(int)c2+(int)c3;
						if( hash[k]==0 ){
							hash[k] = infoidx;
							info[infoidx] = new HashInfo();
							info[infoidx].isConfirm = false; 
							info[infoidx].num = 1;
							infoidx++;
						}else{
							if( info[hash[k]]==null ){
								System.out.println("error! null pointer!");
							}
							if( info[hash[k]].isConfirm==true ){
								//System.out.println("three word duplicate,word=" + c1 + c2 + c3 + ",stock = " + info[hash[k]].name[0]);
							}else{
								info[hash[k]].num++;
							}
						}
					}						
				}
			}while(true);
			bf.close();
			isr.close();
			//System.out.println("infoidx=" + infoidx);
//			if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
//	                .getExternalStorageState())) {
//				
//	            final File updateDir = new File(Environment.getExternalStorageDirectory(),
//	                    Config.saveSecidFileName);
//	            File file = new File(updateDir.getPath(),"secid.txt");
//				 if(file.exists()){
//					 isr = new InputStreamReader(new FileInputStream(file),"UTF-8");
//				 }else{
//					 isr = new InputStreamReader(DemoApplication.getInstance().getApplicationContext().getResources().openRawResource(R.raw.secid), "UTF-8");
//				 }
//			}else{
//				 isr = new InputStreamReader(DemoApplication.getInstance().getApplicationContext().getResources().openRawResource(R.raw.secid), "UTF-8");
//			}
			final  File file3 = new File("D:\\tomact\\webapps\\apk\\stocklist.txt");
			 if(file3.exists()){
				 isr = new InputStreamReader(new FileInputStream(file),"UTF-8");
			 }else{
				 file3.mkdirs();
				 isr = new InputStreamReader(new FileInputStream(file),"UTF-8");
			 }
			bf = new BufferedReader(isr);
			do{
				
				s=bf.readLine();
				if( s==null ) break;
				id = Integer.parseInt(replaceStr(s));
				if( id>0 && id<1000000 ){
					hashid[id] = true;
				}
			}while(true);
			bf.close();
			isr.close();

			
			bInited=true;
			return true;
		}catch(Exception ex){
			//ex.printStackTrace();
			//String ss = getStackMsg(ex);
			// Log.i("Tag", "getStackMsg(ex)--------" + getStackMsg(ex));
			return false;
		}
	}
	
	public String replaceStr(String s){
		return s.replaceAll("\\D+","").replaceAll("\r", "").replaceAll("\n", "").trim();
	}
	private static String getStackMsg(Exception e) {  
        StringBuffer sb = new StringBuffer();  
        StackTraceElement[] stackArray = e.getStackTrace();  
        for (int i = 0; i < stackArray.length; i++) {  
            StackTraceElement element = stackArray[i];  
            sb.append(element.toString() + "\n");  
        }  
        return sb.toString();  
    }  

	public static boolean isNumeric(String str){
		for (int i = 0; i < str.length(); i++){
			if (!Character.isDigit(str.charAt(i))){
				return false;
			}
		}
		return true;
	}

	//-1:错误,0:没找到,1:找到但不确定匹配,2:匹配
	TextAnalyserResult search(String s){
		try{
			TextAnalyserResult r = new TextAnalyserResult();
			init();
			if( bInited==false ){
				r.result = TextAnalyserResult.codeError;
				return r;
			} 
			
			int k=0;
			char c;
			for(int h=0;h<s.length();h++){
				c=s.charAt(h);
				k=k+(int)c;
			}
			
			if( hash[k]==0 ){
				r.result = TextAnalyserResult.codeNotFound;
				return r;
			}else{
				if( info[hash[k]].isConfirm==false ){
					r.result = TextAnalyserResult.codeFoundButNotSure;
					return r;
				}else{
					for(int m=0;m<info[hash[k]].num;m++){
						if( s.equals(info[hash[k]].name[m]) || (info[hash[k]].name[m].indexOf(s)==0 && s.length()==4) ){
							r.result = TextAnalyserResult.codeSure;
							r.code = info[hash[k]].code[m];
							r.name = info[hash[k]].name[m];
							return r;
						}
					}
					r.result = TextAnalyserResult.codeFoundButNotSure;
					return r;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
			TextAnalyserResult r = new TextAnalyserResult();
			r.result = TextAnalyserResult.codeError;
			return r;
		}
	}
	
	
	
	@RequestMapping(value = "/analyse", method = { RequestMethod.POST})
	@ResponseBody
	public Map<String, Object>  analyse(String s,String token){
		//String token = getDefaultToken();
		SimpleDateFormat xgsdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date datenow = new Date();
		String now = xgsdf.format(datenow);
		Map<String, Object> map = new HashMap<String, Object>();
		try{
			TextAnalyserResult r = new TextAnalyserResult();
			init();
			if( bInited==false ){
				r.result = TextAnalyserResult.codeError;
				map.put("msg", "您问的问题我不大清楚哦，等我长大点再回答你吧");
				map.put("data", "");
				map.put("status","1");
				return map;
			} 
	
			TextAnalyserResult r1 = new TextAnalyserResult();
			String s1;
			int k;
			List<Map<String, Object>> arr= new ArrayList<Map<String,Object>>();
			for(int i=0;i<s.length();i++){
				if( Character.isDigit(s.charAt(i)) ){
					if( (i+6)<=s.length() ){
						s1 = s.substring(i,i+6);
						if( isNumeric(s1) ){
							k = Integer.parseInt(s1);
							if( k<1000000 ){
								if( hashid[k]==true ){
									r.result=TextAnalyserResult.codeSure;
									r.code=s1;
									String stock = r.code.startsWith("6") ? r.code + ".SS"
											: r.code + ".SZ";
									
									//获取买卖点advice
									String url = "http://fage008.com:8081/quote/v1/bs";
									HashMap<String, String> map3 = new HashMap<String, String>();
									map3.put("get_type", "offset");
									map3.put("prod_code", stock);
									map3.put("candle_period", "6");
									String haha = ControllerTest.doGet(url, map3);
									JSONObject json=new JSONObject(haha);
									JSONObject candle =json.getJSONObject("data").getJSONObject("candle");
									String advice=candle.getString("advice");
								

									
									//获取最新价等
									String result = GetHSTokenUtils.getReal2(stock, token);
									JSONObject jsonObject = new JSONObject(result);
									//System.out.println(result+"ghfdkjghkfdghjkdf");
//									{"data":{"snapshot":{"fields":["data_timestamp","shares_per_hand","last_px","px_change_rate","px_change","prod_name","preclose_px"],"000001.SZ":[155903000,100,10.65,1.82,0.19,"平安银行",10.46]}}}ghfdkjghkfdghjkdf
								try {
									JSONObject	data=jsonObject.getJSONObject("data");
									JSONObject snapshot = data.getJSONObject("snapshot");
									double last_px = snapshot.getJSONArray(stock).getDouble(2);//最新价
									double preclose_px = snapshot.getJSONArray(stock).getDouble(6);//昨收价
									if(last_px==0.00){
										last_px=preclose_px;
									}
									String name= snapshot.getJSONArray(stock).getString(5);								
									double px_change_rate=snapshot.getJSONArray(stock).getDouble(3);
									double px_change=snapshot.getJSONArray(stock).getDouble(4);
									map.put("code", stock);
									map.put("last_px", last_px);
									map.put("name", name);
									map.put("px_change_rate", px_change_rate);
									map.put("px_change", px_change);
									map.put("advice", advice);
									map.put("time", now);
									arr.add(map);
									map=rspFormat(arr, SUCCESS);
								} catch (Exception e) {
									map=rspFormat("", WRONG_TOKEN);
									// TODO: handle exception
								}	
									return map;
								}
							}
						}
					}
					
				}
				
				r.result = TextAnalyserResult.codeNotFound;
				r1.result = TextAnalyserResult.codeNotFound;
				for(int j=1;j<=4&&j<=s.length()-i;j++){
					s1 = s.substring(i,i+j);
					if( r.result==TextAnalyserResult.codeSure ){
						r1 = search(s1);
						if( r1.result!=TextAnalyserResult.codeFoundButNotSure ) break;
					}else{
						r = search(s1);
						if( r.result==TextAnalyserResult.codeError || r.result==TextAnalyserResult.codeNotFound ) break;
					}
//					if( r.result!=TextAnalyserResult.codeSure ){
//						r = search(s1);
//						if( r.result==TextAnalyserResult.codeError || r.result==TextAnalyserResult.codeNotFound ){
//							break;
//						} 
//					}
				}
				if( r1.result==TextAnalyserResult.codeSure ){
					//r1.order = getOrder(s);
					String stock = r1.code.startsWith("6") ? r1.code + ".SS"
							: r1.code + ".SZ";
					
					//获取买卖点advice
					String url = "http://fage008.com:8081/quote/v1/bs";
					HashMap<String, String> map3 = new HashMap<String, String>();
					map3.put("get_type", "offset");
					map3.put("prod_code", stock);
					map3.put("candle_period", "6");
					String haha = ControllerTest.doGet(url, map3);
					JSONObject json=new JSONObject(haha);
					JSONObject candle =json.getJSONObject("data").getJSONObject("candle");
					String advice=candle.getString("advice");
					
					
					
					String result = GetHSTokenUtils.getReal2(stock, token);
					JSONObject jsonObject = new JSONObject(result);
					
				try {
					JSONObject	data=jsonObject.getJSONObject("data");
					JSONObject snapshot = data.getJSONObject("snapshot");
					double last_px = snapshot.getJSONArray(stock).getDouble(2);//最新价
					double preclose_px = snapshot.getJSONArray(stock).getDouble(6);//昨收价
					if(last_px==0.00){
						last_px=preclose_px;
					}
					String name= snapshot.getJSONArray(stock).getString(5);								
					double px_change_rate=snapshot.getJSONArray(stock).getDouble(3);
					double px_change=snapshot.getJSONArray(stock).getDouble(4);
					map.put("code", stock);
					map.put("last_px", last_px);
					map.put("name", name);
					map.put("px_change_rate", px_change_rate);
					map.put("px_change", px_change);
					map.put("advice", advice);
					map.put("time", now);
					arr.add(map);
					map=rspFormat(arr, SUCCESS);
				} catch (Exception e) {
					// TODO: handle exception
					map=rspFormat("", WRONG_TOKEN);
				}	
					return map;
				}
				if( r.result==TextAnalyserResult.codeSure ){
				
					//r.order = getOrder(s);
					String stock = r.code.startsWith("6") ? r.code + ".SS"
							: r.code + ".SZ";
					//获取买卖点advice
					String url = "http://fage008.com:8081/quote/v1/bs";
					HashMap<String, String> map3 = new HashMap<String, String>();
					map3.put("get_type", "offset");
					map3.put("prod_code", stock);
					map3.put("candle_period", "6");
					String haha = ControllerTest.doGet(url, map3);
					JSONObject json=new JSONObject(haha);
					JSONObject candle =json.getJSONObject("data").getJSONObject("candle");
					String advice=candle.getString("advice");
					
				
					String result = GetHSTokenUtils.getReal2(stock, token);
					JSONObject jsonObject = new JSONObject(result);

				try {
					JSONObject	data=jsonObject.getJSONObject("data");
					JSONObject snapshot = data.getJSONObject("snapshot");
					double last_px = snapshot.getJSONArray(stock).getDouble(2);//最新价
					double preclose_px = snapshot.getJSONArray(stock).getDouble(6);//昨收价
					if(last_px==0.00){
						last_px=preclose_px;
					}
					String name= snapshot.getJSONArray(stock).getString(5);								
					double px_change_rate=snapshot.getJSONArray(stock).getDouble(3);
					double px_change=snapshot.getJSONArray(stock).getDouble(4);
					map.put("code", stock);
					map.put("last_px", last_px);
					map.put("name", name);
					map.put("px_change_rate", px_change_rate);
					map.put("px_change", px_change);
					map.put("advice", advice);
					map.put("time", now);
					arr.add(map);
					map=rspFormat(arr, SUCCESS);
				} catch (Exception e) {
					map=rspFormat("", WRONG_TOKEN);
					// TODO: handle exception
				}	
					return map;
				}
			}
			r.result = TextAnalyserResult.codeNotFound;
			map.put("msg", "您问的问题我不大清楚哦，等我长大点再回答你吧");
			map.put("data", "");
			map.put("status","1");
			return map;
		}catch(Exception ex){
			ex.printStackTrace();
			TextAnalyserResult r = new TextAnalyserResult();
			r.result = TextAnalyserResult.codeError;
			map.put("msg", "您问的问题我不大清楚哦，等我长大点再回答你吧");
			map.put("data", "");
			map.put("status","1");
			return map;
		}
	}
	
}
