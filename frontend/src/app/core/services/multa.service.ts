import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Multa, PagoMulta, PagoMultaRequest } from '../models/models';

@Injectable({ providedIn: 'root' })
export class MultaService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/multas`;

  listar(): Observable<Multa[]> {
    return this.http.get<Multa[]>(this.url);
  }

  obtener(id: string): Observable<Multa> {
    return this.http.get<Multa>(`${this.url}/${id}`);
  }

  pendientesDeUsuario(idUsuario: number): Observable<Multa[]> {
    return this.http.get<Multa[]>(`${this.url}/usuario/${idUsuario}/pendientes`);
  }

  pagar(id: string, datos: PagoMultaRequest): Observable<PagoMulta> {
    return this.http.post<PagoMulta>(`${this.url}/${id}/pagar`, datos);
  }

  listarPagos(): Observable<PagoMulta[]> {
    return this.http.get<PagoMulta[]>(`${environment.apiUrl}/pagos-multas`);
  }
}
