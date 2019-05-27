package com.ivay.ivay_manage.model;

import lombok.Data;

/**
 * create by xx on 2018/1/10
 */
@Data
public class Address {
	
	private int year;

	private int nameDis;//姓名编辑距离
	
	private int contactNum;
	
	private int contactMaxNum;
	
	private int macNum; //同一设备注册数
	
	private int gpsNum;
	
}
