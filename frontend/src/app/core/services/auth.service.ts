import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  Rol,
  ROLES_ADMIN,
  ROLES_LECTOR,
  ROLES_PERSONAL,
  Sesion,
  Usuario,
} from '../models/models';

const CLAVE_SESION = 'biblioteca.sesion';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/auth`;
  private readonly sesionActual = signal<Sesion | null>(this.leerSesion());

  readonly sesion = this.sesionActual.asReadonly();
  readonly autenticado = computed(() => this.sesionActual() !== null);
  readonly esAdmin = computed(() => this.tieneRol(ROLES_ADMIN));
  readonly esPersonal = computed(() => this.tieneRol(ROLES_PERSONAL));
  readonly esLector = computed(() => this.tieneRol(ROLES_LECTOR));
  /** Según los roles del proyecto, el estudiante paga sus multas; las del docente las cobra el personal. */
  readonly puedePagarMultas = computed(() => this.tieneRol(['ROLE_ESTUDIANTE']));

  login(credenciales: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.url}/login`, credenciales)
      .pipe(tap((respuesta) => this.guardarSesion(respuesta)));
  }

  registrar(datos: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.url}/register`, datos)
      .pipe(tap((respuesta) => this.guardarSesion(respuesta)));
  }

  perfil(): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.url}/me`);
  }

  cerrarSesion(): void {
    localStorage.removeItem(CLAVE_SESION);
    this.sesionActual.set(null);
  }

  tieneRol(roles: Rol[]): boolean {
    const sesion = this.sesionActual();
    return sesion !== null && roles.includes(sesion.rol);
  }

  /** Devuelve el token vigente; si ya expiró, cierra la sesión. */
  token(): string | null {
    const sesion = this.sesionActual();
    if (!sesion) {
      return null;
    }
    if (this.tokenExpirado(sesion.token)) {
      this.cerrarSesion();
      return null;
    }
    return sesion.token;
  }

  private guardarSesion(respuesta: AuthResponse): void {
    const { tipo: _tipo, ...sesion } = respuesta;
    localStorage.setItem(CLAVE_SESION, JSON.stringify(sesion));
    this.sesionActual.set(sesion);
  }

  private leerSesion(): Sesion | null {
    try {
      const guardada = localStorage.getItem(CLAVE_SESION);
      if (!guardada) {
        return null;
      }
      const sesion = JSON.parse(guardada) as Sesion;
      return this.tokenExpirado(sesion.token) ? null : sesion;
    } catch {
      return null;
    }
  }

  private tokenExpirado(token: string): boolean {
    try {
      const carga = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      const { exp } = JSON.parse(atob(carga)) as { exp: number };
      return exp * 1000 <= Date.now();
    } catch {
      return true;
    }
  }
}
