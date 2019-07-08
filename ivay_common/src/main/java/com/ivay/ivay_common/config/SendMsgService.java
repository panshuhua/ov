package com.ivay.ivay_common.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ivay.ivay_common.dto.FptAccessTokenReq;
import com.ivay.ivay_common.dto.FptSendReq;
import com.ivay.ivay_common.dto.NoticeMsg;
import com.ivay.ivay_common.utils.Base64Util;
import com.ivay.ivay_common.utils.FirebaseUtil;
import com.ivay.ivay_common.utils.JsonUtils;
import com.ivay.ivay_common.utils.StringUtil;
import com.ivay.ivay_common.utils.UUIDUtils;

/**
 * 发送短信/推送
 * 
 * @author panshuhua
 * @date 2019/07/08
 */
@Component
public class SendMsgService {

    private static final Logger logger = LoggerFactory.getLogger(SendMsgService.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${api_paasoo_url}")
    private String paasooUrl;
    @Value("${api_paasoo_key}")
    private String paasooKey;
    @Value("${api_paasoo_secret}")
    private String paasooSecret;

    @Value("${api_fpt_grant_type}")
    private String grantType;
    @Value("${api_fpt_client_id}")
    private String clientId;
    @Value("${api_fpt_client_secret}")
    private String clientSecret;
    @Value("${api_fpt_scope}")
    private String scope;
    @Value("${api_fpt_accesstoken_url}")
    private String fptAccessTokenUrl;
    @Value("${api_fpt_sendmsg_url}")
    private String fptSendmsgUrl;
    @Value("${api_fpt_brandName}")
    private String brandName;

    @Value("${noticemsg.effectiveTime}")
    private long effectiveTime;

    // 调用接口1发送短信
    public Map<String, String> sendMsgBySMS(String mobile, String authCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("key", paasooKey);
        params.put("secret", paasooSecret);
        params.put("from", "SMS");
        params.put("to", "84" + mobile);
        params.put("text", authCode);
        String ret = restTemplate.getForObject(paasooUrl, String.class, params);
        logger.info("调用短信验证码接口返回ret：" + ret + "------------");
        logger.info("【发送的短信内容】：" + authCode + "----------------------");
        Map<String, String> msgMap = JsonUtils.jsonToMap(ret);
        return msgMap;
    }

    // 调用接口3发送短信
    public String sendMsgByFpt(String mobile, String text) {
        // 获取access_token
        String sessionId = UUIDUtils.getUUID();
        FptAccessTokenReq req = new FptAccessTokenReq();
        req.setGrant_type(grantType);
        req.setClient_id(clientId);
        req.setClient_secret(clientSecret);
        req.setScope(scope);
        req.setSession_id(sessionId);

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        HttpEntity<String> formEntity = new HttpEntity<String>(JsonUtils.objectToJson(req), headers);
        String responseBody = restTemplate.postForEntity(fptAccessTokenUrl, formEntity, String.class).getBody();
        logger.info("获取token返回：" + responseBody);

        Map<String, String> map = JsonUtils.jsonToMap(responseBody);
        String accessToken = map.get("access_token");

        if (accessToken != null) {
            // 发送短信
            String message = Base64Util.encode(text);
            System.out.println("加密后的message：" + message);

            FptSendReq sendReq = new FptSendReq();
            sendReq.setAccess_token(accessToken);
            sendReq.setBrandName(brandName);
            sendReq.setMessage(message);
            sendReq.setPhone(mobile);
            sendReq.setSession_id(sessionId);

            formEntity = new HttpEntity<String>(JsonUtils.objectToJson(sendReq), headers);
            // 注意：如果手机号码不对，这里会直接报400
            responseBody = restTemplate.postForEntity(fptSendmsgUrl, formEntity, String.class).getBody();
            logger.info("发送短信返回：" + responseBody);

            return responseBody;
        }

        logger.info("fpt发送短信获取accessToken失败，返回的错误码为：" + map.get("error") + "，错误信息：" + map.get("error_description"));
        return null;
    }

    // 发送firebase消息推送
    public void sendFirebaseNoticeMsg(NoticeMsg msg) throws Exception {
        // 发送消息推送
        String firebaseTitle = msg.getTitle();
        String firebaseMsg = msg.getFirebaseMsg();
        firebaseTitle = StringUtil.vietnameseToEnglish(firebaseTitle);
        firebaseMsg = StringUtil.vietnameseToEnglish(firebaseMsg);
        msg.setTitle(firebaseTitle);
        msg.setFirebaseMsg(firebaseMsg);
        logger.info("firebase推送标题：" + msg.getTitle());
        logger.info("firebase推送内容：" + msg.getFirebaseMsg());
        logger.info("firebase跳转页面to：" + msg.getPageId());
        logger.info("firebase借款gid：" + msg.getGid());
        logger.info("firebase用户gid：" + msg.getUserGid());
        FirebaseUtil.sendMsgToFmcToken(msg);
    }

    // 发送手机短信
    public void sendPhoneNoticeMsg(NoticeMsg msg) throws Exception {
        String phoneMsg = StringUtil.vietnameseToEnglish(msg.getPhoneMsg());
        msg.setPhoneMsg(phoneMsg);
        logger.info("短信页面短链接的key：" + msg.getKey());
        logger.info("手机短信发送的电话号码：" + msg.getPhone());
        logger.info("手机短信发送内容：" + msg.getPhoneMsg());
        logger.info("手机短信跳转页面：" + msg.getPageId());
        logger.info("手机短信借款gid：" + msg.getGid());
        logger.info("手机短信用户gid：" + msg.getUserGid());
    }

}
