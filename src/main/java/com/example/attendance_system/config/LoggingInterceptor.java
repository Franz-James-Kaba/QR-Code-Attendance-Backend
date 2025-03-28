package com.example.attendance_system.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Slf4j
@Component
public class LoggingInterceptor implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Log request details
        log.info("Request: {} {} [{}]",
                httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                sdf.format(new Date()));

        // Continue with the request
        long startTime = System.currentTimeMillis();
        filterChain.doFilter(servletRequest, servletResponse);
        long duration = System.currentTimeMillis() - startTime;

        // Log response details
        log.info("Response: {} {} - {} ms",
                httpResponse.getStatus(),
                httpRequest.getRequestURI(),
                duration);
    }

}
