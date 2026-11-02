package com.biblioteca.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.models.PrestamoModel;
import com.biblioteca.security.SoloAdmin;
import com.biblioteca.services.PrestamoService;

@SoloAdmin
@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    // Obtener todos los préstamos
    @GetMapping
    public List<PrestamoModel> obtenerTodos() {
        return prestamoService.obtenerTodos();
    }

    // Préstamos vencidos
    @GetMapping("/vencidos")
    public List<PrestamoModel> obtenerVencidos() {
        return prestamoService.obtenerVencidos();
    }

    // Obtener préstamo por ID
    @GetMapping("/{id}")
    public ResponseEntity<PrestamoModel> obtenerPorId(@PathVariable Long id) {
        PrestamoModel prestamo = prestamoService.obtenerPorId(id);
        if (prestamo != null) {
            return ResponseEntity.ok(prestamo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Prestar un ejemplar: POST /api/prestamos?usuarioId=1&ejemplarId=2
    @PostMapping
    public ResponseEntity<PrestamoModel> prestar(@RequestParam Long usuarioId, @RequestParam Long ejemplarId) {
        PrestamoModel prestamo = prestamoService.prestar(usuarioId, ejemplarId);
        return ResponseEntity.status(HttpStatus.CREATED).body(prestamo);
    }

    // Devolver un préstamo (si hay atraso se genera la multa)
    @PutMapping("/{id}/devolver")
    public ResponseEntity<PrestamoModel> devolver(@PathVariable Long id) {
        PrestamoModel prestamo = prestamoService.devolver(id);
        if (prestamo != null) {
            return ResponseEntity.ok(prestamo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
