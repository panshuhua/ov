package com.ivay.ivay_app.advice;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.formula.functions.T;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ivay.ivay_app.dto.CollectionTransactionNotice;
import com.ivay.ivay_app.dto.CollectionTransactionRsp;
import com.ivay.ivay_app.dto.XLoginUser;
import com.ivay.ivay_app.filter.TokenFilter;
import com.ivay.ivay_app.service.SysLogService;
import com.ivay.ivay_app.service.XTokenService;
import com.ivay.ivay_common.advice.BusinessException;
import com.ivay.ivay_common.annotation.LogAnnotation;
import com.ivay.ivay_common.dto.Response;
import com.ivay.ivay_common.dto.ResponseInfo;
import com.ivay.ivay_common.utils.SysVariable;
import com.ivay.ivay_repository.model.LoginInfo;
import com.ivay.ivay_repository.model.ReturnUser;
import com.ivay.ivay_repository.model.SysLogs;

import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 统一日志处理
 * </p>
 *
 * @author xx 2017年8月19日
 */
@Aspect
@Component
public class LogAdvice {
    private static final Logger logger = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private SysLogService logService;
    @Autowired
    private XTokenService tokenService;

    @Around(value = "@annotation(com.ivay.ivay_common.annotation.LogAnnotation)")
    public Object logSave(ProceedingJoinPoint joinPoint) throws Throwable {
        SysLogs sysLogs = new SysLogs();
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        String methodName = methodSignature.getMethod().getName();
        Object[] args = joinPoint.getArgs(); // 参数值
        String[] argNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames(); // 参数名
        String phone = "";
        boolean isNotice = false;
        Integer optType = 0;
        for (int i = 0; i < argNames.length; i++) {
            // 发送短信验证码，重设密码
            if (SysVariable.PARAM_MOBILE.equals(argNames[i])) {
                phone = (String)args[i];
            }
            // 发送短信的类型
            if (SysVariable.PARAM_OPTTYPE.equals(argNames[i])) {
                optType = (Integer)args[i];
            }
            // 登录与注册
            if (SysVariable.PARAM_LOGININFO.equals(argNames[i])) {
                LoginInfo loginInfo = (LoginInfo)args[i];
                phone = loginInfo.getMobile();
            }
            // 还款回调接口
            if (SysVariable.PARAM_NOTICE.equals(argNames[i])) {
                CollectionTransactionNotice c = (CollectionTransactionNotice)args[i];
                String requestId = c.getRequestId();
                sysLogs.setRequestId(requestId);
                isNotice = true;
            }
            // 其他接口，参数必须加上request
            if (SysVariable.PARAM_REQUEST.equals(argNames[i])) {
                HttpServletRequest req = (HttpServletRequest)args[i];
                String token = TokenFilter.getToken(req);
                XLoginUser xLoginUser = tokenService.getLoginUser(token);
                if (xLoginUser != null) {
                    String userGid = xLoginUser.getUserGid();
                    sysLogs.setUserGid(userGid);
                }

            }

        }
        sysLogs.setPhone(phone);
        String module = null;
        LogAnnotation logAnnotation = methodSignature.getMethod().getDeclaredAnnotation(LogAnnotation.class);
        module = logAnnotation.module();
        if (StringUtils.isEmpty(module)) {
            ApiOperation apiOperation = methodSignature.getMethod().getDeclaredAnnotation(ApiOperation.class);
            if (apiOperation != null) {
                module = apiOperation.value();
            }
        }

        if (StringUtils.isEmpty(module)) {
            module = "default";
        }

        sysLogs.setModule(module);

        try {
            Object object = joinPoint.proceed();
            if (!isNotice) {
                Response<T> response = (Response<T>)object;
                ResponseInfo status = response.getStatus();
                String code = status.getCode();
                String message = status.getMessage();
                sysLogs.setCode(code);

                if (!SysVariable.RETURN_SUCESS_CODE.equals(code)) {
                    sysLogs.setFlag(false);
                    sysLogs.setRemark("操作失败，详细信息为：" + message);
                } else {
                    // 记录发送短信验证码方法返回成功的记录
                    if (SysVariable.METHOD_SENDREGISTERCODE.equals(methodName)) {
                        sysLogs.setFlag(true);
                        sysLogs.setRemark("发送短信验证码成功，发送类型：" + optType);
                    }
                    // 注册方法通过返回值区分短信验证码登录的类型
                    if (SysVariable.RETURN_TYPE_REGISTER.equals(methodName)) {
                        Response<ReturnUser> responseR = (Response<ReturnUser>)object;
                        ReturnUser user = responseR.getBo();
                        String type = user.getType();
                        sysLogs.setFlag(true);
                        sysLogs.setRemark("短信验证码登录成功，操作类型：" + type);
                    }

                }
            } else {
                // 还款回调接口
                CollectionTransactionRsp rsp = (CollectionTransactionRsp)object;
                String code = rsp.getResponseCode();
                String message = rsp.getResponseMessage();
                sysLogs.setCode(code);
                if (!SysVariable.RETURN_SUCESS_CODE.equals(code)) {
                    sysLogs.setFlag(false);
                    sysLogs.setRemark("操作失败，详细信息为：" + message);
                }

            }

            if (sysLogs.getFlag() != null) {
                logService.save(sysLogs);
            }

            return object;
        } catch (Exception e) {
            sysLogs.setFlag(false);
            sysLogs.setRemark(e.getMessage());
            throw e;
        }

    }

