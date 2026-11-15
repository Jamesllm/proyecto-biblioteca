import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, Multa, PrestamoDetallado, RegisterRequest, Usuario } from '../models/models';
import { CrudService } from './crud.service';

@Injectable({ providedIn: 'root' })
export class UsuarioService extends CrudService<Usuario> {
  protected readonly recurso = 'usuarios';

  /**
   * Las cuentas nuevas se crean con /auth/register porque ese endpoint cifra la contraseña con BCrypt.
   * El token de la respuesta se descarta para no reemplazar la sesión del administrador.
   */
  registrar(datos: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/register`, datos);
  }

  prestamos(id: number): Observable<PrestamoDetallado[]> {
    return this.http.get<PrestamoDetallado[]>(`${this.url}/${id}/prestamos`);
  }

  prestamosActivos(id: number): Observable<PrestamoDetallado[]> {
    return this.http.get<PrestamoDetallado[]>(`${this.url}/${id}/prestamos/activos`);
  }

  multas(id: number): Observable<Multa[]> {
    return this.http.get<Multa[]>(`${this.url}/${id}/multas`);
  }
}
