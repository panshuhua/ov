package com.ivay.ivay_app.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.ivay.ivay_app.dto.BaokimResponseStatus;
import com.ivay.ivay_app.dto.EbayResponseStatus;
import com.ivay.ivay_app.dto.EbayTransfersReq;
import com.ivay.ivay_app.dto.EbayTransfersRsp;
import com.ivay.ivay_app.dto.EbayVirtualAccountReq;
import com.ivay.ivay_app.dto.EbayVirtualAccountRsp;
import com.ivay.ivay_app.service.XEbayLoanAPIService;
import com.ivay.ivay_app.service.XEbayRepayAPIService;
import com.ivay.ivay_common.utils.DateUtils;
import com.ivay.ivay_common.utils.HttpClientUtils;
import com.ivay.ivay_common.utils.JsonUtils;
import com.ivay.ivay_common.utils.StringUtil;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_common.utils.TripleDESEncryption;
import com.ivay.ivay_repository.dao.master.XEbayVirtualAccountDao;
import com.ivay.ivay_repository.model.XEbayData;
import com.ivay.ivay_repository.model.XEbayTransfersInfo;
import com.ivay.ivay_repository.model.XEbayVirtualAccount;

@Service
public class XEbayRepayAPIServiceImpl implements XEbayRepayAPIService{
	private static final Logger logger = LoggerFactory.getLogger(XEbayRepayAPIServiceImpl.class);
	
	@Value("${ebay_api_collection_url}")
	private String collectionUrl;

    @Value("${ebay_api_merchant_code}")
    private String merchantCode;
    
    @Value("${ebay_api_keyMahoa}")
    private String keyMahoa;
    
    @Value("${ebay_api_effective_year}")
    private Integer effectiveYear;
    
    @Resource
    XEbayVirtualAccountDao xEbayVirtualAccountDao;

	@Override
	public EbayVirtualAccountRsp registVitualAccount(String mapId,Number amount,String customerName) throws Exception {
		String pcode=SysVariable.API_PCODE_REGISTER_VIRTUALACCOUNT;
		String merchant_code = merchantCode;
		//mapId = "DD20190604000032";  //orderId
		amount = 50000;
		String start_date = DateUtils.getNowDateYYYYMMDD()+"000000"; // chu y format yyyyMMddHHmmss
		String end_date = DateUtils.addYears(effectiveYear)+"235959";  // chu y format yyyyMMddHHmmss
		String condition = SysVariable.API_CONDITION_VIRTUALACCOUNT_ONE;
		//customerName = "NGUYEN VAN A";	// Capitalized letters without tones
		String request_id = merchant_code+"_"+UUID.randomUUID(); // No duplicate
		String bank_code = SysVariable.API_BANK_CODE_EBAY;
	
		//扩展信息-可选参数
//		XEbayExtend extendObj = new XEbayExtend();
//		extendObj.setPhone(phone);
//		extendObj.setEmail(email);
//		extendObj.setAddress(address);
//		extendObj.setId(id);
							
		XEbayData dataObj = new XEbayData();
		dataObj.setMap_id(mapId);
		dataObj.setAmount(amount);
		dataObj.setStart_date(start_date);
		dataObj.setEnd_date(end_date);
		dataObj.setCondition(condition);
		dataObj.setCustomer_name(customerName);
		dataObj.setRequest_id(request_id);
		dataObj.setBank_code(bank_code);	
//		dataObj.setExtend(extendObj);
		
		String data = JsonUtils.objectToJson(dataObj);
		logger.info("加密前的请求参数："+data);
		String encryptValue = TripleDESEncryption.encrypt(keyMahoa, data);
		EbayVirtualAccountReq req = new EbayVirtualAccountReq();
		
		req.setPcode(pcode);
		req.setMerchant_code(merchant_code);
		req.setData(encryptValue);
		
		EbayVirtualAccountRsp rsp=callCollectionApi(req, dataObj);
		return rsp;
	}

	@Override
	public EbayVirtualAccountRsp updateVitualAccount(String mapId,Number amount,String customerName,String accountNo) throws Exception {
		String pcode=SysVariable.API_PCODE_UPDATE_VIRTUALACCOUNT;
		String merchant_code = merchantCode;
		//mapId = "DD20190604000032";  //orderId
		amount = 60000;
		String start_date = DateUtils.getNowDateYYYYMMDD()+"000000"; // chu y format yyyyMMddHHmmss
		String end_date = DateUtils.addYears(effectiveYear)+"235959";  // chu y format yyyyMMddHHmmss
		String condition = SysVariable.API_CONDITION_VIRTUALACCOUNT_ONE;
		//customerName = "NGUYEN VAN A";	// Capitalized letters without tones
		String request_id = merchant_code+"_"+UUID.randomUUID(); // No duplicate
		
		XEbayData dataObj = new XEbayData();
		dataObj.setMap_id(mapId);
		dataObj.setAmount(amount);
		dataObj.setStart_date(start_date);
		dataObj.setEnd_date(end_date);
		dataObj.setCondition(condition);
		dataObj.setCustomer_name(customerName);
		dataObj.setRequest_id(request_id);
		dataObj.setAccount_no(accountNo); //修改时需要原来的虚拟账号
		
		String data = JsonUtils.objectToJson(dataObj);
		logger.info("加密前的请求参数："+data);
		String encryptValue = TripleDESEncryption.encrypt(keyMahoa, data);
		EbayVirtualAccountReq req = new EbayVirtualAccountReq();
		
		req.setPcode(pcode);
		req.setMerchant_code(merchant_code);
		req.setData(encryptValue);
		
		EbayVirtualAccountRsp rsp=callCollectionApi(req, dataObj);
		return rsp;
	}

