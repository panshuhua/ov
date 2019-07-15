package com.ivay.ivay_manage.config;

import com.ivay.ivay_manage.filter.TokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * spring security配置
 *
 * @author xx
 * <p>
 * 2017年10月16日
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)  // 启用方法级别的权限认证
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private TokenFilter tokenFilter;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // csrf: 如果不携带token直接拒绝访问
        http.csrf().disable();

        // 基于token，所以不需要session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .antMatchers(
                        "/", "/*.html", "/favicon.ico", "/css/**", "/js/**", "/fonts/**", "/layui/**", "/img/**",
                        "/v2/api-docs/**", "/swagger-resources/**", "/webjars/**", "/pages/**", "/druid/**",
                        "/statics/**", "/test/**", "/actuator/**"
                        , "/manage/**", "/table/**", "/audit/**"
                ).permitAll() // 允许所有用户访问
                .anyRequest().authenticated();  // 其他访问地址需要验证权限

        // 登录界面
        http.formLogin().loginProcessingUrl("/login")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler).and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);

        // 注销界面
        http.logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler);
        // 解决不允许显示在iframe的问题
        http.headers().frameOptions().disable();
        http.headers().cacheControl();

        http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 根据用户名加载特定的用户信息
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

}
