import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/** Rechaza valores formados únicamente por espacios en blanco. */
export function noSoloEspacios(control: AbstractControl): ValidationErrors | null {
  const valor: unknown = control.value;
  return typeof valor === 'string' && valor.length > 0 && valor.trim().length === 0
    ? { soloEspacios: true }
    : null;
}

/** Validador de grupo: comprueba que dos campos tengan el mismo valor. */
export function camposIguales(campo: string, confirmacion: string): ValidatorFn {
  return (grupo: AbstractControl): ValidationErrors | null =>
    grupo.get(campo)?.value === grupo.get(confirmacion)?.value ? null : { noCoincide: true };
}

/** Exige que la fecha y hora elegidas todavía no hayan pasado. */
export function fechaFutura(control: AbstractControl): ValidationErrors | null {
  if (!control.value) {
    return null;
  }
  return new Date(control.value as string) <= new Date() ? { fechaPasada: true } : null;
}

/** Exige que la fecha no sea anterior a la fecha mínima indicada. */
export function fechaDesde(obtenerMinima: () => string | null): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const minima = obtenerMinima();
    if (!control.value || !minima) {
      return null;
    }
    return new Date(control.value as string) < new Date(minima) ? { fechaAnterior: true } : null;
  };
}
