import { Component, inject, input, OnInit, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { ETIQUETAS_ROL, Rol, Usuario } from '../../core/models/models';
import { NotificacionService } from '../../core/services/notificacion.service';
import { UsuarioService } from '../../core/services/usuario.service';
import { aplicarErroresServidor, mensajeError } from '../../core/utils/api-error';
import { noSoloEspacios } from '../../core/validators/validadores';
import { FieldError } from '../../shared/components/field-error';

@Component({
  selector: 'app-usuario-form',
  imports: [ReactiveFormsModule, RouterLink, FieldError],
  templateUrl: './usuario-form.html',
})
export class UsuarioForm implements OnInit {
  private readonly api = inject(UsuarioService);
  private readonly router = inject(Router);
  private readonly notificaciones = inject(NotificacionService);
  private readonly fb = inject(NonNullableFormBuilder);

  /** Parámetro de ruta; solo existe al editar. */
  readonly id = input<string>();

  protected readonly roles = Object.entries(ETIQUETAS_ROL) as [Rol, string][];
  protected readonly tipos = ['ESTUDIANTE', 'DOCENTE', 'INVESTIGADOR', 'EXTERNO'];
  protected readonly estados = ['ACTIVO', 'SANCIONADO', 'INACTIVO'];
  protected readonly enviando = signal(false);
  protected readonly error = signal('');

  protected readonly form = this.fb.group({
    nombre: ['', [Validators.required, noSoloEspacios, Validators.maxLength(255)]],
    dni: ['', Validators.pattern(/^\d{8,15}$/)],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    telefono: ['', Validators.maxLength(30)],
    direccion: ['', Validators.maxLength(255)],
    tipoUsuario: ['ESTUDIANTE', Validators.required],
    rol: this.fb.control<Rol>('ROLE_ESTUDIANTE', Validators.required),
    estado: ['ACTIVO', Validators.required],
  });

  ngOnInit(): void {
    const id = this.id();
    if (!id) {
      return;
    }
    // Al editar no se cambia la contraseña y el backend no actualiza el rol.
    this.form.controls.password.disable();
    this.form.controls.rol.disable();
    this.api.obtener(Number(id)).subscribe({
      next: (usuario) =>
        this.form.patchValue({
          ...usuario,
          dni: usuario.dni ?? '',
          telefono: usuario.telefono ?? '',
          direccion: usuario.direccion ?? '',
        }),
      error: (error: unknown) => this.error.set(mensajeError(error)),
    });
  }

  protected guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.error.set('');
    this.operacion().subscribe({
      next: () => {
        this.notificaciones.exito('Usuario guardado correctamente.');
        void this.router.navigate(['/usuarios']);
      },
      error: (error: unknown) => {
        aplicarErroresServidor(this.form, error);
        this.error.set(mensajeError(error));
        this.enviando.set(false);
      },
    });
  }

  private operacion(): Observable<unknown> {
    const { password, estado, ...datos } = this.form.getRawValue();
    const comunes = {
      ...datos,
      nombre: datos.nombre.trim(),
      dni: datos.dni || undefined,
      telefono: datos.telefono || undefined,
      direccion: datos.direccion || undefined,
    };
    const id = this.id();
    return id
      ? this.api.actualizar(Number(id), { ...comunes, estado } as Partial<Usuario>)
      : this.api.registrar({ ...comunes, password });
  }
}
