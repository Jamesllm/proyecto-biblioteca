import { signal, WritableSignal } from '@angular/core';
import { Observable } from 'rxjs';
import { mensajeError } from './api-error';

/** Agrupa los datos de una consulta HTTP con sus estados de carga y error. */
export class EstadoCarga<T> {
  readonly datos: WritableSignal<T>;
  readonly cargando = signal(true);
  readonly error = signal('');

  constructor(
    valorInicial: T,
    private readonly fuente: () => Observable<T>,
  ) {
    this.datos = signal<T>(valorInicial);
  }

  cargar(): void {
    this.cargando.set(true);
    this.error.set('');
    this.fuente().subscribe({
      next: (datos) => {
        this.datos.set(datos);
        this.cargando.set(false);
      },
      error: (error: unknown) => {
        this.error.set(mensajeError(error));
        this.cargando.set(false);
      },
    });
  }
}
