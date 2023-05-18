package com.gerp.attendance.Proxy;

import com.gerp.attendance.Pojo.ReportTemplate;
import com.gerp.attendance.Proxy.fallback.TemplateServiceFallback;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@Service
//@FeignClient(name = LetterTemplate.SERVICE_NAME, url = LetterTemplate.SERVICE_URL)
    @FeignClient(name = TemplateServiceProxy.SERVICE_NAME, configuration = TemplateServiceProxy.FeignFormConfiguration.class)
    @RibbonClient(name = TemplateServiceProxy.SERVICE_NAME)
public interface TemplateServiceProxy {

        String SERVICE_NAME = "templating-service";
        String SERVICE_URL = "http://103.69.124.84:9090/template";

        @PostMapping("/gerp/report-template")
        ResponseEntity<String> generalTemplate(@RequestBody ReportTemplate generalTemplate);




        @RequiredArgsConstructor
        class FeignFormConfiguration {
            private final CircuitBreakerRegistry circuitBreakerRegistry;
            private final RetryRegistry retryRegistry;
            private final TemplateServiceFallback microserviceTwoFallback;

            @Bean
            public Feign.Builder feignBuilder(){
                CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(TemplateServiceProxy.SERVICE_NAME);
                Retry retry = retryRegistry.retry(TemplateServiceProxy.SERVICE_NAME);
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

