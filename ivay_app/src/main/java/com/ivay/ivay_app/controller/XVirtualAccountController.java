package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.dto.ValVirtualAccountReq;
import com.ivay.ivay_app.model.XVirtualAccount;
import com.ivay.ivay_app.service.XVirtualAccountService;
import com.ivay.ivay_app.utils.HttpClientUtils;
import com.ivay.ivay_app.utils.MsgAuthCode;
import com.ivay.ivay_app.utils.RSAEncryptShaCollection;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Api(tags = "虚拟账号")
@RequestMapping("star/virtualAccount")
@RestController
public class XVirtualAccountController {
	@Autowired
	private XVirtualAccountService xVirtualAccountService;
	
    @PostMapping("getRequestId")
    @ApiOperation(value = "获取RequestId")
    public String getRequestId(@RequestParam String PartnerCode, String date) {
		return xVirtualAccountService.getRequestId(PartnerCode, date);
    }
    
    @ApiOperation(value = "testHttp53")
    @GetMapping("/star/testHttp53")
    @ResponseBody
    public String testHttp53(@RequestParam String url, @RequestParam String operation, @RequestParam String id) {
    	XVirtualAccount xVirtualAccount=xVirtualAccountService.queryVirtualAccount(id);
    	String partnerCode="FINTECH";
    	Date date=new Date();
    	String requestTime=getOneBeforeHour(date);
    	String requestDate=new SimpleDateFormat("yyyyMMdd").format(date);
    	String requestId=partnerCode+"BK"+requestDate+"00"+MsgAuthCode.getAuthCode();
    	String accName=xVirtualAccount.getAccName();
    	String clientIdNo=xVirtualAccount.getClientidNo();
//    	String issuedDate=xVirtualAccount.getIssuedDate();
//    	String issuedPlace=xVirtualAccount.getIssuedPlace();
    	String issuedDate="";
    	String issuedPlace="";
    	Integer collectAmount=xVirtualAccount.getCollectAmount();
    	String expireDate=xVirtualAccount.getExpireDate();
    	Integer accountType=xVirtualAccount.getAccountType();
    	String orderId=xVirtualAccount.getOrderId();
    	
    	ValVirtualAccountReq req=new ValVirtualAccountReq();
    	req.setRequestId(requestId);
    	req.setRequestTime(requestTime);
    	req.setPartnerCode(partnerCode);
    	req.setOperation(operation);
    	req.setAccName(accName);
    	req.setClientIdNo(clientIdNo);
    	req.setIssuedDate(issuedDate);
    	req.setIssuedPlace(issuedPlace);
    	req.setCollectAmount(collectAmount.toString());
    	req.setExpireDate(xVirtualAccount.getExpireDate());
    	req.setAccountType(accountType.toString());
    	req.setOrderId(orderId);
    	
    	String encryptStr = requestId + "|" + requestTime + "|"+ partnerCode + "|" + operation + "|" + accName +
                 "|" + clientIdNo + "|" + issuedDate + "|" + issuedPlace + "|" + collectAmount + "|" + expireDate
                 + "|" + accountType + "|" + orderId;
        System.out.println("加密前：" + encryptStr);
        String signature = RSAEncryptShaCollection.encrypt2Sha1(encryptStr);
        req.setSignature(signature);
    	
    	String responseBody=HttpClientUtils.postForObject(url, req);
        return responseBody;
    }
    
    @ApiOperation(value = "testHttp54")
    @GetMapping("/star/testHttp54")
    @ResponseBody
    public String testHttp54(@RequestParam String url, @RequestParam String operation, @RequestParam String id) {
    	XVirtualAccount xVirtualAccount=xVirtualAccountService.queryVirtualAccount(id);
    	String partnerCode="FINTECH";
    	Date date=new Date();
    	String requestTime=getOneBeforeHour(date);
    	String requestDate=new SimpleDateFormat("yyyyMMdd").format(date);
    	String requestId=partnerCode+"BK"+requestDate+"00"+MsgAuthCode.getAuthCode();
    	String accNo=xVirtualAccount.getAccNo();
    	String accName=xVirtualAccount.getAccName();
//    	String issuedDate=xVirtualAccount.getIssuedDate();
//    	String issuedPlace=xVirtualAccount.getIssuedPlace();
    	String issuedDate="";
    	String issuedPlace="";
    	Integer collectAmount=xVirtualAccount.getCollectAmount();
    	String expireDate=xVirtualAccount.getExpireDate();
    	String clientIdNo=xVirtualAccount.getClientidNo();
    	Integer accountType=xVirtualAccount.getAccountType();
    	String orderId=xVirtualAccount.getOrderId();
    	
    	ValVirtualAccountReq req=new ValVirtualAccountReq();
    	req.setRequestId(requestId);
    	req.setRequestTime(requestTime);
    	req.setPartnerCode(partnerCode);
    	req.setOperation(operation);
    	req.setAccNo(accNo);
    	req.setAccName(accName);
    	req.setIssuedDate(issuedDate);
    	req.setIssuedPlace(issuedPlace);
    	req.setCollectAmount(collectAmount.toString());
    	req.setExpireDate(expireDate);
    	req.setClientIdNo(clientIdNo);
    	req.setAccountType(accountType.toString());
    	req.setOrderId(orderId);
    	
    	String encryptStr = requestId + "|" + requestTime + "|"+ partnerCode + "|" + accNo + "|" + accName + "|" + operation +
                "|" + issuedDate + "|" + issuedPlace + "|" + collectAmount + "|" + expireDate+ "|" + clientIdNo + "|" + orderId ;
    	
        System.out.println("加密前：" + encryptStr);
        String signature = RSAEncryptShaCollection.encrypt2Sha1(encryptStr);
        req.setSignature(signature);
    	
    	String responseBody=HttpClientUtils.postForObject(url, req);
        return responseBody;
    }
    
