package com.gerp.usermgmt.Proxy;

import com.gerp.usermgmt.Proxy.fallback.AttendanceServiceFallBack;
import com.gerp.usermgmt.Proxy.fallback.DartaServiceFallBack;
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
import java.util.List;

@Service
@FeignClient(name = DartaChalaniProxy.SERVICE_NAME,configuration = DartaChalaniProxy.FeignFormConfiguration.class)
@RibbonClient(DartaChalaniProxy.SERVICE_NAME)
public interface DartaChalaniProxy {
    String SERVICE_NAME="darta-chalani";


    @RequiredArgsConstructor
    class FeignFormConfiguration {
        private final CircuitBreakerRegistry circuitBreakerRegistry;
        private final RetryRegistry retryRegistry;
        private final DartaServiceFallBack dartaServiceFallBack;

        @Bean
        public Feign.Builder feignBuilder(){
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(DartaChalaniProxy.SERVICE_NAME);
            Retry retry = retryRegistry.retry(DartaChalaniProxy.SERVICE_NAME);
            FeignDecorators decorators = FeignDecorators.builder()
                    .withCircuitBreaker(circuitBreaker)
                    .withRetry(retry)
                    .withFallbackFactory(dartaServiceFallBack::injectException)
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

    @PostMapping("/dashboard/count/darta-chalani-tippani")
    ResponseEntity<?> getMasterDashboard(@RequestBody AttendanceKaajAndLeavePojo attendanceKaajAndLeavePojo);

    @GetMapping("/dashboard/master-dashboard")
    ResponseEntity<?> getMasterDasboardDarta(@RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate,
                                             @RequestParam(name = "limit") Integer limit,
                                             @RequestParam(name = "pageNo") Integer pageNo,
                                             @RequestParam(name = "officeList") String officeList,
                                             @RequestParam(name = "by", required = false) Integer by,
                                             @RequestParam(name = "type", required = false) String type);

    @GetMapping("/dashboard/master-dashboard-excel")
    ResponseEntity<?> getMasterDasboardDartaExcel(@RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate,
                                             @RequestParam(name = "officeList") String officeList,
                                                  @RequestParam("by") Integer by,
                                                  @RequestParam("type") String type);

    @GetMapping("/dashboard/master-dashboard-total")
    ResponseEntity<?> getMasterDasboardDartaTotal(@RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate,
                                             @RequestParam(name = "officeList") String officeList);

    @GetMapping("/received-letter/check/section")
    ResponseEntity<?> checkLetter(@RequestParam("sectionCode") String sectionCode);


}
