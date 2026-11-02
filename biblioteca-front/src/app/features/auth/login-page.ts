import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';
import { SesionService } from '../../core/services/sesion.service';

@Component({
  selector: 'app-login-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login-page.html',
})
export class LoginPage {
  private readonly authService = inject(AuthService);
  private readonly sesion = inject(SesionService);
  private readonly router = inject(Router);
  private readonly fb = inject(NonNullableFormBuilder);

  protected readonly enviando = signal(false);

  protected readonly form = this.fb.group({
    documento: ['', Validators.required],
    contrasena: ['', Validators.required],
  });

  protected iniciarSesion(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.authService.iniciarSesion(this.form.getRawValue()).subscribe({
      next: () => this.router.navigateByUrl(this.sesion.rutaInicio()),
      // El mensaje ("Documento o contraseña incorrectos") lo muestra el interceptor
      error: () => this.enviando.set(false),
    });
  }
}
