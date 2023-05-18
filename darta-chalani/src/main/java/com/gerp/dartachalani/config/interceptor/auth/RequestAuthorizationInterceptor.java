package com.gerp.dartachalani.config.interceptor.auth;

import com.gerp.dartachalani.cache.TokenCacheRedisRepo;
import com.gerp.dartachalani.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.constants.ModuleType;
import com.gerp.shared.constants.OperationConstants;
import com.gerp.shared.exception.UnAuthenticateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

@Component
@Slf4j
public class RequestAuthorizationInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private CustomMessageSource customMessageSource;

    @Autowired
    private TokenCacheRedisRepo tokenCacheRedisRepo;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        log.info("calling api "+ uri);
        if(tokenProcessorService.isSuperAdmin() || isValidHeaders(request) || authorizeSubClients(request)) {
            return true;
        }
        if (Boolean.FALSE.equals(isAuthorized(uri,
                tokenProcessorService.getUserId(), request.getMethod().toUpperCase()))) {
            throw new UnAuthenticateException(customMessageSource.get("user.unauthorized.access"));
        }
        return true;
    }
    private boolean isValidHeaders(HttpServletRequest request) {
        String header = request.getHeader(OperationConstants.MODULE_SERVICE_TYPE);
        if (header == null)
            return false;
        else
            return header.equals(ModuleType.DARTA_CHALANI) || header.equals(ModuleType.TEMPLATING_SERVICE) || header.equals(ModuleType.EATTENDANCE)
                    || header.equals(ModuleType.USERMGMT_SERVICE);
    }

    private Boolean isAuthorized(String uri,  Long userId,  String method) {
//        String value = "#".concat(userId.toString()).concat("#").concat(uri).concat("#").concat(method);
//        List<String> datas= tokenCacheRedisRepo.findByUserName(userId.toString());
//        if(datas == null) {
//            throw new RoleException("Role Changed");
//        }
//       return datas.contains(value);
        return true;
    }

    private boolean authorizeSubClients(HttpServletRequest request) {
        String clientId = tokenProcessorService.getClientId();
        if(clientId != null && clientId.equals("QR-APP")) {
            return qrClientValidator(request);
        } else {
            return false;
        }
    }

    private boolean qrClientValidator(HttpServletRequest request) {
        String uri = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if(Pattern.matches("^/dispatch-view-log/.*", uri)){
            return true;
        } else {
            throw new UnAuthenticateException(customMessageSource.get("client.unauthorized.access"));
        }
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) {

    }
}
