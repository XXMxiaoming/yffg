package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao1.RevenueMapper;
import com.yfwl.yfgp.model.Revenue;
import com.yfwl.yfgp.service.RevenueService;
@Service
public class RevenueServiceImpl implements RevenueService {
	@Autowired
	RevenueMapper revenueMapper;
	
	@Override
	public Integer insertRevenue(Integer userid) {
		// TODO Auto-generated method stub
		return revenueMapper.insertRevenue(userid);
	}

	@Override
	public Integer deleteRevenue(Integer userid) {
		// TODO Auto-generated method stub
		return revenueMapper.deleteRevenue(userid);
	}

	@Override
	public List<Revenue> getAllRevenue() {
		// TODO Auto-generated method stub
		return revenueMapper.getAllRevenue();
	}

	@Override
	public Integer updateRevenue(Revenue revenue) {
		// TODO Auto-generated method stub
		return revenueMapper.updateRevenue(revenue);
	}

	@Override
	public Integer insertRevenue2(Revenue revenue) {
		// TODO Auto-generated method stub
		return revenueMapper.insertRevenue2(revenue);
	}

	@Override
	public Revenue selectRevenue(Integer userid) {
		// TODO Auto-generated method stub
		return revenueMapper.selectRevenue(userid);
	}

	@Override
	public Integer updateRevenue2(Revenue revenue) {
		// TODO Auto-generated method stub
		return revenueMapper.updateRevenue2(revenue);
	}

	@Override
	public Revenue selectRevenue2() {
		// TODO Auto-generated method stub
		return revenueMapper.selectRevenue2();
	}

}
