package com.ivay.ivay_app.dto;

import lombok.Data;

/**
 * ebay还款回调接口实体
 * @author panshuhua
 *
 */
@Data
public class XBalanceFuctNoticeReq {
   private String RequestId;
   private String RequestTime;
   private String BankTranTime;
   private String ReferenceId;
   private String MapId;
   private String Amount;
   private String Signature;
   private String MerchantCode;
   private String Fee;
   private String VaName;
   private String VaAcc;
   private String BankCode;
   private String BankName;
}
