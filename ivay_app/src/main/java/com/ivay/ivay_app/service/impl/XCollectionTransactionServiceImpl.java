package com.ivay.ivay_app.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ivay.ivay_app.dto.BaokimResponseStatus;
import com.ivay.ivay_app.dto.CollectionTransactionNotice;
import com.ivay.ivay_app.dto.CollectionTransactionRsp;
import com.ivay.ivay_app.dto.EbayBlanceFlucNoticeRsp;
import com.ivay.ivay_app.dto.EbayResponseStatus;
import com.ivay.ivay_app.dto.XBalanceFuctNoticeReq;
import com.ivay.ivay_app.service.XCollectionTransactionService;
import com.ivay.ivay_app.service.XConfigService;
import com.ivay.ivay_app.service.XRecordRepaymentService;
import com.ivay.ivay_common.utils.DateUtils;
import com.ivay.ivay_common.utils.JsonUtils;
import com.ivay.ivay_common.utils.MsgAuthCode;
import com.ivay.ivay_common.utils.RSAEncryptShaCollection;
import com.ivay.ivay_common.utils.RSASign;
import com.ivay.ivay_common.utils.StringUtil;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_common.utils.UUIDUtils;
import com.ivay.ivay_repository.dao.master.TokenDao;
import com.ivay.ivay_repository.dao.master.XCollectionTransactionDao;
import com.ivay.ivay_repository.dao.master.XRecordLoanDao;
import com.ivay.ivay_repository.model.XCollectionTransaction;
import com.ivay.ivay_repository.model.XEbayCollectionNotice;
import com.ivay.ivay_repository.model.XRecordLoan;
import com.ivay.ivay_repository.model.XRecordRepayment;

@Service
public class XCollectionTransactionServiceImpl implements XCollectionTransactionService {
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Autowired
    TokenDao tokenDao;
    @Autowired
    XCollectionTransactionDao xCollectionTransactionDao;
    @Autowired
    private XRecordRepaymentService xRecordRepaymentService;
    @Autowired
    private XRecordLoanDao xRecordLoanDao;
    @Autowired
    private XConfigService xConfigService;

    @Value("${ebay_api_notice_publickey}")
    private String ebayNoticePublicKeyPath;
    @Value("${ebay_api_merchant_code}")
    private String ebayMerchantCode;
    @Value("${spring.profiles.include}")
    private String environment;

    @Override
    public String getRequestId(String PartnerCode, String date) {
        String uniqueId = tokenDao.getUniqueId();
        return PartnerCode + "BK" + date + uniqueId;
    }

    @Override
    public boolean checkRequestId(String requestId) {
        String reqId = xCollectionTransactionDao.findRequestId(requestId);
        if (!StringUtils.isEmpty(reqId)) {
            return true;
        }
        return false;
    }

    @Override
    public long getCollectAmount(String accNo) {
        return xCollectionTransactionDao.getCollectAmount(accNo);
    }

