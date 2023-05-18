package com.gerp.communication.config.interceptor.audit;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Component
public class AuditServiceInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String currentLanguage = request.getHeader("accept-language");
        if (currentLanguage == null) {
            Locale.setDefault(Locale.ENGLISH);
        } else {
            if (currentLanguage.contains("np")) {
                Locale.setDefault(new Locale("np", "NEPAL"));
            } else {
                Locale.setDefault(Locale.ENGLISH);

            }
        }
        return true;
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

    //TODO NEED TO ADD IN OTHER SERVICE ALSO
}
