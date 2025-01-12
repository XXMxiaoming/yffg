package com.yfwl.yfgp.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.AccountsMapper2;
import com.yfwl.yfgp.dao.OrderMapper;
import com.yfwl.yfgp.model.Accounts;
import com.yfwl.yfgp.model.Accounts2;
import com.yfwl.yfgp.model.Order;
import com.yfwl.yfgp.service.AccountsService2;

@Service
public class AccountsServiceImpl2 implements AccountsService2 {
	
	@Autowired
	private AccountsMapper2 accountsMapper2;
	@Autowired
	private OrderMapper orderMapper;
	
	@Override
	public Accounts2 getFeeAccounts(Integer gid) {
		// TODO Auto-generated method stub
		//return accountsMapper2.getFeeAccounts(gid);
		//remove by allen;
		
		Order order = orderMapper.getZJHBorder(gid);
		Accounts2 accounts2 = accountsMapper2.getFeeAccounts(gid);
		Integer subscribeNum = accountsMapper2.getSubscribeNum(gid);
		accounts2.setSubscribeNum(subscribeNum);
		accounts2.setSubscribeFee(order.getFeeTotal());
		accounts2.setRedPacket(order.getFeeLeft());
		accounts2.setGoalEarnings(Float.parseFloat(order.getBody()));
		accounts2.setLimitPeopleNum(Integer.parseInt(order.getDetail()));
		return accounts2;
	}

	@Override
	public List<Map<String, Object>> getTJAccounts() {
		// TODO Auto-generated method stub
		List<Accounts2> list = accountsMapper2.getTJAccounts();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		if(list.size() > 0){
			for(int i=0; i<list.size(); i++){
				Accounts2 ac2 = list.get(i);
				String gid = String.valueOf(ac2.getGid());
				String seq = accountsMapper2.getTJAccountsSequences(gid);
				Map<String, Object> map = new HashMap<String,Object>();
				map.put("gid", gid);
				map.put("gname", ac2.getGname());
				map.put("seq", seq);
				resultList.add(map);
			}
		}
		return resultList;
	}

	@Override
	public Integer updateGZnum(Integer gid) {
		// TODO Auto-generated method stub
		return accountsMapper2.updateGZnum(gid);
	}

	
	

}
