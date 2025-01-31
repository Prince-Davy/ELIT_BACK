package com.elit.outildedecision.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * FIXME : extended class is deprecated, need to review relevance / alternatives.
 */
public class RequestInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestInterceptor.class);

    /**
     * This method is called before the actual handler is executed. It logs the request URL, URI,
     * and the initial response status. If there is an exception, it could be propagated to spring
     * framework.
     *
     * @param request  The HttpServletRequest which is being processed
     * @param response The HttpServletResponse which is being created
     * @param handler  Chosen handler to execute, for type and/or instance evaluation
     * @return true if the execution chain should proceed with the next interceptor or the handler
     *         itself, else DispatcherServlet assumes that this interceptor has already dealt with
     *         the response itself
     * @throws Exception in case of errors
     */
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {

      // Micro optimization
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Request URL: {}", request.getRequestURL());
        LOGGER.debug("Request URI: {}", request.getRequestURI());
        LOGGER.debug("Response Status: {}", response.getStatus());
      }

      return super.preHandle(request, response, handler);
    }
}
