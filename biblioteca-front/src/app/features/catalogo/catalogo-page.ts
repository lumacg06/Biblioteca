import { Component, OnInit, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule } from '@angular/forms';

import { CampoBusquedaLibro, DisponibilidadLibro } from '../../core/models/libro.model';
import { LibroService } from '../../core/services/libro.service';

/** RF06 para estudiantes y docentes: buscar libros y ver si hay ejemplares disponibles. */
@Component({
  selector: 'app-catalogo-page',
  imports: [ReactiveFormsModule],
  templateUrl: './catalogo-page.html',
})
export class CatalogoPage implements OnInit {
  private readonly libroService = inject(LibroService);
  private readonly fb = inject(NonNullableFormBuilder);

  protected readonly libros = signal<DisponibilidadLibro[]>([]);

  protected readonly form = this.fb.group({
    campo: this.fb.control<CampoBusquedaLibro>('titulo'),
    texto: [''],
  });

  ngOnInit(): void {
    this.buscar();
  }

  protected buscar(): void {
    const { campo, texto } = this.form.getRawValue();
    this.libroService.disponibilidad(campo, texto).subscribe({
      next: (libros) => this.libros.set(libros),
      error: () => this.libros.set([]),
    });
  }

  protected limpiar(): void {
    this.form.reset();
    this.buscar();
  }
}
