package com.biblioteca.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.models.EjemplarModel;
import com.biblioteca.models.EstadoEjemplar;
import com.biblioteca.models.EstadoPrestamo;
import com.biblioteca.models.PrestamoModel;
import com.biblioteca.models.UsuarioModel;
import com.biblioteca.repositories.EjemplarRepository;
import com.biblioteca.repositories.MultaRepository;
import com.biblioteca.repositories.PrestamoRepository;
import com.biblioteca.repositories.UsuarioRepository;

@Service
public class PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EjemplarRepository ejemplarRepository;

    @Autowired
    private MultaRepository multaRepository;

    @Autowired
    private MultaService multaService;

    // Obtener todos los préstamos
    public List<PrestamoModel> obtenerTodos() {
        return prestamoRepository.findAll();
    }

    // Obtener préstamo por ID
    public PrestamoModel obtenerPorId(Long id) {
        return prestamoRepository.findById(id).orElse(null);
    }

    // Obtener los préstamos de un usuario
    public List<PrestamoModel> obtenerPorUsuario(Long usuarioId) {
        return prestamoRepository.findByUsuarioId(usuarioId);
    }

    // Prestar un ejemplar a un usuario (RF03)
    @Transactional
    public PrestamoModel prestar(Long usuarioId, Long ejemplarId) {
        UsuarioModel usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            throw new IllegalArgumentException("No existe el usuario con id " + usuarioId);
        }
        // Los administradores gestionan la biblioteca, no toman libros prestados
        if (usuario.esAdmin()) {
            throw new IllegalArgumentException("Un administrador no puede tener préstamos");
        }
        EjemplarModel ejemplar = ejemplarRepository.findById(ejemplarId).orElse(null);
        if (ejemplar == null) {
            throw new IllegalArgumentException("No existe el ejemplar con id " + ejemplarId);
        }

        // RN04: con multas sin pagar no hay préstamos nuevos
        if (multaRepository.existsByUsuarioIdAndPagadaFalse(usuarioId)) {
            throw new IllegalArgumentException("El usuario tiene multas sin pagar y no puede hacer préstamos");
        }

        // RN01: límite de préstamos activos según el tipo de usuario
        long prestamosActivos = prestamoRepository.countByUsuarioIdAndEstado(usuarioId, EstadoPrestamo.ACTIVO);
        if (prestamosActivos >= usuario.getTipo().getLimitePrestamos()) {
            throw new IllegalArgumentException("Un usuario " + usuario.getTipo() + " puede tener máximo "
                    + usuario.getTipo().getLimitePrestamos() + " préstamos activos");
        }

        // RN06: solo se prestan ejemplares disponibles
        if (ejemplar.getEstado() != EstadoEjemplar.DISPONIBLE) {
            throw new IllegalArgumentException(
                    "El ejemplar no se puede prestar porque está en estado " + ejemplar.getEstado());
        }

        ejemplar.setEstado(EstadoEjemplar.PRESTADO);
        ejemplarRepository.save(ejemplar);

        PrestamoModel prestamo = new PrestamoModel();
        prestamo.setUsuario(usuario);
        prestamo.setEjemplar(ejemplar);
        prestamo.setFechaPrestamo(LocalDate.now());
        // RN02: el vencimiento depende del tipo de usuario
        prestamo.setFechaVencimiento(LocalDate.now().plusDays(usuario.getTipo().getPlazoDias()));
        prestamo.setEstado(EstadoPrestamo.ACTIVO);
        return prestamoRepository.save(prestamo);
    }

    // Devolver un préstamo (RF04) y generar multa si hubo atraso (RF05)
    @Transactional
    public PrestamoModel devolver(Long id) {
        PrestamoModel prestamo = obtenerPorId(id);
        if (prestamo == null) {
            return null;
        }
        if (prestamo.getEstado() != EstadoPrestamo.ACTIVO) {
            throw new IllegalArgumentException("El préstamo no está activo, su estado es " + prestamo.getEstado());
        }

        prestamo.setFechaDevolucion(LocalDate.now());
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);

        EjemplarModel ejemplar = prestamo.getEjemplar();
        ejemplar.setEstado(EstadoEjemplar.DISPONIBLE);
        ejemplarRepository.save(ejemplar);

        PrestamoModel prestamoDevuelto = prestamoRepository.save(prestamo);
        multaService.generarMultaSiHayRetraso(prestamoDevuelto);
        return prestamoDevuelto;
    }

    // Préstamos activos que ya pasaron su fecha de vencimiento (RF08)
    public List<PrestamoModel> obtenerVencidos() {
        return prestamoRepository.findByEstadoAndFechaVencimientoBefore(EstadoPrestamo.ACTIVO, LocalDate.now());
    }
}
