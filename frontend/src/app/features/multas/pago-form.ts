import { Component, computed, inject, input, OnInit, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Multa } from '../../core/models/models';
import { AuthService } from '../../core/services/auth.service';
import { MultaService } from '../../core/services/multa.service';
import { NotificacionService } from '../../core/services/notificacion.service';
import { aplicarErroresServidor, mensajeError } from '../../core/utils/api-error';
import { FieldError } from '../../shared/components/field-error';
import { SolesPipe } from '../../shared/pipes/soles.pipe';

@Component({
  selector: 'app-pago-form',
  imports: [ReactiveFormsModule, RouterLink, FieldError, SolesPipe],
  templateUrl: './pago-form.html',
})
export class PagoForm implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly api = inject(MultaService);
  private readonly router = inject(Router);
  private readonly notificaciones = inject(NotificacionService);
  private readonly fb = inject(NonNullableFormBuilder);

  /** Parámetro de ruta :id. */
  readonly id = input.required<string>();

  protected readonly metodos = ['EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'YAPE_PLIN'];
  protected readonly multa = signal<Multa | null>(null);
  protected readonly enviando = signal(false);
  protected readonly error = signal('');
  protected readonly rutaRegreso = computed(() => (this.auth.esPersonal() ? '/multas' : '/mis-multas'));

  protected readonly form = this.fb.group({
    montoPagado: this.fb.control<number | null>(null, [Validators.required]),
    metodoPago: ['EFECTIVO', Validators.required],
    comprobante: ['', Validators.maxLength(50)],
  });

  ngOnInit(): void {
    this.api.obtener(this.id()).subscribe({
      next: (multa) => {
        // Un lector solo puede pagar sus propias multas.
        if (this.auth.esLector() && multa.idUsuario !== this.auth.sesion()?.id) {
          void this.router.navigate(['/acceso-denegado']);
          return;
        }
        this.multa.set(multa);
        const monto = this.form.controls.montoPagado;
        monto.setValidators([Validators.required, Validators.min(multa.monto)]);
        monto.setValue(multa.monto);
      },
      error: (error: unknown) => this.error.set(mensajeError(error)),
    });
  }

  protected pagar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { montoPagado, metodoPago, comprobante } = this.form.getRawValue();
    this.enviando.set(true);
    this.error.set('');
    this.api.pagar(this.id(), { montoPagado: montoPagado!, metodoPago, comprobante: comprobante || undefined }).subscribe({
      next: (pago) => {
        this.notificaciones.exito(`Pago registrado. Comprobante: ${pago.comprobante}`);
        void this.router.navigateByUrl(this.rutaRegreso());
      },
      error: (error: unknown) => {
        aplicarErroresServidor(this.form, error);
        this.error.set(mensajeError(error));
        this.enviando.set(false);
      },
    });
  }
}
