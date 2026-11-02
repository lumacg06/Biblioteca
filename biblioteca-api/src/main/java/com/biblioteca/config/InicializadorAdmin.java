package com.biblioteca.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.biblioteca.models.TipoUsuario;
import com.biblioteca.models.UsuarioModel;
import com.biblioteca.repositories.UsuarioRepository;
import com.biblioteca.security.HasheadorContrasena;

// Al arrancar, si no existe ningún ADMIN, crea el primero con los datos de application.properties
@Component
public class InicializadorAdmin implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InicializadorAdmin.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HasheadorContrasena hasheador;

    @Value("${biblioteca.admin.documento}")
    private String documento;

    @Value("${biblioteca.admin.nombre}")
    private String nombre;

    @Value("${biblioteca.admin.contrasena}")
    private String contrasena;

    @Override
    public void run(String... args) {
        if (usuarioRepository.existsByTipo(TipoUsuario.ADMIN)) {
            return;
        }
        UsuarioModel admin = new UsuarioModel();
        admin.setNombre(nombre);
        admin.setDocumento(documento);
        admin.setTipo(TipoUsuario.ADMIN);
        admin.setContrasenaHash(hasheador.hashear(contrasena));
        usuarioRepository.save(admin);
        log.info("Administrador inicial creado con documento '{}'. Cambia su contraseña en producción.", documento);
    }
}
