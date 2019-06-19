package com.ivay.ivay_common.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


public class TripleDESEncryption {

	static final byte[] SALT = {
			(byte) 0x09, /*
							 * snip - randomly chosen but static salt
							 */ };
	static final int ITERATIONS = 11;

	/**
	 * Gen key base64String
	 * 
	 * @return String base64
	 */

	public static String encrypt(String key, String input)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException, NoSuchProviderException, Exception {

		byte[] bytes = null;
		SecretKey sKey = null;
		Security.addProvider(new BouncyCastleProvider());
		Cipher encipher = Cipher.getInstance("DESede/ECB/PKCS5PADDING", "BC");
		bytes = input.getBytes("UTF-8");
		sKey = getKey(key);
		// Encrypt
		byte[] enc;
		encipher.init(Cipher.ENCRYPT_MODE, sKey);
		enc = encipher.doFinal(bytes);
		return bytesToHex(enc);
	}
	private static SecretKey getKey(String key) {
		byte[] bKey = key.getBytes();
		try {
			DESedeKeySpec keyspec = new DESedeKeySpec(bKey);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");

			SecretKey lclSK = keyFactory.generateSecret(keyspec);

			return lclSK;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String bytesToHex(final byte[] bytes) {
		final StringBuilder buf = new StringBuilder(bytes.length * 2);
		for (final byte b : bytes) {
			final String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) {
				buf.append("0");
			}
			buf.append(hex);
		}
		return buf.toString();
	}

	public static void main(String[] args) {
		
	}

}
