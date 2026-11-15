import { Component, inject, input, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ConfirmacionService } from '../../core/services/confirmacion.service';
import { MaestroService } from '../../core/services/maestro.service';
import { NotificacionService } from '../../core/services/notificacion.service';
import { mensajeError } from '../../core/utils/api-error';
import { EstadoCarga } from '../../core/utils/estado-carga';
import { DataState } from '../../shared/components/data-state';
import { PageHeader } from '../../shared/components/page-header';
import { MaestroConfig } from './maestro.config';

type Registro = { id: number } & Record<string, unknown>;

@Component({
  selector: 'app-maestro-list',
  imports: [RouterLink, PageHeader, DataState],
  templateUrl: './maestro-list.html',
})
export class MaestroList implements OnInit {
  private readonly api = inject(MaestroService);
  private readonly confirmacion = inject(ConfirmacionService);
  private readonly notificaciones = inject(NotificacionService);

  /** Configuración recibida desde data de la ruta. */
  readonly maestro = input.required<MaestroConfig>();

  protected readonly registros = new EstadoCarga<Registro[]>([], () =>
    this.api.listar<Registro>(this.maestro().recurso),
  );

  ngOnInit(): void {
    this.registros.cargar();
  }

  protected async eliminar(registro: Registro): Promise<void> {
    const { recurso, singular } = this.maestro();
    const confirmado = await this.confirmacion.confirmar({
      titulo: `Eliminar ${singular}`,
      mensaje: `¿Deseas dar de baja "${String(registro['nombre'])}"?`,
      textoConfirmar: 'Eliminar',
      peligro: true,
    });
    if (!confirmado) {
      return;
    }
    this.api.eliminar(recurso, registro.id).subscribe({
      next: () => {
        this.notificaciones.exito('Registro eliminado correctamente.');
        this.registros.cargar();
      },
      error: (error: unknown) => this.notificaciones.error(mensajeError(error)),
    });
  }
}
