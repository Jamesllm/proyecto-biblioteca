import { Component, input, output } from '@angular/core';

/** Presenta los estados de una consulta: cargando, error, vacío o contenido. */
@Component({
  selector: 'app-data-state',
  template: `
    @if (cargando()) {
      <p class="mensaje-estado">Cargando...</p>
    } @else if (error()) {
      <div class="mensaje-estado">
        <p>{{ error() }}</p>
        <button type="button" class="btn" (click)="reintentar.emit()">Reintentar</button>
      </div>
    } @else if (vacio()) {
      <p class="mensaje-estado">{{ mensajeVacio() }}</p>
    } @else {
      <ng-content />
    }
  `,
})
export class DataState {
  readonly cargando = input.required<boolean>();
  readonly error = input('');
  readonly vacio = input(false);
  readonly mensajeVacio = input('No hay registros para mostrar.');
  readonly reintentar = output<void>();
}
