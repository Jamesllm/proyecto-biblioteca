import { Component, inject, input, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MaestroService } from '../../core/services/maestro.service';
import { NotificacionService } from '../../core/services/notificacion.service';
import { aplicarErroresServidor, mensajeError } from '../../core/utils/api-error';
import { noSoloEspacios } from '../../core/validators/validadores';
import { FieldError } from '../../shared/components/field-error';
import { CampoMaestro, MaestroConfig } from './maestro.config';

type FormularioMaestro = FormGroup<Record<string, FormControl<string>>>;

@Component({
  selector: 'app-maestro-form',
  imports: [ReactiveFormsModule, RouterLink, FieldError],
  templateUrl: './maestro-form.html',
})
export class MaestroForm implements OnInit {
  private readonly api = inject(MaestroService);
  private readonly router = inject(Router);
  private readonly notificaciones = inject(NotificacionService);

  readonly maestro = input.required<MaestroConfig>();
  /** Parámetro de ruta; solo existe al editar. */
  readonly id = input<string>();

  protected form!: FormularioMaestro;
  protected readonly enviando = signal(false);
  protected readonly error = signal('');

  ngOnInit(): void {
    const controles: Record<string, FormControl<string>> = {};
    for (const campo of this.maestro().campos) {
      controles[campo.nombre] = new FormControl('', {
        nonNullable: true,
        validators: this.validadores(campo),
      });
    }
    this.form = new FormGroup(controles);

    const id = this.id();
    if (id) {
      this.api.obtener<Record<string, string | null>>(this.maestro().recurso, id).subscribe({
        next: (registro) => {
          for (const campo of this.maestro().campos) {
            this.form.controls[campo.nombre].setValue(registro[campo.nombre] ?? '');
          }
        },
        error: (error: unknown) => this.error.set(mensajeError(error)),
      });
    }
  }

  protected guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { recurso } = this.maestro();
    const id = this.id();
    const datos = this.form.getRawValue();
    const operacion = id ? this.api.actualizar(recurso, id, datos) : this.api.crear(recurso, datos);

    this.enviando.set(true);
    this.error.set('');
    operacion.subscribe({
      next: () => {
        this.notificaciones.exito('Registro guardado correctamente.');
        void this.router.navigate(['/', recurso]);
      },
      error: (error: unknown) => {
        aplicarErroresServidor(this.form, error);
        this.error.set(mensajeError(error));
        this.enviando.set(false);
      },
    });
  }

  private validadores(campo: CampoMaestro): ValidatorFn[] {
    const validadores: ValidatorFn[] = [noSoloEspacios];
    if (campo.requerido) validadores.push(Validators.required);
    if (campo.maxLength) validadores.push(Validators.maxLength(campo.maxLength));
    return validadores;
  }
}
