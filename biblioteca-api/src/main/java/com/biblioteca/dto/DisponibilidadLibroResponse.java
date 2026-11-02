package com.biblioteca.dto;

// RF06: catálogo para cualquier usuario, sin exponer quién tiene cada ejemplar
public record DisponibilidadLibroResponse(Long libroId, String titulo, String autor, String isbn,
        long totalEjemplares, long ejemplaresDisponibles) {
}
