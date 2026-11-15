import { Injectable, signal } from '@angular/core';

export interface Notificacion {
  id: number;
  tipo: 'exito' | 'error';
  mensaje: string;
}

@Injectable({ providedIn: 'root' })
export class NotificacionService {
  private siguienteId = 1;
  readonly notificaciones = signal<Notificacion[]>([]);

  exito(mensaje: string): void {
    this.agregar('exito', mensaje);
  }

  error(mensaje: string): void {
    this.agregar('error', mensaje);
  }

  cerrar(id: number): void {
    this.notificaciones.update((lista) => lista.filter((n) => n.id !== id));
  }

  private agregar(tipo: Notificacion['tipo'], mensaje: string): void {
    const id = this.siguienteId++;
    this.notificaciones.update((lista) => [...lista, { id, tipo, mensaje }]);
    setTimeout(() => this.cerrar(id), 6000);
  }
}
