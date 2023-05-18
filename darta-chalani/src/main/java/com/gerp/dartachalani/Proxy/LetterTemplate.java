package com.gerp.dartachalani.Proxy;

import com.gerp.dartachalani.Proxy.fallback.LetterTemplateFallback;
import com.gerp.dartachalani.dto.GeneralTemplate;
import com.gerp.dartachalani.dto.template.TippaniDetail;
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
//@FeignClient(name = LetterTemplate.SERVICE_NAME, url = LetterTemplate.SERVICE_URL_DELEGATION)
//@FeignClient(name = LetterTemplate.SERVICE_NAME, url = LetterTemplate.SERVICE_URL_DELEGATION)
//@FeignClient(name = LetterTemplate.SERVICE_NAME,url = "localhost:8090")
@FeignClient(name = LetterTemplate.SERVICE_NAME, configuration = LetterTemplate.FeignFormConfiguration.class)
@RibbonClient(name = LetterTemplate.SERVICE_NAME)
public interface LetterTemplate {

    String SERVICE_NAME = "templating-service";
    String SERVICE_URL = "http://103.69.124.84:9090/template";
    String SERVICE_URL_DELEGATION = "http://103.69.127.118:9090/template";
    String SERVICE_URL_LOCAL = "http://192.168.40.205:9090/template";

    @PostMapping("/gerp/general-template")
    ResponseEntity<String> generalTemplate(@RequestBody GeneralTemplate generalTemplate, @RequestParam(name = "lan") String lan);

    @PostMapping("/gerp/tippani-template")
    ResponseEntity<String> tippaniTemplate(@RequestBody TippaniDetail tippaniDetail);

    @PostMapping("/gerp/tippani-header")
    ResponseEntity<String> tippaniHeader(@RequestBody TippaniDetail tippaniDetail);

    @PostMapping("/gerp/general-template/many-to")
    ResponseEntity<String> generalMultipleTemplate(@RequestBody GeneralTemplate generalTemplate, @RequestParam(name = "lan") String lan);

    @PostMapping("/gerp/ocr-template")
    ResponseEntity<String> ocTemplate(@RequestBody GeneralTemplate generalTemplate, @RequestParam(name = "lan") String lan);

    @PostMapping("/gerp/general-template-for-qr")
    ResponseEntity<String> generalTemplateForQr(@RequestBody GeneralTemplate generalTemplate, @RequestParam(name = "lan") String lan);

    @PostMapping("/gerp/general-template/many-to-for-qr")
    ResponseEntity<String> generalMultipleTemplateForQr(@RequestBody GeneralTemplate generalTemplate, @RequestParam(name = "lan") String lan);

    @RequiredArgsConstructor
    class FeignFormConfiguration {
        private final CircuitBreakerRegistry circuitBreakerRegistry;
        private final RetryRegistry retryRegistry;
        private final LetterTemplateFallback microserviceTwoFallback;

        @Bean
        public Feign.Builder feignBuilder(){
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(LetterTemplate.SERVICE_NAME);
            Retry retry = retryRegistry.retry(LetterTemplate.SERVICE_NAME);
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
