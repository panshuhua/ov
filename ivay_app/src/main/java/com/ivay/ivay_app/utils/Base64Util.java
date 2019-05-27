package com.ivay.ivay_app.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64Util {

    private final static Base64.Decoder DECODER = Base64.getDecoder();
    private final static Base64.Encoder ENCODER = Base64.getEncoder();

    public static void main(String[] args) throws UnsupportedEncodingException {
        final String text = "字串文字qwe112313";
        // 编码
        final String encodedText = encode(text);
        System.out.println(encodedText);
        // 解码
        System.out.println(decode(encodedText));

    }

    public static String encode(String str) {
        byte[] textByte;
        try {
            textByte = str.getBytes("UTF-8");
            return ENCODER.encodeToString(textByte);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return str;
        }
    }
    
    public static String encode(byte[] textByte) {
            return ENCODER.encodeToString(textByte);

    }

    public static String decode(String str) {

        try {
            return new String(DECODER.decode(str), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return str;
        }

    }
    
    public static byte[] decode2(String str) {
    	return DECODER.decode(str);
    }
    
    

}
