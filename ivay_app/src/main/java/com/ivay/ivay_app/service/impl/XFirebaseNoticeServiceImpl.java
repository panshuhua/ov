package com.ivay.ivay_app.service.impl;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.ivay.ivay_app.service.XFirebaseNoticeService;
import com.ivay.ivay_app.utils.FirebaseUtil;
import com.ivay.ivay_common.config.I18nService;
import com.ivay.ivay_repository.dao.master.XUserInfoDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class XFirebaseNoticeServiceImpl implements XFirebaseNoticeService{
	
	private static final Logger logger = LoggerFactory.getLogger(XFirebaseNoticeService.class);
	 
	@Autowired
	XUserInfoDao xUserInfoDao;
	@Autowired
	I18nService i18nService;

	@Override
	public boolean sendAuditNotice(){
		List<String> fmcTokens=xUserInfoDao.findAuditPassUsers();
		for(String fmcToken:fmcTokens) {
			try {
				if(!StringUtils.isEmpty(fmcToken)) {
					FirebaseUtil.sendMsgToFmcToken(fmcToken, i18nService.getMessage("firebase.notice.audit.msg"));
				}
				
			} catch (FirebaseMessagingException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
				return false;
			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
				return false;
			} catch (ExecutionException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
				return false;
			}
		}
		
		logger.info("审核通知成功发送！");
		return true;
		
	}

	@Override
	public boolean sendLoanNotice(){
		List<String> fmcTokens=xUserInfoDao.findLoanSuccessUsers();
		for(String fmcToken:fmcTokens) {
			try {
				if(!StringUtils.isEmpty(fmcToken)) {
					FirebaseUtil.sendMsgToFmcToken(fmcToken, i18nService.getMessage("firebase.notice.loan.msg"));
				}
				
			} catch (FirebaseMessagingException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
				return false;
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
				return false;
			} catch (ExecutionException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
				return false;
			}
		}
		
		logger.info("借款成功的通知成功发送！");
		return true;
		
	}

	@Override
	public boolean sendRepaymentNotice(){
		List<String> fmcTokens=xUserInfoDao.findShouldRepaymentUsers();
		for(String fmcToken:fmcTokens) {
			try {
				if(!StringUtils.isEmpty(fmcToken)) {
					FirebaseUtil.sendMsgToFmcToken(fmcToken, i18nService.getMessage("firebase.notice.repayment.msg"));
				}
				
			} catch (FirebaseMessagingException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
				return false;
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
				return false;
			} catch (ExecutionException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
				return false;
			}
		}
		
		logger.info("还款到期的通知成功发送！");
		return true;
	}

}
