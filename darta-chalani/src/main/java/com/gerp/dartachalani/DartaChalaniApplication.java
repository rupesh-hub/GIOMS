package com.gerp.dartachalani;

import com.gerp.dartachalani.config.StorageProperties;
import org.modelmapper.ModelMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;

@SpringBootApplication
@EnableFeignClients
@ComponentScan({"com.gerp.shared", "com.gerp.dartachalani.*"})
@EntityScan({"com.gerp.dartachalani.model"})
@MapperScan({"com.gerp.dartachalani.mapper"})
@EnableConfigurationProperties(StorageProperties.class)
@Import(DelegatingWebMvcConfiguration.class)
public class DartaChalaniApplication {

    public static void main(String[] args) {

        SpringApplication.run(DartaChalaniApplication.class, args);

    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.gerp.dartachalani.service.digitalSignature.client.gen");
        return marshaller;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
