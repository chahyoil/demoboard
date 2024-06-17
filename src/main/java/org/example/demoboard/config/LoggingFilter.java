package org.example.demoboard.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public class LoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    private Environment env;
    private final boolean IS_DEV;


    public LoggingFilter(Environment env) {
        this.IS_DEV = !List.of(env.getActiveProfiles()).contains("prod");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if(IS_DEV) {
            logger.info("Request IP: " + req.getRemoteAddr());
            logger.info("Request URI: " + req.getRequestURI());
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}