    @Override
    public CollectionTransactionRsp noticeCollection(CollectionTransactionNotice notice) throws ParseException {
        logger.info("baokim回调还款接口--------------------------------------");
        String TransId = notice.getTransId();
        String ResponseCode = BaokimResponseStatus.CollectionSuccess.getCode();
        String ResponseMessage = BaokimResponseStatus.CollectionSuccess.getMessage();
        String RequestId = notice.getRequestId();
        String RequestTime = notice.getRequestTime();
        String PartnerCode = notice.getPartnerCode();
        String AccNo = notice.getAccNo();
        String ClientIdNo = notice.getClientIdNo();
        String TransAmount = notice.getTransAmount();
        String TransTime = notice.getTransTime();
        String BefTransDebt = notice.getBefTransDebt();
        String AffTransDebt = notice.getAffTransDebt();

        String AccountType = notice.getAccountType();
        if (AccountType == null) {
            AccountType = "";
        }
        String OrderId = notice.getOrderId();
        if (OrderId == null) {
            OrderId = "";
        }

        String Signature = notice.getSignature();

        CollectionTransactionRsp rsp = new CollectionTransactionRsp();
        rsp.setAccNo(notice.getAccNo());
        rsp.setAffTransDebt(notice.getAffTransDebt());
        Date reqTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(RequestTime);
        String RequestDate = new SimpleDateFormat("yyyyMMdd").format(reqTime);
        String ReferenceId = PartnerCode + "BK" + RequestDate + "00" + MsgAuthCode.getAuthCode();
        logger.info("ReferenceId=" + ReferenceId);
        rsp.setReferenceId(ReferenceId);
        String rspEncryptStr =
            ResponseCode + "|" + ResponseMessage + "|" + ReferenceId + "|" + AccNo + "|" + AffTransDebt;
        logger.info("返回给baokim的签名明文=" + rspEncryptStr);

        String rspSignature = RSAEncryptShaCollection.encrypt2Sha1(rspEncryptStr);
        rsp.setSignature(rspSignature);

        // 校验各个字段
        // RequestId查重
        boolean existsRequestId = checkRequestId(RequestId);
        if (existsRequestId) {
            ResponseCode = BaokimResponseStatus.IncorrectRequestId.getCode();
            ResponseMessage = BaokimResponseStatus.IncorrectRequestId.getMessage();
            setRsp(rsp, ResponseCode, ResponseMessage);
            return rsp;
        }

        boolean isCorrectRequestTime = DateUtils.verifyDateTime(RequestTime); // TODO 时间校验增加规则
        if (!isCorrectRequestTime) {
            ResponseCode = BaokimResponseStatus.IncorrectRequestId.getCode();
            ResponseMessage = BaokimResponseStatus.IncorrectRequestId.getMessage();
            setRsp(rsp, ResponseCode, ResponseMessage);
            return rsp;
        }

        boolean isCorrect = StringUtil.isNumeric(TransAmount);
        if (!isCorrect) {
            ResponseCode = BaokimResponseStatus.IncorrectTransAmount.getCode();
            ResponseMessage = BaokimResponseStatus.IncorrectTransAmount.getMessage();
            setRsp(rsp, ResponseCode, ResponseMessage);
            return rsp;
        }

        isCorrect = StringUtil.isNumeric(TransAmount);
        if (!isCorrect) {
            ResponseCode = BaokimResponseStatus.IncorrectTransAmount.getCode();
            ResponseMessage = BaokimResponseStatus.IncorrectTransAmount.getMessage();
            setRsp(rsp, ResponseCode, ResponseMessage);
            return rsp;
        }

        isCorrect = StringUtil.isNumeric(BefTransDebt);
        if (!isCorrect) {
            ResponseCode = BaokimResponseStatus.IncorrectBefTransDebt.getCode();
            ResponseMessage = BaokimResponseStatus.IncorrectBefTransDebt.getMessage();
            setRsp(rsp, ResponseCode, ResponseMessage);
            return rsp;
        }

        isCorrect = StringUtil.isNumeric(AffTransDebt);
        if (!isCorrect) {
            ResponseCode = BaokimResponseStatus.IncorrectAffTransDebt.getCode();
            ResponseMessage = BaokimResponseStatus.IncorrectAffTransDebt.getMessage();
            setRsp(rsp, ResponseCode, ResponseMessage);
            rsp.setSignature("");
            return rsp;
        }

        boolean isCorrectTime = DateUtils.verifyDateTime(TransTime);
        if (!isCorrectTime) {
            ResponseCode = BaokimResponseStatus.IncorrectTransTime.getCode();
            ResponseMessage = BaokimResponseStatus.IncorrectTransTime.getMessage();
            setRsp(rsp, ResponseCode, ResponseMessage);
            return rsp;
        }
        boolean hasSpecialChar = StringUtil.hasSpecialChar(TransId);
        // 查询transId是否存在，存在则不允许调用
        int transIdCount = xCollectionTransactionDao.queryByTransId(TransId);
        if (hasSpecialChar || transIdCount > 1) {
            ResponseCode = BaokimResponseStatus.IncorrectTransId.getCode();
            ResponseMessage = BaokimResponseStatus.IncorrectTransId.getMessage();
            setRsp(rsp, ResponseCode, ResponseMessage);
            return rsp;
        }

        String encryptStr = RequestId + "|" + RequestTime + "|" + PartnerCode + "|" + AccNo + "|" + ClientIdNo + "|"
            + TransId + "|" + TransAmount + "|" + TransTime + "|" + BefTransDebt + "|" + AffTransDebt + "|"
            + AccountType + "|" + OrderId;

        logger.info("baokim请求的签名明文：" + encryptStr);
        logger.info("baokim请求发送的签名：" + Signature);

        Map config = JsonUtils.jsonToMap(xConfigService.getContentByType(SysVariable.BAOKIM_NOTICE_SIGNATURE));
        if (config == null) {
            logger.error("还款回调接口配置获取出错");
            return null;
        }

        for (Object key : config.keySet()) {
            if ("enable".equals(key)) {
                String value = config.get(key).toString();
                if ("true".equals(value)) {
                    // 生产环境才校验签名
                    System.out.println(environment);
                    if (environment.contains("prod")) {
                        // 验证签名认证
                        boolean b = RSAEncryptShaCollection.decrypt2Sha1(encryptStr, Signature);
                        logger.info("签名校验结果：" + b);

                        if (!b) {
                            ResponseCode = BaokimResponseStatus.IncorrectSignature.getCode();
                            ResponseMessage = BaokimResponseStatus.IncorrectSignature.getMessage();
                            setRsp(rsp, ResponseCode, ResponseMessage);
                            return rsp;
                        }
                    }
                }
            }

        }

        // 请求字段存入数据库
        XCollectionTransaction xCollectionTransaction = new XCollectionTransaction();
        xCollectionTransaction.setRequestId(RequestId);
        xCollectionTransaction.setRequestTime(RequestTime);
        xCollectionTransaction.setPartnerCode(PartnerCode);
        xCollectionTransaction.setAccNo(AccNo);
        xCollectionTransaction.setClientidNo(ClientIdNo);
        xCollectionTransaction.setTransId(TransId);
        xCollectionTransaction.setTransAmount(TransAmount);
        xCollectionTransaction.setBeftransDebt(BefTransDebt);
        xCollectionTransaction.setAfftransDebt(AffTransDebt);
        xCollectionTransaction.setAccountType(AccountType);
        xCollectionTransaction.setTransTime(TransTime);
        xCollectionTransaction.setOrderId(OrderId);
        xCollectionTransaction.setReferenceId(ReferenceId);

        try {
            xCollectionTransactionDao.insert(xCollectionTransaction);
        } catch (Exception e) {
            if (e.toString().contains("Duplicate")) {
                // 如果TransId重复了，还是返回200和refenceId给baokim
                ResponseCode = BaokimResponseStatus.CollectionSuccess.getCode();
                ResponseMessage = BaokimResponseStatus.CollectionSuccess.getMessage();
                XCollectionTransaction collectionTransaction = xCollectionTransactionDao.findDataByTransId(TransId);
                // 返回数据库中存在的ReferenceId，把已存在的ReferenceId返回给baokim
                if (collectionTransaction != null) {
                    String referenceId = collectionTransaction.getReferenceId();
                    rsp.setReferenceId(referenceId);
                }
                setRsp(rsp, ResponseCode, ResponseMessage);
                return rsp;
            }

            // 出现其他异常时，清空referenceId，返回失败
            rsp.setReferenceId(null);
            ResponseCode = BaokimResponseStatus.Fail.getCode();
            ResponseMessage = BaokimResponseStatus.Fail.getMessage();
            setRsp(rsp, ResponseCode, ResponseMessage);
            return rsp;
        }

        setRsp(rsp, ResponseCode, ResponseMessage);

        // 更新还款记录
        if (!StringUtils.isEmpty(OrderId)) {
            logger.info("更新还款状态------------------------");
            // 根据orderId查询借款记录
            XRecordLoan xRecordLoan = xRecordLoanDao.getXRecordLoanByOrderId(OrderId);
            // 新增一条还款记录
            XRecordRepayment xRecordRepayment = new XRecordRepayment();
            // 用户gid
            xRecordRepayment.setUserGid(xRecordLoan.getUserGid());
            // 订单id
            xRecordRepayment.setOrderId(xRecordLoan.getOrderId());
            // 借款gid
            xRecordRepayment.setRecordLoanGid(xRecordLoan.getGid());

            // 还款gid
            xRecordRepayment.setGid(UUIDUtils.getUUID());
            // 还款类型
            xRecordRepayment.setRepaymentType(SysVariable.REPAYMENT_MODE_NORMAL);
            // 还款方式 0：银行转账，1：现金账户还款
            xRecordRepayment.setRepaymentWay(0);
            // 还款金额
            xRecordRepayment.setRepaymentAmount(Integer.parseInt(TransAmount));
            // 逾期滞纳金 无用
            xRecordRepayment.setRepaymentOverdueFee(0L);
            // 还款状态
            xRecordRepayment.setRepaymentStatus(SysVariable.REPAYMENT_STATUS_SUCCESS);
            Date now = new Date();
            // 创建时间
            xRecordRepayment.setCreateTime(now);
            // 更新时间
            xRecordRepayment.setUpdateTime(now);

            xRecordRepaymentService.confirmRepayment(xRecordLoan, xRecordRepayment, ResponseCode, TransTime);
            return rsp;
        }

        return null;

    }

