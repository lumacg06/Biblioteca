import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../api.config';
import { Multa } from '../models/multa.model';

@Injectable({ providedIn: 'root' })
export class MultaService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/multas`;

  listar(): Observable<Multa[]> {
    return this.http.get<Multa[]>(this.url);
  }

  pagar(id: number): Observable<Multa> {
    return this.http.put<Multa>(`${this.url}/${id}/pagar`, null);
  }
}
