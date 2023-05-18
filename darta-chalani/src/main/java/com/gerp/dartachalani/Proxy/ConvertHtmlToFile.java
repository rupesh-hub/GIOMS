package com.gerp.dartachalani.Proxy;

import com.gerp.dartachalani.Proxy.fallback.ConvertHtmlToFileFallBack;
import com.gerp.dartachalani.dto.FileConverterPojo;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "ConvertHtmlToFile",url = "http://103.69.127.118:9090/socket")
//@FeignClient(name = "ConvertHtmlToFile",url = "http://103.69.124.84:9090/socket")
//@FeignClient(name = ConvertHtmlToFile.SERVICE_NAME,configuration = ConvertHtmlToFile.FeignFormConfiguration.class)
@RibbonClient(name = ConvertHtmlToFile.SERVICE_NAME)
public interface ConvertHtmlToFile {

    String SERVICE_NAME = "messaging-service";

    @PostMapping("/file-convert")
    ResponseEntity<byte[]> convertToFile(@RequestBody FileConverterPojo fileConverterPojo);
    @RequiredArgsConstructor
     class FeignFormConfiguration {
        private final CircuitBreakerRegistry circuitBreakerRegistry;
        private final RetryRegistry retryRegistry;
        private final ConvertHtmlToFileFallBack convertHtmlToFileFallBack;

        @Bean
        public Feign.Builder feignBuilder() {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(convertHtmlToFileFallBack.SERVICE_NAME);
            Retry retry = retryRegistry.retry(convertHtmlToFileFallBack.SERVICE_NAME);
            FeignDecorators decorators = FeignDecorators.builder()
                    .withCircuitBreaker(circuitBreaker)
                    .withRetry(retry)
                    .withFallbackFactory(convertHtmlToFileFallBack::injectException)
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
