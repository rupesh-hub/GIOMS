package com.gerp.tms.config.auditor;

import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfiguration {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAware<String>() {
            @Override
            public Optional<String> getCurrentAuditor() {
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();
                    if(account.getKeycloakSecurityContext().getToken().getOtherClaims().get("pisCode")!=null)
                        return Optional.of(account.getKeycloakSecurityContext().getToken().getOtherClaims().get("pisCode").toString());
                    else
                        return Optional.empty();
                } else {
                    return Optional.empty();
                }
            }
        };
    }
}
