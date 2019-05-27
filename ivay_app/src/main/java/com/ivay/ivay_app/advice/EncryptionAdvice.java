package com.ivay.ivay_app.advice;

import com.ivay.ivay_app.annotation.Decrypt;
import com.ivay.ivay_app.annotation.Encrypt;
import com.ivay.ivay_app.model.XUserInfo;
import com.ivay.ivay_app.utils.AESEncryption;
import com.ivay.ivay_app.utils.ReflectUtil;
import com.ivay.ivay_app.valid.IdentityCard;
import com.ivay.ivay_app.valid.Password;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Aspect
@Component
@Order(4)
public class EncryptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(EncryptionAdvice.class);

    @Value("${needDecrypt}")
    private boolean needDecrypt;

    /**
     * 定义切入点: 当前方法持有指定注解
     */
    @Pointcut("@annotation(com.ivay.ivay_app.annotation.Encryption)")
    public void paramsEncryptionPointcut() {
    }

    @Around("paramsEncryptionPointcut()")
    public Object doEncryption(ProceedingJoinPoint joinPoint) throws Throwable {
        //1.获取到所有的参数值的数组
        Object[] parameterValues = joinPoint.getArgs();
        if (needDecrypt) {
            try {
                Signature signature = joinPoint.getSignature();
                MethodSignature methodSignature = (MethodSignature) signature;
                //2.获取到方法的所有参数名称的字符串数组
                String[] parameterNames = methodSignature.getParameterNames();
                Method method = methodSignature.getMethod();
                Annotation[][] annotationss = method.getParameterAnnotations();
                for (int i = 0, len = parameterNames.length; i < len; i++) {
                    Annotation[] annotations = annotationss[i];
                    for (int j = 0; j < annotations.length; j++) {
                        if (annotations[j].annotationType().equals(Password.class) ||
                                annotations[j].annotationType().equals(IdentityCard.class) ||
                                annotations[j].annotationType().equals(Decrypt.class)) {
                            // 需要解密
                            parameterValues[i] = AESEncryption.decrypt(parameterValues[i].toString());
                        } else if (annotations[j].annotationType().equals(Encrypt.class)) {
                            // 需要加密
                            parameterValues[i] = AESEncryption.encrypt(parameterValues[i].toString());
                        }
                    }
                }
            } catch (Exception ex) {
                throw new IllegalArgumentException("加解密AOP异常");
            }
        }
        return joinPoint.proceed(parameterValues);
    }

    /**
     * 切入点：controller层的请求实体，含有需要加解密的字段
     */
    @Pointcut("execution(* com.ivay.ivay_app.controller..*.*(@com.ivay.ivay_app.annotation.Encryption *,..))")
    public void bodyEncryptionPointcut() {
    }

    @Before("bodyEncryptionPointcut()")
    public void doEncryption2(JoinPoint joinPoint) {
        try {
            if (needDecrypt) {
                Object[] parameterValues = joinPoint.getArgs();
                for (Object obj : parameterValues) {
                    if (obj instanceof XUserInfo) {
                        Field[] fields = XUserInfo.class.getDeclaredFields();
                        XUserInfo xUserInfo = (XUserInfo) obj;
                        for (Field field : fields) {
                            if (field.getAnnotationsByType(Encrypt.class).length > 0) {
                                ReflectUtil.invokeSetter(xUserInfo, field, AESEncryption.encrypt(ReflectUtil.invokeGetter(xUserInfo, field)));
                            }

                            if (field.getAnnotationsByType(Decrypt.class).length > 0) {
                                ReflectUtil.invokeSetter(xUserInfo, field, AESEncryption.decrypt(ReflectUtil.invokeGetter(xUserInfo, field)));
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.info("加解密aop出現異常了: {}", ex);
        }
    }
}
