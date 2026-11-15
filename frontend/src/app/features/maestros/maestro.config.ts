import { RecursoMaestro } from '../../core/services/maestro.service';

export interface CampoMaestro {
  nombre: string;
  etiqueta: string;
  tipo: 'texto' | 'area';
  requerido?: boolean;
  maxLength?: number;
}

export interface MaestroConfig {
  recurso: RecursoMaestro;
  titulo: string;
  singular: string;
  campos: CampoMaestro[];
}

/** Los límites de longitud provienen del diccionario de datos del proyecto. */
export const MAESTROS: MaestroConfig[] = [
  {
    recurso: 'autores',
    titulo: 'Autores',
    singular: 'autor',
    campos: [
      { nombre: 'nombre', etiqueta: 'Nombre', tipo: 'texto', requerido: true, maxLength: 255 },
      { nombre: 'nacionalidad', etiqueta: 'Nacionalidad', tipo: 'texto', maxLength: 100 },
      { nombre: 'biografia', etiqueta: 'Biografía', tipo: 'area' },
    ],
  },
  {
    recurso: 'categorias',
    titulo: 'Categorías',
    singular: 'categoría',
    campos: [
      { nombre: 'nombre', etiqueta: 'Nombre', tipo: 'texto', requerido: true, maxLength: 100 },
      { nombre: 'descripcion', etiqueta: 'Descripción', tipo: 'area' },
    ],
  },
  {
    recurso: 'editoriales',
    titulo: 'Editoriales',
    singular: 'editorial',
    campos: [
      { nombre: 'nombre', etiqueta: 'Nombre', tipo: 'texto', requerido: true, maxLength: 150 },
      { nombre: 'pais', etiqueta: 'País', tipo: 'texto', maxLength: 100 },
      { nombre: 'contacto', etiqueta: 'Contacto', tipo: 'texto', maxLength: 150 },
    ],
  },
];
