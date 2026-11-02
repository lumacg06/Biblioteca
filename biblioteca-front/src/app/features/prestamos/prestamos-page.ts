import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin, map, switchMap } from 'rxjs';

import { Ejemplar } from '../../core/models/ejemplar.model';
import { Prestamo } from '../../core/models/prestamo.model';
import { REGLAS_TIPO_USUARIO, Usuario, esLector } from '../../core/models/usuario.model';
import { EjemplarService } from '../../core/services/ejemplar.service';
import { NotificacionService } from '../../core/services/notificacion.service';
import { PrestamoService } from '../../core/services/prestamo.service';
import { UsuarioService } from '../../core/services/usuario.service';
import { EstadoBadge } from '../../shared/components/estado-badge/estado-badge';
import { formatearPesos } from '../../shared/utils/dinero';
import { hoyIso } from '../../shared/utils/fecha';

/** RF03 (prestar), RF04 (devolver) y RF05 (aviso de multa al devolver tarde). */
@Component({
  selector: 'app-prestamos-page',
  imports: [ReactiveFormsModule, EstadoBadge],
  templateUrl: './prestamos-page.html',
})
export class PrestamosPage implements OnInit {
  private readonly prestamoService = inject(PrestamoService);
  private readonly usuarioService = inject(UsuarioService);
  private readonly ejemplarService = inject(EjemplarService);
  private readonly notificacion = inject(NotificacionService);
  private readonly fb = inject(NonNullableFormBuilder);

  protected readonly reglas = REGLAS_TIPO_USUARIO;
  protected readonly hoy = hoyIso();
  protected readonly usuarios = signal<Usuario[]>([]);
  protected readonly ejemplares = signal<Ejemplar[]>([]);
  protected readonly prestamos = signal<Prestamo[]>([]);
  protected readonly guardando = signal(false);

  // RN06: en el formulario solo se ofrecen los ejemplares DISPONIBLES
  protected readonly ejemplaresDisponibles = computed(() =>
    this.ejemplares().filter((e) => e.estado === 'DISPONIBLE'),
  );

  protected readonly prestamosOrdenados = computed(() => [...this.prestamos()].sort((a, b) => b.id - a.id));

  protected readonly form = this.fb.group({
    usuarioId: this.fb.control<number | null>(null, Validators.required),
    ejemplarId: this.fb.control<number | null>(null, Validators.required),
  });

  ngOnInit(): void {
    this.usuarioService.listar().subscribe({
      // Los administradores no toman préstamos
      next: (usuarios) => this.usuarios.set(usuarios.filter((u) => esLector(u.tipo))),
      error: () => this.usuarios.set([]),
    });
    this.recargar();
  }

  protected prestar(): void {
    const { usuarioId, ejemplarId } = this.form.getRawValue();
    if (usuarioId === null || ejemplarId === null) {
      this.form.markAllAsTouched();
      return;
    }
    this.guardando.set(true);
    // RN01, RN02, RN04 y RN06 se validan en el backend; si falla, el interceptor muestra el mensaje
    this.prestamoService.prestar(usuarioId, ejemplarId).subscribe({
      next: (prestamo) => {
        this.notificacion.exito(`Préstamo #${prestamo.id} registrado. Vence el ${prestamo.fechaVencimiento}`);
        this.form.reset();
        this.recargar();
        this.guardando.set(false);
      },
      error: () => this.guardando.set(false),
    });
  }

  protected devolver(prestamo: Prestamo): void {
    this.guardando.set(true);
    // El backend crea la multa pero devuelve solo el préstamo, así que se consultan las multas del usuario
    this.prestamoService
      .devolver(prestamo.id)
      .pipe(
        switchMap((devuelto) =>
          this.usuarioService
            .multas(devuelto.usuario.id)
            .pipe(map((multas) => multas.find((m) => m.prestamo.id === devuelto.id))),
        ),
      )
      .subscribe({
        next: (multa) => {
          if (multa) {
            // RF05 + RN03
            this.notificacion.aviso(
              `Devuelto con ${multa.diasRetraso} días de retraso. Multa de ${formatearPesos(multa.valor)}`,
            );
          } else {
            this.notificacion.exito(`Préstamo #${prestamo.id} devuelto a tiempo, sin multa`);
          }
          this.recargar();
          this.guardando.set(false);
        },
        error: () => {
          this.recargar();
          this.guardando.set(false);
        },
      });
  }

  protected estaVencido(prestamo: Prestamo): boolean {
    return prestamo.estado === 'ACTIVO' && prestamo.fechaVencimiento < this.hoy;
  }

  private recargar(): void {
    forkJoin({
      prestamos: this.prestamoService.listar(),
      ejemplares: this.ejemplarService.listar(),
    }).subscribe({
      next: ({ prestamos, ejemplares }) => {
        this.prestamos.set(prestamos);
        this.ejemplares.set(ejemplares);
      },
      error: () => {
        this.prestamos.set([]);
        this.ejemplares.set([]);
      },
    });
  }
}
