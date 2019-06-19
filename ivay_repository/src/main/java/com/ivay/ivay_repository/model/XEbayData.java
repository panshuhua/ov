package com.ivay.ivay_repository.model;

import lombok.Data;
import com.ivay.ivay_repository.model.XEbayExtend;

/**
 * ebay请求参数的data字段
 * @author panshuhua
 */
@Data
public class XEbayData {
   private String map_id;
   private Number amount;
   private String start_date;
   private String end_date;
   private String condition;
   private String customer_name;
   private String request_id;
   private String bank_code;
   private XEbayExtend extend;
   private String account_no;
}
