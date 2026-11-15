import { HttpErrorResponse } from '@angular/common/http';
import { FormGroup } from '@angular/forms';

/** Convierte un error HTTP de la API en un mensaje comprensible para el usuario. */
export function mensajeError(error: unknown): string {
  if (!(error instanceof HttpErrorResponse)) {
    return 'Ocurrió un error inesperado.';
  }
  if (error.status === 0) {
    return 'No se pudo conectar con el servidor. Verifica que el backend esté en ejecución.';
  }

  const cuerpo: unknown = error.error;
  if (typeof cuerpo === 'string' && cuerpo.trim() && !cuerpo.trimStart().startsWith('<')) {
    return cuerpo;
  }
  if (cuerpo && typeof cuerpo === 'object' && 'mensaje' in cuerpo) {
    return String(cuerpo.mensaje);
  }

  switch (error.status) {
    case 401:
      return 'Tu sesión no es válida. Inicia sesión nuevamente.';
    case 403:
      return 'No tienes permisos para realizar esta operación.';
    case 404:
      return 'El registro solicitado no existe.';
    case 409:
      return 'La operación entra en conflicto con el estado actual del registro.';
    default:
      return 'El servidor no pudo completar la operación. Revisa que los datos no estén duplicados.';
  }
}

/** Muestra en cada campo los errores de validación devueltos por el backend (HTTP 400). */
export function aplicarErroresServidor(form: FormGroup, error: unknown): void {
  if (!(error instanceof HttpErrorResponse)) {
    return;
  }
  const errores: unknown = error.error?.errores;
  if (!errores || typeof errores !== 'object') {
    return;
  }
  for (const [campo, mensaje] of Object.entries(errores)) {
    const control = form.get(campo);
    control?.setErrors({ servidor: String(mensaje) });
    control?.markAsTouched();
  }
}
