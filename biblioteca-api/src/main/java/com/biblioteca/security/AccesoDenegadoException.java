package com.biblioteca.security;

// Hay sesión, pero el rol no permite esta acción. Se responde 403
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException() {
        super("No tienes permiso para realizar esta acción");
    }
}
