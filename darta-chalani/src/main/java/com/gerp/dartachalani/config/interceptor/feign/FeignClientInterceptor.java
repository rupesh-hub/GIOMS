package com.gerp.dartachalani.config.interceptor.feign;

import com.gerp.shared.constants.ModuleType;
import com.gerp.dartachalani.constant.PermissionConstants;
import com.gerp.shared.constants.OperationConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String APPLICATION_TYPE = "ATTENDANCE";
    private static final String TOKEN_TYPE = "Bearer";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof SimpleKeycloakAccount) {
            SimpleKeycloakAccount account = (SimpleKeycloakAccount) authentication.getDetails();
            requestTemplate.header(AUTHORIZATION_HEADER, String.format("%s %s", TOKEN_TYPE, account.getKeycloakSecurityContext().getTokenString()));
            requestTemplate.header(OperationConstants.MODULE_SERVICE_TYPE, ModuleType.DARTA_CHALANI);
    }
    }
}