import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, finalize, tap } from 'rxjs';

import { API_URL } from '../api.config';
import { LoginRequest, RegistroRequest, SesionUsuario } from '../models/auth.model';
import { Usuario } from '../models/usuario.model';
import { SesionService } from './sesion.service';

/** Peticiones de login, registro y logout. El estado de la sesión lo guarda SesionService. */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly sesion = inject(SesionService);
  private readonly url = `${API_URL}/auth`;

  iniciarSesion(datos: LoginRequest): Observable<SesionUsuario> {
    return this.http.post<SesionUsuario>(`${this.url}/login`, datos).pipe(tap((s) => this.sesion.iniciar(s)));
  }

  registrar(datos: RegistroRequest): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.url}/registro`, datos);
  }

  /** Avisa al backend para invalidar el token y, pase lo que pase, limpia la sesión local. */
  cerrarSesion(): Observable<void> {
    return this.http.post<void>(`${this.url}/logout`, null).pipe(finalize(() => this.sesion.cerrar()));
  }
}
