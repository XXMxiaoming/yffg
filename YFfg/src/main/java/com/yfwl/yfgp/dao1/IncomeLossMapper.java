package com.yfwl.yfgp.dao1;

import java.util.List;

import com.yfwl.yfgp.model.IncomeLoss;

public interface IncomeLossMapper {
	IncomeLoss getincomeLoss(Integer userid);
	IncomeLoss getincomeLoss2(Integer userid);
	
	
	Integer insertIncomeLoss(IncomeLoss inlo);
	
	Integer deleteIncomeLoss(Integer userid);
	
	List<IncomeLoss> getAllIncomeLoss();
	
	IncomeLoss getIncomeLossExsit(int userid);
	
	
	Integer updateIncomeLoss(IncomeLoss inlo);
}
