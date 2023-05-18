//package com.gerp.shared.cache;
//
//import com.google.common.cache.CacheBuilder;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.concurrent.ConcurrentMapCache;
//import org.springframework.cache.support.SimpleCacheManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Arrays;
//import java.util.concurrent.TimeUnit;
//
//@Configuration
//@EnableCaching
//public class CachingConfig {
//
//    @Bean
//    public CacheManager cacheManager() {
//        SimpleCacheManager cacheManager = new SimpleCacheManager();
//        cacheManager
//                .setCaches(Arrays.asList(
////                          new ConcurrentMapCache("guid"),
//                        new ConcurrentMapCache(
//                                "filePath",
//                                CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS).build().asMap(),
//                                false
//                        ),
//                        new ConcurrentMapCache(
//                                "activeFiscalYear",
//                                CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS)
//                                        .build().asMap(),
//                                false
//                        ),
//                        new ConcurrentMapCache(
//                                "employeeMinimal",
//                                CacheBuilder.newBuilder().expireAfterWrite(600, TimeUnit.SECONDS)
//                                        .build().asMap(),
//                                true
//                        ),
//                        new ConcurrentMapCache(
//                                "officeDetail",
//                                CacheBuilder.newBuilder().expireAfterWrite(600, TimeUnit.SECONDS)
//                                        .build().asMap(),
//                                true
//                        ),
//                        new ConcurrentMapCache(
//                                "positionCodeNode",
//                                CacheBuilder.newBuilder().expireAfterWrite(600, TimeUnit.SECONDS)
//                                        .build().asMap(),
//                                true
//                        ),
//                        new ConcurrentMapCache(
//                                "positionCodeNodeSelf",
//                                CacheBuilder.newBuilder().expireAfterWrite(600, TimeUnit.SECONDS)
//                                        .build().asMap(),
//                                true
//                        ),
//                        new ConcurrentMapCache(
//                                "positionCodeNodeSelf",
//                                CacheBuilder.newBuilder().expireAfterWrite(600, TimeUnit.SECONDS)
//                                        .build().asMap(),
//                                true
//                        ),
//                        new ConcurrentMapCache(
//                                "parentOfficeCodeSelf",
//                                CacheBuilder.newBuilder().expireAfterWrite(600, TimeUnit.SECONDS)
//                                        .build().asMap(),
//                                true
//                        ),
//                        new ConcurrentMapCache(
//                                "sariwaTemplate",
//                                CacheBuilder.newBuilder().expireAfterWrite(600, TimeUnit.SECONDS)
//                                        .build().asMap(),
//                                true
//                        ),
//                        new ConcurrentMapCache(
//                                "convertToFile",
//                                CacheBuilder.newBuilder().expireAfterWrite(600, TimeUnit.SECONDS)
//                                        .build().asMap(),
//                                true
//                        ),
//                        new ConcurrentMapCache(
//                                "attendance",
//                                CacheBuilder.newBuilder().expireAfterWrite(600, TimeUnit.SECONDS)
//                                        .build().asMap(),
//                                true
//                        ),
//                        new ConcurrentMapCache(
//                                "officeParentDetail",
//                                CacheBuilder.newBuilder().expireAfterWrite(600, TimeUnit.SECONDS)
//                                        .build().asMap(),
//                                true
//                        ),new ConcurrentMapCache(
//                                "fiscalYear",
//                                CacheBuilder.newBuilder().expireAfterWrite(600, TimeUnit.SECONDS)
//                                        .build().asMap(),
//                                true
//                        ),new ConcurrentMapCache(
//                                "KaajAndHoliday",
//                                CacheBuilder.newBuilder().expireAfterWrite(600, TimeUnit.SECONDS)
//                                        .build().asMap(),
//                                true
//                        ),new ConcurrentMapCache(
//                                "fiscalYearDetails",
//                                CacheBuilder.newBuilder().expireAfterWrite(600, TimeUnit.SECONDS)
//                                        .build().asMap(),
//                                true
//                        ),new ConcurrentMapCache(
//                                "dateRangeList",
//                                CacheBuilder.newBuilder().expireAfterWrite(600, TimeUnit.SECONDS)
//                                        .build().asMap(),
//                                true
//                        ),new ConcurrentMapCache(
//                                "dateRangeListOfYear",
//                                CacheBuilder.newBuilder().expireAfterWrite(600, TimeUnit.SECONDS)
//                                        .build().asMap(),
//                                true
//                        ),new ConcurrentMapCache(
//                                "nepaliCalenderDetail",
//                                CacheBuilder.newBuilder().expireAfterWrite(600, TimeUnit.SECONDS)
//                                        .build().asMap(),
//                                true
//                        )
//                        )
//                );
//        return cacheManager;
//    }
//}
