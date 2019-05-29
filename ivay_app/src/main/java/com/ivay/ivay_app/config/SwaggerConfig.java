package com.ivay.ivay_app.config;

import com.ivay.ivay_app.filter.TokenFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * swagger文档
 *
 * @author xx
 * <p>
 * 2017年7月21日
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${swagger.enbale}")
    private boolean swaggerEnable;

    @Bean
    public Docket docket() {
        ParameterBuilder token = new ParameterBuilder();
        token.parameterType("header").name(TokenFilter.TOKEN_KEY)
                .description("token")
                .required(false)
                .modelRef(new ModelRef("string")); // 在swagger里显示header

        ParameterBuilder userNo = new ParameterBuilder();
        userNo.parameterType("header").name("Accept-Language")
                .description("语言值")
                .required(false)
                .modelRef(new ModelRef("string")); // 在swagger里显示header

        List<Parameter> pars = new ArrayList<>();
        pars.add(token.build());
        pars.add(userNo.build());

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("swagger接口文档")
                .enable(swaggerEnable)
                .apiInfo(new ApiInfoBuilder()
                        .title("swagger接口文档")
                        .contact(new Contact("xx", "", "xxx@163.com"))
                        .version("1.0")
                        .build()
                )
                .globalOperationParameters(pars)
                .select()
                .paths(PathSelectors.regex("/star.*"))
                .build();
    }
}
