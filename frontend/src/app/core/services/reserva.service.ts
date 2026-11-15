import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Reserva } from '../models/models';

@Injectable({ providedIn: 'root' })
export class ReservaService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/reservas`;

  listar(): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(this.url);
  }

  deUsuario(idUsuario: number): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(`${this.url}/usuario/${idUsuario}`);
  }

  obtener(id: string): Observable<Reserva> {
    return this.http.get<Reserva>(`${this.url}/${id}`);
  }

  crear(isbn: string, idUsuario: number, fechaHoraReserva: string): Observable<Reserva> {
    return this.http.post<Reserva>(this.url, { isbn, idUsuario, fechaHoraReserva });
  }

  // Estos dos endpoints responden texto plano, no JSON.
  cancelar(id: number): Observable<string> {
    return this.http.put(`${this.url}/${id}/cancelar`, null, { responseType: 'text' });
  }

  atender(id: number): Observable<string> {
    return this.http.put(`${this.url}/${id}/atender`, null, { responseType: 'text' });
  }
}
