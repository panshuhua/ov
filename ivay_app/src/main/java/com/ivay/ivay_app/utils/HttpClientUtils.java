package com.ivay.ivay_app.utils;

import com.ivay.ivay_app.dto.TransfersReq;
import com.ivay.ivay_app.dto.TransfersRsp;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpClientUtils {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    public static CloseableHttpClient acceptsUntrustedCertsHttpClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HttpClientBuilder b = HttpClientBuilder.create();
 
        // setup a Trust Strategy that allows all certificates.
        //
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                return true;
            }
        }).build();
        b.setSSLContext(sslContext);
 
        // don't check Hostnames, either.
        //      -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to weaken
        HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
 
        // here's the special part:
        //      -- need to create an SSL Socket Factory, to use our weakened "trust strategy";
        //      -- and create a Registry, to register it.
        //
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();
 
        // now, we create connection-manager using our Registry.
        //      -- allows multi-threaded use
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager( socketFactoryRegistry);
        connMgr.setMaxTotal(200);
        connMgr.setDefaultMaxPerRoute(100);
        b.setConnectionManager( connMgr);
 
        // finally, build the HttpClient;
        //      -- done!
        CloseableHttpClient client = b.build();
 
        return client;
    }

    //
    public static <T> String postForObject(String url, T req) {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = JsonUtils.objToLinkedMultiValueMap(req);

        log.info("请求params:" + params);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        String responseBody = client.postForObject(url, requestEntity, String.class);
        log.info("返回json：" + responseBody);

        //System.out.println("返回valCustomerInfoRspMap：" + JsonUtils.jsonToMap(responseBody));
        return responseBody;
    }

    public static void main(String[] args) {
        String url = "http://13.250.110.81:9095/Sandbox/FirmBanking";
        TransfersReq valCustomerInfoReq = new TransfersReq();
        String RequestId = "FINTECHBK20190509160112";
        String RequestTime = "2019-05-09 16:05:32";
        String PartnerCode = "FINTECH";
        String Operation = "9001";
        String BankNo = "970406";
        String AccNo = "9704060129837294";
        String AccType = "1";

        valCustomerInfoReq.setRequestId(RequestId);
        valCustomerInfoReq.setRequestTime(RequestTime);
        valCustomerInfoReq.setPartnerCode(PartnerCode);
        valCustomerInfoReq.setOperation(Operation);
        valCustomerInfoReq.setBankNo(BankNo);
        valCustomerInfoReq.setAccNo(AccNo);
        valCustomerInfoReq.setAccType(AccType);
        String encryptStr = RequestId + "|" + RequestTime + "|"
                + PartnerCode + "|" + Operation + "|" + BankNo + "|" + AccNo + "|" + AccType;
        log.info("加密前：" + encryptStr);
        String signature = RSAEncryptSha1.encrypt2Sha1(encryptStr);
        //params.add("Signature", signature);
        valCustomerInfoReq.setSignature(signature);
        String retString = postForObject(url, valCustomerInfoReq);

        //处理返回
        TransfersRsp valCustomerInfoRsp = JsonUtils.jsonToPojo(retString, TransfersRsp.class);
        log.info("返回对象valCustomerInfoRsp：" + valCustomerInfoRsp);
    }

}
