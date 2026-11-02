package com.biblioteca.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.models.SesionModel;
import com.biblioteca.models.UsuarioModel;
import com.biblioteca.repositories.SesionRepository;
import com.biblioteca.security.NoAutenticadoException;

// Crea, valida y cierra sesiones (tokens guardados en la tabla "sesiones")
@Service
public class SesionService {

    private static final int BYTES_TOKEN = 32;

    @Autowired
    private SesionRepository sesionRepository;

    @Value("${biblioteca.sesion.horas:8}")
    private long horasDuracion;

    private final SecureRandom random = new SecureRandom();

    @Transactional
    public String crear(UsuarioModel usuario) {
        // Aprovechamos para borrar las sesiones vencidas y no acumular basura
        sesionRepository.deleteByExpiraEnBefore(LocalDateTime.now());

        SesionModel sesion = new SesionModel();
        sesion.setToken(generarToken());
        sesion.setUsuario(usuario);
        sesion.setExpiraEn(LocalDateTime.now().plusHours(horasDuracion));
        return sesionRepository.save(sesion).getToken();
    }

    // Devuelve el dueño del token o lanza NoAutenticadoException (401)
    public UsuarioModel validar(String token) {
        if (token == null || token.isBlank()) {
            throw new NoAutenticadoException("Debes iniciar sesión");
        }
        SesionModel sesion = sesionRepository.findByToken(token)
                .orElseThrow(() -> new NoAutenticadoException("Tu sesión no es válida, inicia sesión de nuevo"));
        if (sesion.getExpiraEn().isBefore(LocalDateTime.now())) {
            throw new NoAutenticadoException("Tu sesión expiró, inicia sesión de nuevo");
        }
        return sesion.getUsuario();
    }

    @Transactional
    public void cerrar(String token) {
        if (token != null) {
            sesionRepository.deleteByToken(token);
        }
    }

    @Transactional
    public void cerrarTodas(Long usuarioId) {
        sesionRepository.deleteByUsuarioId(usuarioId);
    }

    // Texto aleatorio e imposible de adivinar (43 caracteres)
    private String generarToken() {
        byte[] bytes = new byte[BYTES_TOKEN];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
