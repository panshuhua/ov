package com.ivay.ivay_manage.service.impl;

import com.ivay.ivay_manage.dao.master.CustomerDao;
import com.ivay.ivay_manage.model.*;
import com.ivay.ivay_manage.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CustomerServiceImpl implements CustomerService{
	
	@Autowired
	CustomerDao customerDao;

	@Override
	public XFileInfo queryphotoUrl(String id) {
		return customerDao.queryphotoUrl(id);
	}


	@Override
	public int countBasicInfo(Map<String, Object> params) {
		return customerDao.countBasicInfo(params);
	}


	@Override
	public List<XUserInfo> listBasicInfo(Map<String, Object> params, Integer offset,
                                         Integer limit) {
		List<XUserInfo> list=customerDao.listBasicInfo(params, offset, limit);
		for(XUserInfo xUserInfo:list){
			    //用户状态
			   String userStatus=xUserInfo.getUserStatus();
			   if("0".equals(userStatus)){
				   userStatus = "初始状态";
			   }else if("1".equals(userStatus)){
				   userStatus = "注册成功";
			   }else if("2".equals(userStatus)){
				   userStatus = "审核中";
			   }else if("3".equals(userStatus)){
				   userStatus = "授信成功";
			   }else if("4".equals(userStatus)){
				   userStatus = "绑卡成功";
			   }else if("5".equals(userStatus)){
				   userStatus = "借款成功";
			   }else if("6".equals(userStatus)){
				   userStatus = "多次借款";
			   }else if("7".equals(userStatus)){
				   userStatus = "授信失败";
			   }
			   xUserInfo.setUserStatus(userStatus);
			   //性别
			   String sex=xUserInfo.getSex();
			   if("0".equals(sex)){
				   sex = "女";
			   }else if("1".equals(sex)){
				   sex = "男";
			   }else if("2".equals(sex)){
				   sex = "保密";
			   }
			   xUserInfo.setSex(sex);
			   //学历
			   String education=xUserInfo.getEducation();
			   if("0".equals(education)){
				   education = "未上学";
			   }else if("1".equals(education)){
				   education = "小学";
			   }else if("2".equals(education)){
				   education = "初中";
			   }else if("3".equals(education)){
				   education = "高中";
			   }else if("4".equals(education)){
				   education = "大专";
			   }else if("5".equals(education)){
				   education = "本科";
			   }else if("6".equals(education)){
				   education = "研究生及以上学历";
			   }else if("7".equals(education)){
				   education = "其他";
			   }
			   xUserInfo.setEducation(education);
			   //婚姻状况
			   String marital=xUserInfo.getMarital();
			   if("0".equals(marital)){
				   marital = "未婚";
			   }else if("1".equals(marital)){
				   marital = "已婚";
			   }else if("2".equals(marital)){
				   marital = "保密";
			   }
		       xUserInfo.setMarital(marital);
		}
		return list;
	}


	@Override
	public int countContactInfo(Map<String, Object> params) {
		return customerDao.countContactInfo(params);
	}


	@Override
	public List<XUserExtInfo> listContactInfo(Map<String, Object> params,
                                              Integer offset, Integer limit) {
		List<XUserExtInfo> list=customerDao.listContactInfo(params, offset, limit);
		for(XUserExtInfo xUserExtInfo:list){
			String majorRelationship=xUserExtInfo.getMajorRelationship();
			if("0".equals(majorRelationship)){
				majorRelationship = "父母";
			}else if("1".equals(majorRelationship)){
				majorRelationship = "兄弟姐妹";
			}else if("2".equals(majorRelationship)){
				majorRelationship = "亲属";
			}else if("3".equals(majorRelationship)){
				majorRelationship = "朋友";
			}
			xUserExtInfo.setMajorRelationship(majorRelationship);
			
			String bakRelationship=xUserExtInfo.getBakRelationship();
			if("0".equals(bakRelationship)){
				bakRelationship = "父母";
			}else if("1".equals(bakRelationship)){
				bakRelationship = "兄弟姐妹";
			}else if("2".equals(bakRelationship)){
				bakRelationship = "亲属";
			}else if("3".equals(bakRelationship)){
				bakRelationship = "朋友";
			}
			xUserExtInfo.setBakRelationship(bakRelationship);
			
			//各种照片的路径
			String photo1UrlId=xUserExtInfo.getPhoto1Url();
			XFileInfo xFileInfo=customerDao.queryphotoUrl(photo1UrlId);
			if(xFileInfo!=null){
				xUserExtInfo.setPhoto1Url(xFileInfo.getUrl());
			}
			
			String photo2UrlId=xUserExtInfo.getPhoto2Url();
			xFileInfo=customerDao.queryphotoUrl(photo2UrlId);
			if(xFileInfo!=null){
				xUserExtInfo.setPhoto2Url(xFileInfo.getUrl());
			}
			
			String photo3UrlId=xUserExtInfo.getPhoto3Url();
			xFileInfo=customerDao.queryphotoUrl(photo3UrlId);
			if(xFileInfo!=null){
				xUserExtInfo.setPhoto3Url(xFileInfo.getUrl());
			}
			
		}
		
		return list;
		
	}

	@Override
	public int countLoan(Map<String, Object> params) {
		return customerDao.countLoan(params);
	}


	@Override
	public List<XRecordLoan> listLoan(Map<String, Object> params,
                                      Integer offset, Integer limit) {
		List<XRecordLoan> list=customerDao.listLoan(params, offset, limit);
		for(XRecordLoan xRecordLoan:list){
			String loanStatus=xRecordLoan.getLoanStatus();
			
			if("0".equals(loanStatus)){
				loanStatus="打款失败";
			}else if("1".equals(loanStatus)){
				loanStatus="打款成功";
			}else if("2".equals(loanStatus)){
				loanStatus="等待打款";
			}
			xRecordLoan.setLoanStatus(loanStatus);
		}
		return list;
	}


	@Override
	public int countRepay(Map<String, Object> params) {
		return customerDao.countRepay(params);
	}


	@Override
	public List<XRecordRepayment> listRepay(Map<String, Object> params,
                                            Integer offset, Integer limit) {
		List<XRecordRepayment> list=customerDao.listRepay(params, offset, limit);
		for(XRecordRepayment xRecordRepayment:list){
			String repaymentStatus=xRecordRepayment.getRepaymentStatus();
			
			if("0".equals(repaymentStatus)){
				repaymentStatus="待还款";
			}else if("1".equals(repaymentStatus)){
				repaymentStatus="还款中";
			}else if("2".equals(repaymentStatus)){
				repaymentStatus="还款成功";
			}else if("3".equals(repaymentStatus)){
				repaymentStatus="还款失败";
			}
			xRecordRepayment.setRepaymentStatus(repaymentStatus);
		}
		
		return list;
	}


	@Override
	public int countBank(Map<String, Object> params) {
		return customerDao.countBank(params);
	}


	@Override
	public List<XUserBankcoadInfo> listBank(Map<String, Object> params,
                                            Integer offset, Integer limit) {
		return customerDao.listBank(params, offset, limit);
	}
	

}
