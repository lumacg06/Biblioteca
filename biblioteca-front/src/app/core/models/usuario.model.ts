// Refleja UsuarioModel del backend
export type TipoUsuario = 'ESTUDIANTE' | 'DOCENTE' | 'ADMIN';

// Los tipos que pueden tener préstamos (y registrarse solos)
export type TipoLector = Exclude<TipoUsuario, 'ADMIN'>;

export interface Usuario {
  id: number;
  nombre: string;
  documento: string;
  tipo: TipoUsuario;
}

// Lo usa el admin para crear cuentas de cualquier tipo
export interface UsuarioRequest {
  nombre: string;
  documento: string;
  contrasena: string;
  tipo: TipoUsuario;
}

// Solo para mostrar al usuario: las reglas reales (RN01 y RN02) se validan en el backend
export const REGLAS_TIPO_USUARIO: Record<TipoLector, { limitePrestamos: number; plazoDias: number }> = {
  ESTUDIANTE: { limitePrestamos: 3, plazoDias: 7 },
  DOCENTE: { limitePrestamos: 5, plazoDias: 15 },
};

export function esLector(tipo: TipoUsuario): tipo is TipoLector {
  return tipo !== 'ADMIN';
}
