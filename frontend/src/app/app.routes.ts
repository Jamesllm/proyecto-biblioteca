import { Routes } from '@angular/router';
import { invitadoGuard, rolGuard } from './core/guards/auth.guards';
import { ROLES_ADMIN, ROLES_LECTOR, ROLES_PERSONAL } from './core/models/models';
import { MAESTROS, MaestroConfig } from './features/maestros/maestro.config';

const cargarLibroForm = () => import('./features/libros/libro-form').then((m) => m.LibroForm);
const cargarEjemplarForm = () => import('./features/ejemplares/ejemplar-form').then((m) => m.EjemplarForm);
const cargarUsuarioForm = () => import('./features/usuarios/usuario-form').then((m) => m.UsuarioForm);
const cargarMultaList = () => import('./features/multas/multa-list').then((m) => m.MultaList);
const cargarReservaList = () => import('./features/reservas/reserva-list').then((m) => m.ReservaList);

/** Las tres tablas maestras comparten componentes y solo cambian su configuración. */
function rutasMaestro(maestro: MaestroConfig): Routes {
  const comun = { canActivate: [rolGuard] };
  const data = { roles: ROLES_PERSONAL, maestro };
  const cargarForm = () => import('./features/maestros/maestro-form').then((m) => m.MaestroForm);
  return [
    {
      path: maestro.recurso,
      title: maestro.titulo,
      loadComponent: () => import('./features/maestros/maestro-list').then((m) => m.MaestroList),
      ...comun,
      data,
    },
    { path: `${maestro.recurso}/nuevo`, title: maestro.titulo, loadComponent: cargarForm, ...comun, data },
    { path: `${maestro.recurso}/:id/editar`, title: maestro.titulo, loadComponent: cargarForm, ...comun, data },
  ];
}

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'libros' },

  // Autenticación
  {
    path: 'login',
    title: 'Iniciar sesión',
    canActivate: [invitadoGuard],
    loadComponent: () => import('./features/auth/login').then((m) => m.Login),
  },
  {
    path: 'registro',
    title: 'Crear cuenta',
    canActivate: [invitadoGuard],
    loadComponent: () => import('./features/auth/registro').then((m) => m.Registro),
  },
  {
    path: 'perfil',
    title: 'Mi perfil',
    canActivate: [rolGuard],
    loadComponent: () => import('./features/perfil/perfil').then((m) => m.Perfil),
  },

  // Catálogo
  {
    path: 'libros',
    title: 'Catálogo',
    loadComponent: () => import('./features/libros/libro-list').then((m) => m.LibroList),
  },
  {
    path: 'libros/nuevo',
    title: 'Nuevo libro',
    canActivate: [rolGuard],
    data: { roles: ROLES_PERSONAL },
    loadComponent: cargarLibroForm,
  },
  {
    path: 'libros/:isbn',
    title: 'Detalle del libro',
    loadComponent: () => import('./features/libros/libro-detail').then((m) => m.LibroDetail),
  },
  {
    path: 'libros/:isbn/editar',
    title: 'Editar libro',
    canActivate: [rolGuard],
    data: { roles: ROLES_PERSONAL },
    loadComponent: cargarLibroForm,
  },
  {
    path: 'libros/:isbn/ejemplares/nuevo',
    title: 'Nuevo ejemplar',
    canActivate: [rolGuard],
    data: { roles: ROLES_PERSONAL },
    loadComponent: cargarEjemplarForm,
  },
  {
    path: 'ejemplares/:id/editar',
    title: 'Editar ejemplar',
    canActivate: [rolGuard],
    data: { roles: ROLES_PERSONAL },
    loadComponent: cargarEjemplarForm,
  },
  ...MAESTROS.flatMap(rutasMaestro),

  // Circulación
  {
    path: 'prestamos',
    title: 'Préstamos',
    canActivate: [rolGuard],
    data: { roles: ROLES_PERSONAL },
    loadComponent: () => import('./features/prestamos/prestamo-list').then((m) => m.PrestamoList),
  },
  {
    path: 'prestamos/nuevo',
    title: 'Registrar préstamo',
    canActivate: [rolGuard],
    data: { roles: ROLES_PERSONAL },
    loadComponent: () => import('./features/prestamos/prestamo-form').then((m) => m.PrestamoForm),
  },
  {
    path: 'prestamos/:id',
    title: 'Detalle del préstamo',
    canActivate: [rolGuard],
    loadComponent: () => import('./features/prestamos/prestamo-detail').then((m) => m.PrestamoDetail),
  },
  {
    path: 'prestamos/:id/devolver',
    title: 'Registrar devolución',
    canActivate: [rolGuard],
    data: { roles: ROLES_PERSONAL },
    loadComponent: () => import('./features/prestamos/devolucion-form').then((m) => m.DevolucionForm),
  },
  {
    path: 'mis-prestamos',
    title: 'Mis préstamos',
    canActivate: [rolGuard],
    data: { roles: ROLES_LECTOR },
    loadComponent: () => import('./features/prestamos/mis-prestamos').then((m) => m.MisPrestamos),
  },
  {
    path: 'reservas',
    title: 'Reservas',
    canActivate: [rolGuard],
    data: { roles: ROLES_PERSONAL, modo: 'todas' },
    loadComponent: cargarReservaList,
  },
  {
    path: 'reservas/:id',
    title: 'Detalle de la reserva',
    canActivate: [rolGuard],
    loadComponent: () => import('./features/reservas/reserva-detail').then((m) => m.ReservaDetail),
  },
  {
    path: 'libros/:isbn/reservar',
    title: 'Reservar título',
    canActivate: [rolGuard],
    data: { roles: ROLES_LECTOR },
    loadComponent: () => import('./features/reservas/reserva-form').then((m) => m.ReservaForm),
  },
  {
    path: 'mis-reservas',
    title: 'Mis reservas',
    canActivate: [rolGuard],
    data: { roles: ROLES_LECTOR, modo: 'mias' },
    loadComponent: cargarReservaList,
  },

  // Multas y pagos
  {
    path: 'multas',
    title: 'Multas',
    canActivate: [rolGuard],
    data: { roles: ROLES_PERSONAL, modo: 'todas' },
    loadComponent: cargarMultaList,
  },
  {
    path: 'mis-multas',
    title: 'Mis multas',
    canActivate: [rolGuard],
    data: { roles: ROLES_LECTOR, modo: 'mias' },
    loadComponent: cargarMultaList,
  },
  {
    path: 'multas/:id/pagar',
    title: 'Pagar multa',
    canActivate: [rolGuard],
    data: { roles: [...ROLES_PERSONAL, 'ROLE_ESTUDIANTE'] },
    loadComponent: () => import('./features/multas/pago-form').then((m) => m.PagoForm),
  },
  {
    path: 'pagos',
    title: 'Pagos de multas',
    canActivate: [rolGuard],
    data: { roles: ROLES_PERSONAL },
    loadComponent: () => import('./features/multas/pago-list').then((m) => m.PagoList),
  },

  // Administración
  {
    path: 'usuarios',
    title: 'Usuarios',
    canActivate: [rolGuard],
    data: { roles: ROLES_ADMIN },
    loadComponent: () => import('./features/usuarios/usuario-list').then((m) => m.UsuarioList),
  },
  {
    path: 'usuarios/nuevo',
    title: 'Nuevo usuario',
    canActivate: [rolGuard],
    data: { roles: ROLES_ADMIN },
    loadComponent: cargarUsuarioForm,
  },
  {
    path: 'usuarios/:id/editar',
    title: 'Editar usuario',
    canActivate: [rolGuard],
    data: { roles: ROLES_ADMIN },
    loadComponent: cargarUsuarioForm,
  },
  {
    path: 'reportes',
    title: 'Reportes',
    canActivate: [rolGuard],
    data: { roles: ROLES_ADMIN },
    loadComponent: () => import('./features/reportes/reportes').then((m) => m.Reportes),
  },

  // Errores
  {
    path: 'acceso-denegado',
    title: 'Acceso denegado',
    loadComponent: () => import('./features/errores/acceso-denegado').then((m) => m.AccesoDenegado),
  },
  {
    path: '**',
    title: 'Página no encontrada',
    loadComponent: () => import('./features/errores/no-encontrado').then((m) => m.NoEncontrado),
  },
];
