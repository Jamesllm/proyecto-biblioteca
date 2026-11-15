import { Component, inject, OnInit } from '@angular/core';
import { ETIQUETAS_ROL, Usuario } from '../../core/models/models';
import { AuthService } from '../../core/services/auth.service';
import { EstadoCarga } from '../../core/utils/estado-carga';
import { DataState } from '../../shared/components/data-state';
import { EstadoBadge } from '../../shared/components/estado-badge';
import { PageHeader } from '../../shared/components/page-header';

@Component({
  selector: 'app-perfil',
  imports: [PageHeader, DataState, EstadoBadge],
  template: `
    <app-page-header titulo="Mi perfil" />
    <app-data-state [cargando]="perfil.cargando()" [error]="perfil.error()" (reintentar)="perfil.cargar()">
      @if (perfil.datos(); as usuario) {
        <section class="ficha">
          <dl>
            <dt>Nombre</dt>
            <dd>{{ usuario.nombre }}</dd>
            <dt>DNI</dt>
            <dd>{{ usuario.dni || '—' }}</dd>
            <dt>Correo</dt>
            <dd>{{ usuario.email }}</dd>
            <dt>Teléfono</dt>
            <dd>{{ usuario.telefono || '—' }}</dd>
            <dt>Dirección</dt>
            <dd>{{ usuario.direccion || '—' }}</dd>
            <dt>Tipo de usuario</dt>
            <dd>{{ usuario.tipoUsuario }}</dd>
            <dt>Rol</dt>
            <dd>{{ etiquetasRol[usuario.rol] }}</dd>
            <dt>Estado</dt>
            <dd><app-estado-badge [estado]="usuario.estado" /></dd>
          </dl>
        </section>
      }
    </app-data-state>
  `,
})
export class Perfil implements OnInit {
  private readonly auth = inject(AuthService);

  protected readonly etiquetasRol = ETIQUETAS_ROL;
  protected readonly perfil = new EstadoCarga<Usuario | null>(null, () => this.auth.perfil());

  ngOnInit(): void {
    this.perfil.cargar();
  }
}
