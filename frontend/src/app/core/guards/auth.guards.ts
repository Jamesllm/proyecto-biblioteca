import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Rol } from '../models/models';
import { AuthService } from '../services/auth.service';

/** Exige una sesión válida y, si la ruta define data.roles, uno de esos roles. */
export const rolGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.token()) {
    return router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
  }
  const roles = route.data['roles'] as Rol[] | undefined;
  if (roles && !auth.tieneRol(roles)) {
    return router.createUrlTree(['/acceso-denegado']);
  }
  return true;
};

/** Impide que un usuario con sesión vuelva a las pantallas de login y registro. */
export const invitadoGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.token() ? router.createUrlTree(['/libros']) : true;
};
