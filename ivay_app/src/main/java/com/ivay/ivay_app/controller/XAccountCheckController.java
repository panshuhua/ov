package com.ivay.ivay_app.controller;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ivay.ivay_app.service.XAccountCheckService;
import com.ivay.ivay_common.dto.Response;

import io.swagger.annotations.Api;

/**
 * 通道对账
 * 
 * @author panshuhua
 * @date 2019/07/02
 */
@RestController
@RequestMapping("star/accountCheck")
@Api(tags = "通道对账")
public class XAccountCheckController {

    @Autowired
    XAccountCheckService xAccountCheckService;

    /**
     * 导入baokim借/还款数据： type=1表示借款 type=2表示还款
     * 
     * @throws IOException
     * @throws ParseException
     */
    @PostMapping("/importAccountDatas")
    public Response<String> importAccountDatas(@RequestParam String type, MultipartFile file,
        HttpServletRequest request) throws IOException, ParseException {
        Response<String> response = new Response<String>();
        xAccountCheckService.importAccountDatas(type, file);
        return response;
    }

    /**
     * 根据时间比对baokim和VIN FINTECH中的数据是否一致
     * 
     * @param time
     * @return
     */
    @GetMapping("/checkBkCollectionDatas")
    public Response<String> checkBkCollectionDatas(@RequestParam String time) {
        Response<String> response = new Response<String>();
        xAccountCheckService.checkBkCollectionDatas(time);
        return response;
    }

    /**
     * 比对baokim借款数据
     * 
     * @param stime
     * @param etime
     * @return
     */
    @GetMapping("/checkBkTransferDatas")
    public Response<String> checkBkTransferDatas(@RequestParam String time) {
        Response<String> response = new Response<String>();
        xAccountCheckService.checkBkTransferDatas(time);
        return response;
    }

}
