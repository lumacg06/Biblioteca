import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { forkJoin } from 'rxjs';

import { Multa } from '../../core/models/multa.model';
import { Prestamo } from '../../core/models/prestamo.model';
import { REGLAS_TIPO_USUARIO, esLector } from '../../core/models/usuario.model';
import { MiCuentaService } from '../../core/services/mi-cuenta.service';
import { SesionService } from '../../core/services/sesion.service';
import { EstadoBadge } from '../../shared/components/estado-badge/estado-badge';
import { PesosPipe } from '../../shared/pipes/pesos.pipe';
import { diasEntre, hoyIso } from '../../shared/utils/fecha';

/** Lo que ve un estudiante o docente: solo SUS préstamos y SUS multas. */
@Component({
  selector: 'app-mi-cuenta-page',
  imports: [EstadoBadge, PesosPipe],
  templateUrl: './mi-cuenta-page.html',
})
export class MiCuentaPage implements OnInit {
  private readonly miCuentaService = inject(MiCuentaService);
  protected readonly sesion = inject(SesionService);

  protected readonly hoy = hoyIso();
  protected readonly prestamos = signal<Prestamo[]>([]);
  protected readonly multas = signal<Multa[]>([]);

  protected readonly reglas = computed(() => {
    const tipo = this.sesion.usuario()?.tipo;
    return tipo && esLector(tipo) ? REGLAS_TIPO_USUARIO[tipo] : null;
  });

  protected readonly prestamosActivos = computed(() => this.prestamos().filter((p) => p.estado === 'ACTIVO'));
  protected readonly historial = computed(() => this.prestamos().filter((p) => p.estado !== 'ACTIVO'));
  protected readonly multasPendientes = computed(() => this.multas().filter((m) => !m.pagada));
  protected readonly totalPendiente = computed(() =>
    this.multasPendientes().reduce((total, m) => total + m.valor, 0),
  );

  ngOnInit(): void {
    forkJoin({ prestamos: this.miCuentaService.prestamos(), multas: this.miCuentaService.multas() }).subscribe({
      next: ({ prestamos, multas }) => {
        this.prestamos.set([...prestamos].sort((a, b) => b.id - a.id));
        this.multas.set(multas);
      },
      error: () => {
        this.prestamos.set([]);
        this.multas.set([]);
      },
    });
  }

  /** Positivo: días que faltan. Negativo: días de retraso. */
  protected diasRestantes(prestamo: Prestamo): number {
    return diasEntre(this.hoy, prestamo.fechaVencimiento);
  }
}
