package com.gerp.usermgmt.config.auditor;

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
    public AuditorAware<Long> auditorProvider() {
        return new AuditorAware<Long>() {
            @Override
            public Optional<Long> getCurrentAuditor() {
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();
                    if(account.getKeycloakSecurityContext().getToken().getOtherClaims().get("userId")!=null)
                        return Optional.of(Long.valueOf(account.getKeycloakSecurityContext().getToken().getOtherClaims().get("userId").toString()));
                    else
                        return Optional.empty();
                } else {
                    return Optional.empty();
                }
            }
        };
    }
}
