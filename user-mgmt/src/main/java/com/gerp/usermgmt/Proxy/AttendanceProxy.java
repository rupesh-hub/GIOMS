package com.gerp.usermgmt.Proxy;


import com.gerp.usermgmt.Proxy.fallback.AttendanceServiceFallBack;
import com.gerp.usermgmt.pojo.MasterDashboardPojo.AttendanceKaajAndLeavePojo;
import feign.Feign;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Service
@FeignClient(name = AttendanceProxy.SERVICE_NAME,configuration = AttendanceProxy.FeignFormConfiguration.class)
@RibbonClient(AttendanceProxy.SERVICE_NAME)
public interface AttendanceProxy {
    String SERVICE_NAME="attendance";


    @RequiredArgsConstructor
    class FeignFormConfiguration {
        private final CircuitBreakerRegistry circuitBreakerRegistry;
        private final RetryRegistry retryRegistry;
        private final AttendanceServiceFallBack attendanceServiceFallBack;

        @Bean
        public Feign.Builder feignBuilder(){
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(AttendanceProxy.SERVICE_NAME);
            Retry retry = retryRegistry.retry(AttendanceProxy.SERVICE_NAME);
            FeignDecorators decorators = FeignDecorators.builder()
                    .withCircuitBreaker(circuitBreaker)
                    .withRetry(retry)
                    .withFallbackFactory(attendanceServiceFallBack::injectException)
                    .build();
            return Resilience4jFeign
                    .builder(decorators)
                    .encoder(new JacksonEncoder())
                    .decoder(new JacksonDecoder());
        }

        @Bean
        Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> converters) {
            return new SpringFormEncoder(new SpringEncoder(converters));
        }
    }

    @GetMapping("/transfer/attendance-transfer")
    ResponseEntity<?> transferValidate(@RequestParam("pisCode") String pisCode, @RequestParam("targetOfficeCode") String targetOfficeCode);

    @GetMapping("/dashboard/get-master-dashboard")
    ResponseEntity<?> filterWithLeaveKaaj(@RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate,
                                          @RequestParam(name = "limit") Integer limit,
                                          @RequestParam(name = "pageNo") Integer pageNo,
                                          @RequestParam(name = "by") Integer by,
                                          @RequestParam(name = "type") String type);

    @GetMapping("/dashboard/get-master-dashboard-excel")
    ResponseEntity<?> filterWithLeaveKaajExcel(@RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate,
                                          @RequestParam(name = "by") Integer by,
                                          @RequestParam(name = "type") String type);

    @GetMapping("/dashboard/get-master-dashboard-total")
    ResponseEntity<?> getMasterDashboardTotal(@RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate,
                                          @RequestParam(name = "officeList") String officeList);


    @GetMapping("/transfer/attendance-transfer-approve")
    ResponseEntity<?> transferEmployeeApprove(@RequestParam("pisCode") String pisCode, @RequestParam("targetOfficeCode") String targetOfficeCode);

    @PostMapping("/dashboard/count/get-kaaj-leave")
    ResponseEntity<?> getTotalByOfficeCode(@RequestBody AttendanceKaajAndLeavePojo attendanceKaajAndLeavePojo);

    @GetMapping("/remaining-leave/karar-employee")
    ResponseEntity<?> resetLeave(@RequestParam("pisCode") String pisCode, @RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate);


}
