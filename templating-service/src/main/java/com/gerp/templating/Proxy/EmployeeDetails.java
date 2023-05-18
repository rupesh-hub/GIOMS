package com.gerp.templating.Proxy;

import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.templating.Proxy.fallback.UserMgmtFallback;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

//@FeignClient(name = "EmployeeDetail",url = "http://103.69.124.84:9090/usermgmt")
//@FeignClient(name = "EmployeeDetail",url = "http://103.69.127.118:9090/usermgmt")
//@FeignClient(name = "EmployeeDetail",url = "http://localhost:9001")
@FeignClient(name = "usermgmt", configuration = EmployeeDetails.FeignFormConfiguration.class)
@RibbonClient(EmployeeDetails.SERVICE_NAME)
public interface EmployeeDetails {
    String SERVICE_NAME="usermgmt";
//    @GetMapping("/employee/employee-detail-minimal/{pisCode}")
//    ResponseEntity<GlobalApiResponse> getEmployeeDetailMinimal(@PathVariable(name = "pisCode") String pisCode);

    @GetMapping("/employee/employee-detail")
    ResponseEntity<GlobalApiResponse> getEmployeeDetailMinimal(@RequestParam(name = "pisCode") String pisCode);

    @GetMapping("/office/detail?officeCode={pisCode}")
    ResponseEntity<GlobalApiResponse> getOfficeDetail(@PathVariable(name = "pisCode") String pisCode);

    @GetMapping("/employee/office-employee-list")
    ResponseEntity<GlobalApiResponse> getEmployeeListOfLoggedInOffice();
    @GetMapping("/fiscal-year/get-active-year")
    ResponseEntity<GlobalApiResponse> getActiveFiscalYear();

    @RequiredArgsConstructor
    class FeignFormConfiguration {
        private final CircuitBreakerRegistry circuitBreakerRegistry;
        private final RetryRegistry retryRegistry;
        private final UserMgmtFallback microserviceTwoFallback;

        @Bean
        public Feign.Builder feignBuilder(){
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(EmployeeDetails.SERVICE_NAME);
            Retry retry = retryRegistry.retry(EmployeeDetails.SERVICE_NAME);
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
