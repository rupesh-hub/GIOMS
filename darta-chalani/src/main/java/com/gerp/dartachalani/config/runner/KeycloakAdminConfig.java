package com.gerp.dartachalani.config.runner;//package com.gerp.attendance.config.runner;
//
//import org.keycloak.admin.client.Keycloak;
//import org.keycloak.admin.client.KeycloakBuilder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class KeycloakAdminConfig {
//
//    @Value("${keycloak.auth-server-url}")
//    private String keycloakServerUrl;
////    @Value("${keycloak.admin-user}")
////    private String user;
////    @Value("${keycloak.admin-password}")
////    private String password;
//
//    @Bean
//    Keycloak keycloakAdmin() {
//        return KeycloakBuilder.builder()
//                .serverUrl(keycloakServerUrl)
//                .realm("master")
//                .username("admin")
//                .password("admin")
//                .clientId("admin-cli")
//                .build();
//    }
//
//}
