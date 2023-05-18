package com.gerp.usermgmt.Proxy;


import com.gerp.shared.pojo.Mail;
import com.gerp.usermgmt.Proxy.fallback.CommunicationServiceFallback;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = CommunicationServiceProxy.SERVICE_NAME,configuration = CommunicationServiceProxy.CommunicationServiceProxyConfiguration.class)
@RibbonClient(CommunicationServiceProxy.SERVICE_NAME)
public interface CommunicationServiceProxy {

    String SERVICE_NAME="communication-service";

    @PostMapping("/email/send-message")
    ResponseEntity<?> sendMessage(@RequestBody Mail<?> mail);

    @RequiredArgsConstructor
    class CommunicationServiceProxyConfiguration{
        private final CircuitBreakerRegistry circuitBreakerRegistry;
        private final RetryRegistry retryRegistry;
        private final CommunicationServiceFallback communicationServiceFallback;

        @Bean
        public Feign.Builder feignBuilder(){
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(CommunicationServiceProxy.SERVICE_NAME);
            Retry retry = retryRegistry.retry(CommunicationServiceProxy.SERVICE_NAME);
            FeignDecorators decorators = FeignDecorators.builder()
                    .withRetry(retry)
                    .withCircuitBreaker(circuitBreaker)
                    .withFallbackFactory(communicationServiceFallback::injectException)
                    .build();
            return Resilience4jFeign
                    .builder(decorators)
                    .encoder(new JacksonEncoder())
                    .decoder(new JacksonDecoder());
        }
    }

}
