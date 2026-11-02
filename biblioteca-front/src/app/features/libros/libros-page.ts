import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { Ejemplar } from '../../core/models/ejemplar.model';
import { CampoBusquedaLibro, Libro } from '../../core/models/libro.model';
import { EjemplarService } from '../../core/services/ejemplar.service';
import { LibroService } from '../../core/services/libro.service';
import { NotificacionService } from '../../core/services/notificacion.service';
import { EstadoBadge } from '../../shared/components/estado-badge/estado-badge';

/** RF01 (libros y ejemplares), RF06 (búsqueda y disponibilidad) y RF07 (perdido / dañado). */
@Component({
  selector: 'app-libros-page',
  imports: [ReactiveFormsModule, EstadoBadge],
  templateUrl: './libros-page.html',
})
export class LibrosPage implements OnInit {
  private readonly libroService = inject(LibroService);
  private readonly ejemplarService = inject(EjemplarService);
  private readonly notificacion = inject(NotificacionService);
  private readonly fb = inject(NonNullableFormBuilder);

  protected readonly libros = signal<Libro[]>([]);
  protected readonly ejemplares = signal<Ejemplar[]>([]);
  protected readonly libroAbiertoId = signal<number | null>(null);
  protected readonly guardando = signal(false);

  // Se piden todos los ejemplares una vez y se agrupan por libro (evita una petición por libro)
  private readonly ejemplaresPorLibro = computed(() => {
    const mapa = new Map<number, Ejemplar[]>();
    for (const ejemplar of this.ejemplares()) {
      const lista = mapa.get(ejemplar.libro.id) ?? [];
      lista.push(ejemplar);
      mapa.set(ejemplar.libro.id, lista);
    }
    return mapa;
  });

  protected readonly formLibro = this.fb.group({
    titulo: ['', Validators.required],
    autor: ['', Validators.required],
    isbn: ['', Validators.required],
  });

  protected readonly formBusqueda = this.fb.group({
    campo: this.fb.control<CampoBusquedaLibro>('titulo'),
    texto: [''],
  });

  ngOnInit(): void {
    this.buscar();
    this.ejemplarService.listar().subscribe({
      next: (ejemplares) => this.ejemplares.set(ejemplares),
      error: () => this.ejemplares.set([]),
    });
  }

  protected buscar(): void {
    const { campo, texto } = this.formBusqueda.getRawValue();
    this.libroService.buscar(campo, texto).subscribe({
      next: (libros) => this.libros.set(libros),
      error: () => this.libros.set([]),
    });
  }

  protected limpiarBusqueda(): void {
    this.formBusqueda.reset();
    this.buscar();
  }

  protected registrarLibro(): void {
    if (this.formLibro.invalid) {
      this.formLibro.markAllAsTouched();
      return;
    }
    this.guardando.set(true);
    this.libroService.crear(this.formLibro.getRawValue()).subscribe({
      next: (libro) => {
        this.libros.update((lista) => [...lista, libro]);
        this.formLibro.reset();
        this.notificacion.exito(`Libro "${libro.titulo}" registrado`);
        this.guardando.set(false);
      },
      error: () => this.guardando.set(false),
    });
  }

  protected eliminarLibro(libro: Libro): void {
    if (!confirm(`¿Eliminar el libro "${libro.titulo}"?`)) {
      return;
    }
    this.guardando.set(true);
    this.libroService.eliminar(libro.id).subscribe({
      next: () => {
        this.libros.update((lista) => lista.filter((l) => l.id !== libro.id));
        this.notificacion.exito(`Libro "${libro.titulo}" eliminado`);
        this.guardando.set(false);
      },
      error: () => this.guardando.set(false),
    });
  }

  protected alternarEjemplares(libroId: number): void {
    this.libroAbiertoId.update((abierto) => (abierto === libroId ? null : libroId));
  }

  protected ejemplaresDe(libroId: number): Ejemplar[] {
    return this.ejemplaresPorLibro().get(libroId) ?? [];
  }

  protected disponiblesDe(libroId: number): number {
    return this.ejemplaresDe(libroId).filter((e) => e.estado === 'DISPONIBLE').length;
  }

  protected agregarEjemplar(libro: Libro, input: HTMLInputElement): void {
    const codigo = input.value.trim();
    if (!codigo) {
      this.notificacion.aviso('Escribe el código del ejemplar');
      return;
    }
    this.guardando.set(true);
    this.libroService.agregarEjemplar(libro.id, { codigo }).subscribe({
      next: (ejemplar) => {
        this.ejemplares.update((lista) => [...lista, ejemplar]);
        input.value = '';
        this.notificacion.exito(`Ejemplar ${ejemplar.codigo} agregado a "${libro.titulo}"`);
        this.guardando.set(false);
      },
      error: () => this.guardando.set(false),
    });
  }

  protected marcarComoDanado(ejemplar: Ejemplar): void {
    this.cambiarEstado(ejemplar, 'dañado');
  }

  protected marcarComoPerdido(ejemplar: Ejemplar): void {
    this.cambiarEstado(ejemplar, 'perdido');
  }

  private cambiarEstado(ejemplar: Ejemplar, nuevoEstado: 'dañado' | 'perdido'): void {
    if (!confirm(`¿Marcar el ejemplar ${ejemplar.codigo} como ${nuevoEstado}?`)) {
      return;
    }
    const peticion =
      nuevoEstado === 'perdido'
        ? this.ejemplarService.marcarComoPerdido(ejemplar.id)
        : this.ejemplarService.marcarComoDanado(ejemplar.id);

    this.guardando.set(true);
    peticion.subscribe({
      next: (actualizado) => {
        this.ejemplares.update((lista) => lista.map((e) => (e.id === actualizado.id ? actualizado : e)));
        const responsable = actualizado.usuarioResponsable ? ` (responsable: ${actualizado.usuarioResponsable.nombre})` : '';
        this.notificacion.exito(`Ejemplar ${actualizado.codigo} marcado como ${nuevoEstado}${responsable}`);
        this.guardando.set(false);
      },
      error: () => this.guardando.set(false),
    });
  }
}
