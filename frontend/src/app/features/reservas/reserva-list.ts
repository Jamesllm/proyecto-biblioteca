import { DatePipe } from '@angular/common';
import { Component, inject, input, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { Reserva } from '../../core/models/models';
import { AuthService } from '../../core/services/auth.service';
import { ConfirmacionService } from '../../core/services/confirmacion.service';
import { NotificacionService } from '../../core/services/notificacion.service';
import { ReservaService } from '../../core/services/reserva.service';
import { mensajeError } from '../../core/utils/api-error';
import { EstadoCarga } from '../../core/utils/estado-carga';
import { DataState } from '../../shared/components/data-state';
import { EstadoBadge } from '../../shared/components/estado-badge';
import { PageHeader } from '../../shared/components/page-header';

@Component({
  selector: 'app-reserva-list',
  imports: [DatePipe, RouterLink, PageHeader, DataState, EstadoBadge],
  templateUrl: './reserva-list.html',
})
export class ReservaList implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly api = inject(ReservaService);
  private readonly confirmacion = inject(ConfirmacionService);
  private readonly notificaciones = inject(NotificacionService);

  /** Viene de data de la ruta: 'todas' para el personal y 'mias' para el lector. */
  readonly modo = input.required<'todas' | 'mias'>();

  protected readonly reservas = new EstadoCarga<Reserva[]>([], () =>
    this.modo() === 'todas' ? this.api.listar() : this.api.deUsuario(this.auth.sesion()!.id),
  );

  ngOnInit(): void {
    this.reservas.cargar();
  }

  protected atender(reserva: Reserva): void {
    this.ejecutar(this.api.atender(reserva.id), 'Reserva marcada como atendida.');
  }

  protected async cancelar(reserva: Reserva): Promise<void> {
    const confirmado = await this.confirmacion.confirmar({
      titulo: 'Cancelar reserva',
      mensaje: `¿Deseas cancelar la reserva de "${reserva.libro?.titulo ?? reserva.isbn}"?`,
      textoConfirmar: 'Cancelar reserva',
      peligro: true,
    });
    if (confirmado) {
      this.ejecutar(this.api.cancelar(reserva.id), 'Reserva cancelada correctamente.');
    }
  }

  private ejecutar(operacion: Observable<string>, mensaje: string): void {
    operacion.subscribe({
      next: () => {
        this.notificaciones.exito(mensaje);
        this.reservas.cargar();
      },
      error: (error: unknown) => this.notificaciones.error(mensajeError(error)),
    });
  }
}
