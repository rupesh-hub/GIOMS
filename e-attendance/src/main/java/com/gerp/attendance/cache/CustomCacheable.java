//package com.gerp.attendance.cache;
//
//import org.springframework.cache.annotation.Cacheable;
//
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//
//@Target({ElementType.TYPE, ElementType.METHOD})
//@Retention(RetentionPolicy.RUNTIME)
//@Cacheable
//public @interface CustomCacheable {
//
//    /**
//     * The name of the cache where the results should be stored.
//     * @return the cache name
//     */
//    String value() default "";
//
//    String key() default "";
//
//    String hashKey() default "";
//
//    /**
//     * Spring Expression Language (SpEL) expression used for making the method caching conditional.
//     * The method is cached only if the expression evaluates to {@code true}.
//     * @return the SpEL expression
//     */
//    String condition() default "";
//
//    /**
//     * Spring Expression Language (SpEL) expression used to veto method caching.
//     * The method is not cached if the expression evaluates to {@code true}.
//     * @return the SpEL expression
//     */
//    String unless() default "";
//
//    /**
//     * Whether or not all the entries inside the cache(s) should be removed before the method is called.
//     * @return {@code true} if the cache(s) should be cleared before the method is called, {@code false} otherwise
//     */
//    boolean clearBeforeInvocation() default false;
//}
