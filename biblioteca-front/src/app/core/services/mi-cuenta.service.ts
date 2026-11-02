import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../api.config';
import { Multa } from '../models/multa.model';
import { Prestamo } from '../models/prestamo.model';

/** Datos del usuario que inició sesión. El backend sabe quién es por el token. */
@Injectable({ providedIn: 'root' })
export class MiCuentaService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/mi-cuenta`;

  prestamos(): Observable<Prestamo[]> {
    return this.http.get<Prestamo[]>(`${this.url}/prestamos`);
  }

  multas(): Observable<Multa[]> {
    return this.http.get<Multa[]>(`${this.url}/multas`);
  }
}
