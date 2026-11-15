import { Component, computed, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Ejemplar, Libro, ROLES_LECTOR, Usuario } from '../../core/models/models';
import { LibroService } from '../../core/services/libro.service';
import { NotificacionService } from '../../core/services/notificacion.service';
import { PrestamoService } from '../../core/services/prestamo.service';
import { UsuarioService } from '../../core/services/usuario.service';
import { mensajeError } from '../../core/utils/api-error';
import { FieldError } from '../../shared/components/field-error';

@Component({
  selector: 'app-prestamo-form',
  imports: [ReactiveFormsModule, RouterLink, FieldError],
  templateUrl: './prestamo-form.html',
})
export class PrestamoForm implements OnInit {
  private readonly prestamos = inject(PrestamoService);
  private readonly libros = inject(LibroService);
  private readonly usuarios = inject(UsuarioService);
  private readonly router = inject(Router);
  private readonly notificaciones = inject(NotificacionService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(NonNullableFormBuilder);

  private readonly todosUsuarios = signal<Usuario[]>([]);
  protected readonly lectores = computed(() =>
    this.todosUsuarios().filter((usuario) => ROLES_LECTOR.includes(usuario.rol)),
  );
  protected readonly catalogo = signal<Libro[]>([]);
  protected readonly disponibles = signal<Ejemplar[]>([]);
  protected readonly enviando = signal(false);
  protected readonly error = signal('');

  protected readonly form = this.fb.group({
    idUsuario: this.fb.control<number | null>(null, Validators.required),
    isbn: ['', Validators.required],
    idEjemplar: this.fb.control<number | null>(null, Validators.required),
  });

  ngOnInit(): void {
    forkJoin({ usuarios: this.usuarios.listar(), libros: this.libros.listar() }).subscribe({
      next: ({ usuarios, libros }) => {
        this.todosUsuarios.set(usuarios);
        this.catalogo.set(libros);
      },
      error: (error: unknown) => this.error.set(mensajeError(error)),
    });

    this.form.controls.isbn.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((isbn) => this.cargarDisponibles(isbn));
  }

  protected registrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { idEjemplar, idUsuario } = this.form.getRawValue();
    this.enviando.set(true);
    this.error.set('');
    this.prestamos.registrar(idEjemplar!, idUsuario!).subscribe({
      next: (prestamo) => {
        this.notificaciones.exito('Préstamo registrado correctamente.');
        void this.router.navigate(['/prestamos', prestamo.id]);
      },
      error: (error: unknown) => {
        this.error.set(mensajeError(error));
        this.enviando.set(false);
      },
    });
  }

  private cargarDisponibles(isbn: string): void {
    this.form.controls.idEjemplar.reset(null);
    this.disponibles.set([]);
    if (!isbn) {
      return;
    }
    this.libros.ejemplaresDisponibles(isbn).subscribe({
      next: (ejemplares) => this.disponibles.set(ejemplares),
      error: (error: unknown) => this.error.set(mensajeError(error)),
    });
  }
}
