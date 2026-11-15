/** Interfaces que reflejan los modelos y DTO expuestos por la API REST. */

export type Rol = 'ROLE_ADMIN' | 'ROLE_BIBLIOTECARIO' | 'ROLE_DOCENTE' | 'ROLE_ESTUDIANTE';

export const ROLES_PERSONAL: Rol[] = ['ROLE_ADMIN', 'ROLE_BIBLIOTECARIO'];
export const ROLES_LECTOR: Rol[] = ['ROLE_ESTUDIANTE', 'ROLE_DOCENTE'];
export const ROLES_ADMIN: Rol[] = ['ROLE_ADMIN'];

export const ETIQUETAS_ROL: Record<Rol, string> = {
  ROLE_ADMIN: 'Administrador',
  ROLE_BIBLIOTECARIO: 'Bibliotecario',
  ROLE_DOCENTE: 'Docente',
  ROLE_ESTUDIANTE: 'Estudiante',
};

export interface Sesion {
  token: string;
  id: number;
  dni: string | null;
  nombre: string;
  email: string;
  rol: Rol;
  tipoUsuario: string;
  estado: string;
}

export interface AuthResponse extends Sesion {
  tipo: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  dni?: string;
  nombre: string;
  email: string;
  password: string;
  telefono?: string;
  direccion?: string;
  tipoUsuario?: string;
  rol?: Rol;
}

export interface Autor {
  id: number;
  nombre: string;
  nacionalidad: string | null;
  biografia: string | null;
}

export interface Categoria {
  id: number;
  nombre: string;
  descripcion: string | null;
}

export interface Editorial {
  id: number;
  nombre: string;
  pais: string | null;
  contacto: string | null;
}

export interface Libro {
  isbn: string;
  titulo: string;
  sinopsis: string | null;
  idAutor: number;
  idCategoria: number;
  idEditorial: number;
  anioPublicacion: number;
  autor?: Autor | null;
  categoria?: Categoria | null;
  editorial?: Editorial | null;
}

export interface Ejemplar {
  id: number;
  isbn: string;
  numeroCopia: number;
  ubicacion: string | null;
  estadoConservacion: string;
  estado: string;
  libro?: Libro | null;
}

export interface Usuario {
  id: number;
  dni: string | null;
  nombre: string;
  email: string;
  telefono: string | null;
  direccion: string | null;
  tipoUsuario: string;
  rol: Rol;
  estado: string;
}

export interface Prestamo {
  id: number;
  idEjemplar: number;
  idUsuario: number;
  fechaHoraPrestamo: string;
  fechaHoraDevolucionEsperada: string;
  fechaHoraDevolucionReal: string | null;
  estado: string;
  ejemplar?: Ejemplar | null;
  usuario?: Usuario | null;
}

export interface PrestamoDetallado {
  idPrestamo: number;
  estadoPrestamo: string;
  fechaHoraPrestamo: string;
  fechaHoraDevolucionEsperada: string;
  fechaHoraDevolucionReal: string | null;
  enMora: boolean;
  horasRetraso: number;
  diasRetraso: number;
  horasRestantes: number;
  usuario: Usuario | null;
  ejemplar: Ejemplar | null;
  libro: Libro | null;
  multa: Multa | null;
}

export interface DevolucionRequest {
  fechaHoraDevolucionReal: string;
  estadoConservacion: string;
  observaciones?: string;
  montoDanoExtra?: number;
}

export interface Multa {
  id: number;
  idPrestamo: number;
  idUsuario: number;
  tipoMulta: string;
  horasRetraso: number;
  diasRetraso: number;
  monto: number;
  motivo: string | null;
  fechaHoraEmision: string;
  estado: string;
}

export interface PagoMulta {
  id: number;
  idMulta: number;
  montoPagado: number;
  fechaHoraPago: string;
  metodoPago: string;
  comprobante: string | null;
}

export interface PagoMultaRequest {
  montoPagado: number;
  metodoPago: string;
  comprobante?: string;
}

export interface Reserva {
  id: number;
  isbn: string;
  idUsuario: number;
  fechaHoraReserva: string;
  fechaHoraExpiracion: string | null;
  estado: string;
  libro?: Libro | null;
  usuario?: Usuario | null;
}

export interface ReporteDashboard {
  fechaReporte: string;
  catalogo: {
    totalTitulosIsbn: number;
    totalEjemplaresFisicos: number;
    ejemplaresDisponibles: number;
    ejemplaresPrestados: number;
    ejemplaresEnMantenimiento: number;
    totalAutores: number;
    totalCategorias: number;
    totalEditoriales: number;
  };
  usuarios: { totalRegistrados: number; activos: number; sancionados: number };
  prestamos: { totalRegistrados: number; activos: number; devueltos: number; vencidos: number };
  multas: {
    totalMultas: number;
    pagadas: number;
    pendientes: number;
    montoTotal: number;
    montoRecaudado: number;
    montoPendiente: number;
    totalPagosProcesados: number;
  };
}

export interface ReporteConteo {
  tipoReporte: string;
  total?: number;
  datos: Record<string, number>;
}

export interface ReporteFinanciero {
  tipoReporte: string;
  totalMultas: number;
  montoTotal: number;
  recaudado: number;
  pendiente: number;
}
