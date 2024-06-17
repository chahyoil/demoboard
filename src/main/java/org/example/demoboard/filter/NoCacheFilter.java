package org.example.demoboard.filter;

import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Order(1)
@Component
public class NoCacheFilter implements Filter {

    private Environment env;
    private final boolean IS_DEV;

    public NoCacheFilter(Environment env) {
        this.env = env;
        this.IS_DEV = !Arrays.asList(env.getActiveProfiles()).contains("prod");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if(IS_DEV) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            httpResponse.setHeader("Pragma", "no-cache");
            httpResponse.setHeader("Expires", "0");
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }


    @Override
    public void destroy() {
        // Cleanup code, if needed
    }
}