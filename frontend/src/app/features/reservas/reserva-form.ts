import { Component, inject, input, OnInit, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Libro } from '../../core/models/models';
import { AuthService } from '../../core/services/auth.service';
import { LibroService } from '../../core/services/libro.service';
import { NotificacionService } from '../../core/services/notificacion.service';
import { ReservaService } from '../../core/services/reserva.service';
import { aplicarErroresServidor, mensajeError } from '../../core/utils/api-error';
import { ahoraLocal } from '../../core/utils/fechas';
import { fechaFutura } from '../../core/validators/validadores';
import { FieldError } from '../../shared/components/field-error';

@Component({
  selector: 'app-reserva-form',
  imports: [ReactiveFormsModule, RouterLink, FieldError],
  templateUrl: './reserva-form.html',
})
export class ReservaForm implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly reservas = inject(ReservaService);
  private readonly libros = inject(LibroService);
  private readonly router = inject(Router);
  private readonly notificaciones = inject(NotificacionService);
  private readonly fb = inject(NonNullableFormBuilder);

  /** Parámetro de ruta :isbn. */
  readonly isbn = input.required<string>();

  protected readonly libro = signal<Libro | null>(null);
  protected readonly enviando = signal(false);
  protected readonly error = signal('');
  protected readonly fechaMinima = ahoraLocal();

  protected readonly form = this.fb.group({
    fechaHoraReserva: ['', [Validators.required, fechaFutura]],
  });

  ngOnInit(): void {
    this.libros.obtener(this.isbn()).subscribe({
      next: (libro) => this.libro.set(libro),
      error: (error: unknown) => this.error.set(mensajeError(error)),
    });
  }

  protected reservar(): void {
    const sesion = this.auth.sesion();
    // Se revalida al enviar porque la hora elegida pudo pasar con el formulario abierto.
    this.form.controls.fechaHoraReserva.updateValueAndValidity();
    if (this.form.invalid || !sesion) {
      this.form.markAllAsTouched();
      return;
    }
    const { fechaHoraReserva } = this.form.getRawValue();

    this.enviando.set(true);
    this.error.set('');
    this.reservas.crear(this.isbn(), sesion.id, `${fechaHoraReserva}:00`).subscribe({
      next: (reserva) => {
        this.notificaciones.exito('Reserva registrada correctamente.');
        void this.router.navigate(['/reservas', reserva.id]);
      },
      error: (error: unknown) => {
        aplicarErroresServidor(this.form, error);
        this.error.set(mensajeError(error));
        this.enviando.set(false);
      },
    });
  }
}
