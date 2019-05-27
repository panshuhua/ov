package com.ivay.ivay_app.annotation;

import java.lang.annotation.*;

/**
 * @author Anime
 *  描述：
 *  1、需要对请求参数的某些字段加解密，则需要给请求方法增加该注解
 *  2、需要对请求体的某些字段加解密，则需要给请求体增加该注解
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Encryption {
}
