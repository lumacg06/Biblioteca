import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../api.config';
import { Prestamo } from '../models/prestamo.model';

@Injectable({ providedIn: 'root' })
export class PrestamoService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/prestamos`;

  listar(): Observable<Prestamo[]> {
    return this.http.get<Prestamo[]>(this.url);
  }

  vencidos(): Observable<Prestamo[]> {
    return this.http.get<Prestamo[]>(`${this.url}/vencidos`);
  }

  /** El backend recibe los ids como query params: POST /api/prestamos?usuarioId=1&ejemplarId=2 */
  prestar(usuarioId: number, ejemplarId: number): Observable<Prestamo> {
    const params = new HttpParams().set('usuarioId', usuarioId).set('ejemplarId', ejemplarId);
    return this.http.post<Prestamo>(this.url, null, { params });
  }

  devolver(id: number): Observable<Prestamo> {
    return this.http.put<Prestamo>(`${this.url}/${id}/devolver`, null);
  }
}
