//package com.gerp.dartachalani.config.interceptor.auth;
//
//import com.gerp.dartachalani.Proxy.UserMgmtServiceData;
//import com.gerp.dartachalani.token.TokenProcessorService;
//import com.gerp.shared.configuration.CustomMessageSource;
//import com.gerp.shared.enums.ApiMethod;
//import com.gerp.shared.exception.UnAuthenticateException;
//import com.gerp.shared.pojo.json.ApiDetail;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.HandlerMapping;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//@Component
//public class RequestAuthorizationInterceptorConfiguration implements HandlerInterceptor {
//
//    @Autowired
//    private TokenProcessorService tokenProcessorService;
//
//    @Autowired
//    private CustomMessageSource customMessageSource;
//
//    @Autowired
//    private UserMgmtServiceData userMgmtServiceData;
//
//    @Override
//    public boolean preHandle(
//            HttpServletRequest request, HttpServletResponse response, Object handler) {
//        ApiDetail apiDetail = new ApiDetail();
//        apiDetail.setMethod(ApiMethod.valueOf(request.getMethod().toUpperCase()));
//        apiDetail.setName((String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE));
//        if (Boolean.FALSE.equals(userMgmtServiceData.isUserAuthorized(apiDetail))) {
//            throw new UnAuthenticateException(customMessageSource.get("user.unauthorized.access"));
//        }
//        return true;
//    }
//
//    @Override
//    public void postHandle(
//            HttpServletRequest request, HttpServletResponse response, Object handler,
//            ModelAndView modelAndView) {
//
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
//                                Object handler, Exception exception) {
//
//    }
//}
