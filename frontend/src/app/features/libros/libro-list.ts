import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Libro } from '../../core/models/models';
import { AuthService } from '../../core/services/auth.service';
import { ConfirmacionService } from '../../core/services/confirmacion.service';
import { LibroService } from '../../core/services/libro.service';
import { NotificacionService } from '../../core/services/notificacion.service';
import { mensajeError } from '../../core/utils/api-error';
import { EstadoCarga } from '../../core/utils/estado-carga';
import { DataState } from '../../shared/components/data-state';
import { PageHeader } from '../../shared/components/page-header';
import { SearchBox } from '../../shared/components/search-box';

@Component({
  selector: 'app-libro-list',
  imports: [RouterLink, PageHeader, DataState, SearchBox],
  templateUrl: './libro-list.html',
})
export class LibroList implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly api = inject(LibroService);
  private readonly confirmacion = inject(ConfirmacionService);
  private readonly notificaciones = inject(NotificacionService);

  protected readonly libros = new EstadoCarga<Libro[]>([], () => this.api.listar());
  protected readonly termino = signal('');
  protected readonly categoria = signal('');

  protected readonly categorias = computed(() => {
    const nombres = this.libros.datos().map((libro) => libro.categoria?.nombre ?? '');
    return [...new Set(nombres)].filter(Boolean).sort();
  });

  protected readonly filtrados = computed(() => {
    const termino = this.termino().trim().toLowerCase();
    const categoria = this.categoria();
    return this.libros.datos().filter((libro) => {
      const texto = `${libro.titulo} ${libro.autor?.nombre ?? ''} ${libro.isbn}`.toLowerCase();
      return texto.includes(termino) && (!categoria || libro.categoria?.nombre === categoria);
    });
  });

  ngOnInit(): void {
    this.libros.cargar();
  }

  protected async eliminar(libro: Libro): Promise<void> {
    const confirmado = await this.confirmacion.confirmar({
      titulo: 'Eliminar libro',
      mensaje: `¿Deseas dar de baja "${libro.titulo}"?`,
      textoConfirmar: 'Eliminar',
      peligro: true,
    });
    if (!confirmado) {
      return;
    }
    this.api.eliminar(libro.isbn).subscribe({
      next: () => {
        this.notificaciones.exito('Libro eliminado correctamente.');
        this.libros.cargar();
      },
      error: (error: unknown) => this.notificaciones.error(mensajeError(error)),
    });
  }
}
