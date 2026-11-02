import { Prestamo } from './prestamo.model';
import { Usuario } from './usuario.model';

// Refleja MultaModel del backend
export interface Multa {
  id: number;
  usuario: Usuario;
  prestamo: Prestamo;
  valor: number;
  diasRetraso: number;
  pagada: boolean;
}
