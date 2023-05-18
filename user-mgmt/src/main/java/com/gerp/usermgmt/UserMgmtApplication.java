package com.gerp.usermgmt;

import org.modelmapper.ModelMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableFeignClients
@ComponentScan({"com.gerp.shared.*", "com.gerp.usermgmt.*","com.gerp.usermgmt.pisconfig.schedule"})
@EntityScan({"com.gerp.usermgmt.model","com.gerp.usermgmt.pisconfig.model"})
@MapperScan({"com.gerp.usermgmt.mapper"})
@EnableScheduling
@Import(DelegatingWebMvcConfiguration.class)
public class UserMgmtApplication {



    public static void main(String[] args) {
        SpringApplication.run(UserMgmtApplication.class, args);
    }



    @Bean
    public ModelMapper mapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        return new ModelMapper();
    }

    @PostConstruct
    public void init(){
        // Setting Spring Boot SetTimeZone
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kathmandu"));
        System.out.println("timezone setting    -------------------> " +TimeZone.getDefault());
    }
}
