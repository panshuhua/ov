package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.dto.BaokimResponseStatus;
import com.ivay.ivay_app.dto.TransfersRsp;
import com.ivay.ivay_app.service.XAPIService;
import com.ivay.ivay_app.service.XUserInfoService;
import com.ivay.ivay_common.annotation.LogAnnotation;
import com.ivay.ivay_common.config.I18nService;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.utils.MinDistance;
import com.ivay.ivay_common.utils.StringUtil;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_common.utils.UUIDUtils;
import com.ivay.ivay_repository.dao.master.XUserBankcardInfoDao;
import com.ivay.ivay_repository.dto.XUserCardAndBankInfo;
import com.ivay.ivay_repository.model.XUserBankcardInfo;
import com.ivay.ivay_repository.model.XUserInfo;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@Api(tags = "绑卡")
@RequestMapping("star/userBankcardInfo")
public class XUserBankcardInfoController {
    private static final Logger logger = LoggerFactory.getLogger(XUserContactsController.class);

    @Autowired
    private XUserBankcardInfoDao xUserBankcardInfoDao;

    @Autowired
    private I18nService i18nService;

    @Autowired
    private XUserInfoService xUserInfoService;

    @Autowired
    private XAPIService xapiService;

    @PostMapping("add")
    @ApiOperation(value = "添加银行卡")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bankNo", value = "银行编号", dataType = "String", paramType = "query", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "result => 1 已经设置了交易密碼，0 还沒设置之交易密碼")
    })
    @LogAnnotation(module = "添加银行卡")
    public Response<String> save(@RequestBody XUserBankcardInfo xUserBankcardInfo,
                                 @RequestParam String bankNo) {
        Response<String> response = new Response<>();
        if (StringUtils.isEmpty(xUserBankcardInfo.getCardUserName())) {
            logger.info("{}: 输入姓名为空", xUserBankcardInfo.getUserGid());
            response.setStatus(i18nService.getMessage("response.error.bank.account.code"),
                    i18nService.getMessage("response.error.bank.account.msg"));
            return response;
        } else {
            // 将越南名字转化为大写字母
            xUserBankcardInfo.setCardUserName(StringUtil.vietnameseToUpperEnglish(xUserBankcardInfo.getCardUserName()));
        }
        // 卡号是否重复
        List<XUserBankcardInfo> list = xUserBankcardInfoDao.getByCardNo(xUserBankcardInfo.getCardNo());
        if (list != null && list.size() > 0) {
            logger.info("{}: 卡号重复：{}", xUserBankcardInfo.getUserGid(), xUserBankcardInfo.getCardNo());
            response.setStatus(i18nService.getMessage("response.error.bank.repeat.code"),
                    i18nService.getMessage("response.error.bank.repeat.msg"));
            return response;
        }
        XUserInfo xUserInfo = xUserInfoService.getByGid(xUserBankcardInfo.getUserGid());

        // 姓名和银行卡账号是否一致
        if (xUserInfo == null) {
            logger.info("{}: 用户不存在: {}", xUserBankcardInfo.getUserGid(), xUserBankcardInfo.getUserGid());
            response.setStatus(i18nService.getMessage("response.error.user.checkgid.code"),
                    i18nService.getMessage("response.error.user.checkgid.msg"));
            return response;
        } else if (StringUtils.isEmpty(xUserInfo.getName()) ||
                MinDistance.minDistance(xUserInfo.getName(), xUserBankcardInfo.getCardUserName()) > 1) {
            logger.info("{}: 绑定银行卡姓名与系统姓名不一致:{},{}",
                    xUserBankcardInfo.getUserGid(),
                    xUserBankcardInfo.getCardUserName(),
                    xUserInfo.getName());
            response.setStatus(i18nService.getMessage("response.error.bank.account.code"),
                    i18nService.getMessage("response.error.bank.account.msg"));
            return response;
        }

        // 需要判断传入的accType 是否是银行支持的类型
        // 校验身份信息
        TransfersRsp transfersRsp = xapiService.validateCustomerInformation(bankNo,
                xUserBankcardInfo.getCardNo(),
                xUserBankcardInfo.getAccType());
        if (BaokimResponseStatus.SUCCESS.getCode().equals(transfersRsp.getResponseCode())) {
            if (MinDistance.minDistance(xUserBankcardInfo.getCardUserName(), transfersRsp.getAccName()) > 1) {
                logger.info("{}: 绑定银行卡姓名校验失败：{},{}",
                        xUserBankcardInfo.getUserGid(),
                        xUserBankcardInfo.getCardUserName(),
                        transfersRsp.getAccName());
                response.setStatus(i18nService.getMessage("response.error.bank.account.code"),
                        i18nService.getMessage("response.error.bank.account.msg"));
                return response;
            }
            Date now = new Date();
            xUserBankcardInfo.setCreateTime(now);
            xUserBankcardInfo.setBankcardGid(UUIDUtils.getUUID());
            xUserBankcardInfo.setEnableFlag(SysVariable.ENABLE_FLAG_YES);
            xUserBankcardInfo.setStatus(SysVariable.CARD_STATUS_DOING);
            xUserBankcardInfoDao.save(xUserBankcardInfo);
            // 如果没有帮过卡，则更新用户状态
            if ("0123".indexOf(xUserInfo.getUserStatus()) != -1) {
                xUserInfo.setUserStatus(SysVariable.USER_STATUS_BANKCARD_SUCCESS);
                xUserInfo.setUpdateTime(now);
                xUserInfoService.update(xUserInfo);
            }
            response.setBo(StringUtils.isEmpty(xUserInfo.getTransPwd()) ? SysVariable.TRANSFER_PWD_NONE : SysVariable.TRANSFER_PWD_HAS);
        } else {
            response.setStatus(transfersRsp.getResponseCode(), transfersRsp.getResponseMessage());
            logger.info("{}: 绑卡身份验证失败： {}", xUserBankcardInfo.getUserGid(), response.getStatus().getMessage());
        }
        return response;
    }

    @GetMapping("list/{userGid}")
    @ApiOperation(value = "根据userGid获取个人银行卡列表")
    @LogAnnotation(module = "根据userGid获取个人银行卡列表")
    public Response<List<XUserCardAndBankInfo>> getCardList(@PathVariable String userGid, HttpServletRequest request) {
        Response<List<XUserCardAndBankInfo>> response = new Response<>();
        response.setBo(xUserBankcardInfoDao.getByUserGid(userGid));
        return response;
    }

    @PostMapping("deleteCard")
    @ApiOperation(value = "删除某人的某张银行卡")
    @LogAnnotation(module = "删除某人的某张银行卡")
    public Response<Integer> delete(@RequestBody XUserBankcardInfo xUserBankcardInfo, HttpServletRequest request) {
        int num = xUserBankcardInfoDao.delete(xUserBankcardInfo.getBankcardGid(), xUserBankcardInfo.getUserGid());
        Response<Integer> rsp = new Response<>();
        rsp.setBo(num);
        return rsp;
    }

    @PostMapping("deleteCards")
    @ApiOperation(value = "删除某张银行卡所有的绑定")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bankcardGid", value = "银行卡gid", dataType = "String", paramType = "query")
    })
    @LogAnnotation(module = "删除某张银行卡所有的绑定")
    public Response<Integer> deletes(@RequestParam String bankcardGid, HttpServletRequest request) {
        int num = xUserBankcardInfoDao.deletes(bankcardGid);
        Response<Integer> rsp = new Response<>();
        rsp.setBo(num);
        return rsp;
    }

    @PostMapping("getCardStatus")
    @ApiOperation(value = "获取银行卡绑定结果")
    @LogAnnotation(module = "获取银行卡绑定结果")
    public Response<List<XUserBankcardInfo>> getCardStatus(@RequestBody XUserBankcardInfo xUserBankcardInfo, HttpServletRequest request) {
        Response<List<XUserBankcardInfo>> response = new Response<>();
        response.setBo(xUserBankcardInfoDao.getByCardGid(xUserBankcardInfo.getBankcardGid(),
                xUserBankcardInfo.getUserGid()));
        return response;
    }
}
