package com.yfwl.yfgp.serviceImpl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao1.IncomeLossMapper;
import com.yfwl.yfgp.model.IncomeLoss;
import com.yfwl.yfgp.service.IncomeLossService;

@Service
public class IncomeLossServiceImpl implements IncomeLossService{
@Autowired
IncomeLossMapper incomeLossmapper;

@Override
public IncomeLoss getincomeLoss(Integer userid) {
	// TODO Auto-generated method stub
	return incomeLossmapper.getincomeLoss(userid);
}



@Override
public Integer deleteIncomeLoss(Integer userid) {
	// TODO Auto-generated method stub
	return incomeLossmapper.deleteIncomeLoss(userid);
}



@Override
public Integer insertIncomeLoss(IncomeLoss inlo) {
	// TODO Auto-generated method stub
	return incomeLossmapper.insertIncomeLoss(inlo);
}



@Override
public List<IncomeLoss> getAllIncomeLoss() {
	// TODO Auto-generated method stub
	return incomeLossmapper.getAllIncomeLoss();
}



@Override
public IncomeLoss getIncomeLossExsit(int userid) {
	// TODO Auto-generated method stub
	return incomeLossmapper.getIncomeLossExsit(userid);
}



@Override
public Integer updateIncomeLoss(IncomeLoss inlo) {
	// TODO Auto-generated method stub
	return incomeLossmapper.updateIncomeLoss(inlo);
}



@Override
public IncomeLoss getincomeLoss2(Integer userid) {
	// TODO Auto-generated method stub
	return incomeLossmapper.getincomeLoss2(userid);
}

}
