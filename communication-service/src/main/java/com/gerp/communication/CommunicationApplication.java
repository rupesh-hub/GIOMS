package com.gerp.communication;

import com.gerp.communication.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;

@SpringBootApplication
@EnableFeignClients
@ComponentScan({"com.gerp.shared.*","com.gerp.communication.*"})
@EnableAsync
@EnableConfigurationProperties(StorageProperties.class)
@Import(DelegatingWebMvcConfiguration.class)
public class CommunicationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunicationApplication.class, args);
    }

}
