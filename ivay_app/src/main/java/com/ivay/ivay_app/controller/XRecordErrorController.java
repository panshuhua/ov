package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.dto.ErrorMsg;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_app.service.RecordErrorService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 记录前台错误日志
 *
 * @author psh
 */
@RestController
@RequestMapping("star/error")
@Api(tags = "记录错误日志")
public class XRecordErrorController {

    @Autowired
    private RecordErrorService recordErrorService;

    /**
     * 错误名
     * 错误信息
     *
     * @return
     */
    @PostMapping("save")
    public Response<String> saveErrorInfo(@RequestBody ErrorMsg errorMsg) {
        Response<String> response = new Response<>();
        recordErrorService.saveErrorInfo(errorMsg);
        return response;
    }

}
