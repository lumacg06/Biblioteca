import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

import { SesionService } from '../services/sesion.service';

/** Agrega "Authorization: Bearer <token>" a todas las peticiones cuando hay sesión. */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(SesionService).token();
  if (!token) {
    return next(req);
  }
  return next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
};
