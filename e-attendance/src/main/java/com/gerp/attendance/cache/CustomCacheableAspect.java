//package com.gerp.attendance.cache;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.After;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.HashOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Map;
//
//@Aspect
//@Component
//public class CustomCacheableAspect {
//
//    RedisTemplate<String, List<String>> redisTemplate;
//
//    @Pointcut("@annotation(customCacheable)")
//    public void customCacheablePointcut(CustomCacheable customCacheable) {}
//
//    @Around("@annotation(customCacheable)")
//    public Object cacheableMethod(ProceedingJoinPoint joinPoint, CustomCacheable customCacheable) throws Throwable {
//
//        HashOperations h  = redisTemplate.opsForHash();
//        Map<String, Object> data  = (Map<String, Object>) h.get(customCacheable.hashKey(), customCacheable.key());
//        if(data != null && data.get("") != null) {
//            return data;
//        } else {
//            return joinPoint.proceed();
//        }
//    }
//
//    @After("@annotation(customCacheable)")
//}
//
