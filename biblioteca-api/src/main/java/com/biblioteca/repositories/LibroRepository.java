package com.biblioteca.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.models.LibroModel;

@Repository
public interface LibroRepository extends JpaRepository<LibroModel, Long> {
    boolean existsByIsbn(String isbn);

    List<LibroModel> findByTituloContainingIgnoreCase(String titulo);

    List<LibroModel> findByAutorContainingIgnoreCase(String autor);

    List<LibroModel> findByIsbn(String isbn);
}
