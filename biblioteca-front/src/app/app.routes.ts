import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { adminGuard, autenticadoGuard, invitadoGuard } from './core/guards/auth.guards';
import { SesionService } from './core/services/sesion.service';

// Cada pantalla se carga solo cuando se visita (lazy loading)
export const routes: Routes = [
  // La pantalla de inicio depende del rol: el admin va a Préstamos, los demás a Mi cuenta
  { path: '', pathMatch: 'full', redirectTo: () => inject(SesionService).rutaInicio() },

  // Sin sesión
  {
    path: 'login',
    title: 'Iniciar sesión | Biblioteca',
    canActivate: [invitadoGuard],
    loadComponent: () => import('./features/auth/login-page').then((m) => m.LoginPage),
  },
  {
    path: 'registro',
    title: 'Crear cuenta | Biblioteca',
    canActivate: [invitadoGuard],
    loadComponent: () => import('./features/auth/registro-page').then((m) => m.RegistroPage),
  },

  // Cualquier usuario con sesión
  {
    path: 'mi-cuenta',
    title: 'Mi cuenta | Biblioteca',
    canActivate: [autenticadoGuard],
    loadComponent: () => import('./features/mi-cuenta/mi-cuenta-page').then((m) => m.MiCuentaPage),
  },
  {
    path: 'catalogo',
    title: 'Catálogo | Biblioteca',
    canActivate: [autenticadoGuard],
    loadComponent: () => import('./features/catalogo/catalogo-page').then((m) => m.CatalogoPage),
  },

  // Solo ADMIN
  {
    path: 'prestamos',
    title: 'Préstamos | Biblioteca',
    canActivate: [adminGuard],
    loadComponent: () => import('./features/prestamos/prestamos-page').then((m) => m.PrestamosPage),
  },
  {
    path: 'libros',
    title: 'Libros | Biblioteca',
    canActivate: [adminGuard],
    loadComponent: () => import('./features/libros/libros-page').then((m) => m.LibrosPage),
  },
  {
    path: 'usuarios',
    title: 'Usuarios | Biblioteca',
    canActivate: [adminGuard],
    loadComponent: () => import('./features/usuarios/usuarios-page').then((m) => m.UsuariosPage),
  },
  {
    path: 'multas',
    title: 'Multas | Biblioteca',
    canActivate: [adminGuard],
    loadComponent: () => import('./features/multas/multas-page').then((m) => m.MultasPage),
  },
  {
    path: 'reportes',
    title: 'Reportes | Biblioteca',
    canActivate: [adminGuard],
    loadComponent: () => import('./features/reportes/reportes-page').then((m) => m.ReportesPage),
  },

  { path: '**', redirectTo: '' },
];
