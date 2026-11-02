package com.biblioteca.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.dto.RegistroRequest;
import com.biblioteca.models.MultaModel;
import com.biblioteca.models.PrestamoModel;
import com.biblioteca.models.UsuarioModel;
import com.biblioteca.security.SoloAdmin;
import com.biblioteca.services.AutenticacionService;
import com.biblioteca.services.MultaService;
import com.biblioteca.services.PrestamoService;
import com.biblioteca.services.UsuarioService;

@SoloAdmin
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private MultaService multaService;

    @Autowired
    private AutenticacionService autenticacionService;

    // Obtener todos los usuarios
    @GetMapping
    public List<UsuarioModel> obtenerTodos() {
        return usuarioService.obtenerTodos();
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioModel> obtenerPorId(@PathVariable Long id) {
        UsuarioModel usuario = usuarioService.obtenerPorId(id);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Crear un nuevo usuario con su contraseña (el admin puede crear cualquier tipo, incluso ADMIN)
    @PostMapping
    public ResponseEntity<UsuarioModel> crearUsuario(@RequestBody RegistroRequest datos) {
        UsuarioModel nuevoUsuario = autenticacionService.crearCuenta(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    // Actualizar un usuario existente
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioModel> actualizarUsuario(@PathVariable Long id,
            @RequestBody UsuarioModel nuevoUsuario) {
        UsuarioModel usuarioActualizado = usuarioService.actualizarUsuario(id, nuevoUsuario);
        if (usuarioActualizado != null) {
            return ResponseEntity.ok(usuarioActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar un usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // Normalmente pasa cuando el usuario tiene préstamos o multas registradas
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // Préstamos de un usuario
    @GetMapping("/{id}/prestamos")
    public List<PrestamoModel> obtenerPrestamos(@PathVariable Long id) {
        return prestamoService.obtenerPorUsuario(id);
    }

    // Multas de un usuario
    @GetMapping("/{id}/multas")
    public List<MultaModel> obtenerMultas(@PathVariable Long id) {
        return multaService.obtenerPorUsuario(id);
    }
}
