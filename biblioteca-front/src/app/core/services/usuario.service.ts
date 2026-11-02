import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../api.config';
import { Multa } from '../models/multa.model';
import { Prestamo } from '../models/prestamo.model';
import { Usuario, UsuarioRequest } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/usuarios`;

  listar(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.url);
  }

  crear(usuario: UsuarioRequest): Observable<Usuario> {
    return this.http.post<Usuario>(this.url, usuario);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  prestamos(id: number): Observable<Prestamo[]> {
    return this.http.get<Prestamo[]>(`${this.url}/${id}/prestamos`);
  }

  multas(id: number): Observable<Multa[]> {
    return this.http.get<Multa[]>(`${this.url}/${id}/multas`);
  }
}
