import { Injectable, signal } from '@angular/core';

export type TipoNotificacion = 'exito' | 'aviso' | 'error';

export interface Notificacion {
  id: number;
  tipo: TipoNotificacion;
  mensaje: string;
}

const DURACION_MS = 6000;

/** Mensajes temporales que se muestran en la esquina de la pantalla. */
@Injectable({ providedIn: 'root' })
export class NotificacionService {
  private siguienteId = 0;
  readonly notificaciones = signal<Notificacion[]>([]);

  exito(mensaje: string): void {
    this.agregar('exito', mensaje);
  }

  aviso(mensaje: string): void {
    this.agregar('aviso', mensaje);
  }

  error(mensaje: string): void {
    this.agregar('error', mensaje);
  }

  cerrar(id: number): void {
    this.notificaciones.update((lista) => lista.filter((n) => n.id !== id));
  }

  private agregar(tipo: TipoNotificacion, mensaje: string): void {
    const id = ++this.siguienteId;
    this.notificaciones.update((lista) => [...lista, { id, tipo, mensaje }]);
    setTimeout(() => this.cerrar(id), DURACION_MS);
  }
}
