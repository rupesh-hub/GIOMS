package com.gerp.dartachalani.Proxy;

import com.gerp.dartachalani.Proxy.fallback.CalenderServiceFallBack;
import com.gerp.dartachalani.Proxy.fallback.DocumentServiceFallback;
import com.gerp.dartachalani.dto.document.DocumentMasterResponsePojo;
import com.gerp.dartachalani.dto.document.DocumentSystemPojo;
import com.gerp.dartachalani.dto.document.TemporaryDocumentResponsePojo;
import com.gerp.dartachalani.dto.nepalDate.DateApiResponse;
import feign.Feign;
import feign.Headers;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Service
//@FeignClient(name = CalenderService.SERVICE_NAME, url = "http://103.69.124.84:9090/calendar")
//@FeignClient(name = CalenderService.SERVICE_NAME, url = "http://103.69.127.118:9090/calendar")
@FeignClient(name = CalenderService.SERVICE_NAME, configuration = CalenderService.FeignFormConfiguration.class)
@RibbonClient(name = CalenderService.SERVICE_NAME)
public interface CalenderService {

    String SERVICE_NAME = "GIOMSCALENDAR";
//    String SERVICE_URL = "http://103.69.124.84:9095";



    @GetMapping("/api/v1/calendar/{year}/{month}/")
    ResponseEntity<DateApiResponse> getNepaliDateDetails(@PathVariable(name = "month") int month, @PathVariable("year") String year);


    @RequiredArgsConstructor
    class FeignFormConfiguration {
        private final CircuitBreakerRegistry circuitBreakerRegistry;
        private final RetryRegistry retryRegistry;
        private final CalenderServiceFallBack documentServiceFallback;

        @Bean
        public Feign.Builder feignBuilder() {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(CalenderService.SERVICE_NAME);
            Retry retry = retryRegistry.retry(CalenderService.SERVICE_NAME);
            FeignDecorators decorators = FeignDecorators.builder()
                    .withCircuitBreaker(circuitBreaker)
                    .withRetry(retry)
                    .withFallbackFactory(documentServiceFallback::injectException)
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
