package com.biblioteca.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.biblioteca.models.UsuarioModel;
import com.biblioteca.services.SesionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Paso 1 de la seguridad: ¿quién eres? Lee el token y deja el usuario en la petición
@Component
public class AutenticacionInterceptor implements HandlerInterceptor {

    // Nombre con el que los controllers reciben al usuario: @RequestAttribute(USUARIO_ACTUAL)
    public static final String USUARIO_ACTUAL = "usuarioActual";

    private static final String PREFIJO_TOKEN = "Bearer ";

    @Autowired
    private SesionService sesionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Las peticiones previas de CORS (OPTIONS) no llegan a un controller, por eso no son HandlerMethod
        if (!(handler instanceof HandlerMethod metodo) || esPublico(metodo)) {
            return true;
        }
        UsuarioModel usuario = sesionService.validar(extraerToken(request));
        request.setAttribute(USUARIO_ACTUAL, usuario);
        return true;
    }

    // Header esperado: Authorization: Bearer <token>
    public static String extraerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(PREFIJO_TOKEN)) {
            return null;
        }
        return header.substring(PREFIJO_TOKEN.length()).trim();
    }

    private boolean esPublico(HandlerMethod metodo) {
        return metodo.hasMethodAnnotation(Publico.class) || metodo.getBeanType().isAnnotationPresent(Publico.class);
    }
}
