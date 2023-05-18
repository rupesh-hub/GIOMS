package com.gerp.attendance.config.interceptor.auth;

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
                .excludePathPatterns("/**/cache/clear")
                .excludePathPatterns("/**/error")
                .excludePathPatterns("/swagger-ui.html")
                .excludePathPatterns("/actuator/prometheus")
                .excludePathPatterns("/swagger-ui/**")
                .excludePathPatterns("/v3/api-docs")
                .excludePathPatterns("/v3/api-docs/**")
                .excludePathPatterns("/employee-attendance/real-time-update")
                .excludePathPatterns("/employee-attendance/re-init")
                .excludePathPatterns("/employee-attendance/holidayUpdated")
                .excludePathPatterns("/remaining-leave/schedular/**")
                .excludePathPatterns("/forget-password/**");
         }
}
