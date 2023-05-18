package com.gerp.tms.service;


import com.gerp.tms.pojo.document.DocumentMasterResponsePojo;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Service
@FeignClient(name = DmsService.SERVICE_NAME, url = DmsService.SERVICE_URL, configuration = DmsService.FeignFormConfiguration.class)
@RibbonClient(name = DmsService.SERVICE_NAME)
public interface DmsService {

    String SERVICE_NAME="document-service";
//    String SERVICE_URL="http://103.69.124.84:9094";
    String SERVICE_URL="${dms.ribbon.listOfServers}";

    @PostMapping(value = "/api/document_master")
    @Headers("Content-Type: multipart/form-data")
    ResponseEntity<DocumentMasterResponsePojo> create(@ModelAttribute MultiValueMap<String, Object> docPojo);

    @PostMapping(value = "/api/document_version")
    @Headers("Content-Type: multipart/form-data")
    ResponseEntity<DocumentMasterResponsePojo> update(@ModelAttribute MultiValueMap<String, Object> docPojo);

    @RequiredArgsConstructor
    class FeignFormConfiguration {
        private final CircuitBreakerRegistry circuitBreakerRegistry;
        private final RetryRegistry retryRegistry;
        private final DocumentServiceFallback documentServiceFallback;

        @Bean
        public Feign.Builder feignBuilder(){
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(DmsService.SERVICE_NAME);
            Retry retry = retryRegistry.retry(DmsService.SERVICE_NAME);
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
