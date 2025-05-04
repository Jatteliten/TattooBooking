package com.example.tattooplatform.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AntiBotFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String userAgent = httpRequest.getHeader("User-Agent");
        if (userAgent != null) {
            String ua = userAgent.toLowerCase();
            if (ua.contains("bot") || ua.contains("crawler") || ua.contains("spider")) {
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpResponse.getWriter().write("Bots are not allowed.");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
