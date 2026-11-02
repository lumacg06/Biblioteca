package com.biblioteca.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.models.EjemplarModel;
import com.biblioteca.models.EstadoEjemplar;

@Repository
public interface EjemplarRepository extends JpaRepository<EjemplarModel, Long> {
    boolean existsByCodigoIgnoreCase(String codigo);

    List<EjemplarModel> findByLibroId(Long libroId);

    long countByEstado(EstadoEjemplar estado);

    long countByLibroId(Long libroId);

    long countByLibroIdAndEstado(Long libroId, EstadoEjemplar estado);
}
