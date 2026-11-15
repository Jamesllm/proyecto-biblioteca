import { Injectable, signal } from '@angular/core';

export interface SolicitudConfirmacion {
  titulo: string;
  mensaje: string;
  textoConfirmar: string;
  peligro?: boolean;
}

@Injectable({ providedIn: 'root' })
export class ConfirmacionService {
  private resolver: ((respuesta: boolean) => void) | null = null;
  readonly solicitud = signal<SolicitudConfirmacion | null>(null);

  confirmar(solicitud: SolicitudConfirmacion): Promise<boolean> {
    this.solicitud.set(solicitud);
    return new Promise((resolve) => (this.resolver = resolve));
  }

  responder(respuesta: boolean): void {
    this.resolver?.(respuesta);
    this.resolver = null;
    this.solicitud.set(null);
  }
}
