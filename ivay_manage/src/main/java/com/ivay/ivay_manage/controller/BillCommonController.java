package com.ivay.ivay_manage.controller;

import com.ivay.ivay_manage.service.RiskUserService;
import com.ivay.ivay_manage.service.UserService;
import com.ivay.ivay_repository.model.RiskUser;
import com.ivay.ivay_repository.model.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户相关接口
 *
 * @author xx
 */
@Api(tags = "测试接口")

@RestController
public class BillCommonController {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private RiskUserService riskUserService;

    @Autowired
    private UserService userService;

    @PostMapping
    @RequestMapping(value = "/test/risk/list/{username}/{phone}", method = {RequestMethod.GET})
    @ApiOperation(value = "取风控人员")
    public List<RiskUser> getBillNo(@PathVariable String username, @PathVariable String phone) {
        SysUser sys = userService.getUserByName(username);
        log.info("~~~~日志：" + sys);
        //return riskUserService.selectUserListByUserId(Integer.valueOf(userId));

        return riskUserService.selectUserListByPhone(phone);
    }


    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("123"));
    }

}
