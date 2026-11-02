import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { switchMap } from 'rxjs';

import { REGLAS_TIPO_USUARIO, TipoLector } from '../../core/models/usuario.model';
import { AuthService } from '../../core/services/auth.service';
import { NotificacionService } from '../../core/services/notificacion.service';

const LONGITUD_MINIMA_CONTRASENA = 6;

@Component({
  selector: 'app-registro-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './registro-page.html',
})
export class RegistroPage {
  private readonly authService = inject(AuthService);
  private readonly notificacion = inject(NotificacionService);
  private readonly router = inject(Router);
  private readonly fb = inject(NonNullableFormBuilder);

  protected readonly reglas = REGLAS_TIPO_USUARIO;
  protected readonly longitudMinima = LONGITUD_MINIMA_CONTRASENA;
  protected readonly enviando = signal(false);
  protected readonly contrasenasDistintas = signal(false);

  protected readonly form = this.fb.group({
    nombre: ['', Validators.required],
    documento: ['', Validators.required],
    tipo: this.fb.control<TipoLector>('ESTUDIANTE'),
    contrasena: ['', [Validators.required, Validators.minLength(LONGITUD_MINIMA_CONTRASENA)]],
    confirmacion: ['', Validators.required],
  });

  protected registrar(): void {
    const { confirmacion, ...datos } = this.form.getRawValue();
    this.contrasenasDistintas.set(datos.contrasena !== confirmacion);
    if (this.form.invalid || this.contrasenasDistintas()) {
      this.form.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    // Después de crear la cuenta se inicia sesión automáticamente
    this.authService
      .registrar(datos)
      .pipe(switchMap(() => this.authService.iniciarSesion({ documento: datos.documento, contrasena: datos.contrasena })))
      .subscribe({
        next: ({ usuario }) => {
          this.notificacion.exito(`Bienvenido, ${usuario.nombre}`);
          this.router.navigate(['/mi-cuenta']);
        },
        error: () => this.enviando.set(false),
      });
  }
}
