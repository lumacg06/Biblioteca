import { Component, inject } from '@angular/core';

import { NotificacionService } from '../../../core/services/notificacion.service';

@Component({
  selector: 'app-notificaciones',
  template: `
    <div class="notificaciones" aria-live="polite">
      @for (n of notificacionService.notificaciones(); track n.id) {
        <div class="notificacion notificacion-{{ n.tipo }}" role="status">
          <span>{{ n.mensaje }}</span>
          <button type="button" class="cerrar" (click)="notificacionService.cerrar(n.id)" aria-label="Cerrar">
            ×
          </button>
        </div>
      }
    </div>
  `,
})
export class Notificaciones {
  protected readonly notificacionService = inject(NotificacionService);
}
