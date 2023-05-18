package com.gerp.attendance.controller;

import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.exception.ServiceValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * Created by Bibash Bogati on 23-04-2021.
 */
@RestController
@RequestMapping("/cache")
public class CacheController {

    @Autowired
    private CustomMessageSource customMessageSource;

    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private CacheManager cacheManager;      // autowire cache manager
    // clear all cache using cache manager
    @GetMapping(value = "clear")
    public String clearCache(){
        if(!tokenProcessorService.isAdmin()){
            throw new AccessDeniedException(customMessageSource.get("user.unauthorized.access"));
        }
        try {
            for(String name:cacheManager.getCacheNames()){
                cacheManager.getCache(name).clear();            // clear cache by name
            }
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ServiceValidationException(ex.getMessage());
        }
        return "Success";
    }
}