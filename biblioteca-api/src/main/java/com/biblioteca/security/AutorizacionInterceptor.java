package com.biblioteca.security;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.biblioteca.models.UsuarioModel;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Paso 2 de la seguridad: ¿puedes hacer esto? Corre después de AutenticacionInterceptor
@Component
public class AutorizacionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod metodo) || !requiereAdmin(metodo)) {
            return true;
        }
        UsuarioModel usuario = (UsuarioModel) request.getAttribute(AutenticacionInterceptor.USUARIO_ACTUAL);
        if (usuario == null || !usuario.esAdmin()) {
            throw new AccesoDenegadoException();
        }
        return true;
    }

    private boolean requiereAdmin(HandlerMethod metodo) {
        return metodo.hasMethodAnnotation(SoloAdmin.class) || metodo.getBeanType().isAnnotationPresent(SoloAdmin.class);
    }
}
