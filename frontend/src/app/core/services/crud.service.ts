import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

/** Operaciones REST comunes; cada servicio concreto indica su recurso. */
export abstract class CrudService<T, ID extends string | number = number> {
  protected readonly http = inject(HttpClient);
  protected abstract readonly recurso: string;

  protected get url(): string {
    return `${environment.apiUrl}/${this.recurso}`;
  }

  listar(): Observable<T[]> {
    return this.http.get<T[]>(this.url);
  }

  obtener(id: ID): Observable<T> {
    return this.http.get<T>(`${this.url}/${id}`);
  }

  crear(datos: Partial<T>): Observable<T> {
    return this.http.post<T>(this.url, datos);
  }

  actualizar(id: ID, datos: Partial<T>): Observable<T> {
    return this.http.put<T>(`${this.url}/${id}`, datos);
  }

  eliminar(id: ID): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
