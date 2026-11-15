import { Component, computed, inject, input, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Ejemplar, Libro } from '../../core/models/models';
import { AuthService } from '../../core/services/auth.service';
import { ConfirmacionService } from '../../core/services/confirmacion.service';
import { EjemplarService } from '../../core/services/ejemplar.service';
import { LibroService } from '../../core/services/libro.service';
import { NotificacionService } from '../../core/services/notificacion.service';
import { PrestamoService } from '../../core/services/prestamo.service';
import { mensajeError } from '../../core/utils/api-error';
import { EstadoCarga } from '../../core/utils/estado-carga';
import { DataState } from '../../shared/components/data-state';
import { EstadoBadge } from '../../shared/components/estado-badge';
import { PageHeader } from '../../shared/components/page-header';

interface DetalleLibro {
  libro: Libro | null;
  ejemplares: Ejemplar[];
}

@Component({
  selector: 'app-libro-detail',
  imports: [RouterLink, PageHeader, DataState, EstadoBadge],
  templateUrl: './libro-detail.html',
})
export class LibroDetail implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly libros = inject(LibroService);
  private readonly ejemplares = inject(EjemplarService);
  private readonly prestamos = inject(PrestamoService);
  private readonly confirmacion = inject(ConfirmacionService);
  private readonly notificaciones = inject(NotificacionService);

  /** Parámetro de ruta :isbn. */
  readonly isbn = input.required<string>();

  protected readonly detalle = new EstadoCarga<DetalleLibro>({ libro: null, ejemplares: [] }, () =>
    forkJoin({
      libro: this.libros.obtener(this.isbn()),
      ejemplares: this.libros.ejemplares(this.isbn()),
    }),
  );

  protected readonly hayDisponibles = computed(() =>
    this.detalle.datos().ejemplares.some((ejemplar) => ejemplar.estado === 'DISPONIBLE'),
  );

  ngOnInit(): void {
    this.detalle.cargar();
  }

  protected async solicitar(ejemplar: Ejemplar): Promise<void> {
    const sesion = this.auth.sesion();
    if (!sesion) {
      return;
    }
    const confirmado = await this.confirmacion.confirmar({
      titulo: 'Solicitar préstamo',
      mensaje: `Se registrará el préstamo de la copia N.° ${ejemplar.numeroCopia} con un plazo de 14 días.`,
      textoConfirmar: 'Solicitar',
    });
    if (!confirmado) {
      return;
    }
    this.prestamos.registrar(ejemplar.id, sesion.id).subscribe({
      next: () => {
        this.notificaciones.exito('Préstamo registrado. Revísalo en "Mis préstamos".');
        this.detalle.cargar();
      },
      error: (error: unknown) => this.notificaciones.error(mensajeError(error)),
    });
  }

  protected async eliminarEjemplar(ejemplar: Ejemplar): Promise<void> {
    const confirmado = await this.confirmacion.confirmar({
      titulo: 'Eliminar ejemplar',
      mensaje: `¿Deseas dar de baja la copia N.° ${ejemplar.numeroCopia}?`,
      textoConfirmar: 'Eliminar',
      peligro: true,
    });
    if (!confirmado) {
      return;
    }
    this.ejemplares.eliminar(ejemplar.id).subscribe({
      next: () => {
        this.notificaciones.exito('Ejemplar eliminado correctamente.');
        this.detalle.cargar();
      },
      error: (error: unknown) => this.notificaciones.error(mensajeError(error)),
    });
  }
}
