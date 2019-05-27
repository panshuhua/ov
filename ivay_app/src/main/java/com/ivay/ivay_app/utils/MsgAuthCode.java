package com.ivay.ivay_app.utils;

import java.security.SecureRandom;
import java.util.Random;

/**
 * 作用：用于发送短信验证码 
 * 使用场景：注册/找回密码
 * @author psh
 *
 */
public class MsgAuthCode {
	 private static final String SYMBOLS = "0123456789"; // 数字

	 private static final Random RANDOM = new SecureRandom();

	 //获取4位随机数
	 public static String getAuthCode(){
		 char[] chars=new char[4];
		 for(int index=0;index<chars.length;index++){
			 chars[index]=SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
		 }
		return new String(chars);
	 }
	 
	 //获取9位随机数
	 public static String getAuthNineCode(){
		 char[] chars=new char[9];
		 for(int index=0;index<chars.length;index++){
			 chars[index]=SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
		 }
		return new String(chars);
	 }
	 
	 public static void main(String[] args) {
		System.out.println(getAuthCode());
	 }
	 
}
