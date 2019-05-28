package com.ivay.ivay_common.utils;

public class StringFormat {      

  public static void main(String[] args) {      

	

    System.out.println( autoGenericCode("3", 4));       

  }  
 
  
  
  /**
   * 不够位数的在前面补0，保留num的长度位数字
   * @param code
   * @return
   */
  public static String autoGenericCode(String code, int num) {
      String result = "";
      // 保留num的位数
      // 0 代表前面补充0     
      // num 代表长度为4     
      // d 代表参数为正数型 
      result = String.format("%0" + num + "d", Integer.parseInt(code));

      return result;
  }

} 