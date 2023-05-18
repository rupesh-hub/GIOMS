//package com.gerp.attendance.config.runner;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.keycloak.admin.client.Keycloak;
//import org.keycloak.admin.client.KeycloakBuilder;
//import org.keycloak.representations.idm.ClientRepresentation;
//import org.keycloak.representations.idm.CredentialRepresentation;
//import org.keycloak.representations.idm.RealmRepresentation;
//import org.keycloak.representations.idm.UserRepresentation;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class KeycloakInitializerRunner implements CommandLineRunner {
//
//    @Value("${keycloak.auth-server-url}")
//    private String keycloakServerUrl;
//    @Value("${keycloak.realm}")
//    private String realm;
//    @Value("${keycloak.resource}")
//    private String resource;
//
//    private final Keycloak keycloakAdmin;
//
//    @Override
//    public void run(String... args) throws Exception {
//        log.info("Initializing '{}' realm in Keycloak ...", realm);
//
//        Optional<RealmRepresentation> representationOptional = keycloakAdmin.realms().findAll().stream()
//                .filter(r -> r.getRealm().equals(realm)).findAny();
//        if (representationOptional.isPresent()) {
//            log.info("Removing already pre-configured '{}' realm", realm);
//            keycloakAdmin.realm(realm).remove();
//        }
//
//        // Realm
//        RealmRepresentation realmRepresentation = new RealmRepresentation();
//        realmRepresentation.setRealm(realm);
//        realmRepresentation.setEnabled(true);
//        realmRepresentation.setRegistrationAllowed(true);
//
//        // Client
//        ClientRepresentation clientRepresentation = new ClientRepresentation();
//        clientRepresentation.setClientId(resource);
//        clientRepresentation.setDirectAccessGrantsEnabled(true);
//        clientRepresentation.setDefaultRoles(new String[]{APP_ROLES.get(0)});
//        clientRepresentation.setPublicClient(true);
//        clientRepresentation.setRedirectUris(APP_REDIRECT_URLS);
//        realmRepresentation.setClients(Collections.singletonList(clientRepresentation));
//
//        // Users
//        List<UserRepresentation> userRepresentations = APP_USERS.stream().map(userPass -> {
//            // Client roles
//            Map<String, List<String>> clientRoles = new HashMap<>();
//            if ("admin".equals(userPass.getUsername())) {
//                clientRoles.put(resource, APP_ROLES);
//            } else {
//                clientRoles.put(resource, Collections.singletonList(APP_ROLES.get(0)));
//            }
//
//            // User Credentials
//            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
//            credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
//            credentialRepresentation.setValue(userPass.getPassword());
//
//            // User
//            UserRepresentation userRepresentation = new UserRepresentation();
//            userRepresentation.setUsername(userPass.getUsername());
//            userRepresentation.setEnabled(true);
//            userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
//            userRepresentation.setClientRoles(clientRoles);
//
//            return userRepresentation;
//        }).collect(Collectors.toList());
//        realmRepresentation.setUsers(userRepresentations);
//
//        // Create Realm
//        keycloakAdmin.realms().create(realmRepresentation);
//
//        // Testing
//        UserPass admin = APP_USERS.get(0);
//        log.info("Testing getting token for '{}' ...", admin.getUsername());
//
//        Keycloak keycloakMovieApp = KeycloakBuilder.builder().serverUrl(keycloakServerUrl)
//                .realm(realm).username(admin.getUsername()).password(admin.getPassword())
//                .clientId(resource).build();
//
//        log.info("'{}' token: {}", admin.getUsername(), keycloakMovieApp.tokenManager().grantToken().getToken());
//        log.info("'{}' initialization completed successfully!", realm);
//    }
//
//    public static final String USER = "USER";
//    public static final String ADMIN = "ADMIN";
//    private static final List<String> APP_ROLES = Arrays.asList(USER,ADMIN);
//    private static final List<String> APP_REDIRECT_URLS = Arrays.asList("http://localhost:3000/*","http://localhost:8082/*");
//    private static final List<UserPass> APP_USERS = Arrays.asList(new UserPass("admin", "test123"),
//            new UserPass("user", "test123"));
//
//    @Data
//    @AllArgsConstructor
//    private static class UserPass {
//        private String username;
//        private String password;
//    }
//
//}
