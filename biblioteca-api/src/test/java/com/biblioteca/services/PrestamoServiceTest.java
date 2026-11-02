package com.biblioteca.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.biblioteca.models.EjemplarModel;
import com.biblioteca.models.EstadoEjemplar;
import com.biblioteca.models.EstadoPrestamo;
import com.biblioteca.models.PrestamoModel;
import com.biblioteca.models.TipoUsuario;
import com.biblioteca.models.UsuarioModel;
import com.biblioteca.repositories.EjemplarRepository;
import com.biblioteca.repositories.MultaRepository;
import com.biblioteca.repositories.PrestamoRepository;
import com.biblioteca.repositories.UsuarioRepository;

class PrestamoServiceTest {

    @Mock
    private PrestamoRepository prestamoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private EjemplarRepository ejemplarRepository;
    @Mock
    private MultaRepository multaRepository;
    @Mock
    private MultaService multaService;

    @InjectMocks
    private PrestamoService prestamoService;

    private UsuarioModel estudiante;
    private EjemplarModel ejemplar;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        estudiante = new UsuarioModel();
        estudiante.setId(1L);
        estudiante.setTipo(TipoUsuario.ESTUDIANTE);

        ejemplar = new EjemplarModel();
        ejemplar.setId(10L);
        ejemplar.setEstado(EstadoEjemplar.DISPONIBLE);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(ejemplarRepository.findById(10L)).thenReturn(Optional.of(ejemplar));
        when(prestamoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void prestarCalculaElVencimientoYMarcaElEjemplarComoPrestado() {
        PrestamoModel prestamo = prestamoService.prestar(1L, 10L);

        assertEquals(EstadoPrestamo.ACTIVO, prestamo.getEstado());
        assertEquals(LocalDate.now().plusDays(7), prestamo.getFechaVencimiento()); // RN02
        assertEquals(EstadoEjemplar.PRESTADO, ejemplar.getEstado());
    }

    @Test
    void noPrestaSiElUsuarioTieneMultasPendientes() {
        when(multaRepository.existsByUsuarioIdAndPagadaFalse(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> prestamoService.prestar(1L, 10L)); // RN04
    }

    @Test
    void noPrestaSiElUsuarioLlegoAlLimite() {
        when(prestamoRepository.countByUsuarioIdAndEstado(1L, EstadoPrestamo.ACTIVO)).thenReturn(3L);

        assertThrows(IllegalArgumentException.class, () -> prestamoService.prestar(1L, 10L)); // RN01
    }

    @Test
    void noPrestaUnEjemplarQueNoEstaDisponible() {
        ejemplar.setEstado(EstadoEjemplar.DANADO);

        assertThrows(IllegalArgumentException.class, () -> prestamoService.prestar(1L, 10L)); // RN06
    }

    @Test
    void devolverLiberaElEjemplarYRevisaLaMulta() {
        ejemplar.setEstado(EstadoEjemplar.PRESTADO);
        PrestamoModel prestamo = new PrestamoModel();
        prestamo.setId(5L);
        prestamo.setUsuario(estudiante);
        prestamo.setEjemplar(ejemplar);
        prestamo.setEstado(EstadoPrestamo.ACTIVO);
        when(prestamoRepository.findById(5L)).thenReturn(Optional.of(prestamo));

        PrestamoModel devuelto = prestamoService.devolver(5L);

        assertEquals(EstadoPrestamo.DEVUELTO, devuelto.getEstado());
        assertEquals(EstadoEjemplar.DISPONIBLE, ejemplar.getEstado());
        verify(multaService).generarMultaSiHayRetraso(devuelto);
    }
}
