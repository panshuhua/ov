package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.config.I18nService;
import com.ivay.ivay_app.dto.Response;
import com.ivay.ivay_app.model.RiskInfo;
import com.ivay.ivay_app.service.XUserContactsService;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableHandler.CountHandler;
import com.ivay.ivay_common.table.PageTableHandler.ListHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import com.ivay.ivay_repository.dao.master.XUserContactsDao;
import com.ivay.ivay_repository.model.XUserContacts;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("star/xUserContactss")
@Api(tags = "通讯录")
public class XUserContactsController {

    @Autowired
    private XUserContactsDao xUserContactsDao;
    @Autowired
    private XUserContactsService xUserContactsService;
    @Autowired
    private I18nService i18nService;

    @PostMapping("save")
    @ApiOperation(value = "保存")
    public XUserContacts save(@RequestBody XUserContacts xUserContacts) {
        xUserContactsDao.save(xUserContacts);

        return xUserContacts;
    }

    @PostMapping("add_contacts")
    @ApiOperation(value = "批量保存")
    public Response<String> saveAll(@RequestBody RiskInfo riskInfo) {
        Response<String> response = new Response<>();
        boolean flag = xUserContactsService.saveAll(riskInfo);
        if (!flag) {
            response.setStatus(i18nService.getMessage("response.error.risk.save.code"), i18nService.getMessage("response.error.risk.save.msg"));
        }
        return response;
    }

    @GetMapping("get/{id}")
    @ApiOperation(value = "根据id获取")
    public XUserContacts get(@PathVariable Long id) {
        return xUserContactsDao.getById(id);
    }


    @GetMapping("get_contacts/{gid}")
    @ApiOperation(value = "根据gid获取所有联系人信息")
    public PageTableResponse get(int limit, int num, @RequestParam String gid) {
        return xUserContactsService.getByGid(limit, num, gid);
    }

    @PutMapping("modify")
    @ApiOperation(value = "修改")
    public XUserContacts update(@RequestBody XUserContacts xUserContacts) {
        xUserContactsDao.update(xUserContacts);

        return xUserContacts;
    }

    @GetMapping("list")
    @ApiOperation(value = "列表")
    public PageTableResponse list(PageTableRequest request) {
        return new PageTableHandler(new CountHandler() {
            @Override
            public int count(PageTableRequest request) {
                return xUserContactsDao.count(request.getParams());
            }
        }, new ListHandler() {
            @Override
            public List<XUserContacts> list(PageTableRequest request) {
                return xUserContactsDao.list(request.getParams(), request.getOffset(), request.getLimit());
            }
        }).handle(request);
    }

    @DeleteMapping("delete/{id}")
    @ApiOperation(value = "删除")
    public void delete(@PathVariable Long id) {
        xUserContactsDao.delete(id);
    }
}
