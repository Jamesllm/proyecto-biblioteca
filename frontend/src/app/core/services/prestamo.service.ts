import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DevolucionRequest, Prestamo, PrestamoDetallado } from '../models/models';

@Injectable({ providedIn: 'root' })
export class PrestamoService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/prestamos`;

  listar(): Observable<Prestamo[]> {
    return this.http.get<Prestamo[]>(this.url);
  }

  detalle(id: string): Observable<PrestamoDetallado> {
    return this.http.get<PrestamoDetallado>(`${this.url}/${id}/detalle`);
  }

  registrar(idEjemplar: number, idUsuario: number): Observable<Prestamo> {
    return this.http.post<Prestamo>(this.url, { idEjemplar, idUsuario });
  }

  devolver(id: string, datos: DevolucionRequest): Observable<Prestamo> {
    return this.http.put<Prestamo>(`${this.url}/${id}/devolver`, datos);
  }
}
