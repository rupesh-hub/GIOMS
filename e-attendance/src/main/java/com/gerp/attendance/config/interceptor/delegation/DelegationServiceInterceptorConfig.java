package com.gerp.attendance.config.interceptor.delegation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class DelegationServiceInterceptorConfig implements WebMvcConfigurer {
    @Autowired
    private DelegationServiceInterceptor delegationServiceInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(delegationServiceInterceptor)
                .excludePathPatterns("/swagger-ui.html")
                .excludePathPatterns("/actuator/prometheus")
                .excludePathPatterns("/swagger-ui/**")
                .excludePathPatterns("/v3/api-docs")
                .excludePathPatterns("/v3/api-docs/**")
                .excludePathPatterns("/forget-password/**");;
    }
}
