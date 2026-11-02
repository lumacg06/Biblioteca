import { Injectable, computed, signal } from '@angular/core';

import { SesionUsuario } from '../models/auth.model';

const CLAVE_ALMACENAMIENTO = 'biblioteca.sesion';

/**
 * Guarda quién inició sesión (en memoria y en localStorage para sobrevivir a un F5).
 * No hace peticiones HTTP: así los interceptores pueden usarlo sin dependencias circulares.
 *
 * Ojo: el rol guardado aquí solo sirve para mostrar u ocultar pantallas.
 * Quien de verdad protege los datos es el backend, que revisa el token en cada petición.
 */
@Injectable({ providedIn: 'root' })
export class SesionService {
  private readonly sesion = signal<SesionUsuario | null>(leerSesionGuardada());

  readonly usuario = computed(() => this.sesion()?.usuario ?? null);
  readonly token = computed(() => this.sesion()?.token ?? null);
  readonly autenticado = computed(() => this.sesion() !== null);
  readonly esAdmin = computed(() => this.usuario()?.tipo === 'ADMIN');

  iniciar(sesion: SesionUsuario): void {
    this.sesion.set(sesion);
    try {
      localStorage.setItem(CLAVE_ALMACENAMIENTO, JSON.stringify(sesion));
    } catch {
      // Si el navegador bloquea localStorage, la sesión dura hasta recargar la página
    }
  }

  cerrar(): void {
    this.sesion.set(null);
    try {
      localStorage.removeItem(CLAVE_ALMACENAMIENTO);
    } catch {
      // Nada que limpiar
    }
  }

  /** Pantalla de inicio según el rol. */
  rutaInicio(): string {
    return this.esAdmin() ? '/prestamos' : '/mi-cuenta';
  }
}

function leerSesionGuardada(): SesionUsuario | null {
  try {
    const texto = localStorage.getItem(CLAVE_ALMACENAMIENTO);
    return texto ? (JSON.parse(texto) as SesionUsuario) : null;
  } catch {
    return null;
  }
}
