import { Component, OnInit, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { REGLAS_TIPO_USUARIO, TipoUsuario, Usuario, esLector } from '../../core/models/usuario.model';
import { NotificacionService } from '../../core/services/notificacion.service';
import { UsuarioService } from '../../core/services/usuario.service';

/** RF02: registrar usuarios. */
@Component({
  selector: 'app-usuarios-page',
  imports: [ReactiveFormsModule],
  templateUrl: './usuarios-page.html',
})
export class UsuariosPage implements OnInit {
  private readonly usuarioService = inject(UsuarioService);
  private readonly notificacion = inject(NotificacionService);
  private readonly fb = inject(NonNullableFormBuilder);

  protected readonly reglas = REGLAS_TIPO_USUARIO;
  protected readonly usuarios = signal<Usuario[]>([]);
  protected readonly guardando = signal(false);

  protected readonly form = this.fb.group({
    nombre: ['', Validators.required],
    documento: ['', Validators.required],
    contrasena: ['', [Validators.required, Validators.minLength(6)]],
    tipo: this.fb.control<TipoUsuario>('ESTUDIANTE'),
  });

  protected limiteYPlazo(tipo: TipoUsuario): string {
    if (!esLector(tipo)) {
      return 'Sin préstamos';
    }
    const reglas = REGLAS_TIPO_USUARIO[tipo];
    return `${reglas.limitePrestamos} préstamos / ${reglas.plazoDias} días`;
  }

  ngOnInit(): void {
    this.usuarioService.listar().subscribe({
      next: (usuarios) => this.usuarios.set(usuarios),
      error: () => this.usuarios.set([]),
    });
  }

  protected registrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.guardando.set(true);
    this.usuarioService.crear(this.form.getRawValue()).subscribe({
      next: (usuario) => {
        this.usuarios.update((lista) => [...lista, usuario]);
        this.form.reset();
        this.notificacion.exito(`Usuario ${usuario.nombre} registrado`);
        this.guardando.set(false);
      },
      error: () => this.guardando.set(false),
    });
  }

  protected eliminar(usuario: Usuario): void {
    if (!confirm(`¿Eliminar a ${usuario.nombre}?`)) {
      return;
    }
    this.guardando.set(true);
    this.usuarioService.eliminar(usuario.id).subscribe({
      next: () => {
        this.usuarios.update((lista) => lista.filter((u) => u.id !== usuario.id));
        this.notificacion.exito(`Usuario ${usuario.nombre} eliminado`);
        this.guardando.set(false);
      },
      error: () => this.guardando.set(false),
    });
  }
}
