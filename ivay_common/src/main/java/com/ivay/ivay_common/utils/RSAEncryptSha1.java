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

public class RSAEncryptSha1 {

//	@Value("${key.path}")
//	private String filesPath;

    public static final String DEFAULT_PUBLIC_KEY =
//		"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChDzcjw/rWgFwnxunbKp7/4e8w" + "\r" +
//		"/UmXx2jk6qEEn69t6N2R1i/LmcyDT1xr/T2AHGOiXNQ5V8W4iCaaeNawi7aJaRht" + "\r" +
//		"Vx1uOH/2U378fscEESEG8XDqll0GCfB1/TjKI2aitVSzXOtRs8kYgGU78f7VmDNg" + "\r" +
//		"XIlk3gdhnzh+uoEQywIDAQAB" + "\r";

            "MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgHYh6nL5kXBCJxVviD9SqhLLa1CE" + "\r" +
                    "0Ij4hG2tgB3DPDYU1BY0A6hkV9v1X7ZV5oqg2SevPksUaQcpX1b5dMRlcIZrHOaJ" + "\r" +
                    "x+NX1Q/ELA/Ke7WzPeux2Y/1SEuv5TVnAuOa70bbcT4xU5O2PfLglVsfvBrzpDDl" + "\r" +
                    "Kqd07bzMkLLD184jAgMBAAE=" + "\r";

    public static final String DEFAULT_PRIVATE_KEY =

//		"MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKEPNyPD+taAXCfG" + "\r" +
//		"6dsqnv/h7zD9SZfHaOTqoQSfr23o3ZHWL8uZzINPXGv9PYAcY6Jc1DlXxbiIJpp4" + "\r" +
//		"1rCLtolpGG1XHW44f/ZTfvx+xwQRIQbxcOqWXQYJ8HX9OMojZqK1VLNc61GzyRiA" + "\r" +
//		"ZTvx/tWYM2BciWTeB2GfOH66gRDLAgMBAAECgYBp4qTvoJKynuT3SbDJY/XwaEtm" + "\r" +
//		"u768SF9P0GlXrtwYuDWjAVue0VhBI9WxMWZTaVafkcP8hxX4QZqPh84td0zjcq3j" + "\r" +
//		"DLOegAFJkIorGzq5FyK7ydBoU1TLjFV459c8dTZMTu+LgsOTD11/V/Jr4NJxIudo" + "\r" +
//		"MBQ3c4cHmOoYv4uzkQJBANR+7Fc3e6oZgqTOesqPSPqljbsdF9E4x4eDFuOecCkJ" + "\r" +
//		"DvVLOOoAzvtHfAiUp+H3fk4hXRpALiNBEHiIdhIuX2UCQQDCCHiPHFd4gC58yyCM" + "\r" +
//		"6Leqkmoa+6YpfRb3oxykLBXcWx7DtbX+ayKy5OQmnkEG+MW8XB8wAdiUl0/tb6cQ" + "\r" +
//		"FaRvAkBhvP94Hk0DMDinFVHlWYJ3xy4pongSA8vCyMj+aSGtvjzjFnZXK4gIjBjA" + "\r" +
//		"2Z9ekDfIOBBawqp2DLdGuX2VXz8BAkByMuIh+KBSv76cnEDwLhfLQJlKgEnvqTvX" + "\r" +
//		"TB0TUw8avlaBAXW34/5sI+NUB1hmbgyTK/T/IFcEPXpBWLGO+e3pAkAGWLpnH0Zh" + "\r" +
//		"Fae7oAqkMAd3xCNY6ec180tAe57hZ6kS+SYLKwb4gGzYaCxc22vMtYksXHtUeamo" + "\r" +
//		"1NMLzI2ZfUoX" + "\r";
//			
//		"MIICWgIBAAKBgHYh6nL5kXBCJxVviD9SqhLLa1CE0Ij4hG2tgB3DPDYU1BY0A6hk" + "\r" +
//		"V9v1X7ZV5oqg2SevPksUaQcpX1b5dMRlcIZrHOaJx+NX1Q/ELA/Ke7WzPeux2Y/1" + "\r" +
//		"SEuv5TVnAuOa70bbcT4xU5O2PfLglVsfvBrzpDDlKqd07bzMkLLD184jAgMBAAEC" + "\r" +
//		"gYBSxp0jh2CzyobcbFSKGoB1vvgpaYc+EvWobKSTNlSgiKO8EyJlAjrWM++nMjXi" + "\r" +
//		"+aZwOklqDpkxnXni1dVOMM5RSeZbuBhANf/YiyenFl9K4N3KnfCW5v1sMSd+WvHJ" + "\r" +
//		"y+IhlU3AyDaHrraiIzjtj6oX2Ud3aUvurpN+odVpUHkw4QJBANJX2ZCXHCmYiEeL" + "\r" +
//		"4wROLBRvHTb/J/yUa04WZJDISRWEIWPjaYmXef6yDZz78pPRNf5e2GzWuJdVsqaR" + "\r" +
//		"krHJ/BsCQQCPxjCmFWHmC01IHHdlgs/ZYfsRTohw0uYFObrCOeiAYSMg+NG7+YL4" + "\r" +
//		"kVIPZGC0jDSrEI+N/qQHROdG65uHHoaZAkBUr84GqvBJxwHHXIjQKPESYRIwHFbr" + "\r" +
//		"GI6DZ/yhViImqYYQA3VwUi5p3yIf/EhUPz0v5tvYJjM3quc8rQuUzS/VAkBvRfKs" + "\r" +
//		"9+uXAbREVVPLCt0W35BTKrbKq08/SBjU7cCWa5emkyywEUJeYwphw0xdMA4rP1v7" + "\r" +
//		"h9MnlRpydzMd9nAxAkBaXLzdtQj5pmO7Kc2BhKi/Lr8M+YdjwlFydQS+beuRDM47" + "\r" +
//		"3MIHjbk2GK4tMKXs0tTWiGAWd71O1R6+zB+pd7jy" + "\r";

