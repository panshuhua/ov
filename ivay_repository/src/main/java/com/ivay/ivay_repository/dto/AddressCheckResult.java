package com.ivay.ivay_repository.dto;

import lombok.Data;

/**
 * create by xx on 2018/1/10
 */
@Data
public class AddressCheckResult {
	
    private boolean yearResult = false; // true:通过校验；false：未通过校验
    
    private boolean nameDisResult = false;

    private boolean contactNumResult = false; 

    private boolean macNumResult = false; 
    
    private boolean gpsNumResult = false; 
        

}
