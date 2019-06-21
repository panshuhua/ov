package com.ivay.ivay_common.utils;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public class AESEncryption {

    public static final String VIPARA = "ivay001234567890";
	public static final String UTF_8 = "utf-8";
 
	/**
	 * 字节数组转化为大写16进制字符串
	 * 
	 * @param b
	 * @return
	 */
	private static String byte2HexStr(byte[] b) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			String s = Integer.toHexString(b[i] & 0xFF);
			if (s.length() == 1) {
				sb.append("0");
			}
 
			sb.append(s.toUpperCase());
		}
 
		return sb.toString();
	}
 
	/**
	 * 16进制字符串转字节数组
	 * 
	 * @param s
	 * @return
	 */
	private static byte[] str2ByteArray(String s) {
		int byteArrayLength = s.length() / 2;
		byte[] b = new byte[byteArrayLength];
		for (int i = 0; i < byteArrayLength; i++) {
			byte b0 = (byte) Integer.valueOf(s.substring(i * 2, i * 2 + 2), 16)
					.intValue();
			b[i] = b0;
		}
 
		return b;
	}
 
 
	/**
	 * AES 加密
	 * 
	 * @param content
	 *            明文
	 * @param password
	 *            生成秘钥的关键字
	 * @return
	 */
 
	public static String aesEncrypt(String content, String password) {
		try {
			IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
			SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
			byte[] encryptedData = cipher.doFinal(content.getBytes(UTF_8));
			
			return Base64.encode(encryptedData);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
 
		return null;
	}
 
	/**
	 * AES 解密
	 * 
	 * @param content
	 *            密文
	 * @param password
	 *            生成秘钥的关键字
	 * @return
	 */
 
	public static String aesDecrypt(String content, String password) {
		try {
			byte[] byteMi = Base64.decode(content);
//			byte[] byteMi=	str2ByteArray(content);
			IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
			SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
			byte[] decryptedData = cipher.doFinal(byteMi);
			return new String(decryptedData, "utf-8");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String encrypt(String content) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }
        return aesEncrypt(content, VIPARA);

    }

    public static String decrypt(String content) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }
        return aesDecrypt(content, VIPARA);
    }
 
    public static void main(String[] args) throws Exception {
        String content = "小行星的撒旦撒erwrwe";
        System.out.println("加密之前：" + content);

        content = aesEncrypt(content, VIPARA);
        // 加密
        System.out.println("加密后的内容：" + content);

        // 解密
        System.out.println("解密后的：" + aesDecrypt(content, VIPARA));
    }
    
}
