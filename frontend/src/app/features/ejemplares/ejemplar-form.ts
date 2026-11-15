import { Component, inject, input, OnInit, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Ejemplar } from '../../core/models/models';
import { EjemplarService } from '../../core/services/ejemplar.service';
import { NotificacionService } from '../../core/services/notificacion.service';
import { aplicarErroresServidor, mensajeError } from '../../core/utils/api-error';
import { FieldError } from '../../shared/components/field-error';

@Component({
  selector: 'app-ejemplar-form',
  imports: [ReactiveFormsModule, RouterLink, FieldError],
  templateUrl: './ejemplar-form.html',
})
export class EjemplarForm implements OnInit {
  private readonly api = inject(EjemplarService);
  private readonly router = inject(Router);
  private readonly notificaciones = inject(NotificacionService);
  private readonly fb = inject(NonNullableFormBuilder);

  /** Parámetro :isbn al crear un ejemplar. */
  readonly isbn = input<string>();
  /** Parámetro :id al editar un ejemplar. */
  readonly id = input<string>();

  protected readonly conservaciones = ['EXCELENTE', 'BUENO', 'REGULAR', 'DETERIORADO', 'DAÑADO'];
  protected readonly estados = ['DISPONIBLE', 'PRESTADO', 'EN_MANTENIMIENTO', 'DADO_DE_BAJA'];
  protected readonly isbnLibro = signal('');
  protected readonly enviando = signal(false);
  protected readonly error = signal('');

  protected readonly form = this.fb.group({
    numeroCopia: this.fb.control<number | null>(null, [Validators.required, Validators.min(1)]),
    ubicacion: ['', Validators.maxLength(150)],
    estadoConservacion: ['BUENO', Validators.required],
    estado: ['DISPONIBLE', Validators.required],
  });

  ngOnInit(): void {
    const id = this.id();
    if (!id) {
      this.isbnLibro.set(this.isbn() ?? '');
      return;
    }
    this.api.obtener(Number(id)).subscribe({
      next: (ejemplar) => {
        this.isbnLibro.set(ejemplar.isbn);
        this.form.patchValue({ ...ejemplar, ubicacion: ejemplar.ubicacion ?? '' });
        // El estado PRESTADO lo controla el flujo de préstamos y devoluciones.
        if (ejemplar.estado === 'PRESTADO') {
          this.form.controls.estado.disable();
        }
      },
      error: (error: unknown) => this.error.set(mensajeError(error)),
    });
  }

  protected guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const datos = { ...this.form.getRawValue(), isbn: this.isbnLibro() } as Partial<Ejemplar>;
    const id = this.id();
    const operacion = id ? this.api.actualizar(Number(id), datos) : this.api.crear(datos);

    this.enviando.set(true);
    this.error.set('');
    operacion.subscribe({
      next: () => {
        this.notificaciones.exito('Ejemplar guardado correctamente.');
        void this.router.navigate(['/libros', this.isbnLibro()]);
      },
      error: (error: unknown) => {
        aplicarErroresServidor(this.form, error);
        this.error.set(mensajeError(error));
        this.enviando.set(false);
      },
    });
  }
}
