import { Component, inject, input, OnInit, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Autor, Categoria, Editorial, Libro } from '../../core/models/models';
import { LibroService } from '../../core/services/libro.service';
import { MaestroService } from '../../core/services/maestro.service';
import { NotificacionService } from '../../core/services/notificacion.service';
import { aplicarErroresServidor, mensajeError } from '../../core/utils/api-error';
import { noSoloEspacios } from '../../core/validators/validadores';
import { FieldError } from '../../shared/components/field-error';

@Component({
  selector: 'app-libro-form',
  imports: [ReactiveFormsModule, RouterLink, FieldError],
  templateUrl: './libro-form.html',
})
export class LibroForm implements OnInit {
  private readonly libros = inject(LibroService);
  private readonly maestros = inject(MaestroService);
  private readonly router = inject(Router);
  private readonly notificaciones = inject(NotificacionService);
  private readonly fb = inject(NonNullableFormBuilder);

  /** Parámetro de ruta; solo existe al editar. */
  readonly isbn = input<string>();

  protected readonly autores = signal<Autor[]>([]);
  protected readonly categorias = signal<Categoria[]>([]);
  protected readonly editoriales = signal<Editorial[]>([]);
  protected readonly enviando = signal(false);
  protected readonly error = signal('');
  protected readonly anioActual = new Date().getFullYear();

  protected readonly form = this.fb.group({
    isbn: ['', [Validators.required, Validators.maxLength(20), Validators.pattern(/^[0-9][0-9Xx-]{8,19}$/)]],
    titulo: ['', [Validators.required, noSoloEspacios, Validators.maxLength(255)]],
    sinopsis: [''],
    idAutor: this.fb.control<number | null>(null, Validators.required),
    idCategoria: this.fb.control<number | null>(null, Validators.required),
    idEditorial: this.fb.control<number | null>(null, Validators.required),
    anioPublicacion: this.fb.control<number | null>(null, [
      Validators.required,
      Validators.min(1000),
      Validators.max(this.anioActual),
    ]),
  });

  ngOnInit(): void {
    forkJoin({
      autores: this.maestros.listar<Autor>('autores'),
      categorias: this.maestros.listar<Categoria>('categorias'),
      editoriales: this.maestros.listar<Editorial>('editoriales'),
    }).subscribe({
      next: ({ autores, categorias, editoriales }) => {
        this.autores.set(autores);
        this.categorias.set(categorias);
        this.editoriales.set(editoriales);
      },
      error: (error: unknown) => this.error.set(mensajeError(error)),
    });

    const isbn = this.isbn();
    if (isbn) {
      this.form.controls.isbn.disable();
      this.libros.obtener(isbn).subscribe({
        next: (libro) =>
          this.form.patchValue({
            isbn: libro.isbn,
            titulo: libro.titulo,
            sinopsis: libro.sinopsis ?? '',
            idAutor: libro.idAutor,
            idCategoria: libro.idCategoria,
            idEditorial: libro.idEditorial,
            anioPublicacion: libro.anioPublicacion,
          }),
        error: (error: unknown) => this.error.set(mensajeError(error)),
      });
    }
  }

  protected guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const datos = this.form.getRawValue() as Partial<Libro>;
    const isbn = this.isbn();
    const operacion = isbn ? this.libros.actualizar(isbn, datos) : this.libros.crear(datos);

    this.enviando.set(true);
    this.error.set('');
    operacion.subscribe({
      next: (libro) => {
        this.notificaciones.exito('Libro guardado correctamente.');
        void this.router.navigate(['/libros', libro.isbn]);
      },
      error: (error: unknown) => {
        aplicarErroresServidor(this.form, error);
        this.error.set(mensajeError(error));
        this.enviando.set(false);
      },
    });
  }
}