    @AfterThrowing(value = "@annotation(com.ivay.ivay_common.annotation.LogAnnotation)", throwing = "e")
    public void errorLogSave(JoinPoint joinPoint, Exception e) throws Throwable {
        SysLogs sysLogs = new SysLogs();
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        String methodName = methodSignature.getMethod().getName();
        Object[] args = joinPoint.getArgs(); // 参数值
        String[] argNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames(); // 参数名
        String phone = "";
        Integer optType = 0;
        for (int i = 0; i < argNames.length; i++) {
            // 发送短信验证码，重设密码
            if (SysVariable.PARAM_MOBILE.equals(argNames[i])) {
                phone = (String)args[i];
            }

            // 发送短信的类型
            if (SysVariable.PARAM_OPTTYPE.equals(argNames[i])) {
                optType = (Integer)args[i];
            }

            // 登录与注册
            if (SysVariable.PARAM_LOGININFO.equals(argNames[i])) {
                LoginInfo loginInfo = (LoginInfo)args[i];
                phone = loginInfo.getMobile();
            }

            // 其他接口，参数必须加上request
            if (SysVariable.PARAM_REQUEST.equals(argNames[i])) {
                HttpServletRequest req = (HttpServletRequest)args[i];
                String token = TokenFilter.getToken(req);
                XLoginUser xLoginUser = tokenService.getLoginUser(token);
                if (xLoginUser != null) {
                    String userGid = xLoginUser.getUserGid();
                    sysLogs.setUserGid(userGid);
                }

            }
        }

        sysLogs.setPhone(phone);

        // 获取堆栈信息
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        StringBuffer error = stringWriter.getBuffer();
        String errInfo = error.toString().substring(0, 1000);
        if (e instanceof BusinessException) {
            BusinessException be = (BusinessException)e;
            String code = be.getCode();
            sysLogs.setCode(code);
            sysLogs.setRemark("app操作失败，错误信息：" + e.getMessage());
            if (SysVariable.METHOD_SENDREGISTERCODE.equals(methodName)) {
                sysLogs.setRemark("发送短信验证码失败，发送类型：" + optType + "，错误信息：" + e.getMessage());
            }
        } else {
            sysLogs.setRemark("app操作失败，错误信息：" + e.getMessage() + "\n" + errInfo);
            if (SysVariable.METHOD_SENDREGISTERCODE.equals(methodName)) {
                sysLogs.setRemark("发送短信验证码失败，发送类型：" + optType + "，异常信息：" + e.getMessage() + "\n" + errInfo);
            }
        }

        sysLogs.setFlag(false);
        String module = null;
        LogAnnotation logAnnotation = methodSignature.getMethod().getDeclaredAnnotation(LogAnnotation.class);
        module = logAnnotation.module();
        if (StringUtils.isEmpty(module)) {
            ApiOperation apiOperation = methodSignature.getMethod().getDeclaredAnnotation(ApiOperation.class);
            if (apiOperation != null) {
                module = apiOperation.value();
            }
        }

        sysLogs.setModule(module);

        try {
            logService.save(sysLogs);
        } catch (Throwable ex) {
            sysLogs.setFlag(false);
            sysLogs.setRemark(e.getMessage());
            throw ex;
        }

    }

}
