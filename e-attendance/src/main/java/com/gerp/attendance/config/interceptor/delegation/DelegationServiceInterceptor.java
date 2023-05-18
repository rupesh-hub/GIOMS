package com.gerp.attendance.config.interceptor.delegation;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class DelegationServiceInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws AccessDeniedException {
        KeycloakAuthenticationToken keycloakAuthenticationToken = (KeycloakAuthenticationToken) request.getUserPrincipal();
        if(keycloakAuthenticationToken !=null) {
            KeycloakPrincipal<?> principal = (KeycloakPrincipal<?>) keycloakAuthenticationToken.getPrincipal();
            Map<String, Object> otherClaims = principal.getKeycloakSecurityContext().getToken().getOtherClaims();
            String expireDate = (String) otherClaims.get("expireDate");
            if (expireDate != null && LocalDateTime.parse(expireDate).isBefore(LocalDateTime.now())) {
                throw new AccessDeniedException("Access Denied");
            }
        }
        return true;
    }
}
