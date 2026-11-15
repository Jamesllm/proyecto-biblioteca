import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { NotificacionService } from '../services/notificacion.service';

/** Si la API rechaza un token (HTTP 401), cierra la sesión y envía al usuario al login. */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const notificaciones = inject(NotificacionService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && req.headers.has('Authorization')) {
        auth.cerrarSesion();
        notificaciones.error('Tu sesión expiró. Inicia sesión nuevamente.');
        void router.navigate(['/login'], { queryParams: { returnUrl: router.url } });
      }
      return throwError(() => error);
    }),
  );
};
