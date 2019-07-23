package com.ivay.ivay_common.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * spring获取bean工具类
 *
 * @author xx
 */
@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    private static String keyFilePath;

    @Value("${keyfilepath}")
    public void setKeyFilePath(String keyFilePath) {
        SpringUtil.keyFilePath = keyFilePath;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> cla) {
        return applicationContext.getBean(cla);
    }

    public static <T> T getBean(String name, Class<T> cal) {
        return applicationContext.getBean(name, cal);
    }

    public static String getProperty(String key) {
        return applicationContext.getBean(Environment.class).getProperty(key);
    }

    public static String getResourceFile() {
        // String root = System.getProperty("user.dir");
        // String filePath = root+File.separator+"src"+File.separator+"main"+ File.separator+"resources"+
        // File.separator;
        // 部署到服务器上的公私钥存放路径
         return keyFilePath;
    }

    public static void main(String[] args) {
        System.out.println(getResourceFile());
    }
}
