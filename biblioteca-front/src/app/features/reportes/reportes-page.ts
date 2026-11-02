import { Component, OnInit, computed, inject, signal } from '@angular/core';

import { ESTADOS_EJEMPLAR, Inventario } from '../../core/models/ejemplar.model';
import { Prestamo } from '../../core/models/prestamo.model';
import { EjemplarService } from '../../core/services/ejemplar.service';
import { PrestamoService } from '../../core/services/prestamo.service';
import { EstadoBadge } from '../../shared/components/estado-badge/estado-badge';
import { diasEntre, hoyIso } from '../../shared/utils/fecha';

/** RF08: reportes de préstamos vencidos y de inventario. */
@Component({
  selector: 'app-reportes-page',
  imports: [EstadoBadge],
  templateUrl: './reportes-page.html',
})
export class ReportesPage implements OnInit {
  private readonly prestamoService = inject(PrestamoService);
  private readonly ejemplarService = inject(EjemplarService);

  protected readonly estados = ESTADOS_EJEMPLAR;
  protected readonly hoy = hoyIso();
  protected readonly vencidos = signal<Prestamo[]>([]);
  protected readonly inventario = signal<Inventario | null>(null);

  protected readonly totalEjemplares = computed(() => {
    const inventario = this.inventario();
    return inventario ? this.estados.reduce((total, estado) => total + (inventario[estado] ?? 0), 0) : 0;
  });

  ngOnInit(): void {
    this.actualizar();
  }

  protected actualizar(): void {
    this.prestamoService.vencidos().subscribe({
      next: (vencidos) => this.vencidos.set(vencidos),
      error: () => this.vencidos.set([]),
    });
    this.ejemplarService.inventario().subscribe({
      next: (inventario) => this.inventario.set(inventario),
      error: () => this.inventario.set(null),
    });
  }

  protected diasVencido(prestamo: Prestamo): number {
    return diasEntre(prestamo.fechaVencimiento, this.hoy);
  }
}
