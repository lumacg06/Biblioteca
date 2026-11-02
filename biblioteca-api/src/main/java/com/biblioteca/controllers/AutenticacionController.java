package com.biblioteca.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.dto.LoginRequest;
import com.biblioteca.dto.LoginResponse;
import com.biblioteca.dto.RegistroRequest;
import com.biblioteca.models.UsuarioModel;
import com.biblioteca.security.AutenticacionInterceptor;
import com.biblioteca.security.Publico;
import com.biblioteca.services.AutenticacionService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AutenticacionController {

    @Autowired
    private AutenticacionService autenticacionService;

    // Crear cuenta de ESTUDIANTE o DOCENTE
    @Publico
    @PostMapping("/registro")
    public ResponseEntity<UsuarioModel> registrar(@RequestBody RegistroRequest datos) {
        UsuarioModel usuario = autenticacionService.registrar(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    // Iniciar sesión con documento y contraseña
    @Publico
    @PostMapping("/login")
    public LoginResponse iniciarSesion(@RequestBody LoginRequest datos) {
        return autenticacionService.iniciarSesion(datos);
    }

    // Cerrar la sesión actual
    @PostMapping("/logout")
    public ResponseEntity<Void> cerrarSesion(HttpServletRequest request) {
        autenticacionService.cerrarSesion(AutenticacionInterceptor.extraerToken(request));
        return ResponseEntity.noContent().build();
    }
}
