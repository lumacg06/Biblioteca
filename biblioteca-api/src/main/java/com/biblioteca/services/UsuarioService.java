package com.biblioteca.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.models.UsuarioModel;
import com.biblioteca.repositories.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SesionService sesionService;

    // Obtener todos los usuarios
    public List<UsuarioModel> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    // Obtener usuario por ID
    public UsuarioModel obtenerPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    // Crear un nuevo usuario (RF02)
    public UsuarioModel crearUsuario(UsuarioModel usuario) {
        validarCampos(usuario);
        if (usuarioRepository.existsByDocumento(usuario.getDocumento())) {
            throw new IllegalArgumentException("Ya existe un usuario con el documento " + usuario.getDocumento());
        }
        return usuarioRepository.save(usuario);
    }

    // Actualizar un usuario existente
    public UsuarioModel actualizarUsuario(Long id, UsuarioModel nuevoUsuario) {
        UsuarioModel usuarioActualizado = obtenerPorId(id);
        if (usuarioActualizado != null) {
            validarCampos(nuevoUsuario);
            if (!nuevoUsuario.getDocumento().equals(usuarioActualizado.getDocumento())
                    && usuarioRepository.existsByDocumento(nuevoUsuario.getDocumento())) {
                throw new IllegalArgumentException(
                        "Ya existe un usuario con el documento " + nuevoUsuario.getDocumento());
            }
            usuarioActualizado.setNombre(nuevoUsuario.getNombre());
            usuarioActualizado.setDocumento(nuevoUsuario.getDocumento());
            usuarioActualizado.setTipo(nuevoUsuario.getTipo());
            return usuarioRepository.save(usuarioActualizado);
        } else {
            return null;
        }
    }

    // Eliminar un usuario. Si falla (tiene préstamos), la transacción también deshace el borrado de sus sesiones
    @Transactional
    public void eliminarUsuario(Long id) {
        sesionService.cerrarTodas(id);
        try {
            usuarioRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el usuario con id " + id, e);
        }
    }

    // Validar que los campos obligatorios vengan llenos
    private void validarCampos(UsuarioModel usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (usuario.getDocumento() == null || usuario.getDocumento().isBlank()) {
            throw new IllegalArgumentException("El documento es obligatorio");
        }
        if (usuario.getTipo() == null) {
            throw new IllegalArgumentException("El tipo de usuario es obligatorio (ESTUDIANTE o DOCENTE)");
        }
    }
}
