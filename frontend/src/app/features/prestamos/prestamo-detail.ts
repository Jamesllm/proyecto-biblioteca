import { DatePipe } from '@angular/common';
import { Component, inject, input, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { tap } from 'rxjs';
import { PrestamoDetallado } from '../../core/models/models';
import { AuthService } from '../../core/services/auth.service';
import { PrestamoService } from '../../core/services/prestamo.service';
import { EstadoCarga } from '../../core/utils/estado-carga';
import { DataState } from '../../shared/components/data-state';
import { EstadoBadge } from '../../shared/components/estado-badge';
import { PageHeader } from '../../shared/components/page-header';
import { SolesPipe } from '../../shared/pipes/soles.pipe';

@Component({
  selector: 'app-prestamo-detail',
  imports: [DatePipe, RouterLink, PageHeader, DataState, EstadoBadge, SolesPipe],
  templateUrl: './prestamo-detail.html',
})
export class PrestamoDetail implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly api = inject(PrestamoService);
  private readonly router = inject(Router);

  /** Parámetro de ruta :id. */
  readonly id = input.required<string>();

  protected readonly detalle = new EstadoCarga<PrestamoDetallado | null>(null, () =>
    this.api.detalle(this.id()).pipe(
      tap((prestamo) => {
        // Un lector solo puede ver sus propios préstamos.
        if (this.auth.esLector() && prestamo.usuario?.id !== this.auth.sesion()?.id) {
          void this.router.navigate(['/acceso-denegado']);
        }
      }),
    ),
  );

  ngOnInit(): void {
    this.detalle.cargar();
  }
}
