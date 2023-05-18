package com.gerp.dartachalani.Proxy;

import com.gerp.dartachalani.Proxy.fallback.UserMgmtFallback;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.shared.pojo.SalutationPojo;
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

import java.util.List;

@Service
//@FeignClient(name = "usermgmt", url = "http://103.69.127.118:9090/usermgmt")
//@FeignClient(name = "usermgmt", url = "http://103.69.124.84:9090/usermgmt")
//@FeignClient(name = "usermgmt", url = "http://103.69.124.56:9090/usermgmt")
@FeignClient(name = "usermgmt", configuration = UserMgmtProxy.FeignFormConfiguration.class)
@RibbonClient(name = UserMgmtProxy.SERVICE_NAME)
public interface UserMgmtProxy {

    String SERVICE_NAME = "usermgmt";

    @GetMapping("/fiscal-year/get-active-year-pojo")
    ResponseEntity<?> getActiveFiscalYearPojo();

    @GetMapping("/fiscal-year/get-active-year")
    ResponseEntity<?> getFiscalActiveYear();

    @GetMapping("/fiscal-year/detail-by-code")
    ResponseEntity<?> getFiscalDetailByFiscalYearCode(@RequestParam(name = "fiscalYearCode") String fiscalYearCode);

    @GetMapping("/employee/employee-detail")
    ResponseEntity<GlobalApiResponse> getEmployeeDetail(@RequestParam(name = "pisCode") String pisCode);

    @GetMapping("/employee/office-head")
    ResponseEntity<GlobalApiResponse> getOfficeHead(@RequestParam(name = "officeCode") String officeCode);

    //cache
    @GetMapping("/employee/employee-detail-minimal/{pisCode}")
    ResponseEntity<GlobalApiResponse> getEmployeeDetailMinimal(@PathVariable(name = "pisCode") String pisCode);


    // cache
    @GetMapping("/office/detail")
    ResponseEntity<GlobalApiResponse> getOfficeDetail(@RequestParam(name = "officeCode") String officeCode);

    // cache
    @GetMapping("/office/minimal-detail")
    ResponseEntity<GlobalApiResponse> getOfficeDetailMinimal(@RequestParam(name = "officeCode") String officeCode);

    @PostMapping("/office/office-status")
    ResponseEntity<GlobalApiResponse> getActiveOfficeStatus(@RequestBody List<String> officeCodes);

    //cache
    @GetMapping("/section/{sectionId}")
    ResponseEntity<GlobalApiResponse> getSectionBySectionId(@PathVariable(name = "sectionId") Long sectionId);

    // cache
    @GetMapping("/designation/{designationCode}")
    ResponseEntity<GlobalApiResponse> getDesignationByDesignationCode(@PathVariable(name = "designationCode") String designationCode);

    //cache
    @GetMapping("/designation/by-code/{code}")
    ResponseEntity<GlobalApiResponse> getDesignationByCode(@PathVariable(name = "code") String code);

    // cache
    @GetMapping("/position/{positionCode}")
    ResponseEntity<GlobalApiResponse> getPositionByPositionCode(@PathVariable(name = "positionCode") String positionCode);

    //cache
    @GetMapping("/service-group/{serviceGroup}")
    ResponseEntity<GlobalApiResponse> getServiceGroupByCode(@PathVariable(name = "serviceGroup") String serviceGroup);

    @GetMapping("/employee/section-employee-list")
    ResponseEntity<GlobalApiResponse> getSectionEmployeeList(@RequestParam(name = "id") Long id);

    @GetMapping("/transfer-authority/date-range")
    ResponseEntity<GlobalApiResponse> getDateRangeList(@RequestParam(name = "currentFiscalYear") int year, @RequestParam("currentDate") boolean currentDate);

    @GetMapping("/transfer-authority/year-date-range")
    ResponseEntity<GlobalApiResponse> getDateRangeListForYear(@RequestParam(name = "currentFiscalYear") int year);

    //cache
    @GetMapping("delegation/{id}")
    ResponseEntity<GlobalApiResponse> getDelegationDetailsById(@PathVariable(name = "id") Integer id);

    @PostMapping("/salutation-details")
    ResponseEntity<GlobalApiResponse> getSalutationDetail(@RequestBody List<SalutationPojo> salutationPojos);

    // cache
    @GetMapping("/employee-work-log/{pisCode}")
    ResponseEntity<GlobalApiResponse> getPreviousEmployeeDetail(@PathVariable(name = "pisCode") String pisCode, @RequestParam(name = "sectionId") Long sectionId);

    //cache
    @GetMapping("/office-template/get-active-by-office")
    ResponseEntity<GlobalApiResponse> getOfficeTemplate(@RequestParam(name = "officeCode") String officeCode, @RequestParam(name = "type") String type);

    @GetMapping("/office-template/{id}")
    ResponseEntity<GlobalApiResponse> getOfficeTemplateById(@PathVariable("id") Long id);

    @GetMapping("/office-group/{id}")
    ResponseEntity<GlobalApiResponse> getOfficeGroupById(@PathVariable("id") Integer id);

    @PostMapping("/users/authorize")
    ResponseEntity<GlobalApiResponse> isUserAuthorized(@RequestBody ApiDetail apiDetail);

    @RequiredArgsConstructor
    class FeignFormConfiguration {
        private final CircuitBreakerRegistry circuitBreakerRegistry;
        private final RetryRegistry retryRegistry;
        private final UserMgmtFallback microserviceTwoFallback;

        @Bean
        public Feign.Builder feignBuilder() {
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
