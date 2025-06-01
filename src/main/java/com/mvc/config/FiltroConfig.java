package com.mvc.config;

import com.mvc.security.JwtFilter;
import com.mvc.security.JwtUtil;

import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FiltroConfig {

    @Bean
    public FilterRegistrationBean<Filter> jwtFilter(JwtUtil jwtUtil) {
        FilterRegistrationBean<Filter> registro = new FilterRegistrationBean<>();
        registro.setFilter(new JwtFilter(jwtUtil)); // ✅ le pasas el JwtUtil inyectado por Spring
        registro.addUrlPatterns("/api/*");
        registro.setOrder(1);
        return registro;
    }

}
