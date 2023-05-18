package com.gerp.templating;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;

@SpringBootApplication
@EnableFeignClients
@ComponentScan({"com.gerp.shared.*","com.gerp.templating.*"})
@Import(DelegatingWebMvcConfiguration.class)
public class TemplatingApplication {
	
	 public static void main(String[] args) {
	        SpringApplication.run(TemplatingApplication.class, args);
	    }

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
