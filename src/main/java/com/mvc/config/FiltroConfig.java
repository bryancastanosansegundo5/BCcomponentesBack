package com.mvc.config;

import com.mvc.security.JwtFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FiltroConfig {

    @Bean
    public FilterRegistrationBean<Filter> jwtFilter() {
        FilterRegistrationBean<Filter> registro = new FilterRegistrationBean<>();
        registro.setFilter(new JwtFilter());
        registro.addUrlPatterns("/api/*"); // aplica solo a /api/...
        registro.setOrder(1); // prioridad del filtro
        return registro;
    }
}
