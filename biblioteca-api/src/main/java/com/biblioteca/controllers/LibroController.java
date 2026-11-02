package com.biblioteca.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.dto.DisponibilidadLibroResponse;
import com.biblioteca.models.EjemplarModel;
import com.biblioteca.models.LibroModel;
import com.biblioteca.security.SoloAdmin;
import com.biblioteca.services.EjemplarService;
import com.biblioteca.services.LibroService;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    @Autowired
    private LibroService libroService;

    @Autowired
    private EjemplarService ejemplarService;

    // Obtener todos los libros, o buscar por título, autor o ISBN (RF06)
    @GetMapping
    public List<LibroModel> buscarLibros(@RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) String isbn) {
        if (titulo != null && !titulo.isBlank()) {
            return libroService.buscarPorTitulo(titulo);
        } else if (autor != null && !autor.isBlank()) {
            return libroService.buscarPorAutor(autor);
        } else if (isbn != null && !isbn.isBlank()) {
            return libroService.buscarPorIsbn(isbn);
        } else {
            return libroService.obtenerTodos();
        }
    }

    // Catálogo con disponibilidad (RF06). Lo puede ver cualquier usuario con sesión
    @GetMapping("/disponibilidad")
    public List<DisponibilidadLibroResponse> disponibilidad(@RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) String isbn) {
        return buscarLibros(titulo, autor, isbn).stream()
                .map(ejemplarService::disponibilidad)
                .toList();
    }

    // Obtener libro por ID
    @GetMapping("/{id}")
    public ResponseEntity<LibroModel> obtenerPorId(@PathVariable Long id) {
        LibroModel libro = libroService.obtenerPorId(id);
        if (libro != null) {
            return ResponseEntity.ok(libro);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Crear un nuevo libro
    @SoloAdmin
    @PostMapping
    public ResponseEntity<LibroModel> crearLibro(@RequestBody LibroModel libro) {
        libro.setId(null); // el id lo genera la base de datos
        LibroModel nuevoLibro = libroService.crearLibro(libro);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoLibro);
    }

    // Actualizar un libro existente
    @SoloAdmin
    @PutMapping("/{id}")
    public ResponseEntity<LibroModel> actualizarLibro(@PathVariable Long id, @RequestBody LibroModel nuevoLibro) {
        LibroModel libroActualizado = libroService.actualizarLibro(id, nuevoLibro);
        if (libroActualizado != null) {
            return ResponseEntity.ok(libroActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar un libro
    @SoloAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLibro(@PathVariable Long id) {
        try {
            libroService.eliminarLibro(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // Normalmente pasa cuando el libro todavía tiene ejemplares
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // Obtener los ejemplares de un libro (incluye quién es responsable de un ejemplar perdido)
    @SoloAdmin
    @GetMapping("/{id}/ejemplares")
    public List<EjemplarModel> obtenerEjemplares(@PathVariable Long id) {
        return ejemplarService.obtenerPorLibro(id);
    }

    // Agregar un ejemplar a un libro
    @SoloAdmin
    @PostMapping("/{id}/ejemplares")
    public ResponseEntity<EjemplarModel> crearEjemplar(@PathVariable Long id, @RequestBody EjemplarModel ejemplar) {
        EjemplarModel nuevoEjemplar = ejemplarService.crearEjemplar(id, ejemplar);
        if (nuevoEjemplar != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEjemplar);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
