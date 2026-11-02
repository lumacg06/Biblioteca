package com.biblioteca.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.models.EjemplarModel;
import com.biblioteca.models.EstadoEjemplar;
import com.biblioteca.security.SoloAdmin;
import com.biblioteca.services.EjemplarService;

@SoloAdmin
@RestController
@RequestMapping("/api/ejemplares")
public class EjemplarController {

    @Autowired
    private EjemplarService ejemplarService;

    // Obtener todos los ejemplares
    @GetMapping
    public List<EjemplarModel> obtenerTodos() {
        return ejemplarService.obtenerTodos();
    }

    // Inventario: cantidad de ejemplares por estado
    @GetMapping("/inventario")
    public Map<EstadoEjemplar, Long> inventario() {
        return ejemplarService.contarPorEstado();
    }

    // Obtener ejemplar por ID
    @GetMapping("/{id}")
    public ResponseEntity<EjemplarModel> obtenerPorId(@PathVariable Long id) {
        EjemplarModel ejemplar = ejemplarService.obtenerPorId(id);
        if (ejemplar != null) {
            return ResponseEntity.ok(ejemplar);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Marcar ejemplar como dañado
    @PutMapping("/{id}/danado")
    public ResponseEntity<EjemplarModel> marcarComoDanado(@PathVariable Long id) {
        EjemplarModel ejemplar = ejemplarService.marcarComoDanado(id);
        if (ejemplar != null) {
            return ResponseEntity.ok(ejemplar);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Marcar ejemplar como perdido
    @PutMapping("/{id}/perdido")
    public ResponseEntity<EjemplarModel> marcarComoPerdido(@PathVariable Long id) {
        EjemplarModel ejemplar = ejemplarService.marcarComoPerdido(id);
        if (ejemplar != null) {
            return ResponseEntity.ok(ejemplar);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
