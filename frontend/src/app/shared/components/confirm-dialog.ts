import { Component, inject } from '@angular/core';
import { ConfirmacionService } from '../../core/services/confirmacion.service';

/** Diálogo modal para confirmar operaciones sensibles. */
@Component({
  selector: 'app-confirm-dialog',
  template: `
    @if (servicio.solicitud(); as solicitud) {
      <div class="fondo">
        <div class="dialogo" role="dialog" aria-modal="true">
          <h2>{{ solicitud.titulo }}</h2>
          <p>{{ solicitud.mensaje }}</p>
          <div class="acciones">
            <button type="button" class="btn" (click)="servicio.responder(false)">Volver</button>
            <button
              type="button"
              class="btn"
              [class.btn--peligro]="solicitud.peligro"
              [class.btn--primario]="!solicitud.peligro"
              (click)="servicio.responder(true)"
            >
              {{ solicitud.textoConfirmar }}
            </button>
          </div>
        </div>
      </div>
    }
  `,
  styles: `
    @use 'variables' as *;

    .fondo {
      position: fixed;
      inset: 0;
      z-index: 40;
      display: grid;
      place-items: center;
      padding: $espacio-m;
      background: rgba($color-marino, 0.55);
    }

    .dialogo {
      width: min(420px, 100%);
      padding: $espacio-l;
      border-radius: $radio;
      background: $color-superficie;
    }

    h2 {
      font-size: 1.2rem;
    }

    .acciones {
      justify-content: flex-end;
    }
  `,
})
export class ConfirmDialog {
  protected readonly servicio = inject(ConfirmacionService);
}
