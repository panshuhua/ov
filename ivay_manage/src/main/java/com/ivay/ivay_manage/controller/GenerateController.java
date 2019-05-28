package com.ivay.ivay_manage.controller;

import com.ivay.ivay_common.dto.GenerateInput;
import com.ivay.ivay_manage.annotation.LogAnnotation;
import com.ivay.ivay_manage.dto.BeanField;
import com.ivay.ivay_manage.dto.GenerateDetail;
import com.ivay.ivay_manage.service.GenerateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 代码生成接口
 *
 * @author xx
 */
@Api(tags = "代码生成")
@RestController
@RequestMapping("/generate")
public class GenerateController {

    @Autowired
    private GenerateService generateService;

    @ApiOperation("根据表名显示表信息")
    @GetMapping(params = {"tableName"})
    @PreAuthorize("hasAuthority('generate:edit')")
    public GenerateDetail generateByTableName(String tableName) {
        GenerateDetail detail = new GenerateDetail();
        detail.setBeanName(generateService.upperFirstChar(tableName));
        List<BeanField> fields = generateService.listBeanField(tableName);
        detail.setFields(fields);

        return detail;
    }

    @LogAnnotation
    @ApiOperation("生成代码")
    @PostMapping
    @PreAuthorize("hasAuthority('generate:edit')")
    public void save(@RequestBody GenerateInput input) {
        generateService.saveCode(input);
    }

}
