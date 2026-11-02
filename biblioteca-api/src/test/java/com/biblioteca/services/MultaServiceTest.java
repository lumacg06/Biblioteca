package com.biblioteca.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.biblioteca.models.MultaModel;
import com.biblioteca.models.PrestamoModel;
import com.biblioteca.repositories.MultaRepository;

class MultaServiceTest {

    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private MultaService multaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(multaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    private PrestamoModel prestamoDevuelto(LocalDate vencimiento, LocalDate devolucion) {
        PrestamoModel prestamo = new PrestamoModel();
        prestamo.setFechaVencimiento(vencimiento);
        prestamo.setFechaDevolucion(devolucion);
        return prestamo;
    }

    @Test
    void cobraMilPesosPorCadaDiaDeAtraso() {
        LocalDate vencimiento = LocalDate.of(2026, 1, 8);

        MultaModel multa = multaService.generarMultaSiHayRetraso(prestamoDevuelto(vencimiento, vencimiento.plusDays(3)));

        assertEquals(3, multa.getDiasRetraso());
        assertEquals(new BigDecimal("3000"), multa.getValor()); // RN03
    }

    @Test
    void noGeneraMultaSiSeDevuelveATiempo() {
        LocalDate vencimiento = LocalDate.of(2026, 1, 8);

        assertNull(multaService.generarMultaSiHayRetraso(prestamoDevuelto(vencimiento, vencimiento.minusDays(1))));
    }

    @Test
    void unaMultaNoSePuedePagarDosVeces() {
        MultaModel multa = new MultaModel();
        multa.setId(1L);
        multa.setPagada(true);
        when(multaRepository.findById(1L)).thenReturn(Optional.of(multa));

        assertThrows(IllegalArgumentException.class, () -> multaService.pagarMulta(1L)); // RN07
    }
}
