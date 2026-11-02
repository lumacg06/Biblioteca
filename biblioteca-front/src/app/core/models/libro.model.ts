// Refleja LibroModel del backend
export interface Libro {
  id: number;
  titulo: string;
  autor: string;
  isbn: string;
}

export interface LibroRequest {
  titulo: string;
  autor: string;
  isbn: string;
}

export type CampoBusquedaLibro = 'titulo' | 'autor' | 'isbn';

// RF06: GET /api/libros/disponibilidad (catálogo para cualquier usuario)
export interface DisponibilidadLibro {
  libroId: number;
  titulo: string;
  autor: string;
  isbn: string;
  totalEjemplares: number;
  ejemplaresDisponibles: number;
}