            "MIICXAIBAAKBgQCHmipZ3lFvAG50oM95XSiTpBxg7tw569l34ceutq7bpIMxs0to" + "\r" +
                    "WaULkvd7ClgDSpHbHVLZmxYN5CJ8aQKB6U6sc82N9Fveuxt/HrNS8g21htd3LVxn" + "\r" +
                    "4z3rVCC1v0APzrXnjZcUiMi76GqzWbrw6XR3manChmkSQkVOxBANYahXPwIDAQAB" + "\r" +
                    "AoGAfvyLXp+IiTfROJ0DznbEMgcEoG3vhLpubArfEal4dK5KQffSzUTt/7nA0tOr" + "\r" +
                    "+mER2C1M0gWfEKEs/m7kbz5Kcmbf6DHWqsVzpreaPJpTHS/MCTsFkaWWBYUyeTYs" + "\r" +
                    "LTvAD5gBUCwH8MlJyaAOhTfHZq53cr0ZRR0tCPxxPFF3YDECQQDkB6kDQ6KJBLAJ" + "\r" +
                    "qjvH9MAZAPlKTCxKezyX8ph0jJ0trNkOH5kPuttouUX4/HvZUw63jlJOOXqMz0LO" + "\r" +
                    "M3HCpTInAkEAmDwxoF+292+VjTzUUpJtMYATmZi8uXF+Wz25v0n7JznwCyk/CwRf" + "\r" +
                    "G1O6x5WtpxNr9CwTlCPrsLJspJwWqjaZKQJBANsnaY5TkIBzWuTPEUlptr3RK8X2" + "\r" +
                    "U5L5whCe/Vr2UZG2T529TJPLmFebyN41eqoxozFSvVOk9l0iTSh0VN8neX0CQCHV" + "\r" +
                    "Nt5Ch2JEXUNPYoybWEMpCLIZHNBF5eQXOsHFili0aqHAX+9t4EkteZRDsp0wFszk" + "\r" +
                    "QtGojgfuD3Eh83OOwMkCQFCsWy/PUUq+SOFQnP+4f6lMrIk7wXggiMBanjP72+SB" + "\r" +
                    "Rc7Ffzwxdsvxku2iwEOa2XlL5pbE50Eo+0Vw17nc540=" + "\r";


    private static final RSAEncryptSha1 RSA_ENCRYPT;
    private static final Base64 BASE_64 = new Base64();

