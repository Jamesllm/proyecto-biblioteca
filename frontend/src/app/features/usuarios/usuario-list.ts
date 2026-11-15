import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ETIQUETAS_ROL, Usuario } from '../../core/models/models';
import { AuthService } from '../../core/services/auth.service';
import { ConfirmacionService } from '../../core/services/confirmacion.service';
import { NotificacionService } from '../../core/services/notificacion.service';
import { UsuarioService } from '../../core/services/usuario.service';
import { mensajeError } from '../../core/utils/api-error';
import { EstadoCarga } from '../../core/utils/estado-carga';
import { DataState } from '../../shared/components/data-state';
import { EstadoBadge } from '../../shared/components/estado-badge';
import { PageHeader } from '../../shared/components/page-header';
import { SearchBox } from '../../shared/components/search-box';

@Component({
  selector: 'app-usuario-list',
  imports: [RouterLink, PageHeader, DataState, EstadoBadge, SearchBox],
  templateUrl: './usuario-list.html',
})
export class UsuarioList implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly api = inject(UsuarioService);
  private readonly confirmacion = inject(ConfirmacionService);
  private readonly notificaciones = inject(NotificacionService);

  protected readonly etiquetasRol = ETIQUETAS_ROL;
  protected readonly usuarios = new EstadoCarga<Usuario[]>([], () => this.api.listar());
  protected readonly termino = signal('');
  protected readonly filtrados = computed(() => {
    const termino = this.termino().trim().toLowerCase();
    return this.usuarios
      .datos()
      .filter((usuario) => `${usuario.nombre} ${usuario.email} ${usuario.dni ?? ''}`.toLowerCase().includes(termino));
  });

  ngOnInit(): void {
    this.usuarios.cargar();
  }

  protected async eliminar(usuario: Usuario): Promise<void> {
    const confirmado = await this.confirmacion.confirmar({
      titulo: 'Eliminar usuario',
      mensaje: `¿Deseas dar de baja a "${usuario.nombre}"?`,
      textoConfirmar: 'Eliminar',
      peligro: true,
    });
    if (!confirmado) {
      return;
    }
    this.api.eliminar(usuario.id).subscribe({
      next: () => {
        this.notificaciones.exito('Usuario eliminado correctamente.');
        this.usuarios.cargar();
      },
      error: (error: unknown) => this.notificaciones.error(mensajeError(error)),
    });
  }
}
