package com.gerp.templating.config.interceptor.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
public class RequestAuthorizationInterceptorConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private RequestAuthorizationInterceptor requestAuthorizationInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(requestAuthorizationInterceptor)
                    .excludePathPatterns("/**/users/authorize")
            .excludePathPatterns("/**/auth-map/**")
            .excludePathPatterns("/users/get-user-info")
            .excludePathPatterns("/permission-managements/screen-group-wise/privilege-list")
            .excludePathPatterns("/section-designation/all-by-employee")
            .excludePathPatterns("/delegation/list")
            .excludePathPatterns("/users/check-password")
            .excludePathPatterns("/screen-group-wise/privilege-list")
            .excludePathPatterns("/office/set-up-status")
            .excludePathPatterns("/fiscal-year/with-inactive")
            .excludePathPatterns("/fiscal-year")
            .excludePathPatterns("/employee/current-employee")
                    .excludePathPatterns("/swagger-ui.html")
                    .excludePathPatterns("/actuator/prometheus")
                    .excludePathPatterns("/swagger-ui/**")
                    .excludePathPatterns("/v3/api-docs")
                    .excludePathPatterns("/v3/api-docs/**")
                    .excludePathPatterns("/forget-password/**")
                    .excludePathPatterns("/**/error");
    }
}
