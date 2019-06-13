package com.ivay.ivay_common.utils;

import java.util.Calendar;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @作者: xx
 * @时间: 2019年4月7日下午7:05:14
 * @ 生成随机字符串的工具类 uuid
 */
@Component
public class UUIDUtils {
	
	private static String apiRequestIdPrefix;
	
	private static String ebayApiRequestIdPrefix;
	
	@Value("${api_request_id_prefix}")
	public  void setApiRequestIdPrefix(String apiRequestIdPrefix) {
		UUIDUtils.apiRequestIdPrefix = apiRequestIdPrefix;
	}
	
	@Value("${ebay_api_request_id_prefix}")
	public  void setEbayApiRequestIdPrefix(String ebayApiRequestIdPrefix) {
		UUIDUtils.ebayApiRequestIdPrefix = ebayApiRequestIdPrefix;
	}



	public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getRequestId() {
        // 越南为东七区（差一个小时）
        long now = System.currentTimeMillis() - 60 * 60 * 1000L;
        String uniqueId = Long.toString(System.nanoTime());
        uniqueId = uniqueId.substring(uniqueId.length() - 6);
        return  apiRequestIdPrefix+ DateUtils.timeMillisToString(now, "yyyyMMdd") + uniqueId;
    }
    
    public static String getEbayRequestId() {
        // 越南为东七区（差一个小时）
        long now = System.currentTimeMillis() - 60 * 60 * 1000L;
        String uniqueId = Long.toString(System.nanoTime());
        uniqueId = uniqueId.substring(uniqueId.length() - 6);
        return  ebayApiRequestIdPrefix+ DateUtils.timeMillisToString(now, "yyyyMMdd") + uniqueId;
    }

    public static String getRequestTime() {
        Calendar cal = Calendar.getInstance();
        int offset = cal.get(Calendar.ZONE_OFFSET);
        cal.add(Calendar.MILLISECOND, -offset);
        // 世界统一时间
        Long timeStampUTC = cal.getTimeInMillis();
        // 当前时区
        Long timeStamp = System.currentTimeMillis();
        // 越南时区
        long vietnamTimeZone = 7;
        long currentTimeZone = (timeStamp - timeStampUTC) / (1000 * 3600);
        // 相差秒数
        long differSecond = 60 * 60 * 1000L * (currentTimeZone - vietnamTimeZone);
        return DateUtils.timeMillisToString_YYYY_MM_DD_HH_MM_SS(System.currentTimeMillis() - differSecond);
    }

    public static void main(String[] args) {
        System.out.println("格式前的UUID ： " + UUID.randomUUID().toString());
        System.out.println("格式化后的UUID ：" + getUUID());
    }
    
}
