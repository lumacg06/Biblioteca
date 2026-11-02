package com.biblioteca.services;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biblioteca.models.MultaModel;
import com.biblioteca.models.PrestamoModel;
import com.biblioteca.repositories.MultaRepository;

@Service
public class MultaService {

    // RN03: $1.000 por cada día de atraso
    public static final BigDecimal VALOR_POR_DIA = BigDecimal.valueOf(1000);

    @Autowired
    private MultaRepository multaRepository;

    // Obtener todas las multas
    public List<MultaModel> obtenerTodas() {
        return multaRepository.findAll();
    }

    // Obtener las multas de un usuario
    public List<MultaModel> obtenerPorUsuario(Long usuarioId) {
        return multaRepository.findByUsuarioId(usuarioId);
    }

    // Generar la multa solo si el préstamo se devolvió tarde (RF05 + RN03)
    public MultaModel generarMultaSiHayRetraso(PrestamoModel prestamo) {
        int diasRetraso = calcularDiasRetraso(prestamo);
        if (diasRetraso == 0) {
            return null; // no existen multas de $0
        }
        MultaModel multa = new MultaModel();
        multa.setUsuario(prestamo.getUsuario());
        multa.setPrestamo(prestamo);
        multa.setDiasRetraso(diasRetraso);
        multa.setValor(VALOR_POR_DIA.multiply(BigDecimal.valueOf(diasRetraso)));
        multa.setPagada(false);
        return multaRepository.save(multa);
    }

    // Pagar una multa (RN07: solo se paga una vez)
    public MultaModel pagarMulta(Long id) {
        MultaModel multa = multaRepository.findById(id).orElse(null);
        if (multa == null) {
            return null;
        }
        if (multa.isPagada()) {
            throw new IllegalArgumentException("La multa " + id + " ya fue pagada");
        }
        multa.setPagada(true);
        return multaRepository.save(multa);
    }

    // Días entre el vencimiento y la devolución (nunca negativo)
    public int calcularDiasRetraso(PrestamoModel prestamo) {
        if (prestamo.getFechaDevolucion() == null) {
            return 0;
        }
        long dias = ChronoUnit.DAYS.between(prestamo.getFechaVencimiento(), prestamo.getFechaDevolucion());
        return (int) Math.max(0, dias);
    }
}
