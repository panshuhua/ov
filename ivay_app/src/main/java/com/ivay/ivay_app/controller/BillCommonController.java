package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.dto.TransfersReq;
import com.ivay.ivay_app.service.BillCommonService;
import com.ivay.ivay_common.utils.JsonUtils;
import com.ivay.ivay_common.utils.RSAEncryptSha1;
import com.ivay.ivay_common.utils.SCaptcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户相关接口
 *
 * @author xx
 */
@Api(tags = "公共接口")
@RestController
public class BillCommonController {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private BillCommonService billCommonService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api_paasoo_url}")
    private String paasooUrl;

    @Value("${api_paasoo_key}")
    private String paasooKey;

    @Value("${api_paasoo_secret}")
    private String paasooSecret;

    @PostMapping
    @RequestMapping(value = "star/bill/getbillNo", method = {RequestMethod.GET})
    @ApiOperation(value = "取订单号")
    public String getBillNo() {
        return billCommonService.getBillNo();

    }

    @ApiOperation(value = "paasoo发送短信")
    //@RequestMapping(value = "star/sendsms", method = {RequestMethod.GET})
    @GetMapping("star/sendsms/{to}/{text}")
    @ResponseBody
    public String sendPaasooSms(@PathVariable String text, @PathVariable String to) {
        //接口地址
        Map<String, Object> params = new HashMap<>();
        params.put("key", paasooKey);
        params.put("secret", paasooSecret);
        params.put("from", "SMS");
        params.put("to", to);
        params.put("text", text);

        // RestTemplate restTemplate = new RestTemplate();//此处直接autowire即可，不用new
        //JSONObject mutiData = restTemplate.getForObject(url, JSONObject.class, params);

        String ret = restTemplate.getForObject(paasooUrl, String.class, params);
        return ret;
    }


    /**
     * @author xx
     * @date 2019/4/23
     * @description 生成图片验证码
     */
    @ApiOperation(value = "生成图片验证码")
    // @RequestMapping(value = "star/verification", method = {RequestMethod.POST})
    @GetMapping("star/verification/{macCode}")
    @ResponseBody
    public void verification(HttpServletRequest request, HttpServletResponse response, @PathVariable String macCode)
            throws IOException {
        // 设置响应的类型格式为图片格式
        response.setContentType("image/jpeg");
        // 禁止图像缓存。
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 实例生成验证码对象
        SCaptcha instance = new SCaptcha();
        // 将验证码存入session
        log.info("randomStr  result: " + instance.getCode());
        // redisService.delete("USER:IMG:" + macCode);
        redisTemplate.delete("USER:IMG:" + macCode);
        // redisService.save("USER:IMG:" + macCode, randomStr); // 存储到redis中，后续用于作验证
        redisTemplate.opsForValue().set("USER:IMG:" + macCode, instance.getCode());
        // 向页面输出验证码图片
        instance.write(response.getOutputStream());
    }

//		@ApiOperation(value = "获取图形验证码")
//	    @GetMapping("/test/img/{macCode}")
//	    public byte[] getByte(@PathVariable String macCode, HttpServletResponse response){
//	        // 设置响应的类型格式为图片格式
//	        response.setContentType("image/jpeg");
//	        // 禁止图像缓存。
//	        response.setHeader("Pragma", "no-cache");
//	        response.setHeader("Cache-Control", "no-cache");
//	        response.setDateHeader("Expires", 0);
//	        
//	        Object[] objs = VerifyUtil.createImage();
//	        String randomStr = (String) objs[0];
//	        log.info("randomStr  result: "+randomStr);
//	        //redisService.delete("USER:IMG:" + macCode);
//	        redisTemplate.delete("USER:IMG:" + macCode);
//	        //redisService.save("USER:IMG:" + macCode, randomStr);  // 存储到redis中，后续用于作验证
//	        redisTemplate.opsForValue().set("USER:IMG:" + macCode, randomStr);
//	        return (byte[]) objs[1];
//	    }

//    @PostMapping("/verify")
//    public Response<String> sendCaptcha(@Validated @RequestBody SmsRequest smsRequest) {
//       // 验证 图形验证码
//        if(captchaRequest.getImageCode().equals(redisService.select("USER:IMG"+ smRequest.getMacCode()))){
//                return smsService.getSms(smsRequest.getPhone); // 获取短信
//        }else{
//                return Response.ofError(ERRORCODE.CODE_ERROR); // 验证码错误
//                redisService.delete(RedisKeys.USER_CAPTCHA_MACADDRESS + captchaRequest.getMacCode());
//        }
//    }


//    @Autowired
//    private RestTemplate restTemplate;
//
//    @ApiOperation(value = "testHttps")
//    // @RequestMapping(value = "star/verification", method = {RequestMethod.POST})
//    @GetMapping("star/testHttps")
//    @ResponseBody
//    public void testHttps() {
//        String url = "https://www.baidu.com/"; // 百度返回乱码
//        url = "https://www.so.com/";
//        String responseBody;
//
//        responseBody = restTemplate.getForObject(url, String.class);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
//        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);
//        responseBody = restTemplate.postForObject(url, requestEntity, String.class);
//    }

    @ApiOperation(value = "testHttp")
    // @RequestMapping(value = "star/verification", method = {RequestMethod.POST})
    @GetMapping("star/testHttp")
    @ResponseBody
    public static String testHttp() {
        String url = "http://13.250.110.81:9095/Sandbox/FirmBanking";
        String responseBody;

        //HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


        TransfersReq valCustomerInfoReq = new TransfersReq();
        String RequestId = "FINTECHBK20190508100111";
        String RequestTime = "2019-05-08 18:05:32";
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
        MultiValueMap<String, String> params = JsonUtils.objToLinkedMultiValueMap(valCustomerInfoReq);

//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//
////        params.putAll(map);
//        
//        params.add("RequestId", RequestId);
//        params.add("RequestTime", RequestTime);
//        params.add("PartnerCode", PartnerCode);
//        params.add("Operation", Operation);
//        params.add("BankNo", BankNo);
//        params.add("AccNo", AccNo);
//        params.add("AccType", AccType);


        String encryptStr = RequestId + "|" + RequestTime + "|"
                + PartnerCode + "|" + Operation + "|" + BankNo + "|" + AccNo + "|" + AccType;
        System.out.println("加密前：" + encryptStr);
        String signature = RSAEncryptSha1.encrypt2Sha1(encryptStr);


        //String signature = generateSHA1withRSASigature(RSAEncryptOld.DEFAULT_PRIVATE_KEY , encryptStr);

        //String signature = signWhole(RSAEncryptOld.DEFAULT_PRIVATE_KEY, encryptStr);

        params.add("Signature", signature);

        System.out.println("~~~~~~请求params:" + params);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        responseBody = client.postForObject(url, requestEntity, String.class);
        System.out.println("返回：" + responseBody);

        System.out.println("返回valCustomerInfoRspMap：" + JsonUtils.jsonToMap(responseBody));

        //ValCustomerInfoRsp valCustomerInfoRsp = JsonUtils.jsonToPojo(responseBody, ValCustomerInfoRsp.class);
        //System.out.println("返回valCustomerInfoRsp：" + valCustomerInfoRsp);
        return responseBody;
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("123"));
    }

}
