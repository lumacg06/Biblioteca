package com.biblioteca.dto;

import com.biblioteca.models.TipoUsuario;

// Datos para crear una cuenta (registro público o creación por el admin)
public record RegistroRequest(String nombre, String documento, String contrasena, TipoUsuario tipo) {
}
