package com.biblioteca.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.biblioteca.security.AutenticacionInterceptor;
import com.biblioteca.security.AutorizacionInterceptor;

// Registra la seguridad: primero se identifica al usuario y luego se revisa su rol
@Configuration
public class InterceptoresConfig implements WebMvcConfigurer {

    @Autowired
    private AutenticacionInterceptor autenticacionInterceptor;

    @Autowired
    private AutorizacionInterceptor autorizacionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autenticacionInterceptor).addPathPatterns("/api/**").order(1);
        registry.addInterceptor(autorizacionInterceptor).addPathPatterns("/api/**").order(2);
    }
}
