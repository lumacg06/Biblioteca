import { Component, computed, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { AuthService } from './core/services/auth.service';
import { SesionService } from './core/services/sesion.service';
import { Notificaciones } from './shared/components/notificaciones/notificaciones';

interface Enlace {
  ruta: string;
  texto: string;
}

const ENLACES_ADMIN: Enlace[] = [
  { ruta: '/prestamos', texto: 'Préstamos' },
  { ruta: '/libros', texto: 'Libros' },
  { ruta: '/usuarios', texto: 'Usuarios' },
  { ruta: '/multas', texto: 'Multas' },
  { ruta: '/reportes', texto: 'Reportes' },
];

const ENLACES_LECTOR: Enlace[] = [
  { ruta: '/mi-cuenta', texto: 'Mi cuenta' },
  { ruta: '/catalogo', texto: 'Catálogo' },
];

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, Notificaciones],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  protected readonly sesion = inject(SesionService);

  // El menú cambia según el rol
  protected readonly enlaces = computed(() => (this.sesion.esAdmin() ? ENLACES_ADMIN : ENLACES_LECTOR));

  protected cerrarSesion(): void {
    const irAlLogin = () => this.router.navigate(['/login']);
    this.authService.cerrarSesion().subscribe({ complete: irAlLogin, error: irAlLogin });
  }
}
