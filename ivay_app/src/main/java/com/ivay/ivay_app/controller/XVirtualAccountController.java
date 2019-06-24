package com.ivay.ivay_app.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ivay.ivay_app.dto.ValVirtualAccountReq;
import com.ivay.ivay_app.service.XVirtualAccountService;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.utils.HttpClientUtils;
import com.ivay.ivay_common.utils.MsgAuthCode;
import com.ivay.ivay_common.utils.RSAEncryptShaCollection;
import com.ivay.ivay_repository.dao.master.XRecordLoanDao;
import com.ivay.ivay_repository.model.XVirtualAccount;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "虚拟账号")
@RequestMapping("star/virtualAccount")
@RestController
public class XVirtualAccountController {

    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private XVirtualAccountService xVirtualAccountService;

    @Value("${api_partner_code}")
    private String partnerCode;

    @Value("${api_request_id_prefix}")
    private String requestIdPrefix;

    @Resource
    XRecordLoanDao xRecordLoanDao;

    @ApiOperation(value = "自动创建还款虚拟账号-test")
    @GetMapping("/createVirtualAccount")
    @ResponseBody
    public Response<String> createVirtualAccount() {
        Response<String> response = new Response<String>();
        xVirtualAccountService.saveVirtualAccount();
        return response;
    }

    @ApiOperation(value = "testHttp53")
    @GetMapping("/testHttp53")
    @ResponseBody
    public String testHttp53(@RequestParam String url, @RequestParam String operation, @RequestParam String id) {
        XVirtualAccount xVirtualAccount = xVirtualAccountService.queryVirtualAccount(id);
        Date date = new Date();
        String requestTime = getOneBeforeHour(date);
        String requestDate = new SimpleDateFormat("yyyyMMdd").format(date);
        String requestId = requestIdPrefix + requestDate + "00" + MsgAuthCode.getAuthCode();
        String accName = xVirtualAccount.getAccName();
        String clientIdNo = xVirtualAccount.getClientidNo();
        // String issuedDate=xVirtualAccount.getIssuedDate();
        // String issuedPlace=xVirtualAccount.getIssuedPlace();
        String issuedDate = "";
        String issuedPlace = "";
        Integer collectAmount = xVirtualAccount.getCollectAmount();
        String expireDate = xVirtualAccount.getExpireDate();
        Integer accountType = xVirtualAccount.getAccountType();
        String orderId = xVirtualAccount.getOrderId();

        ValVirtualAccountReq req = new ValVirtualAccountReq();
        req.setRequestId(requestId);
        req.setRequestTime(requestTime);
        req.setPartnerCode(partnerCode);
        req.setOperation(operation);
        req.setAccName(accName);
        req.setClientIdNo(clientIdNo);
        req.setIssuedDate(issuedDate);
        req.setIssuedPlace(issuedPlace);
        req.setCollectAmount(collectAmount.toString());
        if (StringUtils.isEmpty(xVirtualAccount.getExpireDate())) {
            expireDate = "";
        }
        req.setExpireDate(xVirtualAccount.getExpireDate());
        req.setAccountType(accountType.toString());
        req.setOrderId(orderId);

        String encryptStr = requestId + "|" + requestTime + "|" + partnerCode + "|" + operation + "|" + accName + "|"
            + clientIdNo + "|" + issuedDate + "|" + issuedPlace + "|" + collectAmount + "|" + expireDate + "|"
            + accountType + "|" + orderId;
        System.out.println("加密前：" + encryptStr);
        String signature = RSAEncryptShaCollection.encrypt2Sha1(encryptStr);
        req.setSignature(signature);

        String responseBody = HttpClientUtils.postForObject(url, req);
        return responseBody;
    }

