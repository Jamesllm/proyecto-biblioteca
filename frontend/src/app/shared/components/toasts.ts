import { Component, inject } from '@angular/core';
import { NotificacionService } from '../../core/services/notificacion.service';

/** Lista flotante de notificaciones de éxito y error. */
@Component({
  selector: 'app-toasts',
  template: `
    @for (item of servicio.notificaciones(); track item.id) {
      <div class="toast" [class.toast--error]="item.tipo === 'error'" role="status">
        <span>{{ item.mensaje }}</span>
        <button type="button" aria-label="Cerrar" (click)="servicio.cerrar(item.id)">×</button>
      </div>
    }
  `,
  styles: `
    @use 'variables' as *;

    :host {
      position: fixed;
      right: $espacio-m;
      bottom: $espacio-m;
      z-index: 30;
      display: flex;
      flex-direction: column;
      gap: $espacio-s;
      width: min(380px, calc(100vw - 2rem));
    }

    .toast {
      display: flex;
      justify-content: space-between;
      gap: $espacio-m;
      padding: 0.7rem 0.9rem;
      border-radius: $radio;
      background: $color-exito;
      color: #fff;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);

      &--error {
        background: $color-peligro;
      }
    }

    button {
      border: 0;
      background: none;
      color: inherit;
      font-size: 1.2rem;
      line-height: 1;
      cursor: pointer;
    }
  `,
})
export class Toasts {
  protected readonly servicio = inject(NotificacionService);
}
