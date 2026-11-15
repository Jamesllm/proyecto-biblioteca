import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ReporteConteo, ReporteDashboard, ReporteFinanciero } from '../models/models';

@Injectable({ providedIn: 'root' })
export class ReporteService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/reportes`;

  dashboard(): Observable<ReporteDashboard> {
    return this.http.get<ReporteDashboard>(`${this.url}/dashboard`);
  }

  librosPorCategoria(): Observable<ReporteConteo> {
    return this.http.get<ReporteConteo>(`${this.url}/libros-por-categoria`);
  }

  prestamos(): Observable<ReporteConteo> {
    return this.http.get<ReporteConteo>(`${this.url}/prestamos`);
  }

  financieroMultas(): Observable<ReporteFinanciero> {
    return this.http.get<ReporteFinanciero>(`${this.url}/financiero-multas`);
  }
}