    @ApiOperation(value = "testHttp55")
    @GetMapping("/star/testHttp55")
    @ResponseBody
    public String testHttp55(@RequestParam String url, @RequestParam String operation, @RequestParam String id) {
    	XVirtualAccount xVirtualAccount=xVirtualAccountService.queryVirtualAccount(id);
    	String partnerCode="FINTECH";
    	Date date=new Date();
    	String requestTime=getOneBeforeHour(date);
    	String requestDate=new SimpleDateFormat("yyyyMMdd").format(date);
    	String requestId=partnerCode+"BK"+requestDate+"00"+MsgAuthCode.getAuthCode();
    	String accNo=xVirtualAccount.getAccNo();
    	
    	ValVirtualAccountReq req=new ValVirtualAccountReq();
    	req.setRequestId(requestId);
    	req.setRequestTime(requestTime);
    	req.setPartnerCode(partnerCode);
    	req.setOperation(operation);
    	req.setAccNo(accNo);
    	
    	String encryptStr = requestId + "|" + requestTime + "|"+ partnerCode + "|" + operation +
                  "|" + accNo;
        System.out.println("加密前：" + encryptStr);
        String signature = RSAEncryptShaCollection.encrypt2Sha1(encryptStr);
        
        req.setSignature(signature);
    	
    	String responseBody=HttpClientUtils.postForObject(url, req);
        return responseBody;
    }
    
    @ApiOperation(value = "testHttp56")
    @GetMapping("/star/testHttp56")
    @ResponseBody
    public String testHttp56(@RequestParam String url, @RequestParam String operation, @RequestParam String id) {
    	String partnerCode="FINTECH";
    	Date date=new Date();
    	String requestTime=getOneBeforeHour(date);
    	String requestDate=new SimpleDateFormat("yyyyMMdd").format(date);
    	String requestId=xVirtualAccountService.getRequestId(partnerCode, requestDate);
    	String referenceId="FINTECHBK20190513006646";
    	
    	ValVirtualAccountReq req=new ValVirtualAccountReq();
    	req.setRequestId(requestId);
    	req.setRequestTime(requestTime);
    	req.setPartnerCode(partnerCode);
    	req.setOperation(operation);
    	req.setReferenceId(referenceId);
    	
    	String encryptStr = requestId + "|" + requestTime + "|"+ partnerCode + "|" + operation +
                  "|" + referenceId;
        System.out.println("加密前：" + encryptStr);
        String signature = RSAEncryptShaCollection.encrypt2Sha1(encryptStr);
        req.setSignature(signature);
    	
    	String responseBody=HttpClientUtils.postForObject(url, req);
        return responseBody;
    }
    
    //获取当前时间的上一个小时
    public static String getOneBeforeHour(Date date){
    	Calendar calen = Calendar.getInstance();
    	calen.setTime(date);
    	calen.set(Calendar.HOUR_OF_DAY, calen.get(Calendar.HOUR_OF_DAY) - 2);
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String time =format.format(calen.getTime());
    	System.out.println(time);
		return time;
    }
    
    public static void main(String []args){
    	String encryptStr1 = "FINTECHBK20190510140149|2019-05-10 14:10:32|FINTECH|9001|Toan|101158559|2010-09-23|ban|50000000|2020-09-23|2|13098792234521485335";
//      String encryptStr2="FINTECHBK20190511041079|2019-05-11 15:54:32|FINTECH|900300000103|Toan|9002|2010-09-23|ban|50000000|2020-09-23|101158559|";
    	String encryptStr2="FINTECHBK20190511001337|2019-05-11 15:34:14|FINTECH|900300000103|Toan|9002|2010-09-23|yuenan|50000000|2020-09-23|101158559|";
//    	String encryptStr3="FINTECHBK20190510140279|2019-05-10 14:10:32|FINTECH|9003|900300000103";
    	String encryptStr3="FINTECHBK20190513006175|2019-05-13 07:13:08|FINTECH|9003|900300000103";
    			
    	String encryptStr4="FINTECHBK20190510140479|2019-05-10 14:10:32|FINTECH|9004|FINTECHBK20190510140479";
    	String encryptStr5="BAOKIMBK20190510140299|2019-05-11 10:55:32|BAOKIM|900300000103|101158559|123456|500000|2019-05-11 10:55:32|200000|100000|1|";
        String encryptStr="BAOKIMBK20190513001899|2019-05-13 09:07:00|BAOKIM|900300000103|101158559|123456|100000|2019-05-13 09:07:00|500000|400000|1|";
    	String e="FINTECHBK20190514895922|2019-05-14 16:34:44|FINTECH|9001|test|1234567|2009-05-14|yuenan|0|2019-05-14|1|";
    	System.out.println("加密前：" + e);
//      String signature = RSAEncryptSha1.encrypt2Sha1(encryptStr);
    	String signature = RSAEncryptShaCollection.encrypt2Sha1(e);
        boolean b=RSAEncryptShaCollection.decrypt2Sha1(e, signature);
        System.out.println(b);
    }
    
}
