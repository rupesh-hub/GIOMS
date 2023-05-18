//package com.gerp.communication.config;
//
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//
//import java.util.Properties;
//
//@Configuration
//public class EmailConfig {
//
//    @RefreshScope
//    @Bean
//    public JavaMailSender getJavaMailSender(){
//        try{
//            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//            mailSender.setHost("");
//            mailSender.setPort(1);
//            mailSender.setPassword("");
//            mailSender.setUsername("");
//            Properties props = mailSender.getJavaMailProperties();
//            props.put("","");
//            return mailSender;
//        }catch(Exception ex){
//            return new JavaMailSenderImpl();
//        }
//    }
//}
