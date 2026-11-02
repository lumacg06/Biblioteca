package com.biblioteca.dto;

import com.biblioteca.models.UsuarioModel;

// El front guarda el token y lo envía en cada petición: Authorization: Bearer <token>
public record LoginResponse(String token, UsuarioModel usuario) {
}
