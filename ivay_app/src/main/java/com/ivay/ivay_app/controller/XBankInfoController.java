package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.dao.XBankInfoDao;
import com.ivay.ivay_app.dto.Response;
import com.ivay.ivay_app.model.XBankInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "银行")
@RequestMapping("star/xBankInfo")
public class XBankInfoController {

    @Autowired
    private XBankInfoDao xBankInfoDao;

    @PostMapping("save")
    @ApiOperation(value = "保存")
    public XBankInfo save(@RequestBody XBankInfo xBankInfo) {
        xBankInfoDao.save(xBankInfo);

        return xBankInfo;
    }

    @GetMapping("get/{id}")
    @ApiOperation(value = "根据id获取")
    public XBankInfo get(@PathVariable Long id) {
        return xBankInfoDao.getById(id);
    }
    
    @GetMapping("getBankList")
    @ApiOperation(value = "银行列表获取")
    public Response<List<XBankInfo>> getBankList() {
        Response<List<XBankInfo>> response = new Response<>();
        response.setBo(xBankInfoDao.getBankList());
        return response;
    }

    @PutMapping("update")
    @ApiOperation(value = "修改")
    public XBankInfo update(@RequestBody XBankInfo xBankInfo) {
        xBankInfoDao.update(xBankInfo);
        return xBankInfo;
    }

    @DeleteMapping("delete/{id}")
    @ApiOperation(value = "删除")
    public void delete(@PathVariable Long id) {
        xBankInfoDao.delete(id);
    }
}
