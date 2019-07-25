package com.ivay.ivay_app.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ivay.ivay_app.dto.VTPTranInfo;
import com.ivay.ivay_app.dto.VTPTranOutputData;
import com.ivay.ivay_app.dto.VTPTranProcessInput;
import com.ivay.ivay_app.dto.VTPTranProcessOutput;
import com.ivay.ivay_app.dto.VTPUpdateTranStatusResponse;
import com.ivay.ivay_app.service.VTPRepayApiService;
import com.ivay.ivay_common.dto.Response;

import io.swagger.annotations.Api;

/**
 * VTP 还款API
 * 
 * @author panshuhua
 * @date 2019/07/19
 */
@RestController
@RequestMapping("star/vtpRepayApi")
@Api(tags = "VTP还款接口")
public class VTPRepayApiController {
    private static final Logger logger = LoggerFactory.getLogger(VTPRepayApiController.class);
    @Autowired
    VTPRepayApiService vTPRepayApiService;

    /**
     * 提供接口给VTP
     * 
     * @param id
     * @param req
     * @return
     */
    @GetMapping("/get-info")
    public VTPTranInfo getTransationInfo(@RequestParam String id, HttpServletRequest req) {
        String xToken = req.getHeader("X_TOKEN");
        String xSigned = req.getHeader("X_SIGNED");
        logger.info("xToken=" + xToken);
        logger.info("xSigned=" + xSigned);
        VTPTranInfo vtpTranInfo = vTPRepayApiService.getTransInfo(id, xToken, xSigned);
        return vtpTranInfo;
    }

    /**
     * 提供接口给VTP
     * 
     * @param input
     * @param req
     * @return
     */

    @PostMapping("/transaction-process")
    public VTPTranProcessOutput transactionProcess(@RequestBody VTPTranProcessInput input, HttpServletRequest req) {
        String xToken = req.getHeader("X_TOKEN");
        String xSigned = req.getHeader("X_SIGNED");
        VTPTranProcessOutput VTPTranProcessOutput = vTPRepayApiService.transactionProcess(input, xToken, xSigned);
        return VTPTranProcessOutput;
    }

    /**
     * 提供接口给VTP
     * 
     * @param tid
     * @param req
     * @return
     */
    @GetMapping("/tracking-status")
    public VTPTranProcessOutput trackingStatus(@RequestParam String tid, HttpServletRequest req) {
        String xToken = req.getHeader("X_TOKEN");
        String xSigned = req.getHeader("X_SIGNED");
        VTPTranProcessOutput VTPTranProcessOutput = vTPRepayApiService.trackingStatus(tid, xToken, xSigned);
        return VTPTranProcessOutput;
    }

    /**
     * 调用VTP的接口修改最终的交易状态
     * 
     * @return
     * @throws IOException
     */
    @GetMapping("/update-transaction-status")
    public Response<VTPUpdateTranStatusResponse> updatesTransactionStatus(@RequestBody VTPTranOutputData data)
        throws IOException {
        Response<VTPUpdateTranStatusResponse> response = new Response<VTPUpdateTranStatusResponse>();
        response.setBo(vTPRepayApiService.updateTransactionStatus(data));
        return response;
    }

}
