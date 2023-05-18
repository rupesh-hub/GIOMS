package custom.keycloak;

import custom.keycloak.auth.support.EmbeddedKeycloakConfig;
import custom.keycloak.auth.support.KeycloakCustomProperties;
import custom.keycloak.auth.support.KeycloakProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
@EnableConfigurationProperties({KeycloakProperties.class, KeycloakCustomProperties.class})
@ComponentScan(basePackageClasses = EmbeddedKeycloakConfig.class,basePackages = {"custom.keycloak.*"})
@EntityScan({"custom.keycloak.model"})
public class CustomMain {
    public static void main(String[] args) {

        SpringApplication.run(CustomMain.class, args);
    }
}
