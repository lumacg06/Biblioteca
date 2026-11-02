import { Ejemplar } from './ejemplar.model';
import { Usuario } from './usuario.model';

// Refleja PrestamoModel del backend. Las fechas llegan como texto 'yyyy-MM-dd'
export type EstadoPrestamo = 'ACTIVO' | 'DEVUELTO' | 'PERDIDO';

export interface Prestamo {
  id: number;
  usuario: Usuario;
  ejemplar: Ejemplar;
  fechaPrestamo: string;
  fechaVencimiento: string;
  fechaDevolucion: string | null;
  estado: EstadoPrestamo;
}
