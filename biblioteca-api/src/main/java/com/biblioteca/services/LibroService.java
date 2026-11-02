package com.biblioteca.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biblioteca.models.LibroModel;
import com.biblioteca.repositories.LibroRepository;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    // Obtener todos los libros
    public List<LibroModel> obtenerTodos() {
        return libroRepository.findAll();
    }

    // Obtener libro por ID
    public LibroModel obtenerPorId(Long id) {
        return libroRepository.findById(id).orElse(null);
    }

    // Crear un nuevo libro (RF01)
    public LibroModel crearLibro(LibroModel libro) {
        validarCampos(libro);
        libro.setIsbn(normalizarIsbn(libro.getIsbn()));
        if (libroRepository.existsByIsbn(libro.getIsbn())) {
            throw new IllegalArgumentException("Ya existe un libro con el ISBN " + libro.getIsbn());
        }
        return libroRepository.save(libro);
    }

    // Actualizar un libro existente
    public LibroModel actualizarLibro(Long id, LibroModel nuevoLibro) {
        LibroModel libroActualizado = obtenerPorId(id);
        if (libroActualizado != null) {
            validarCampos(nuevoLibro);
            String isbn = normalizarIsbn(nuevoLibro.getIsbn());
            if (!isbn.equals(libroActualizado.getIsbn()) && libroRepository.existsByIsbn(isbn)) {
                throw new IllegalArgumentException("Ya existe un libro con el ISBN " + isbn);
            }
            libroActualizado.setTitulo(nuevoLibro.getTitulo());
            libroActualizado.setAutor(nuevoLibro.getAutor());
            libroActualizado.setIsbn(isbn);
            return libroRepository.save(libroActualizado);
        } else {
            return null;
        }
    }

    // Eliminar un libro
    public void eliminarLibro(Long id) {
        try {
            libroRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el libro con id " + id, e);
        }
    }

    // Buscar libros por título (RF06)
    public List<LibroModel> buscarPorTitulo(String titulo) {
        return libroRepository.findByTituloContainingIgnoreCase(titulo);
    }

    // Buscar libros por autor (RF06)
    public List<LibroModel> buscarPorAutor(String autor) {
        return libroRepository.findByAutorContainingIgnoreCase(autor);
    }

    // Buscar libros por ISBN (RF06)
    public List<LibroModel> buscarPorIsbn(String isbn) {
        return libroRepository.findByIsbn(normalizarIsbn(isbn));
    }

    // Validar que los campos obligatorios vengan llenos
    private void validarCampos(LibroModel libro) {
        if (libro.getTitulo() == null || libro.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }
        if (libro.getAutor() == null || libro.getAutor().isBlank()) {
            throw new IllegalArgumentException("El autor es obligatorio");
        }
        String isbn = normalizarIsbn(libro.getIsbn());
        if (!isbn.matches("\\d{13}|\\d{9}[\\dX]")) {
            throw new IllegalArgumentException("El ISBN debe tener 10 o 13 dígitos");
        }
    }

    // Quitar guiones y espacios para poder comparar ISBN
    private String normalizarIsbn(String isbn) {
        if (isbn == null) {
            return "";
        }
        return isbn.replace("-", "").replace(" ", "").toUpperCase();
    }
}
