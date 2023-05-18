package com.gerp.attendance.Proxy;


import com.gerp.attendance.Pojo.DigitalSignatureDto;
import com.gerp.attendance.Proxy.fallback.UserMgmtFallback;
import com.gerp.shared.pojo.GlobalApiResponse;
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
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name= DigitalSignatureServiceProxy.SERVICE_NAME, url = DigitalSignatureServiceProxy.SERVICE_URL)
public interface DigitalSignatureServiceProxy {
    String SERVICE_NAME="darta-chalanii";
    String SERVICE_URL = "http://103.69.124.84:9090/darta-chalani/o";

    @PostMapping("/signature/verify")
    ResponseEntity<GlobalApiResponse> verifySignature(@RequestBody DigitalSignatureDto digitalSignatureDto);

   @PostMapping("/generate/hash")
    ResponseEntity<GlobalApiResponse> generateHasValue(@RequestBody DigitalSignatureDto digitalSignatureDto);

    @RequiredArgsConstructor
    class FeignFormConfiguration {
        private final CircuitBreakerRegistry circuitBreakerRegistry;
        private final RetryRegistry retryRegistry;
        private final UserMgmtFallback microserviceTwoFallback;

        @Bean
        public Feign.Builder feignBuilder(){
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(DartaChalaniServiceProxy.SERVICE_NAME);
            Retry retry = retryRegistry.retry(DartaChalaniServiceProxy.SERVICE_NAME);
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
