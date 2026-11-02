package com.biblioteca.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biblioteca.dto.LoginRequest;
import com.biblioteca.dto.LoginResponse;
import com.biblioteca.dto.RegistroRequest;
import com.biblioteca.models.TipoUsuario;
import com.biblioteca.models.UsuarioModel;
import com.biblioteca.repositories.UsuarioRepository;
import com.biblioteca.security.HasheadorContrasena;
import com.biblioteca.security.NoAutenticadoException;

// Registro, inicio y cierre de sesión
@Service
public class AutenticacionService {

    private static final int LONGITUD_MINIMA_CONTRASENA = 6;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SesionService sesionService;

    @Autowired
    private HasheadorContrasena hasheador;

    // Registro público: solo ESTUDIANTE o DOCENTE
    public UsuarioModel registrar(RegistroRequest datos) {
        if (datos.tipo() == TipoUsuario.ADMIN) {
            throw new IllegalArgumentException("No puedes registrarte como ADMIN");
        }
        return crearCuenta(datos);
    }

    // Lo usa el admin: puede crear cuentas de cualquier tipo, incluso otro ADMIN
    public UsuarioModel crearCuenta(RegistroRequest datos) {
        validarContrasena(datos.contrasena());
        UsuarioModel usuario = new UsuarioModel();
        usuario.setNombre(datos.nombre());
        usuario.setDocumento(datos.documento());
        usuario.setTipo(datos.tipo());
        usuario.setContrasenaHash(hasheador.hashear(datos.contrasena()));
        return usuarioService.crearUsuario(usuario);
    }

    public LoginResponse iniciarSesion(LoginRequest datos) {
        if (datos.documento() == null || datos.contrasena() == null) {
            throw new IllegalArgumentException("El documento y la contraseña son obligatorios");
        }
        UsuarioModel usuario = usuarioRepository.findByDocumento(datos.documento().trim()).orElse(null);
        // El mismo mensaje en ambos casos para no revelar qué documentos existen
        if (usuario == null || usuario.getContrasenaHash() == null
                || !hasheador.coincide(datos.contrasena(), usuario.getContrasenaHash())) {
            throw new NoAutenticadoException("Documento o contraseña incorrectos");
        }
        return new LoginResponse(sesionService.crear(usuario), usuario);
    }

    public void cerrarSesion(String token) {
        sesionService.cerrar(token);
    }

    private void validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.length() < LONGITUD_MINIMA_CONTRASENA) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos " + LONGITUD_MINIMA_CONTRASENA + " caracteres");
        }
    }
}
