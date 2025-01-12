package com.yfwl.yfgp.service;

import java.util.List;

import com.yfwl.yfgp.model.Stocksend;

public interface StocksendService {
	Integer insertStocksend(Stocksend stocksend);

	Integer updateStocksend(Stocksend stocksend);

	List<Stocksend> selectStocksend(int userid);
	
	Integer updateStocksendStatus(int userid);
}
