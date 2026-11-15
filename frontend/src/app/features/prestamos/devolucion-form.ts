import { Component, inject, input, OnInit, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { DevolucionRequest, PrestamoDetallado } from '../../core/models/models';
import { NotificacionService } from '../../core/services/notificacion.service';
import { PrestamoService } from '../../core/services/prestamo.service';
import { mensajeError } from '../../core/utils/api-error';
import { ahoraLocal } from '../../core/utils/fechas';
import { fechaDesde } from '../../core/validators/validadores';
import { FieldError } from '../../shared/components/field-error';

@Component({
  selector: 'app-devolucion-form',
  imports: [ReactiveFormsModule, RouterLink, FieldError],
  templateUrl: './devolucion-form.html',
})
export class DevolucionForm implements OnInit {
  private readonly api = inject(PrestamoService);
  private readonly router = inject(Router);
  private readonly notificaciones = inject(NotificacionService);
  private readonly fb = inject(NonNullableFormBuilder);

  /** Parámetro de ruta :id. */
  readonly id = input.required<string>();

  protected readonly conservaciones = ['EXCELENTE', 'BUENO', 'REGULAR', 'DETERIORADO', 'DAÑADO', 'EXTRAVIADO'];
  protected readonly prestamo = signal<PrestamoDetallado | null>(null);
  protected readonly enviando = signal(false);
  protected readonly error = signal('');

  protected readonly form = this.fb.group({
    fechaHoraDevolucionReal: [
      ahoraLocal(),
      [Validators.required, fechaDesde(() => this.prestamo()?.fechaHoraPrestamo ?? null)],
    ],
    estadoConservacion: ['BUENO', Validators.required],
    montoDanoExtra: this.fb.control<number | null>(null, Validators.min(0)),
    observaciones: [''],
  });

  ngOnInit(): void {
    this.api.detalle(this.id()).subscribe({
      next: (prestamo) => {
        this.prestamo.set(prestamo);
        this.form.controls.fechaHoraDevolucionReal.updateValueAndValidity();
      },
      error: (error: unknown) => this.error.set(mensajeError(error)),
    });
  }

  protected registrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { fechaHoraDevolucionReal, estadoConservacion, montoDanoExtra, observaciones } = this.form.getRawValue();
    const datos: DevolucionRequest = {
      fechaHoraDevolucionReal: `${fechaHoraDevolucionReal}:00`,
      estadoConservacion,
      observaciones: observaciones || undefined,
      montoDanoExtra: montoDanoExtra ?? undefined,
    };

    this.enviando.set(true);
    this.error.set('');
    this.api.devolver(this.id(), datos).subscribe({
      next: () => {
        this.notificaciones.exito('Devolución registrada correctamente.');
        void this.router.navigate(['/prestamos', this.id()]);
      },
      error: (error: unknown) => {
        this.error.set(mensajeError(error));
        this.enviando.set(false);
      },
    });
  }
}
