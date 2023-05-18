package com.gerp.sbm;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;

@SpringBootApplication
@EnableFeignClients
@ComponentScan({"com.gerp.shared.*", "com.gerp.sbm.*"})
@EntityScan({"com.gerp.sbm.model"})
@Import(DelegatingWebMvcConfiguration.class)
public class SampatiBibaranManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampatiBibaranManagementApplication.class, args);
    }
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
