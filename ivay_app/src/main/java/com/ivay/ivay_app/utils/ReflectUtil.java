package com.ivay.ivay_app.utils;

import com.ivay.ivay_app.annotation.Encrypt;
import com.ivay.ivay_app.model.XUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {
    private static final Logger logger = LoggerFactory.getLogger(ReflectUtil.class);

    /**
     * 根据字段名获得getter方法名
     *
     * @param property
     * @return
     */
    public static String getGetterNameByFieldName(String property) {
        if (Character.isUpperCase(property.charAt(0))) {
            return "get" + property;
        } else {
            return (new StringBuilder("get").append(Character.toUpperCase(property.charAt(0))).append(property.substring(1))).toString();
        }
    }

    /**
     * 根据字段名获得setter方法名
     *
     * @param property
     * @return
     */
    public static String getSetterNameByFieldName(String property) {
        if (Character.isUpperCase(property.charAt(0))) {
            return "set" + property;
        } else {
            return (new StringBuilder("set").append(Character.toUpperCase(property.charAt(0))).append(property.substring(1))).toString();
        }
    }

    /**
     * 根据字段名调用getter方法
     *
     * @param obj
     * @param field
     * @return
     */
    public static <T> T invokeGetter(Object obj, Field field) {
        try {
            Method method = obj.getClass().getDeclaredMethod(getGetterNameByFieldName(field.getName()));
            if (method != null) {
                // 调用目标类的setter函数
                return (T) method.invoke(obj);
            } else {
                logger.info("not find setter on {}", field.getName());
            }
        } catch (Exception ex) {
            logger.info("invoke setter on {} error: {}", field.getName(), ex.toString());
        }
        return null;
    }

    /**
     * 根据字段名调用setter方法
     *
     * @param obj
     * @param field
     * @param value
     * @return
     */
    public static boolean invokeSetter(Object obj, Field field, Object value) {
        try {
            Method method = obj.getClass().getDeclaredMethod(getSetterNameByFieldName(field.getName()), field.getType());
            if (method != null) {
                // 调用目标类的setter函数
                method.invoke(obj, value);
                return true;
            } else {
                logger.info("not find setter on {}", field.getName());
            }
        } catch (Exception ex) {
            logger.info("invoke setter on {} error: {}", field.getName(), ex.toString());
        }
        return false;
    }

    public static void main(String[] args) {
        XUserInfo xUserInfo = new XUserInfo();
        xUserInfo.setPhone("c197cd7b49946528fae4272adcfe3e90"); // 123
        xUserInfo.setPassword("9750a540f58a41d2bf9da1ee99bae8ee"); // 123456
        xUserInfo.setIdentityCard("33e8319c8bdee015d6d5adbff7767ae5"); // 123456789
        System.out.println(AESEncryption.encrypt("123456789"));
        Field[] fields = xUserInfo.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotationsByType(Encrypt.class).length > 0) {
                invokeSetter(xUserInfo, field, AESEncryption.decrypt(invokeGetter(xUserInfo, field)));
                System.out.println(field.getName() + ": " + invokeGetter(xUserInfo, field));
            }
        }
        System.out.println(xUserInfo);
    }
}
