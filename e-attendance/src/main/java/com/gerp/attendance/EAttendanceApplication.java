package com.gerp.attendance;

import com.gerp.attendance.config.StorageProperties;
import com.google.common.net.HttpHeaders;
import org.modelmapper.ModelMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;

import java.util.Random;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@ComponentScan({"com.gerp.shared", "com.gerp.attendance"})
@EntityScan({"com.gerp.attendance.model"})
@MapperScan({"com.gerp.attendance.mapper"})
@EnableConfigurationProperties(StorageProperties.class)
@Import(DelegatingWebMvcConfiguration.class)
@EnableCaching
public class EAttendanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EAttendanceApplication.class, args);
    }


    @Bean
    public RestTemplate getRestTemplate(){
      return new RestTemplate();
    }


    @Bean
    public WebClient getWebClient() {

        WebClient webClient2 = WebClient.builder()
                .baseUrl("http://192.168.50.130:2012/attlog")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        return webClient2;

    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper;
    }

    @Bean
    public Random random() {
        return new Random();
    }

}
