import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../api.config';
import { Ejemplar, Inventario } from '../models/ejemplar.model';

@Injectable({ providedIn: 'root' })
export class EjemplarService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/ejemplares`;

  listar(): Observable<Ejemplar[]> {
    return this.http.get<Ejemplar[]>(this.url);
  }

  inventario(): Observable<Inventario> {
    return this.http.get<Inventario>(`${this.url}/inventario`);
  }

  marcarComoDanado(id: number): Observable<Ejemplar> {
    return this.http.put<Ejemplar>(`${this.url}/${id}/danado`, null);
  }

  marcarComoPerdido(id: number): Observable<Ejemplar> {
    return this.http.put<Ejemplar>(`${this.url}/${id}/perdido`, null);
  }
}
