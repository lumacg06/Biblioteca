package com.biblioteca.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200", "http://localhost:5173") // URLs de los front a conectar
                .allowedMethods("*") // permitir todos los métodos (GET, POST, PUT, DELETE...)
                .allowedHeaders("*") // permitir todos los headers
                .allowCredentials(true) // permitir las credenciales
                .maxAge(3600); // tiempo de caducidad en segundos
    }
}
