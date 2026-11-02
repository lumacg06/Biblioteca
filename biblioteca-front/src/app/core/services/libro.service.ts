import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../api.config';
import { Ejemplar, EjemplarRequest } from '../models/ejemplar.model';
import { CampoBusquedaLibro, DisponibilidadLibro, Libro, LibroRequest } from '../models/libro.model';

@Injectable({ providedIn: 'root' })
export class LibroService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/libros`;

  /** Sin texto lista todos; con texto busca por el campo elegido (RF06). */
  buscar(campo: CampoBusquedaLibro, texto: string): Observable<Libro[]> {
    return this.http.get<Libro[]>(this.url, { params: crearFiltro(campo, texto) });
  }

  /** Catálogo con ejemplares disponibles: lo puede usar cualquier usuario con sesión. */
  disponibilidad(campo: CampoBusquedaLibro, texto: string): Observable<DisponibilidadLibro[]> {
    return this.http.get<DisponibilidadLibro[]>(`${this.url}/disponibilidad`, { params: crearFiltro(campo, texto) });
  }

  crear(libro: LibroRequest): Observable<Libro> {
    return this.http.post<Libro>(this.url, libro);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  agregarEjemplar(libroId: number, ejemplar: EjemplarRequest): Observable<Ejemplar> {
    return this.http.post<Ejemplar>(`${this.url}/${libroId}/ejemplares`, ejemplar);
  }
}

function crearFiltro(campo: CampoBusquedaLibro, texto: string): HttpParams | undefined {
  const valor = texto.trim();
  return valor ? new HttpParams().set(campo, valor) : undefined;
}
