package com.gerp.kasamu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;

@SpringBootApplication
@EnableFeignClients
@ComponentScan({"com.gerp.shared.*", "com.gerp.kasamu.*"})
@EntityScan({"com.gerp.kasamu.model"})
@Import(DelegatingWebMvcConfiguration.class)
public class KasamuApplication {

    public static void main(String[] args) {
        SpringApplication.run(KasamuApplication.class, args);
    }

}
