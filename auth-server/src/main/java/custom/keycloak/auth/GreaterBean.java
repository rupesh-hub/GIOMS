package custom.keycloak.auth;

import custom.keycloak.auth.userspi.DefaultPasswordEncoderFactories;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GreaterBean {

    public void greet(String username) {
        log.info("Hello {}", username);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return DefaultPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
