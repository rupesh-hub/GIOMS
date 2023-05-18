package com.gerp.dartachalani.config.auth;

import com.gerp.dartachalani.Proxy.UserMgmtServiceData;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.ApiMethod;
import com.gerp.shared.pojo.json.ApiDetail;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

@Aspect
@Configuration
@Slf4j
public class AuthorizationImpl implements PermissionEvaluator {
    @Autowired
    HttpServletRequest request;

    @Autowired
    private CustomMessageSource customMessageSource;

    @Autowired
    private UserMgmtServiceData userMgmtServiceData;


    @Before("@annotation(authorized)")
    private void permissionEvaluator(Authorized authorized) {
        ApiDetail apiDetail = new ApiDetail();
        apiDetail.setName(request.getRequestURI());
        apiDetail.setMethod(ApiMethod.valueOf(request.getMethod()));
        if (userMgmtServiceData.isUserAuthorized(apiDetail) == Boolean.FALSE) {
            throw new AccessDeniedException(customMessageSource.get("user.unauthorized.access"));
        }
    }

    @Override
    public boolean hasPermission(
            Authentication auth, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(
            Authentication auth, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}