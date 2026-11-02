package com.biblioteca.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.models.EjemplarModel;
import com.biblioteca.models.LibroModel;
import com.biblioteca.repositories.LibroRepository;
import com.biblioteca.services.EjemplarService;
import com.biblioteca.services.LibroService;

// Datos de ejemplo: si la tabla de libros está vacía, carga 15 libros de desarrollo personal
// con 2 ejemplares cada uno (30 ejemplares). Se desactiva con biblioteca.datos-ejemplo=false
@Component
@ConditionalOnProperty(name = "biblioteca.datos-ejemplo", havingValue = "true", matchIfMissing = true)
public class InicializadorCatalogo implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InicializadorCatalogo.class);

    private static final int EJEMPLARES_POR_LIBRO = 2;

    // Títulos en español; el ISBN es el de la edición original de cada libro
    private static final List<LibroEjemplo> LIBROS = List.of(
            new LibroEjemplo("Hábitos atómicos", "James Clear", "9780735211292"),
            new LibroEjemplo("Los 7 hábitos de la gente altamente efectiva", "Stephen R. Covey", "9780743269513"),
            new LibroEjemplo("Cómo ganar amigos e influir sobre las personas", "Dale Carnegie", "9780671027032"),
            new LibroEjemplo("Piense y hágase rico", "Napoleon Hill", "9781585424337"),
            new LibroEjemplo("El poder del ahora", "Eckhart Tolle", "9781577314806"),
            new LibroEjemplo("Mindset: la actitud del éxito", "Carol S. Dweck", "9780345472328"),
            new LibroEjemplo("El hombre en busca de sentido", "Viktor E. Frankl", "9780807014271"),
            new LibroEjemplo("El sutil arte de que (casi todo) te importe una mierda", "Mark Manson", "9780062457714"),
            new LibroEjemplo("Padre rico, padre pobre", "Robert T. Kiyosaki", "9781612680194"),
            new LibroEjemplo("Los cuatro acuerdos", "Miguel Ruiz", "9781878424310"),
            new LibroEjemplo("Enfócate (Deep Work)", "Cal Newport", "9781455586691"),
            new LibroEjemplo("Pensar rápido, pensar despacio", "Daniel Kahneman", "9780374533557"),
            new LibroEjemplo("Ikigai: los secretos de Japón para una vida larga y feliz",
                    "Héctor García y Francesc Miralles", "9780143130727"),
            new LibroEjemplo("Esencialismo", "Greg McKeown", "9780804137386"),
            new LibroEjemplo("La psicología del dinero", "Morgan Housel", "9780857197689"));

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private LibroService libroService;

    @Autowired
    private EjemplarService ejemplarService;

    @Override
    @Transactional
    public void run(String... args) {
        // Solo la primera vez: si ya hay libros (cargados aquí o por el admin), no se toca nada
        if (libroRepository.count() > 0) {
            return;
        }
        int consecutivo = 1;
        for (LibroEjemplo datos : LIBROS) {
            LibroModel libro = libroService.crearLibro(datos.aModelo());
            for (int i = 0; i < EJEMPLARES_POR_LIBRO; i++) {
                EjemplarModel ejemplar = new EjemplarModel();
                ejemplar.setCodigo(String.format("EJ-%03d", consecutivo++));
                ejemplarService.crearEjemplar(libro.getId(), ejemplar);
            }
        }
        log.info("Catálogo de ejemplo cargado: {} libros y {} ejemplares", LIBROS.size(), consecutivo - 1);
    }

    private record LibroEjemplo(String titulo, String autor, String isbn) {

        LibroModel aModelo() {
            LibroModel libro = new LibroModel();
            libro.setTitulo(titulo);
            libro.setAutor(autor);
            libro.setIsbn(isbn);
            return libro;
        }
    }
}
