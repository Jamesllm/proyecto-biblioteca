import { Component, computed, effect, input, signal } from '@angular/core';
import { AbstractControl } from '@angular/forms';

/** Muestra el mensaje de validación de un campo cuando ya fue tocado. */
@Component({
  selector: 'app-field-error',
  template: `
    @if (mensaje(); as texto) {
      <small class="error-campo" role="alert">{{ texto }}</small>
    }
  `,
})
export class FieldError {
  readonly control = input.required<AbstractControl>();
  readonly etiqueta = input('Este campo');
  readonly mensajeFormato = input('El formato no es válido.');

  /** Se incrementa con cada evento del control para recalcular el mensaje. */
  private readonly cambios = signal(0);

  constructor() {
    effect((alLimpiar) => {
      const suscripcion = this.control().events.subscribe(() => this.cambios.update((n) => n + 1));
      alLimpiar(() => suscripcion.unsubscribe());
    });
  }

  protected readonly mensaje = computed(() => {
    this.cambios();
    const control = this.control();
    const errores = control.errors;
    if (!errores || !control.touched) {
      return '';
    }
    if (errores['servidor']) return String(errores['servidor']);
    if (errores['required']) return 'Este campo es obligatorio.';
    if (errores['soloEspacios']) return `${this.etiqueta()} no puede contener solo espacios.`;
    if (errores['email']) return 'Ingresa un correo electrónico válido.';
    if (errores['minlength']) return `Debe tener al menos ${errores['minlength'].requiredLength} caracteres.`;
    if (errores['maxlength']) return `No debe superar ${errores['maxlength'].requiredLength} caracteres.`;
    if (errores['min']) return `El valor mínimo es ${errores['min'].min}.`;
    if (errores['max']) return `El valor máximo es ${errores['max'].max}.`;
    if (errores['pattern']) return this.mensajeFormato();
    if (errores['fechaAnterior']) return 'La fecha no puede ser anterior a la del préstamo.';
    if (errores['fechaPasada']) return 'Elige una fecha y hora que todavía no hayan pasado.';
    return 'El valor ingresado no es válido.';
  });
}