    private void setRsp(CollectionTransactionRsp rsp, String ResponseCode, String ResponseMessage) {
        rsp.setResponseCode(ResponseCode);
        rsp.setResponseMessage(ResponseMessage);
    }

    @Override
    public EbayBlanceFlucNoticeRsp balanceFuctNotice(XBalanceFuctNoticeReq notice) {
        EbayBlanceFlucNoticeRsp rsp = new EbayBlanceFlucNoticeRsp();
        String responseCode = EbayResponseStatus.NOTICE_SUCCESS.getCode();
        String responseMessage = EbayResponseStatus.NOTICE_SUCCESS.getMessage();
        String requestId = notice.getRequestId();
        String merchantCode = notice.getMerchantCode();
        String amount = notice.getAmount();
        String fee = notice.getFee();
        String mapId = notice.getMapId();
        String vaAcc = notice.getVaAcc();
        String vaName = notice.getVaName();
        String requestTime = notice.getRequestTime();
        String referenceId = notice.getReferenceId();
        String bankTranTime = notice.getBankTranTime();
        String bankCode = notice.getBankCode();
        String bankName = notice.getBankName();
        String signature = notice.getSignature();

        try {
            // 检查各个字段
            // referenceId是否重复判断
            boolean existsReferenceId = checkReferenceId(referenceId);
            if (existsReferenceId) {
                responseCode = EbayResponseStatus.NOTICE_DUPLICATE_REFERENCEID.getCode();
                responseMessage = EbayResponseStatus.NOTICE_DUPLICATE_REFERENCEID.getMessage();
                setRsp(rsp, responseCode, responseMessage);
                return rsp;
            }

            // PartnerCode wrong
            if (!ebayMerchantCode.equals(merchantCode)) {
                responseCode = EbayResponseStatus.NOTICE_PARTNERCODE_WRONG.getCode();
                responseMessage = EbayResponseStatus.NOTICE_PARTNERCODE_WRONG.getMessage();
                setRsp(rsp, responseCode, responseMessage);
                return rsp;
            }

            // Amount wrong
            boolean isCorrect = StringUtil.isNumeric(amount);
            if (!isCorrect) {
                responseCode = EbayResponseStatus.NOTICE_AMOUNT_WRONG.getCode();
                responseMessage = EbayResponseStatus.NOTICE_AMOUNT_WRONG.getMessage();
                setRsp(rsp, responseCode, responseMessage);
                return rsp;
            }

            // 有没有传递必要的字段
            if (StringUtils.isEmpty(requestId) || StringUtils.isEmpty(requestTime) || StringUtils.isEmpty(referenceId)
                || StringUtils.isEmpty(mapId) || StringUtils.isEmpty(amount) || StringUtils.isEmpty(signature)
                || StringUtils.isEmpty(merchantCode) || StringUtils.isEmpty(fee) || StringUtils.isEmpty(fee)
                || StringUtils.isEmpty(vaName) || StringUtils.isEmpty(vaAcc)) {
                responseCode = EbayResponseStatus.NOTICE_MISSING_FIELD.getCode();
                responseMessage = EbayResponseStatus.NOTICE_MISSING_FIELD.getMessage();
                setRsp(rsp, responseCode, responseMessage);
                return rsp;
            }

            // 检测签名
            // RequestId|ReferenceId|RequestTime|Amount|Fee
            String message = requestId + "|" + referenceId + "|" + requestTime + "|" + amount + "|" + fee;
            boolean flag = RSASign.verifySHA256(message, signature, ebayNoticePublicKeyPath);
            if (!flag) {
                responseCode = EbayResponseStatus.NOTICE_SIGNATURE_WRONG.getCode();
                responseMessage = EbayResponseStatus.NOTICE_SIGNATURE_WRONG.getMessage();
                setRsp(rsp, responseCode, responseMessage);
                return rsp;
            }

            // 请求字段存入数据库
            XEbayCollectionNotice xEbayCollectionNotice = new XEbayCollectionNotice();
            xEbayCollectionNotice.setRequestId(requestId);
            xEbayCollectionNotice.setRequestTime(requestTime);
            xEbayCollectionNotice.setBankTranTime(bankTranTime);
            xEbayCollectionNotice.setReferenceId(referenceId);
            xEbayCollectionNotice.setMapId(mapId);
            BigDecimal bigAmount = new BigDecimal(amount);
            xEbayCollectionNotice.setAmount(bigAmount);
            xEbayCollectionNotice.setMerchantCode(merchantCode);
            BigDecimal bigFee = new BigDecimal(fee);
            xEbayCollectionNotice.setFee(bigFee);
            xEbayCollectionNotice.setVaName(vaName);
            xEbayCollectionNotice.setVaAcc(vaAcc);
            xEbayCollectionNotice.setBankCode(bankCode);
            xEbayCollectionNotice.setBankName(bankName);
            xCollectionTransactionDao.insertEbayNotice(xEbayCollectionNotice);
            setRsp(rsp, responseCode, responseMessage);
            return rsp;
        } catch (Exception e) {
            e.printStackTrace();
            responseCode = EbayResponseStatus.NOTICE_FAIL.getCode();
            responseMessage = EbayResponseStatus.NOTICE_FAIL.getMessage();
            setRsp(rsp, responseCode, responseMessage);
            return rsp;
        }

    }

    public boolean checkReferenceId(String referenceId) {
        String refId = xCollectionTransactionDao.findRefenceId(referenceId);
        if (!StringUtils.isEmpty(refId)) {
            return true;
        }
        return false;
    }

    private void setRsp(EbayBlanceFlucNoticeRsp rsp, String ResponseCode, String ResponseMessage) {
        rsp.setResponseCode(ResponseCode);
        rsp.setResponseMessage(ResponseMessage);
    }

}
