package com.gerp.usermgmt.Proxy;

import com.gerp.usermgmt.Proxy.fallback.MessagingServiceFallBack;
import com.gerp.usermgmt.pojo.transfer.FileConverterPojo;
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

//@FeignClient(name = "TemplateServiceProxy",url = "localhost:8090")
@FeignClient(name = MessagingServiceProxy.SERVICE_NAME,configuration = MessagingServiceProxy.FeignFormConfiguration.class)
@RibbonClient(MessagingServiceProxy.SERVICE_NAME)
public interface MessagingServiceProxy {
    String SERVICE_NAME="messaging-service";

//    @PostMapping("/template/gerp/saruwa-template")
//    ResponseEntity<String> getSaruwaLetterTemplate(@RequestBody SaruwaLetterPojo saruwaLetterPojo);
//
//    @PostMapping("/template/gerp/saruwa-request-detail-template")
//    ResponseEntity<String> getSaruwaRequestTemplate(@RequestBody SaruwaRequestPojo saruwaRequestPojo);
//
//    @PostMapping("/template/gerp/ramana-template")
//    ResponseEntity<String> getRawanaTemplate(@RequestBody RawanaTemplate rawanaTemplate);

    @PostMapping("/file-convert")
    ResponseEntity<byte[]> convertToFile(@RequestBody FileConverterPojo fileConverterPojo);

//    @GetMapping("/attendance/dashboard/date-range")
//    ResponseEntity<GlobalApiResponse> getAttendance(@RequestParam(name = "from") String from, @RequestParam(name = "to") String to);

    @RequiredArgsConstructor
    class FeignFormConfiguration {
        private final CircuitBreakerRegistry circuitBreakerRegistry;
        private final RetryRegistry retryRegistry;
        private final MessagingServiceFallBack messagingServiceFallBack;

        @Bean
        public Feign.Builder feignBuilder(){
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(MessagingServiceProxy.SERVICE_NAME);
            Retry retry = retryRegistry.retry(MessagingServiceProxy.SERVICE_NAME);
            FeignDecorators decorators = FeignDecorators.builder()
                    .withCircuitBreaker(circuitBreaker)
                    .withRetry(retry)
                    .withFallbackFactory(messagingServiceFallBack::injectException)
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
