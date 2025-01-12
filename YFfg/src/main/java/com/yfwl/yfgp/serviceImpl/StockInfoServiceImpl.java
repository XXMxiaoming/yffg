package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao1.StockinfoMapper;
import com.yfwl.yfgp.model.Stockinfo;
import com.yfwl.yfgp.service.StockInfoService;
@Service
public class StockInfoServiceImpl implements StockInfoService{
	
	@Autowired
	private StockinfoMapper stockinfoMapper;

	@Override
	public Stockinfo getStock(String stockCode) {
		// TODO Auto-generated method stub
		return stockinfoMapper.getStock(stockCode);
	}

	@Override
	public List<Stockinfo> getAllStockinfo() {
		// TODO Auto-generated method stub
		return stockinfoMapper.getAllStockinfo();
	}

	@Override
	public Integer updateStockinfo(Stockinfo stockinfo) {
		// TODO Auto-generated method stub
		return stockinfoMapper.updateStockinfo(stockinfo);
	}

	@Override
	public Stockinfo getStockinfo(Stockinfo stockinfo) {
		// TODO Auto-generated method stub
		return stockinfoMapper.getStockinfo(stockinfo);
	}

	@Override
	public Integer insertStockinfo(Stockinfo stockinfo) {
		// TODO Auto-generated method stub
		return stockinfoMapper.insertStockinfo(stockinfo);
	}

	@Override
	public Stockinfo getStockinfoByName(Stockinfo stockinfo) {
		// TODO Auto-generated method stub
		return stockinfoMapper.getStockinfoByName(stockinfo);
	}


	
	

}
