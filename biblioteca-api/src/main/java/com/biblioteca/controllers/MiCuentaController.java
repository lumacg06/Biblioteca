package com.biblioteca.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.models.MultaModel;
import com.biblioteca.models.PrestamoModel;
import com.biblioteca.models.UsuarioModel;
import com.biblioteca.security.AutenticacionInterceptor;
import com.biblioteca.services.MultaService;
import com.biblioteca.services.PrestamoService;

// Datos del usuario que inició sesión. El id sale del token, nunca de la URL:
// así un estudiante no puede ver los préstamos de otro cambiando un número
@RestController
@RequestMapping("/api/mi-cuenta")
public class MiCuentaController {

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private MultaService multaService;

    @GetMapping
    public UsuarioModel miPerfil(@RequestAttribute(AutenticacionInterceptor.USUARIO_ACTUAL) UsuarioModel usuario) {
        return usuario;
    }

    @GetMapping("/prestamos")
    public List<PrestamoModel> misPrestamos(
            @RequestAttribute(AutenticacionInterceptor.USUARIO_ACTUAL) UsuarioModel usuario) {
        return prestamoService.obtenerPorUsuario(usuario.getId());
    }

    @GetMapping("/multas")
    public List<MultaModel> misMultas(@RequestAttribute(AutenticacionInterceptor.USUARIO_ACTUAL) UsuarioModel usuario) {
        return multaService.obtenerPorUsuario(usuario.getId());
    }
}
