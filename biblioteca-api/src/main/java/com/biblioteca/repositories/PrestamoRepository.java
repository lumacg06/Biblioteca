package com.biblioteca.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.models.EstadoPrestamo;
import com.biblioteca.models.PrestamoModel;

@Repository
public interface PrestamoRepository extends JpaRepository<PrestamoModel, Long> {
    long countByUsuarioIdAndEstado(Long usuarioId, EstadoPrestamo estado);

    List<PrestamoModel> findByUsuarioId(Long usuarioId);

    // Último préstamo de un ejemplar (RN05)
    Optional<PrestamoModel> findFirstByEjemplarIdOrderByFechaPrestamoDescIdDesc(Long ejemplarId);

    // Préstamos activos que ya pasaron su fecha de vencimiento
    List<PrestamoModel> findByEstadoAndFechaVencimientoBefore(EstadoPrestamo estado, LocalDate fecha);
}
