import { Component, computed, input } from '@angular/core';

/** Etiqueta de color para cualquier estado: DISPONIBLE, PRESTADO, ACTIVO, DEVUELTO... */
@Component({
  selector: 'app-estado-badge',
  template: `<span [class]="'badge badge-' + estado().toLowerCase()">{{ etiqueta() }}</span>`,
})
export class EstadoBadge {
  readonly estado = input.required<string>();

  // El enum del backend no lleva tildes ni Ñ
  protected readonly etiqueta = computed(() => (this.estado() === 'DANADO' ? 'DAÑADO' : this.estado()));
}
