import { DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PrestamoDetallado } from '../../core/models/models';
import { AuthService } from '../../core/services/auth.service';
import { UsuarioService } from '../../core/services/usuario.service';
import { EstadoCarga } from '../../core/utils/estado-carga';
import { DataState } from '../../shared/components/data-state';
import { EstadoBadge } from '../../shared/components/estado-badge';
import { PageHeader } from '../../shared/components/page-header';

@Component({
  selector: 'app-mis-prestamos',
  imports: [DatePipe, RouterLink, PageHeader, DataState, EstadoBadge],
  templateUrl: './mis-prestamos.html',
})
export class MisPrestamos implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly usuarios = inject(UsuarioService);

  protected readonly soloActivos = signal(true);
  protected readonly prestamos = new EstadoCarga<PrestamoDetallado[]>([], () => {
    const id = this.auth.sesion()!.id;
    return this.soloActivos() ? this.usuarios.prestamosActivos(id) : this.usuarios.prestamos(id);
  });

  ngOnInit(): void {
    this.prestamos.cargar();
  }

  protected cambiarVista(soloActivos: boolean): void {
    this.soloActivos.set(soloActivos);
    this.prestamos.cargar();
  }
}
