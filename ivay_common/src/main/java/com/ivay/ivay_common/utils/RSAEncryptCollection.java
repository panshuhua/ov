package com.ivay.ivay_common.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * <p>
 * rsa加密解密-使用collection(还款)接口密钥
 * </p>
 *
 * @author xx
 * @version 1.0.0
 */
public class RSAEncryptCollection extends RSAEncrypt {

    private static final String PRIVATE_KEY = "pkcs8_private0527.pem";  //dev
//	private static final String PRIVATE_KEY = "pkcs8_partner_privatekey.pem";  //prov

	private static final String PUBLIC_KEY = "bk_public_key.pem"; //dev
//	private static final String PUBLIC_KEY = "partner_publickey.pem";  //prov
	
	/**
	 * 随机生成密钥对
	 */
	public static void genKeyPair(String filePath) {
		// KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
		KeyPairGenerator keyPairGen = null;
		try {
			keyPairGen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		// 初始化密钥对生成器，密钥大小为96-1024位
		keyPairGen.initialize(1024, new SecureRandom());
		// 生成一个密钥对，保存在keyPair中
		KeyPair keyPair = keyPairGen.generateKeyPair();
		// 得到私钥
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		// 得到公钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		try {
			// 得到公钥字符串
			Base64 base64 = new Base64();
			String publicKeyString = new String(base64.encode(publicKey.getEncoded()));
			// 得到私钥字符串
			String privateKeyString = new String(base64.encode(privateKey.getEncoded()));
			// 将密钥对写入到文件
			FileWriter pubfw = new FileWriter(filePath + PUBLIC_KEY);
			FileWriter prifw = new FileWriter(filePath + PRIVATE_KEY);
			BufferedWriter pubbw = new BufferedWriter(pubfw);
			BufferedWriter pribw = new BufferedWriter(prifw);
			pubbw.write(publicKeyString);
			pribw.write(privateKeyString);
			pubbw.flush();
			pubbw.close();
			pubfw.close();
			pribw.flush();
			pribw.close();
			prifw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * 从文件中输入流中加载公钥
     *
     * @param path 公钥输入流
     * @throws Exception 加载公钥时产生的异常
     */
    public static String loadPublicKeyByFile(String path) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path + PUBLIC_KEY));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            throw new Exception("公钥数据流读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥输入流为空");
        }
    }

    /**
     * 从文件中加载私钥
     *
     * @param path 私钥文件名
     * @return 是否成功
     * @throws Exception
     */
    public static String loadPrivateKeyByFile(String path) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path + PRIVATE_KEY));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            throw new Exception("私钥数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥输入流为空");
        }
    }


}
