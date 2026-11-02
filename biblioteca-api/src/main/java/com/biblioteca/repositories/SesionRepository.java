package com.biblioteca.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.models.SesionModel;

@Repository
public interface SesionRepository extends JpaRepository<SesionModel, Long> {
    Optional<SesionModel> findByToken(String token);

    void deleteByToken(String token);

    void deleteByUsuarioId(Long usuarioId);

    void deleteByExpiraEnBefore(LocalDateTime fecha);
}