    static {
        RSA_ENCRYPT = new RSAEncryptSha1();
        //加载公钥
        try {
            RSA_ENCRYPT.loadPublicKey(RSAEncrypt.loadPublicKeyByFile(SpringUtil.getResourceFile()));
            System.out.println("加载公钥成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.err.println("加载公钥失败");
        }

        //加载私钥
        try {
            RSA_ENCRYPT.loadPrivateKey(RSAEncrypt.loadPrivateKeyByFile(SpringUtil.getResourceFile()));
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
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    /**
     * 获取私钥
     *
     * @return 当前的私钥对象
     */
    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * 获取公钥
     *
     * @return 当前的公钥对象
     */
    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * 随机生成密钥对
     */
    public void genKeyPair() {
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyPairGen.initialize(1024, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
    }

    /**
     * 从文件中输入流中加载公钥
     *
     * @param in 公钥输入流
     * @throws Exception 加载公钥时产生的异常
     */
    public void loadPublicKey(InputStream in) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
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
            loadPublicKey(sb.toString());
        } catch (IOException e) {
            throw new Exception("公钥数据流读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥输入流为空");
        }
    }


    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @throws Exception 加载公钥时产生的异常
     */
    public void loadPublicKey(String publicKeyStr) throws Exception {
        try {
            BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] buffer = base64Decoder.decodeBuffer(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
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
     *
     * @param keyFileName 私钥文件名
     * @return 是否成功
     * @throws Exception
     */
    public void loadPrivateKey(InputStream in) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
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
            loadPrivateKey(sb.toString());
        } catch (IOException e) {
            throw new Exception("私钥数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥输入流为空");
        }
    }

    public void loadPrivateKey(String privateKeyStr) throws Exception {
        try {
            BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] buffer = base64Decoder.decodeBuffer(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
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
     *
     * @param publicKey     公钥
     * @param plainTextData 明文数据
     * @return
     * @throws Exception 加密过程中的异常信息
     */
    public byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception {
        if (publicKey == null) {
            throw new Exception("加密公钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] output = cipher.doFinal(plainTextData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
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
        String terminal = Base64Util.encode(signature);
        return terminal;
    }

    public static boolean verifyWithSHA1(String param, String signature, RSAPublicKey publicKey) {
        try {
            //用获取到的公钥对   入参中未加签参数param 与  入参中的加签之后的参数signature 进行验签
            Signature sign = Signature.getInstance("SHA1WithRSA");
            sign.initVerify(publicKey);
            sign.update(param.getBytes());

            //base64解码成字符数组
            byte[] base64Byte = Base64Util.decode2(signature);
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
     *
     * @param privateKey 私钥
     * @param cipherData 密文数据
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception {
        if (privateKey == null) {
            throw new Exception("解密私钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] output = cipher.doFinal(cipherData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("解密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("密文数据已损坏");
        }
    }

    public byte[] decrypt2(RSAPublicKey publicKey, byte[] cipherData) throws Exception {
        if (publicKey == null) {
            throw new Exception("解密公钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] output = cipher.doFinal(cipherData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("解密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("密文数据已损坏");
        }
    }


    /**
     * 字节数据转十六进制字符串
     *
     * @param data 输入数据
     * @return 十六进制内容
     */
    public static String byteArrayToString(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            //取出字节的高四位 作为索引得到相应的十六进制标识符 注意无符号右移
            stringBuilder.append(HEX_CHAR[(data[i] & 0xf0) >>> 4]);
            //取出字节的低四位 作为索引得到相应的十六进制标识符
            stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);
            if (i < data.length - 1) {
                stringBuilder.append(' ');
            }
        }
        return stringBuilder.toString();
    }


    public static void main(String[] args) {
        //测试字符串
        String encryptStr = "Test String xjd 哈哈 xxxx |二 |";

        try {
            System.out.println("加密前：" + encryptStr);
            String para = encrypt2Sha1(encryptStr);

            boolean b = verifyWithSHA1(encryptStr, para, RSA_ENCRYPT.getPublicKey());
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
            // TODO Auto-generated catch block
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
            // TODO Auto-generated catch block
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
            // TODO Auto-generated catch block
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
            // TODO Auto-generated catch block
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
