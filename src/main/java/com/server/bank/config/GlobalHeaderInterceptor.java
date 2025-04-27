package com.server.bank.config;

import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class GlobalHeaderInterceptor implements HandlerInterceptor {

    @Value("${custom.header.name}")
    private String headerName;

    @Value("${custom.header.value}")
    private String headerValue;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String providedKey = request.getHeader(headerName);

        if (!headerValue.equals(providedKey)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Unauthorized: Invalid API Key");
            return false;
        }

        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            String methodName = method.getMethod().getName();
            String beanName = method.getBean().getClass().getSimpleName();
            System.out.println("API Key valid. Handling request with " + beanName + "." + methodName);
        }
        return true;
    }
}
