package com.gerp.attendance.config;

import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DelegationGenerator implements ValueGenerator<Integer> {



    @Override
    public Integer generateValue(Session session, Object o) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();
            Map<String, Object> data = account.getKeycloakSecurityContext().getToken().getOtherClaims();
            return data.get("delegatedId")!=null?(Integer)data.get("delegatedId"):null;
        }else
            return null;    }
}
