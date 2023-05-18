package com.gerp.attendance.Proxy;

import com.gerp.attendance.Proxy.fallback.UserMgmtFallback;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.shared.pojo.json.ApiDetail;
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
import org.springframework.web.bind.annotation.*;

@Service
//@FeignClient(name = "usermgmt", url = "http://103.69.124.56:9090/usermgmt")
@FeignClient(name = "usermgmt", configuration = UserMgmtProxy.FeignFormConfiguration.class)
@RibbonClient(name = UserMgmtProxy.SERVICE_NAME)
public interface UserMgmtProxy {

    String SERVICE_NAME="usermgmt";

    @GetMapping("/fiscal-year/get-active-year-pojo")
    ResponseEntity<?> getActiveFiscalYearPojo();

    @GetMapping("/employee/employee-detail")
    ResponseEntity<GlobalApiResponse> getEmployeeDetail(@RequestParam(name = "pisCode") String pisCode);

    @GetMapping("/employee/office-head")
    ResponseEntity<GlobalApiResponse> getOfficeHead(@RequestParam(name = "officeCode") String officeCode);

    // cache
    @GetMapping("/employee/employee-detail-minimal/{pisCode}")
    ResponseEntity<GlobalApiResponse> getEmployeeDetailMinimal(@PathVariable(name = "pisCode") String pisCode);

    @GetMapping("/employee/section-employee-list")
    ResponseEntity<GlobalApiResponse> getSectionEmployee(@RequestParam(name = "id") Long id);

    @GetMapping("/office/detail")
    ResponseEntity<GlobalApiResponse> getOfficeDetail(@RequestParam(name = "officeCode") String officeCode);

    @GetMapping("/employee/lower-hierarchy-ids")
    ResponseEntity<GlobalApiResponse> getLowerHierchyPisCode();

    @GetMapping("/office/office-higher-hierarchy-list")
    ResponseEntity<GlobalApiResponse> getHierarchyOffice(@RequestParam(name = "officeCode") String officeCode);

    @GetMapping("/section/section-list")
    ResponseEntity<GlobalApiResponse> getSectionList();

    @PostMapping("/users/authorize")
    ResponseEntity<GlobalApiResponse> isUserAuthorized(@RequestBody ApiDetail apiDetail);


    @RequiredArgsConstructor
    class FeignFormConfiguration {
        private final CircuitBreakerRegistry circuitBreakerRegistry;
        private final RetryRegistry retryRegistry;
        private final UserMgmtFallback microserviceTwoFallback;

        @Bean
        public Feign.Builder feignBuilder(){
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(UserMgmtProxy.SERVICE_NAME);
            Retry retry = retryRegistry.retry(UserMgmtProxy.SERVICE_NAME);
            FeignDecorators decorators = FeignDecorators.builder()
                    .withCircuitBreaker(circuitBreaker)
                    .withRetry(retry)
                    .withFallbackFactory(microserviceTwoFallback::injectException)
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
}
