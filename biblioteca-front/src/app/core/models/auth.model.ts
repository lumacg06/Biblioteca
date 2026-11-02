import { TipoLector, Usuario } from './usuario.model';

export interface LoginRequest {
  documento: string;
  contrasena: string;
}

// Registro público: solo estudiantes y docentes
export interface RegistroRequest {
  nombre: string;
  documento: string;
  contrasena: string;
  tipo: TipoLector;
}

// Respuesta de POST /api/auth/login
export interface SesionUsuario {
  token: string;
  usuario: Usuario;
}
