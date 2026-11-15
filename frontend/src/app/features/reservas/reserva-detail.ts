import { DatePipe } from '@angular/common';
import { Component, inject, input, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { tap } from 'rxjs';
import { Reserva } from '../../core/models/models';
import { AuthService } from '../../core/services/auth.service';
import { ReservaService } from '../../core/services/reserva.service';
import { EstadoCarga } from '../../core/utils/estado-carga';
import { DataState } from '../../shared/components/data-state';
import { EstadoBadge } from '../../shared/components/estado-badge';
import { PageHeader } from '../../shared/components/page-header';

@Component({
  selector: 'app-reserva-detail',
  imports: [DatePipe, RouterLink, PageHeader, DataState, EstadoBadge],
  templateUrl: './reserva-detail.html',
})
export class ReservaDetail implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly api = inject(ReservaService);
  private readonly router = inject(Router);

  /** Parámetro de ruta :id. */
  readonly id = input.required<string>();

  protected readonly detalle = new EstadoCarga<Reserva | null>(null, () =>
    this.api.obtener(this.id()).pipe(
      tap((reserva) => {
        // Un lector solo puede ver sus propias reservas.
        if (this.auth.esLector() && reserva.idUsuario !== this.auth.sesion()?.id) {
          void this.router.navigate(['/acceso-denegado']);
        }
      }),
    ),
  );

  ngOnInit(): void {
    this.detalle.cargar();
  }
}
