import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Ejemplar, Libro } from '../models/models';
import { CrudService } from './crud.service';

@Injectable({ providedIn: 'root' })
export class LibroService extends CrudService<Libro, string> {
  protected readonly recurso = 'libros';

  ejemplares(isbn: string): Observable<Ejemplar[]> {
    return this.http.get<Ejemplar[]>(`${this.url}/${isbn}/ejemplares`);
  }

  ejemplaresDisponibles(isbn: string): Observable<Ejemplar[]> {
    return this.http.get<Ejemplar[]>(`${this.url}/${isbn}/ejemplares/disponibles`);
  }
}
