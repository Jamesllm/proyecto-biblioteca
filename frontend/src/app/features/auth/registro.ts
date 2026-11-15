import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { RegisterRequest } from '../../core/models/models';
import { AuthService } from '../../core/services/auth.service';
import { NotificacionService } from '../../core/services/notificacion.service';
import { aplicarErroresServidor, mensajeError } from '../../core/utils/api-error';
import { camposIguales, noSoloEspacios } from '../../core/validators/validadores';
import { FieldError } from '../../shared/components/field-error';

@Component({
  selector: 'app-registro',
  imports: [ReactiveFormsModule, RouterLink, FieldError],
  templateUrl: './registro.html',
})
export class Registro {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly notificaciones = inject(NotificacionService);
  private readonly fb = inject(NonNullableFormBuilder);

  protected readonly enviando = signal(false);
  protected readonly error = signal('');
  protected readonly form = this.fb.group(
    {
      nombre: ['', [Validators.required, noSoloEspacios, Validators.maxLength(255)]],
      dni: ['', [Validators.pattern(/^\d{8,15}$/)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmar: ['', Validators.required],
      telefono: ['', Validators.maxLength(30)],
      direccion: ['', Validators.maxLength(255)],
    },
    { validators: camposIguales('password', 'confirmar') },
  );

  protected registrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { nombre, dni, email, password, telefono, direccion } = this.form.getRawValue();
    // El rol no se envía: el backend asigna ROLE_ESTUDIANTE por defecto.
    const datos: RegisterRequest = {
      nombre: nombre.trim(),
      email,
      password,
      dni: dni || undefined,
      telefono: telefono || undefined,
      direccion: direccion || undefined,
    };

    this.enviando.set(true);
    this.error.set('');
    this.auth.registrar(datos).subscribe({
      next: () => {
        this.notificaciones.exito('Tu cuenta fue creada correctamente.');
        void this.router.navigate(['/libros']);
      },
      error: (error: unknown) => {
        aplicarErroresServidor(this.form, error);
        this.error.set(mensajeError(error));
        this.enviando.set(false);
      },
    });
  }
}
