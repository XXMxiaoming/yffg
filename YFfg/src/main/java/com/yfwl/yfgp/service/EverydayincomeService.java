package com.yfwl.yfgp.service;

import java.util.List;

import com.yfwl.yfgp.model.Everydayincome;

public interface EverydayincomeService {

	
	public int insertEverydayincome(Everydayincome everydayincome);
	
	
	public List<Everydayincome> getAllEverydayincome();
	
	public int updateEverydayincome(Everydayincome everydayincome);
	
	public List<Everydayincome> getDisEverydayincome();

	public List<Everydayincome> getAllEverydayincomeByUserid(int userid);
	
	 public List<Everydayincome> getAllEverydayincomeByUserid2(int userid);
	
	public Integer deleteEverydayincome(int userid);
}
