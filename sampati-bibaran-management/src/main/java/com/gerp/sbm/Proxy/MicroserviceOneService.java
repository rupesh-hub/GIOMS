package com.gerp.sbm.Proxy;

import com.gerp.sbm.Proxy.fallback.MicroserviceOneFallback;
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
import org.springframework.stereotype.Service;

@Service
@FeignClient(name = MicroserviceOneService.SERVICE_NAME, configuration = MicroserviceOneService.FeignFormConfiguration.class)
@RibbonClient(name = MicroserviceOneService.SERVICE_NAME)
public interface MicroserviceOneService {

    String SERVICE_NAME="microservice-one";

    @RequiredArgsConstructor
    class FeignFormConfiguration {
        private final CircuitBreakerRegistry circuitBreakerRegistry;
        private final RetryRegistry retryRegistry;
        private final MicroserviceOneFallback microserviceOneFallback;

        @Bean
        public Feign.Builder feignBuilder(){
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(MicroserviceOneService.SERVICE_NAME);
            Retry retry = retryRegistry.retry(MicroserviceOneService.SERVICE_NAME);
            FeignDecorators decorators = FeignDecorators.builder()
                    .withCircuitBreaker(circuitBreaker)
                    .withRetry(retry)
                    .withFallbackFactory(microserviceOneFallback::injectException)
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
