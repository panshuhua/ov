package com.ivay.ivay_app.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Druid数据源配置
 * 2018.05.24改为配置的方式,配置前缀spring.datasource.druid，看下application.yml
 *
 * @author xx
 */
@Deprecated
//@Configuration
public class DruidConfig {
    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    /**
     * 数据源配置
     */
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druid() {
        return new DruidDataSource();
    }

    @Bean
    public ServletRegistrationBean<StatViewServlet> statViewServlet() {
        log.info("init Druid Servlet Configuration ");
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(
                new StatViewServlet(), "/druid/*");
        Map<String, String> params = new HashMap<>();
        params.put("loginUsername", "admin");
        params.put("loginPassword", "admin");

        // IP白名单
        params.put("allow", "");
        // IP黑名单(共同存在时，deny优先于allow)
//        params.put("deny", "");
//		// 是否能够重置数据 禁用HTML页面上的“Reset All”功能
//		params.put("resetEnable", "false");
        bean.setInitParameters(params);
        return bean;
    }

    // 配置web監控的filter
    @Bean
    public FilterRegistrationBean<WebStatFilter> webStatFilter() {
        FilterRegistrationBean<WebStatFilter> bean = new FilterRegistrationBean<>(new WebStatFilter());
        bean.setFilter(new WebStatFilter());
        Map<String, String> params = new HashMap<>();
        params.put("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        bean.setInitParameters(params);
        bean.setUrlPatterns(Arrays.asList("/*"));
        return bean;
    }
}
