package com.gerp.usermgmt.config.interceptor.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
public class AuditServiceInterceptorConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private AuditServiceInterceptor auditServiceInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditServiceInterceptor);
    }

}
