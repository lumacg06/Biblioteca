package com.biblioteca.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.dto.DisponibilidadLibroResponse;
import com.biblioteca.models.EjemplarModel;
import com.biblioteca.models.EstadoEjemplar;
import com.biblioteca.models.EstadoPrestamo;
import com.biblioteca.models.LibroModel;
import com.biblioteca.models.PrestamoModel;
import com.biblioteca.repositories.EjemplarRepository;
import com.biblioteca.repositories.LibroRepository;
import com.biblioteca.repositories.PrestamoRepository;

@Service
public class EjemplarService {

    @Autowired
    private EjemplarRepository ejemplarRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private PrestamoRepository prestamoRepository;

    // Obtener todos los ejemplares
    public List<EjemplarModel> obtenerTodos() {
        return ejemplarRepository.findAll();
    }

    // Disponibilidad de un libro para el catálogo (RF06)
    public DisponibilidadLibroResponse disponibilidad(LibroModel libro) {
        long total = ejemplarRepository.countByLibroId(libro.getId());
        long disponibles = ejemplarRepository.countByLibroIdAndEstado(libro.getId(), EstadoEjemplar.DISPONIBLE);
        return new DisponibilidadLibroResponse(libro.getId(), libro.getTitulo(), libro.getAutor(), libro.getIsbn(),
                total, disponibles);
    }

    // Obtener ejemplar por ID
    public EjemplarModel obtenerPorId(Long id) {
        return ejemplarRepository.findById(id).orElse(null);
    }

    // Obtener los ejemplares de un libro (RF06: disponibilidad)
    public List<EjemplarModel> obtenerPorLibro(Long libroId) {
        return ejemplarRepository.findByLibroId(libroId);
    }

    // Crear un ejemplar dentro de un libro (RF01)
    public EjemplarModel crearEjemplar(Long libroId, EjemplarModel ejemplar) {
        LibroModel libro = libroRepository.findById(libroId).orElse(null);
        if (libro == null) {
            return null;
        }
        if (ejemplar.getCodigo() == null || ejemplar.getCodigo().isBlank()) {
            throw new IllegalArgumentException("El código del ejemplar es obligatorio");
        }
        if (ejemplarRepository.existsByCodigoIgnoreCase(ejemplar.getCodigo().trim())) {
            throw new IllegalArgumentException("Ya existe un ejemplar con el código " + ejemplar.getCodigo());
        }
        ejemplar.setId(null);
        ejemplar.setCodigo(ejemplar.getCodigo().trim());
        ejemplar.setLibro(libro);
        ejemplar.setEstado(EstadoEjemplar.DISPONIBLE); // todo ejemplar nuevo empieza disponible
        ejemplar.setUsuarioResponsable(null);
        return ejemplarRepository.save(ejemplar);
    }

    // Marcar un ejemplar como dañado (RF07)
    public EjemplarModel marcarComoDanado(Long id) {
        EjemplarModel ejemplar = obtenerPorId(id);
        if (ejemplar == null) {
            return null;
        }
        // Un ejemplar prestado primero se devuelve y luego se marca como dañado
        if (ejemplar.getEstado() != EstadoEjemplar.DISPONIBLE) {
            throw new IllegalArgumentException(
                    "Solo un ejemplar DISPONIBLE puede marcarse como DANADO, estado actual: " + ejemplar.getEstado());
        }
        ejemplar.setEstado(EstadoEjemplar.DANADO);
        return ejemplarRepository.save(ejemplar);
    }

    // Marcar un ejemplar como perdido (RF07 + RN05)
    @Transactional
    public EjemplarModel marcarComoPerdido(Long id) {
        EjemplarModel ejemplar = obtenerPorId(id);
        if (ejemplar == null) {
            return null;
        }
        if (ejemplar.getEstado() == EstadoEjemplar.PERDIDO) {
            throw new IllegalArgumentException("El ejemplar ya está marcado como PERDIDO");
        }

        // RN05: el responsable es el último usuario que lo tuvo prestado
        PrestamoModel ultimoPrestamo = prestamoRepository
                .findFirstByEjemplarIdOrderByFechaPrestamoDescIdDesc(id)
                .orElse(null);
        if (ultimoPrestamo != null) {
            ejemplar.setUsuarioResponsable(ultimoPrestamo.getUsuario());
            if (ultimoPrestamo.getEstado() == EstadoPrestamo.ACTIVO) {
                ultimoPrestamo.setEstado(EstadoPrestamo.PERDIDO);
                prestamoRepository.save(ultimoPrestamo);
            }
        }

        ejemplar.setEstado(EstadoEjemplar.PERDIDO);
        return ejemplarRepository.save(ejemplar);
    }

    // Inventario: cuántos ejemplares hay en cada estado (RF08)
    public Map<EstadoEjemplar, Long> contarPorEstado() {
        Map<EstadoEjemplar, Long> inventario = new LinkedHashMap<>();
        for (EstadoEjemplar estado : EstadoEjemplar.values()) {
            inventario.put(estado, ejemplarRepository.countByEstado(estado));
        }
        return inventario;
    }
}