	@Override
	public EbayVirtualAccountRsp cancelMapping(String mapId,String accountNo) throws Exception{
		String pcode=SysVariable.API_PCODE_MAPPING_CANCELATION;
		String merchant_code = merchantCode;
		String request_id = merchant_code+"_"+UUID.randomUUID(); 
		XEbayData dataObj=new XEbayData();
		dataObj.setMap_id(mapId);
		dataObj.setRequest_id(request_id);
		dataObj.setAccount_no(accountNo);
		
		String data = JsonUtils.objectToJson(dataObj);
		logger.info("加密前的请求参数："+data);
		String encryptValue = TripleDESEncryption.encrypt(keyMahoa, data);
		EbayVirtualAccountReq req = new EbayVirtualAccountReq();
		
		req.setPcode(pcode);
		req.setMerchant_code(merchant_code);
		req.setData(encryptValue);
		
		EbayVirtualAccountRsp rsp=callCollectionApi(req, dataObj);
		return rsp;
	}

	@Override
	public EbayVirtualAccountRsp viewMappingStatus(EbayVirtualAccountReq ebayDataObjectReq) {
		
		return null;
	}

	@Override
	public EbayVirtualAccountRsp BalanceFlucfluctuationNotice(EbayVirtualAccountReq ebayDataObjectReq) {
		// TODO Auto-generated method stub
		return null;
	}

	private EbayVirtualAccountRsp callCollectionApi(EbayVirtualAccountReq ebayVirtualAccountReq,XEbayData data) throws IOException {
		String json = HttpClientUtils.postByJson(collectionUrl, ebayVirtualAccountReq);
		EbayVirtualAccountRsp ebayVirtualAccountRsp = JSON.parseObject(json, EbayVirtualAccountRsp.class);
        loggerVirtualAccountInfo(ebayVirtualAccountReq, ebayVirtualAccountRsp,data);
        return ebayVirtualAccountRsp;
    }
	
	/**
     * 调用接口虚拟账号接口时，记录请求和响应信息到数据库
     * @param transfersReq
     * @param transfersRsp
     */
    private void loggerVirtualAccountInfo(EbayVirtualAccountReq ebayVirtualAccountReq, EbayVirtualAccountRsp ebayVirtualAccountRsp,XEbayData data) {
        XEbayVirtualAccount xEbayVirtualAccount = new XEbayVirtualAccount();
        xEbayVirtualAccount.setPcode(ebayVirtualAccountReq.getPcode());
        xEbayVirtualAccount.setMerchantCode(ebayVirtualAccountReq.getMerchant_code());
        xEbayVirtualAccount.setMapId(data.getMap_id());
        if(!org.springframework.util.StringUtils.isEmpty(data.getAmount())) {
        	BigDecimal bigAmount=new BigDecimal(Integer.parseInt(data.getAmount().toString()));
        	xEbayVirtualAccount.setAmount(bigAmount);
        }
        
        xEbayVirtualAccount.setStartDate(data.getStart_date());
        xEbayVirtualAccount.setEndDate(data.getEnd_date());
        xEbayVirtualAccount.setCondition(data.getCondition());
        xEbayVirtualAccount.setCustomerName(data.getCustomer_name());
        xEbayVirtualAccount.setRequestId(data.getRequest_id());
        xEbayVirtualAccount.setBankCode(data.getBank_code());
        
        String pcode = ebayVirtualAccountReq.getPcode();
        
        if (ebayVirtualAccountRsp == null) {
        	ebayVirtualAccountRsp = new EbayVirtualAccountRsp();
        	ebayVirtualAccountRsp.setMessage("调用还款虚拟账号接口，没有返回");
            logger.info("虚拟账号接口请求没有返回");
        } else {
        	xEbayVirtualAccount.setResponseCode(ebayVirtualAccountRsp.getResponse_code());
        	xEbayVirtualAccount.setMessage(ebayVirtualAccountRsp.getMessage());
        	xEbayVirtualAccount.setAccountNo(ebayVirtualAccountRsp.getAccount_no());
        	xEbayVirtualAccount.setAccountName(ebayVirtualAccountRsp.getAccount_name());
        	xEbayVirtualAccount.setBankName(ebayVirtualAccountRsp.getBank_name());
        }
        
        if(SysVariable.API_PCODE_REGISTER_VIRTUALACCOUNT.equals(pcode)) {
        	 xEbayVirtualAccountDao.sava(xEbayVirtualAccount);
        }else if(SysVariable.API_PCODE_UPDATE_VIRTUALACCOUNT.equals(pcode)) {
        	//修改时出现错误时不能，必须调用接口取消mapping记录才有意义，否则mapping没取消实际上还没更新
        	 if("00".equals(ebayVirtualAccountRsp.getResponse_code())) {
        		 xEbayVirtualAccountDao.update(xEbayVirtualAccount);
        	 }else {
        		 //记录修改错误的信息
        		 xEbayVirtualAccountDao.sava(xEbayVirtualAccount);
        	 }
        	
        	 
        }else if(SysVariable.API_PCODE_MAPPING_CANCELATION.equals(pcode)) {
        	 xEbayVirtualAccountDao.delete(xEbayVirtualAccount);
        }
          
     }
    
    
}
