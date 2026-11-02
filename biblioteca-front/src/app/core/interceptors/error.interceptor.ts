import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { ApiError } from '../models/api-error.model';
import { NotificacionService } from '../services/notificacion.service';
import { SesionService } from '../services/sesion.service';

/**
 * Único lugar donde se muestran los errores HTTP.
 * Las pantallas no repiten este código: solo reaccionan (por ejemplo, reactivan un botón).
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const notificacion = inject(NotificacionService);
  const sesion = inject(SesionService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // 401 en cualquier petición que no sea el login = el token ya no sirve: volver a iniciar sesión
      if (error.status === 401 && !req.url.endsWith('/auth/login')) {
        sesion.cerrar();
        router.navigate(['/login']);
      }
      notificacion.error(obtenerMensaje(error));
      return throwError(() => error);
    }),
  );
};

function obtenerMensaje(error: HttpErrorResponse): string {
  const cuerpo = error.error as Partial<ApiError> | null;

  switch (error.status) {
    case 0:
      return 'No hay conexión con el servidor. ¿Está corriendo el backend en http://localhost:8081?';
    case 400:
      // Reglas de negocio RN01..RN07: el backend envía { "mensaje": "..." }
      return cuerpo?.mensaje ?? 'La solicitud no es válida';
    case 401:
      return cuerpo?.mensaje ?? 'Debes iniciar sesión';
    case 403:
      return cuerpo?.mensaje ?? 'No tienes permiso para realizar esta acción';
    case 404:
      return 'El registro solicitado no existe';
    case 409:
      return 'No se puede eliminar porque tiene registros asociados';
    default:
      // En desarrollo, si el backend está apagado el proxy de Angular responde 500
      return `Error del servidor (${error.status}). Revisa que el backend esté corriendo en http://localhost:8081`;
  }
}
