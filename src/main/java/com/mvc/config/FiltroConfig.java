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
        // instanciamos el filtro
        FilterRegistrationBean<Filter> registro = new FilterRegistrationBean<>();
        // Asigna el filtro personalizado JwtFilter para interceptar y procesar las peticiones HTTP
        registro.setFilter(new JwtFilter());
        // aplica a todo lo que empiece por /api/...
        registro.addUrlPatterns("/api/*"); 
        // prioridad del filtro
        registro.setOrder(1); 
        return registro;
    }
}
