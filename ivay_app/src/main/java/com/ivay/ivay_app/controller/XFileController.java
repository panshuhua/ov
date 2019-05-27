package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.dao.XFileInfoDao;
import com.ivay.ivay_app.dto.Response;
import com.ivay.ivay_app.model.XFileInfo;
import com.ivay.ivay_app.service.XFileService;
import com.ivay.ivay_app.service.XUserInfoService;
import com.ivay.ivay_app.table.PageTableHandler;
import com.ivay.ivay_app.table.PageTableRequest;
import com.ivay.ivay_app.table.PageTableResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Api(tags = "授信-身份校验")
@RestController
@RequestMapping("star/files")
public class XFileController {
    @Autowired
    private XFileService xFileService;

    @Autowired
    private XFileInfoDao xFileInfoDao;

    @Autowired
    private XUserInfoService xUserInfoService;

    @PostMapping("save")
    @ApiOperation(value = "文件上传")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flag", value = "照片类型标志位，0正面 1反面 2手持", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "userGid", value = "用户gid，", dataType = "String", paramType = "query", required = true)
    })
    public Response<XFileInfo> uploadFile(MultipartFile file, String flag, String userGid) throws IOException {
        Response<XFileInfo> response = new Response<>();
        response.setBo(xFileService.save(file, flag, userGid));
        return response;
    }

    //    @GetMapping("checking")
//    @ApiOperation(value = "查询待审核文件")
//    @PreAuthorize("hasAuthority('sys:file:query')")
    public PageTableResponse listFiles(PageTableRequest request) {
        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return xFileInfoDao.count(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<XFileInfo> list(PageTableRequest request) {
                List<XFileInfo> list = xFileInfoDao.list(request.getParams(), request.getOffset(), request.getLimit());
                return list;
            }
        }).handle(request);
    }

    @PostMapping("submit")
    @ApiOperation(value = "提交审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gid", value = "用户gid", dataType = "String", paramType = "query", required = true)
    })
    public Response<Integer> submit(@RequestParam String gid) {
        Response<Integer> response = new Response<>();
        response.setBo(xUserInfoService.submit(gid));
        return response;
    }

    @PostMapping("approve")
    @ApiOperation(value = "审核通过")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userGid", value = "用户gid", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "flag", value = "审核结果：0拒绝 1通过", dataType = "Long", paramType = "query", required = true)
    })
    public Response<Integer> approve(@RequestParam String userGid, @RequestParam int flag) {
        Response<Integer> response = new Response<>();
        response.setBo(xUserInfoService.approve(userGid, flag));
        return response;
    }
}
