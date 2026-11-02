package com.biblioteca.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.models.TipoUsuario;
import com.biblioteca.models.UsuarioModel;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {
    boolean existsByDocumento(String documento);

    Optional<UsuarioModel> findByDocumento(String documento);

    boolean existsByTipo(TipoUsuario tipo);
}
