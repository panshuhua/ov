package com.ivay.ivay_manage.advice;

import com.ivay.ivay_common.annotation.LogAnnotation;
import com.ivay.ivay_manage.service.SysLogService;
import com.ivay.ivay_manage.utils.UserUtil;
import com.ivay.ivay_repository.model.SysLogs;
import io.swagger.annotations.ApiOperation;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 统一日志处理
 *
 * @author xx
 * <p>
 * 2017年8月19日
 */
@Aspect
@Component
public class LogAdvice {

    @Autowired
    private SysLogService logService;

    @Around(value = "@annotation(com.ivay.ivay_common.annotation.LogAnnotation)")
    public Object logSave(ProceedingJoinPoint joinPoint) throws Throwable {
        SysLogs sysLogs = new SysLogs();
//        sysLogs.setUser(UserUtil.getLoginUser()); // 设置当前登录用户
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

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
            throw new RuntimeException("没有指定日志module");
        }
        sysLogs.setModule(module);

        try {
            Object object = joinPoint.proceed();
            sysLogs.setFlag(true);

            return object;
        } catch (Exception e) {
            sysLogs.setFlag(false);
            sysLogs.setRemark(e.getMessage());
            throw e;
        } finally {
//            if (sysLogs.getUser() != null) {
//                //logService.save(sysLogs);
//            }
        }

    }
    
    @AfterThrowing(value = "@annotation(com.ivay.ivay_common.annotation.LogAnnotation)",throwing="e")
    public void errorLogSave(JoinPoint joinPoint,Exception e) throws Throwable{
    	SysLogs sysLogs = new SysLogs();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //获取堆栈信息
	    StringWriter stringWriter = new StringWriter();
	    PrintWriter printWriter = new PrintWriter(stringWriter);
	    e.printStackTrace(printWriter);
	    StringBuffer error = stringWriter.getBuffer();
        sysLogs.setRemark("manage错误信息："+e.getMessage()+"\n"+error.toString());
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

        if (StringUtils.isEmpty(module)) {
            throw new RuntimeException("没有指定日志module");
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
