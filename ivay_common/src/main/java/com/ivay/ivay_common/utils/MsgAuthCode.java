package com.ivay.ivay_common.utils;

import java.security.SecureRandom;
import java.util.Random;

/**
 * 作用：用于发送短信验证码 使用场景：注册/找回密码
 * 
 * @author psh
 *
 */
public class MsgAuthCode {
    private static final String SYMBOLS = "0123456789"; // 数字

    private static final Random RANDOM = new SecureRandom();

    // 获取4位随机数
    public static String getAuthCode() {
        char[] chars = new char[4];
        for (int index = 0; index < chars.length; index++) {
            chars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }
        return new String(chars);
    }

    // 获取9位随机数
    public static String getAuthNineCode() {
        char[] chars = new char[9];
        for (int index = 0; index < chars.length; index++) {
            chars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }
        return new String(chars);
    }

    /**
     * 
     * 生成随机数字和大写字母串,
     * 
     * @param length
     * 
     * @return
     * 
     */

    public static String getNumBigCharRandom(int length) {
        String val = "";
        Random random = new Random();
        // 参数length，表示生成几位随机数
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 输出大写字母
                val += (char)(random.nextInt(26) + 65);
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                // 输出数字
                val += String.valueOf(random.nextInt(10));
            }

        }
        return val;

    }

    public static void main(String[] args) {
        // System.out.println(getAuthCode());
        System.out.println(getNumBigCharRandom(6));
    }

}
