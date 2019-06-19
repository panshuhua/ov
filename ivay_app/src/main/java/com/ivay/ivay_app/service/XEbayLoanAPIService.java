package com.ivay.ivay_app.service;

import com.ivay.ivay_app.dto.EbayTransfersRsp;

public interface XEbayLoanAPIService {
	 /**
     * 接口：校验用户信息
     *
     * @param bankNo
     * @param accNo
     * @param accType
     * @return
     */
	EbayTransfersRsp validateCustomerInformation(String bankNo, String accNo, String accType);

    /**
     * 接口：交易
     *
     * @param bankNo        银行代码
     * @param accNo         借款方卡号
     * @param requestAmount
     * @param memo
     * @param accType
     * @return
     */
	EbayTransfersRsp transfers(String bankNo, String accNo, long requestAmount, String memo, String accType,String accountName,String contractNumber,String extend);

    /**
     * 接口：查询交易状态
     *
     * @param referenceId
     * @return
     */
	EbayTransfersRsp transfersInfo(String referenceId);

    /**
     * 接口：查询账户余额?
     *
     * @return
     */
	EbayTransfersRsp balance();
    
}
