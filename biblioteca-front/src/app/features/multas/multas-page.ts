import { Component, OnInit, computed, inject, signal } from '@angular/core';

import { Multa } from '../../core/models/multa.model';
import { Usuario } from '../../core/models/usuario.model';
import { MultaService } from '../../core/services/multa.service';
import { NotificacionService } from '../../core/services/notificacion.service';
import { UsuarioService } from '../../core/services/usuario.service';
import { PesosPipe } from '../../shared/pipes/pesos.pipe';
import { formatearPesos } from '../../shared/utils/dinero';

/** RN07: consultar y pagar multas. */
@Component({
  selector: 'app-multas-page',
  imports: [PesosPipe],
  templateUrl: './multas-page.html',
})
export class MultasPage implements OnInit {
  private readonly multaService = inject(MultaService);
  private readonly usuarioService = inject(UsuarioService);
  private readonly notificacion = inject(NotificacionService);

  protected readonly usuarios = signal<Usuario[]>([]);
  protected readonly multas = signal<Multa[]>([]);
  protected readonly usuarioFiltroId = signal<number | null>(null);
  protected readonly guardando = signal(false);

  protected readonly totalPendiente = computed(() =>
    this.multas()
      .filter((m) => !m.pagada)
      .reduce((total, m) => total + m.valor, 0),
  );

  ngOnInit(): void {
    this.usuarioService.listar().subscribe({
      next: (usuarios) => this.usuarios.set(usuarios),
      error: () => this.usuarios.set([]),
    });
    this.cargar();
  }

  protected filtrarPorUsuario(valor: string): void {
    this.usuarioFiltroId.set(valor ? Number(valor) : null);
    this.cargar();
  }

  protected pagar(multa: Multa): void {
    if (!confirm(`¿Registrar el pago de ${formatearPesos(multa.valor)} de ${multa.usuario.nombre}?`)) {
      return;
    }
    this.guardando.set(true);
    this.multaService.pagar(multa.id).subscribe({
      next: (pagada) => {
        this.multas.update((lista) => lista.map((m) => (m.id === pagada.id ? pagada : m)));
        this.notificacion.exito(`Multa #${pagada.id} pagada`);
        this.guardando.set(false);
      },
      error: () => this.guardando.set(false),
    });
  }

  private cargar(): void {
    const usuarioId = this.usuarioFiltroId();
    const peticion = usuarioId === null ? this.multaService.listar() : this.usuarioService.multas(usuarioId);
    peticion.subscribe({
      next: (multas) => this.multas.set(multas),
      error: () => this.multas.set([]),
    });
  }
}
