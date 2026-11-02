package com.biblioteca.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.models.MultaModel;
import com.biblioteca.security.SoloAdmin;
import com.biblioteca.services.MultaService;

@SoloAdmin
@RestController
@RequestMapping("/api/multas")
public class MultaController {

    @Autowired
    private MultaService multaService;

    // Obtener todas las multas
    @GetMapping
    public List<MultaModel> obtenerTodas() {
        return multaService.obtenerTodas();
    }

    // Pagar una multa
    @PutMapping("/{id}/pagar")
    public ResponseEntity<MultaModel> pagarMulta(@PathVariable Long id) {
        MultaModel multa = multaService.pagarMulta(id);
        if (multa != null) {
            return ResponseEntity.ok(multa);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
