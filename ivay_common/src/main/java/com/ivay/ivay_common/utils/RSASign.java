package com.ivay.ivay_common.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

// import com.vnptepay.config.ApplicationCfg;

public class RSASign {

    static BASE64Decoder decode = new BASE64Decoder();
    static BASE64Encoder encoder = new BASE64Encoder();
    private final static String SIGNMODE = "SHA256withRSA";// "SHA1withRSA";//
                                                           // SHA256

    public static String[] genKey2() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair keypair = keyGen.genKeyPair();
            PrivateKey privateKey = keypair.getPrivate();
            PublicKey publicKey = keypair.getPublic();
            //
            BASE64Encoder encoder = new BASE64Encoder();
            // private_key = encoder.encode(privateKey.getEncoded());
            // public_key = encoder.encode(publicKey.getEncoded());
            String[] keypairs = new String[2];
            keypairs[0] = encoder.encode(privateKey.getEncoded());
            keypairs[1] = encoder.encode(publicKey.getEncoded());
            return keypairs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void writeKeyBytesToFile(byte[] key, String file) throws IOException {
        OutputStream out = new FileOutputStream(file);
        out.write(key);
        out.close();
    }

    private static String Readfile(String path) {
        String xau = "";
        try {
            FileInputStream fstream = new FileInputStream(path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                xau += strLine + " ";
            }
            xau = xau.trim();
            xau = xau.replace(" ", "\n");
            br.close();
            in.close();
            fstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xau;
    }

    private static String getRSAPublicKeyAsXMLString(RSAPublicKey key)
        throws UnsupportedEncodingException, ParserConfigurationException, TransformerException {
        Document xml = getRSAPublicKeyAsXML(key);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StringWriter sw = new StringWriter();
        transformer.transform(new DOMSource(xml), new StreamResult(sw));
        return sw.getBuffer().toString();
    }

    private static Document getRSAPublicKeyAsXML(RSAPublicKey key)
        throws ParserConfigurationException, UnsupportedEncodingException {
        Document result = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element rsaKeyValue = result.createElement("RSAKeyValue");
        result.appendChild(rsaKeyValue);
        Element modulus = result.createElement("Modulus");
        rsaKeyValue.appendChild(modulus);

        byte[] modulusBytes = key.getModulus().toByteArray();
        modulusBytes = stripLeadingZeros(modulusBytes);
        modulus.appendChild(result.createTextNode(new String(new sun.misc.BASE64Encoder().encode(modulusBytes))));

        Element exponent = result.createElement("Exponent");
        rsaKeyValue.appendChild(exponent);

        byte[] exponentBytes = key.getPublicExponent().toByteArray();
        exponent.appendChild(result.createTextNode(new String(new sun.misc.BASE64Encoder().encode(exponentBytes))));

        return result;
    }

    private static byte[] stripLeadingZeros(byte[] a) {
        int lastZero = -1;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == 0) {
                lastZero = i;
            } else {
                break;
            }
        }
        lastZero++;
        byte[] result = new byte[a.length - lastZero];
        System.arraycopy(a, lastZero, result, 0, result.length);
        return result;
    }

    private static String getKey(String filename) throws IOException {
        // Read key from file
        String strKeyPEM = "";
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            strKeyPEM += line + "\n";
        }
        br.close();
        return strKeyPEM;
    }

    private static String getKey(FileReader fileReader) throws IOException {
        // Read key from file
        String strKeyPEM = "";
        BufferedReader br = new BufferedReader(fileReader);
        String line;
        while ((line = br.readLine()) != null) {
            strKeyPEM += line + "\n";
        }
        br.close();
        return strKeyPEM;
    }

    public static String signSHA256(String data, String path_private_key_) {
        try {
            String key = getKey(path_private_key_);
            String privateKeyStr = getPrivateFromString(key);
            // System.out.println("privateKeyStr: "+ privateKeyStr);
            byte[] privateKeyBytes = decode.decodeBuffer(privateKeyStr.trim());
            PrivateKey privateKey =
                KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            Signature rsa = Signature.getInstance(SIGNMODE);

            rsa.initSign(privateKey);
            rsa.update(data.getBytes());

            return HexaTool.toHexString(rsa.sign());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Verify chu ky su dung key truyen vao tu config
     * 
     * @param data
     * @param sign
     * @param key_public
     * @return
     */

    public static boolean verifySHA256(String data, String sign, String key_public) {
        try {
            String key = getKey(key_public);
            String privateKeyStr = getPublicFromString(key);
            BASE64Decoder decodeBase64 = new BASE64Decoder();
            HexaTool decodeHex = new HexaTool();
            byte[] publicKeyBytes = decodeBase64.decodeBuffer(privateKeyStr);
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            Signature rsa = Signature.getInstance(SIGNMODE);
            rsa.initVerify(publicKey);
            rsa.update(data.getBytes());
            byte[] signByte = HexaTool.fromHexString(sign);
            return (rsa.verify(signByte));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getPublicFromString(String key) throws IOException, GeneralSecurityException {
        String privateKeyPEM = key;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PUBLIC KEY-----", "");
        privateKeyPEM = privateKeyPEM.replace("\n", "");
        return privateKeyPEM;
    }

    public static String getPrivateFromString(String key) throws IOException, GeneralSecurityException {
        String privateKeyPEM = key;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
        privateKeyPEM = privateKeyPEM.replace("\n", "");
        return privateKeyPEM;
    }

    public static void main(String[] args) {
        genKey2();
        // RequestId|ReferenceId|RequestTime |Amount| Fee
        String requestId = "VAP0012019062" + "1001";
        String referenceId = "123456789";
        String requestTime = "2019-06-21 10:00:00";
        String amount = "50000";
        String fee = "50";
        String message = requestId + "|" + referenceId + "|" + requestTime + "|" + amount + "|" + fee;

        String basePath = "C:\\usr\\ebay_notice_key\\";
        String path = basePath + "ebay_notice_private_key.pem";
        String pathPublic = basePath + "ebay_notice_public_key.pem";
        String sign = RSASign.signSHA256(message, path);

        System.out.println("sign==" + sign);
        System.out.println("OK=====" + RSASign.verifySHA256(message, sign, pathPublic));
    }
}
