import { Libro } from './libro.model';
import { Usuario } from './usuario.model';

// Refleja EjemplarModel del backend
export type EstadoEjemplar = 'DISPONIBLE' | 'PRESTADO' | 'PERDIDO' | 'DANADO';

export const ESTADOS_EJEMPLAR: EstadoEjemplar[] = ['DISPONIBLE', 'PRESTADO', 'PERDIDO', 'DANADO'];

export interface Ejemplar {
  id: number;
  codigo: string;
  estado: EstadoEjemplar;
  libro: Libro;
  usuarioResponsable: Usuario | null; // RN05: se llena cuando el ejemplar se pierde
}

export interface EjemplarRequest {
  codigo: string;
}

// GET /api/ejemplares/inventario devuelve { "DISPONIBLE": 3, "PRESTADO": 1, ... }
export type Inventario = Record<EstadoEjemplar, number>;
