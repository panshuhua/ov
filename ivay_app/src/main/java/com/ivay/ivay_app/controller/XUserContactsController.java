package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.service.XUserContactsService;
import com.ivay.ivay_common.annotation.LogAnnotation;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_repository.dto.XRiskInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("star/xUserContactss")
@Api(tags = "风控数据上传")
public class XUserContactsController {
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private XUserContactsService xUserContactsService;

    @PostMapping("add_contacts")
    @ApiOperation(value = "上传风控信息")
    @LogAnnotation(module = "上传联系人/GPS/社交类app的个数等风控信息")
    public Response<String> saveAll(@RequestParam(required = false) String type,
                                    @RequestBody XRiskInfo riskInfo,
                                    HttpServletRequest request) throws Exception {
        logger.info("前台参数类型：" + type + "参数内容：" + riskInfo.toString());
        logger.info("上传风控数据方法入口-----------------");
        Response<String> response = new Response<>();
        xUserContactsService.saveAll(type, riskInfo, request);
        return response;
    }

}
