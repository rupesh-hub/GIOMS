//package com.gerp.attendance.config;
//
//import com.gerp.attendance.cache.TokenCacheRedisRepo;
//import com.gerp.shared.enums.ApprovalParamStatus;
//import com.gerp.shared.utils.StringConstants;
//import lombok.extern.slf4j.Slf4j;
//import org.keycloak.KeycloakPrincipal;
//import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.access.PermissionEvaluator;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.Serializable;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Configuration
//public class CustomPermissionEvaluator implements PermissionEvaluator {
//
//    @Autowired
//    private TokenCacheRedisRepo tokenCacheRedisRepo;
//
//    @Autowired
//    private HttpServletRequest httpServletRequest;
//
//    @Override
//    public boolean hasPermission(
//            Authentication auth, Object targetDomainObject, Object permission) {
//        if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)) {
//            return false;
//        }
//        String targetType = targetDomainObject.toString().toUpperCase();
//        return hasPrivilege(auth, targetType, permission.toString().toUpperCase());
//    }
//
//    @Override
//    public boolean hasPermission(
//            Authentication auth, Serializable targetId, String targetType, Object permission) {
//        if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
//            return false;
//        }
//        return hasPrivilege(auth, targetType.toUpperCase(),
//                permission.toString().toUpperCase());
//    }
//
//    public boolean hasPermissionStatus(String targetType, String targetType2, Object permission){
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if(permission.equals(ApprovalParamStatus.update.toString()))
//            return hasPrivilege(auth, targetType.toUpperCase(),
//                    permission.toString().toUpperCase());
//        else
//            return hasPrivilege(auth, targetType2.toUpperCase(),
//                    permission.toString().toUpperCase());
//    }
//
//    private boolean hasPrivilege(Authentication auth, String targetType, String permission) {
////        if(checkMobile(auth))
////            return true;
//        if (isAdmin(auth))
//            return true;
//        KeycloakPrincipal<?> principal =  (KeycloakPrincipal<?>)auth.getPrincipal();
//        Map<String, Object> otherClaims = principal.getKeycloakSecurityContext().getToken().getOtherClaims();
//        List<String> tokenCache = tokenCacheRedisRepo.findByUserName((String) otherClaims.get("username"));
////        List<String> tokenCache = tokenCacheRedisRepo.findByUserName(auth.getName());
//        if (tokenCache == null)
//            return false;
//        for (String authority : tokenCache) {
//            if (authority.split("#")[0].equals(targetType)) {
//                if (authority.contains(permission)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
////    private boolean checkMobile(Authentication auth) {
////        try {
////            SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();
////            if(!account.getKeycloakSecurityContext().getToken().getIssuedFor().equals("gerp-mobile")) {
////                return false;
////            }
////            log.info(">>--------------------mob---------------------<<");
////            return true;
////        }catch (Exception e){
////            return false;
////        }
////    }
//
//    public boolean hasTokenScope(String... scope) {
//        List<String> scopes = Arrays.asList(scope);
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        try {
//            SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();
//            for (String s : scopes) {
//                return account.getKeycloakSecurityContext().getToken().getScope().contains(s);
//            }
//            return false;
//        }catch (Exception e){
//            return false;
//        }
//    }
//
//    private boolean isAdmin(Authentication auth) {
////        TODO change later after user config finish
////        return true;
//        SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();
//        Map<String, Object> data = account.getKeycloakSecurityContext().getToken().getOtherClaims();
//        if(data.get("roles")!=null && !data.get("roles").toString().trim().equals(""))
//            return Arrays.asList(data.get("roles").toString().split(",")).stream().anyMatch(x -> x.equals(StringConstants.ADMIN_ROLE));
//        else
//            return false;
//    }
//}
