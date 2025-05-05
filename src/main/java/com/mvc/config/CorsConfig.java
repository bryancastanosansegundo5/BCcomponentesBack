package com.mvc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
  // Esto seria para poner en properties el URL de mi app desde application.properties
  // @Value("${app.cors.origin}")
  // private String corsOrigin;

  // @Override
  // public void addCorsMappings(CorsRegistry registry) {
  //     registry.addMapping("/**")
  //             .allowedOrigins(corsOrigin)
  //             .allowedMethods("GET", "POST", "PUT", "DELETE")
  //             .allowCredentials(true);
  // }
  
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOrigins("http://localhost:5173")
      .allowedMethods("GET", "POST", "PUT", "DELETE")
      .allowCredentials(true);
  }
}