    @ApiOperation(value = "testHttp54")
    @GetMapping("/testHttp54")
    @ResponseBody
    public String testHttp54(@RequestParam String url, @RequestParam String operation, @RequestParam String id) {
        XVirtualAccount xVirtualAccount = xVirtualAccountService.queryVirtualAccount(id);
        Date date = new Date();
        String requestTime = getOneBeforeHour(date);
        String requestDate = new SimpleDateFormat("yyyyMMdd").format(date);
        String requestId = requestIdPrefix + requestDate + "00" + MsgAuthCode.getAuthCode();
        String accNo = xVirtualAccount.getAccNo();
        String accName = xVirtualAccount.getAccName();
        // String issuedDate=xVirtualAccount.getIssuedDate();
        // String issuedPlace=xVirtualAccount.getIssuedPlace();
        String issuedDate = "";
        String issuedPlace = "";
        Integer collectAmount = xVirtualAccount.getCollectAmount();
        String expireDate = xVirtualAccount.getExpireDate();
        String clientIdNo = xVirtualAccount.getClientidNo();
        Integer accountType = xVirtualAccount.getAccountType();
        String orderId = xVirtualAccount.getOrderId();

        ValVirtualAccountReq req = new ValVirtualAccountReq();
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

        String encryptStr = requestId + "|" + requestTime + "|" + partnerCode + "|" + accNo + "|" + accName + "|"
            + operation + "|" + issuedDate + "|" + issuedPlace + "|" + collectAmount + "|" + expireDate + "|"
            + clientIdNo + "|" + orderId;

        System.out.println("加密前：" + encryptStr);
        String signature = RSAEncryptShaCollection.encrypt2Sha1(encryptStr);
        req.setSignature(signature);

        String responseBody = HttpClientUtils.postForObject(url, req);
        return responseBody;
    }

    @ApiOperation(value = "testHttp55")
    @GetMapping("/testHttp55")
    @ResponseBody
    public String testHttp55(@RequestParam String url, @RequestParam String operation, @RequestParam String id) {
        XVirtualAccount xVirtualAccount = xVirtualAccountService.queryVirtualAccount(id);
        Date date = new Date();
        String requestTime = getOneBeforeHour(date);
        String requestDate = new SimpleDateFormat("yyyyMMdd").format(date);
        String requestId = requestIdPrefix + requestDate + "00" + MsgAuthCode.getAuthCode();
        String accNo = xVirtualAccount.getAccNo();

        ValVirtualAccountReq req = new ValVirtualAccountReq();
        req.setRequestId(requestId);
        req.setRequestTime(requestTime);
        req.setPartnerCode(partnerCode);
        req.setOperation(operation);
        req.setAccNo(accNo);

        String encryptStr = requestId + "|" + requestTime + "|" + partnerCode + "|" + operation + "|" + accNo;
        System.out.println("加密前：" + encryptStr);
        String signature = RSAEncryptShaCollection.encrypt2Sha1(encryptStr);

        req.setSignature(signature);

        String responseBody = HttpClientUtils.postForObject(url, req);
        return responseBody;
    }

    @ApiOperation(value = "testHttp56")
    @GetMapping("/testHttp56")
    @ResponseBody
    public String testHttp56(@RequestParam String url, @RequestParam String operation, @RequestParam String id) {
        Date date = new Date();
        String requestTime = getOneBeforeHour(date);
        String requestDate = new SimpleDateFormat("yyyyMMdd").format(date);
        String requestId = xVirtualAccountService.getRequestId(partnerCode, requestDate);
        String referenceId = "FINTECHBK20190513006646";

        ValVirtualAccountReq req = new ValVirtualAccountReq();
        req.setRequestId(requestId);
        req.setRequestTime(requestTime);
        req.setPartnerCode(partnerCode);
        req.setOperation(operation);
        req.setReferenceId(referenceId);

        String encryptStr = requestId + "|" + requestTime + "|" + partnerCode + "|" + operation + "|" + referenceId;
        System.out.println("加密前：" + encryptStr);
        String signature = RSAEncryptShaCollection.encrypt2Sha1(encryptStr);
        req.setSignature(signature);

        String responseBody = HttpClientUtils.postForObject(url, req);
        return responseBody;
    }

    // 获取当前时间的上一个小时
    public static String getOneBeforeHour(Date date) {
        Calendar calen = Calendar.getInstance();
        calen.setTime(date);
        calen.set(Calendar.HOUR_OF_DAY, calen.get(Calendar.HOUR_OF_DAY) - 2);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(calen.getTime());
        System.out.println(time);
        return time;
    }

    public static void main(String[] args) {
        String encryptStr =
            "BAOKIMBK20190513001899|2019-05-13 09:07:00|BAOKIM|900300000103|101158559|123456|100000|2019-05-13 09:07:00|500000|400000|1|";
        System.out.println("加密前：" + encryptStr);
        String signature = RSAEncryptShaCollection.encrypt2Sha1(encryptStr);
        boolean b = RSAEncryptShaCollection.decrypt2Sha1(encryptStr, signature);
        System.out.println(b);
    }

}
