package com.ivay.ivay_app.config;

import com.ivay.ivay_common.table.PageTableArgumentResolver;
import com.ivay.ivay_common.utils.StringUtil;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 跨域支持
     *
     * @return
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedMethods("*");
            }
        };
    }

    /**
     * datatable分页解析
     *
     * @return
     */
    @Bean
    public PageTableArgumentResolver tableHandlerMethodArgumentResolver() {
        return new PageTableArgumentResolver();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(tableHandlerMethodArgumentResolver());
    }

    /**
     * 上传文件根路径
     */
    @Value("${files.path}")
    private String filesPath;

    /**
     * 外部文件访问
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/statics/**")
                .addResourceLocations(ResourceUtils.FILE_URL_PREFIX + filesPath + File.separator);
    }

    @Autowired
    private MessageSource messageSource;

    @Override
    public Validator getValidator() {
        return validator();
    }

    /**
     * 参数校验的国际化配置
     *
     * @return
     */
    @Bean
    public Validator validator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource);
        validator.setMessageInterpolator(new MessageInterpolator());
        return validator;
    }

    private class MessageInterpolator extends ResourceBundleMessageInterpolator {
        @Override
        public String interpolate(String message, Context context, Locale locale) {
            // 如果注解上指定的message不是默认的javax.validation或者org.hibernate.validator等开头的情况，
            // 即修改自定义校验消息
            if (!message.startsWith("javax.validation") && !message.startsWith("org.hibernate.validator.constraints")) {
                message = messageSource.getMessage(message, null, locale);

                Map<String, Object> attributes = context.getConstraintDescriptor().getAttributes();
                // 可以用{min},{max}等方式动态配置提示信息
                message = StringUtil.replaceStringInParentheses(message, attributes);

                // 获取注解类型
                Annotation annotation = context.getConstraintDescriptor().getAnnotation();
                String annotationTypeName = annotation.annotationType().getSimpleName();
                if (!"Password".equals(annotationTypeName)) {
                    System.out.println("1");
                }
            }
            return super.interpolate(message, context, locale);
        }
    }
}
