package com.elit.outildedecision.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestInterceptor extends HandlerInterceptorAdapter {
    Logger log = LoggerFactory.getLogger(RequestInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        throw new ResourceNotFoundException();
        log.info("Request URL: " + request.getRequestURL());
        log.info("Request URI: " + request.getRequestURI());
        log.info("Response Status: " + response.getStatus());

        return super.preHandle(request, response, handler);
    }
}
