package com.biblioteca.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.models.MultaModel;

@Repository
public interface MultaRepository extends JpaRepository<MultaModel, Long> {
    boolean existsByUsuarioIdAndPagadaFalse(Long usuarioId);

    List<MultaModel> findByUsuarioId(Long usuarioId);
}
