package com.gerp.sbm.Helper;

import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class HelperUtil {

    public static String getLoginEmployeeCode(){
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();
            if(account.getKeycloakSecurityContext().getToken().getOtherClaims().get("pisCode")!=null)
                return account.getKeycloakSecurityContext().getToken().getOtherClaims().get("pisCode").toString();
            else
                return null;
        } else {
            return null;
        }
    }
}
