package com.mvc.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    Argon2PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(
                16, // Tamaño del salt aleatorio en bytes
                32, // Tamaño del hash generado (en bytes)
                1, // Número de hilos paralelos usados durante el hashing
                4096, // Memoria usada por operación
                3);// Número de veces que se repite el algoritmo
    }
}
