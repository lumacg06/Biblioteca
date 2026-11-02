package com.biblioteca.models;

public enum TipoUsuario {

    ESTUDIANTE(3, 7), // RN01: máximo 3 préstamos | RN02: 7 días de plazo
    DOCENTE(5, 15),   // RN01: máximo 5 préstamos | RN02: 15 días de plazo
    ADMIN(0, 0);      // Administra la biblioteca: ve todo y no toma préstamos

    private final int limitePrestamos;
    private final int plazoDias;

    TipoUsuario(int limitePrestamos, int plazoDias) {
        this.limitePrestamos = limitePrestamos;
        this.plazoDias = plazoDias;
    }

    public int getLimitePrestamos() {
        return limitePrestamos;
    }

    public int getPlazoDias() {
        return plazoDias;
    }
}
