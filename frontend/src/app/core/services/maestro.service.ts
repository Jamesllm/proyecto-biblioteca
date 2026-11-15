import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export type RecursoMaestro = 'autores' | 'categorias' | 'editoriales';

/** Acceso REST a las tablas maestras: autores, categorías y editoriales. */
@Injectable({ providedIn: 'root' })
export class MaestroService {
  private readonly http = inject(HttpClient);

  listar<T>(recurso: RecursoMaestro): Observable<T[]> {
    return this.http.get<T[]>(this.url(recurso));
  }

  obtener<T>(recurso: RecursoMaestro, id: string): Observable<T> {
    return this.http.get<T>(`${this.url(recurso)}/${id}`);
  }

  crear<T>(recurso: RecursoMaestro, datos: object): Observable<T> {
    return this.http.post<T>(this.url(recurso), datos);
  }

  actualizar<T>(recurso: RecursoMaestro, id: string, datos: object): Observable<T> {
    return this.http.put<T>(`${this.url(recurso)}/${id}`, datos);
  }

  eliminar(recurso: RecursoMaestro, id: number): Observable<void> {
    return this.http.delete<void>(`${this.url(recurso)}/${id}`);
  }

  private url(recurso: RecursoMaestro): string {
    return `${environment.apiUrl}/${recurso}`;
  }
}
