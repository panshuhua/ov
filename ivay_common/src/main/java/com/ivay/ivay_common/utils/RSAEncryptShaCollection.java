package com.ivay.ivay_common.utils;

import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Decoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAEncryptShaCollection extends RSAEncryptSha1{
	private static final RSAEncryptShaCollection RSA_ENCRYPT;
	private static final Base64 BASE_64 = new Base64();
	static {
		RSA_ENCRYPT = new RSAEncryptShaCollection();
		//加载公钥
		try {
			RSA_ENCRYPT.loadPublicKey(RSAEncryptCollection.loadPublicKeyByFile(SpringUtil.getResourceFile()));
			System.out.println("加载公钥成功");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			System.err.println("加载公钥失败");
		}
 
		//加载私钥
		try {
			RSA_ENCRYPT.loadPrivateKey(RSAEncryptCollection.loadPrivateKeyByFile(SpringUtil.getResourceFile()));
			System.out.println("加载私钥成功");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			System.err.println("加载私钥失败");
		}
	}
	
	/**
	 * 私钥
	 */
	private RSAPrivateKey privateKey;
 
	/**
	 * 公钥
	 */
	private RSAPublicKey publicKey;
	
	/**
	 * 字节数据转字符串专用集合
	 */
	private static final char[] HEX_CHAR= {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	
 
	/**
	 * 获取私钥
	 * @return 当前的私钥对象
	 */
	@Override
    public RSAPrivateKey getPrivateKey() {
		return privateKey;
	}
 
	/**
	 * 获取公钥
	 * @return 当前的公钥对象
	 */
	@Override
    public RSAPublicKey getPublicKey() {
		return publicKey;
	}
 
	/**
	 * 随机生成密钥对
	 */
	@Override
    public void genKeyPair(){
		KeyPairGenerator keyPairGen= null;
		try {
			keyPairGen= KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keyPairGen.initialize(1024, new SecureRandom());
		KeyPair keyPair= keyPairGen.generateKeyPair();
		this.privateKey= (RSAPrivateKey) keyPair.getPrivate();
		this.publicKey= (RSAPublicKey) keyPair.getPublic();
	}
 
	/**
	 * 从文件中输入流中加载公钥
	 * @param in 公钥输入流
	 * @throws Exception 加载公钥时产生的异常
	 */
	@Override
    public void loadPublicKey(InputStream in) throws Exception{
		try {
			BufferedReader br= new BufferedReader(new InputStreamReader(in));
			String readLine= null;
			StringBuilder sb= new StringBuilder();
			while((readLine= br.readLine())!=null){
				if(readLine.charAt(0)=='-'){
					continue;
				}else{
					sb.append(readLine);
					sb.append('\r');
				}
			}
			loadPublicKey(sb.toString());
		} catch (IOException e) {
			throw new Exception("公钥数据流读取错误");
		} catch (NullPointerException e) {
			throw new Exception("公钥输入流为空");
		}
	}
 
 
	/**
	 * 从字符串中加载公钥
	 * @param publicKeyStr 公钥数据字符串
	 * @throws Exception 加载公钥时产生的异常
	 */
	@Override
    public void loadPublicKey(String publicKeyStr) throws Exception{
		try {
			BASE64Decoder base64Decoder= new BASE64Decoder();
			byte[] buffer= base64Decoder.decodeBuffer(publicKeyStr);
			KeyFactory keyFactory= KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec= new X509EncodedKeySpec(buffer);
			this.publicKey= (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (IOException e) {
			throw new Exception("公钥数据内容读取错误");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		}
	}
 
	/**
	 * 从文件中加载私钥
	 * @param keyFileName 私钥文件名
	 * @return 是否成功
	 * @throws Exception 
	 */
	@Override
    public void loadPrivateKey(InputStream in) throws Exception{
		try {
			BufferedReader br= new BufferedReader(new InputStreamReader(in));
			String readLine= null;
			StringBuilder sb= new StringBuilder();
			while((readLine= br.readLine())!=null){
				if(readLine.charAt(0)=='-'){
					continue;
				}else{
					sb.append(readLine);
					sb.append('\r');
				}
			}
			loadPrivateKey(sb.toString());
		} catch (IOException e) {
			throw new Exception("私钥数据读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥输入流为空");
		}
	}
 
	@Override
    public void loadPrivateKey(String privateKeyStr) throws Exception{
		try {
			BASE64Decoder base64Decoder= new BASE64Decoder();
			byte[] buffer= base64Decoder.decodeBuffer(privateKeyStr);
			PKCS8EncodedKeySpec keySpec= new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory= KeyFactory.getInstance("RSA");
			this.privateKey= (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			throw new Exception("私钥非法");
		} catch (IOException e) {
			throw new Exception("私钥数据内容读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥数据为空");
		}
	}
 
	/**
	 * 加密过程
	 * @param publicKey 公钥
	 * @param plainTextData 明文数据
	 * @return
	 * @throws Exception 加密过程中的异常信息
	 */
	@Override
    public byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception{
		if(publicKey== null){
			throw new Exception("加密公钥为空, 请设置");
		}
		Cipher cipher= null;
		try {
			cipher= Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] output= cipher.doFinal(plainTextData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此加密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		}catch (InvalidKeyException e) {
			throw new Exception("加密公钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("明文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("明文数据已损坏");
		}
	}
	
	/**
	 * 私钥加密过程
	 *
	 * @param privateKey    私钥
	 * @param plainTextData 明文数据
	 * @return
	 * @throws Exception 加密过程中的异常信息
	 */
	@Override
    public byte[] encrypt(RSAPrivateKey privateKey, byte[] plainTextData) throws Exception {
		if (privateKey == null) {
			throw new Exception("加密私钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			// 使用默认RSA
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			byte[] output = cipher.doFinal(plainTextData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此加密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("加密私钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("明文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("明文数据已损坏");
		}
	}
	
    public static String signWithSHA1(PrivateKey privatekey, String param) {
        // 使用私钥加签
        byte[] signature = null;
        try {
             //用私钥给入参加签
             Signature sign = Signature.getInstance("SHA1WithRSA");
             sign.initSign(privatekey);
             sign.update(param.getBytes());
             signature = sign.sign();

        } catch (NoSuchAlgorithmException e) {
             e.printStackTrace();
        } catch (SignatureException e) {
             e.printStackTrace();
        } catch (InvalidKeyException e) {
             e.printStackTrace();
        }
        //将加签后的base64编码
        String terminal =  Base64Util.encode(signature);
        return terminal;
  }
    
    public static boolean verifyWithSHA1(String param,String signature, RSAPublicKey publicKey){
           try {
                //用获取到的公钥对   入参中未加签参数param 与  入参中的加签之后的参数signature 进行验签
                Signature sign=Signature.getInstance("SHA1WithRSA");
                sign.initVerify(publicKey);
                sign.update(param.getBytes());
 
                //base64解码成字符数组
                byte[] base64Byte=Base64Util.decode2(signature);
                //System.out.println("解码后：" + StringUtil.utfToString(base64Byte));
               //验证签名
                return sign.verify(base64Byte);
 
           } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
           } catch (SignatureException e) {
                e.printStackTrace();
           } catch (InvalidKeyException e) {
                e.printStackTrace();
           }
           return false;
     }

 
	/**
	 * 解密过程
	 * @param privateKey 私钥
	 * @param cipherData 密文数据
	 * @return 明文
	 * @throws Exception 解密过程中的异常信息
	 */
	@Override
    public byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception{
		if (privateKey== null){
			throw new Exception("解密私钥为空, 请设置");
		}
		Cipher cipher= null;
		try {
			cipher= Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] output= cipher.doFinal(cipherData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此解密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		}catch (InvalidKeyException e) {
			throw new Exception("解密私钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("密文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("密文数据已损坏");
		}		
	}
	
	@Override
    public byte[] decrypt2(RSAPublicKey publicKey, byte[] cipherData) throws Exception{
		if (publicKey== null){
			throw new Exception("解密公钥为空, 请设置");
		}
		Cipher cipher= null;
		try {
			cipher= Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] output= cipher.doFinal(cipherData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此解密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		}catch (InvalidKeyException e) {
			throw new Exception("解密公钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("密文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("密文数据已损坏");
		}		
	}
 
	
	/**
	 * 字节数据转十六进制字符串
	 * @param data 输入数据
	 * @return 十六进制内容
	 */
	public static String byteArrayToString(byte[] data){
		StringBuilder stringBuilder= new StringBuilder();
		for (int i=0; i<data.length; i++){
			//取出字节的高四位 作为索引得到相应的十六进制标识符 注意无符号右移
			stringBuilder.append(HEX_CHAR[(data[i] & 0xf0)>>> 4]);
			//取出字节的低四位 作为索引得到相应的十六进制标识符
			stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);
			if (i<data.length-1){
				stringBuilder.append(' ');
			}
		}
		return stringBuilder.toString();
	}
 
 
	public static void main(String[] args){
		
		//测试字符串
		String encryptStr= "Test String xjd 哈哈 xxxx |二 |";
 
		try {
			System.out.println("加密前：" + encryptStr);
			//String string = encrypt(encryptStr);
			//decrypt(string);
			String para = encrypt2Sha1(encryptStr);
			
			boolean b = verifyWithSHA1(encryptStr,  para, RSA_ENCRYPT.getPublicKey());
			System.out.println("解密验证：" + b);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	//私钥加密
	public static String encrypt(String encryptStr) {
		//加密
		byte[] cipher;
		try {
			cipher = RSA_ENCRYPT.encrypt(RSA_ENCRYPT.getPrivateKey(), encryptStr.getBytes());
			String str = new String(BASE_64.encode(cipher));
			System.out.println("加密后：" + str);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptStr;
	}
	
	//
	public static String encrypt2Sha1(String encryptStr) {
		//加密
		try {
			String str = signWithSHA1(RSA_ENCRYPT.getPrivateKey(), encryptStr);
			System.out.println("加密后：" + str);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptStr;
	}
	
	public static String encrypt2(String encryptStr) {
		//加密
		byte[] cipher;
		try {
			cipher = RSA_ENCRYPT.encrypt(RSA_ENCRYPT.getPublicKey(), encryptStr.getBytes());
			String str = new String(BASE_64.encode(cipher));
			System.out.println("加密后：" + str);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptStr;
	}
	
	public static String decrypt(String encryptStr) {
		byte[] plainText;
		try {
			plainText = RSA_ENCRYPT.decrypt(RSA_ENCRYPT.getPrivateKey(), BASE_64.decode(encryptStr.getBytes()));
			String str = new String(plainText);
			System.out.println("解密后：" + str);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptStr;
	}
	
	public static boolean decrypt2Sha1(String encryptStr, String signature) {
		boolean ret = verifyWithSHA1(encryptStr, signature, RSA_ENCRYPT.getPublicKey());
		System.out.println("解密后：" + ret);
		return ret;
		
	}
	
}
