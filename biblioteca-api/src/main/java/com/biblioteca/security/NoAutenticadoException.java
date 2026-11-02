package com.biblioteca.security;

// No hay sesión, el token no es válido o venció. Se responde 401
public class NoAutenticadoException extends RuntimeException {

    public NoAutenticadoException(String mensaje) {
        super(mensaje);
    }
}
