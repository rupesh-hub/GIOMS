package com.gerp.dartachalani.config;

import com.gerp.dartachalani.cache.TokenCacheRedisRepo;
import com.gerp.shared.utils.StringConstants;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private TokenCacheRedisRepo tokenCacheRedisRepo;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Override
    public boolean hasPermission(
            Authentication auth, Object targetDomainObject, Object permission) {
        if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)) {
            return false;
        }
        String targetType = targetDomainObject.toString().toUpperCase();
        return hasPrivilege(auth, targetType, permission.toString().toUpperCase());
    }

    @Override
    public boolean hasPermission(
            Authentication auth, Serializable targetId, String targetType, Object permission) {
        if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
            return false;
        }
        return hasPrivilege(auth, targetType.toUpperCase(),
                permission.toString().toUpperCase());
    }

    private boolean hasPrivilege(Authentication auth, String targetType, String permission) {

        if (isAdmin(auth))
            return true;

        KeycloakPrincipal<?> principal =  (KeycloakPrincipal<?>)auth.getPrincipal();
        Map<String, Object> otherClaims = principal.getKeycloakSecurityContext().getToken().getOtherClaims();

        List<String> tokenCache = tokenCacheRedisRepo.findByUserName((String) otherClaims.get("username"));
        if (tokenCache == null)
            return false;
//        for (GrantedAuthority grantedAuth : tokenCache.getAuthorities()) {
//            System.out.println(grantedAuth.getAuthority());
//            if (grantedAuth.getAuthority().split("#")[0].equals(targetType)) {
//                String grantedAuthority = grantedAuth.getAuthority();
//                if (grantedAuthority.contains(permission)) {
//                    return true;
//                }
//            }
//        }
        for (String authority : tokenCache) {
            if (authority.split("#")[0].equals(targetType)) {
                if (authority.contains(permission)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasTokenScope(String... scope) {
        List<String> scopes = Arrays.asList(scope);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();
            for (String s : scopes) {
                return account.getKeycloakSecurityContext().getToken().getScope().contains(s);
            }
            return false;
        }catch (Exception e){
            return false;
        }
    }

    private boolean isAdmin(Authentication auth) {
//        TODO change later after user config finish
//        return true;
        SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();
        Map<String, Object> data = account.getKeycloakSecurityContext().getToken().getOtherClaims();
        if(data.get("roles")!=null && !data.get("roles").toString().trim().equals(""))
            return Arrays.asList(data.get("roles").toString().split(",")).stream().anyMatch(x -> x.equals(StringConstants.ADMIN_ROLE));
        else
            return false;
    }
}