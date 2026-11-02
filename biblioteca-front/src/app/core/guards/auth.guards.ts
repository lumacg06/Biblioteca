import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { SesionService } from '../services/sesion.service';

// Los guards solo deciden qué pantallas se muestran; la seguridad real está en el backend

/** Pantallas para cualquier usuario con sesión. */
export const autenticadoGuard: CanActivateFn = () => {
  const sesion = inject(SesionService);
  return sesion.autenticado() || inject(Router).createUrlTree(['/login']);
};

/** Pantallas solo para ADMIN. Un estudiante o docente vuelve a "Mi cuenta". */
export const adminGuard: CanActivateFn = () => {
  const sesion = inject(SesionService);
  const router = inject(Router);
  if (!sesion.autenticado()) {
    return router.createUrlTree(['/login']);
  }
  return sesion.esAdmin() || router.createUrlTree(['/mi-cuenta']);
};

/** Login y registro: si ya hay sesión, no tiene sentido mostrarlos. */
export const invitadoGuard: CanActivateFn = () => {
  const sesion = inject(SesionService);
  return !sesion.autenticado() || inject(Router).createUrlTree([sesion.rutaInicio()]);
};
