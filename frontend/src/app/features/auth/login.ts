import { Component, inject, input, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { mensajeError } from '../../core/utils/api-error';
import { FieldError } from '../../shared/components/field-error';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink, FieldError],
  templateUrl: './login.html',
})
export class Login {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly fb = inject(NonNullableFormBuilder);

  /** Ruta a la que se regresa después de iniciar sesión (parámetro de consulta). */
  readonly returnUrl = input<string>();

  protected readonly enviando = signal(false);
  protected readonly error = signal('');
  protected readonly form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  protected ingresar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.error.set('');
    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => void this.router.navigateByUrl(this.returnUrl() ?? '/libros'),
      error: (error: unknown) => {
        this.error.set(mensajeError(error));
        this.enviando.set(false);
      },
    });
  }
}
