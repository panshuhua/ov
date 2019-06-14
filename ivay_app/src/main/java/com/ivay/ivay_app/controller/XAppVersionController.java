package com.ivay.ivay_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ivay.ivay_app.service.XAppVersionUpdateService;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_repository.model.XVersionUpdate;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * app版本更新
 * @author panshuhua
 */
@RestController
@RequestMapping("star/appVersion")
@Api(tags = "app版本更新")
public class XAppVersionController {
	@Autowired
	XAppVersionUpdateService xAppVersionUpdateService;
	
	@PostMapping("update")
	@ApiOperation("更新app版本")
    public Response<XVersionUpdate> updateAppVersion(@RequestParam String versionNumber){
		 Response<XVersionUpdate> response=new Response<XVersionUpdate>();
		 response.setBo(xAppVersionUpdateService.findUpdate(versionNumber));
		 return response;
    }
    
}
