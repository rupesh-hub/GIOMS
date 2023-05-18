package com.gerp.usermgmt.config.runner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class KeycloakInitializerRunner implements CommandLineRunner {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;
    private final String REALM = "gerp-services" ;
    private List<String> resource;
    private final Keycloak keycloakAdmin;

    public KeycloakInitializerRunner(Keycloak keycloakAdmin){
        this.keycloakAdmin = keycloakAdmin;
        resource = Arrays.asList(
                "gerp-main",
                "gerp-mobile",
                "artifact-app",
                "django-test-app"
        );
    }

    @Override
    public void run(String... args) throws Exception {
//        log.info("Initializing '{}' realm in Keycloak ...", REALM);
//
//        // Realm
//        Optional<RealmRepresentation> representationOptional = keycloakAdmin.realms().findAll().stream()
//                .filter(r -> r.getRealm().equals(REALM)).findAny();
//        if (!representationOptional.isPresent()) {
//            RealmRepresentation realmRepresentation = new RealmRepresentation();
//            realmRepresentation.setRealm(REALM);
//            realmRepresentation.setSslRequired("none");
//            realmRepresentation.setEnabled(true);
//            keycloakAdmin.realms().create(realmRepresentation);
//        }
//        RealmResource realmResource = keycloakAdmin.realm(REALM);
//
//        log.info("Initializing clients in Keycloak ..."+realmResource);
//
//        // Client
//        for(String x:resource){
//            if(realmResource.clients().findByClientId(x)==null
//                    || realmResource.clients().findByClientId(x).isEmpty()){
//                ClientRepresentation clientRepresentation = new ClientRepresentation();
//                clientRepresentation.setClientId(x);
//                clientRepresentation.setSecret(x);
//                clientRepresentation.setDirectAccessGrantsEnabled(true);
//                clientRepresentation.setPublicClient(false);
//                clientRepresentation.setDefaultRoles(new String[]{APP_ROLES.get(0)});
//                clientRepresentation.setRedirectUris(APP_REDIRECT_URLS);
//                realmResource.clients().create(clientRepresentation);
//                // Users
//                for(UserPass userPass:APP_USERS){
//                    Map<String, List<String>> clientRoles = new HashMap<>();
//                    if ("admin".equals(userPass.getUsername())) {
//                        clientRoles.put(x, APP_ROLES);
//                    } else {
//                        clientRoles.put(x, Collections.singletonList(APP_ROLES.get(0)));
//                    }
//
//                    // User Credentials
//                    CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
//                    credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
//                    credentialRepresentation.setValue(userPass.getPassword());
//
//                    // User
//                    UserRepresentation userRepresentation = new UserRepresentation();
//                    userRepresentation.setUsername(userPass.getUsername());
//                    userRepresentation.setEnabled(true);
//                    userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
//                    userRepresentation.setClientRoles(clientRoles);
//                    realmResource.users().create(userRepresentation);
//                }
//            }
//        }
    }

    public static final String USER = "USER";
    public static final String ADMIN = "ADMIN";
    private static final List<String> APP_ROLES = Arrays.asList(USER);
    private static final List<String> APP_REDIRECT_URLS = Arrays.asList("http://localhost:3000/*","http://localhost:8082/*");
    private static final List<UserPass> APP_USERS = Arrays.asList(new UserPass("admin", "test123"),
            new UserPass("user", "test123"));

    @Data
    @AllArgsConstructor
    private static class UserPass {
        private String username;
        private String password;
    }

}
