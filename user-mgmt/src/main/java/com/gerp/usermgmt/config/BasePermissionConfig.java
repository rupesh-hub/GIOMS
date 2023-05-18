package com.gerp.usermgmt.config;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.usermgmt.cache.TokenCacheRedisRepo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Configuration
@Slf4j
public abstract class BasePermissionConfig {


    @Autowired
    private TokenCacheRedisRepo tokenCacheRedisRepo;

    @Autowired
    private CustomMessageSource customMessageSource;


    protected abstract void permissionEvaluator();
}
