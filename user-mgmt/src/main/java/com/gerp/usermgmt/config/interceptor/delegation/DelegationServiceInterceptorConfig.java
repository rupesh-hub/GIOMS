package com.gerp.usermgmt.config.interceptor.delegation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DelegationServiceInterceptorConfig implements WebMvcConfigurer {
    @Autowired
    private DelegationServiceInterceptor delegationServiceInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(delegationServiceInterceptor);
    }
}
