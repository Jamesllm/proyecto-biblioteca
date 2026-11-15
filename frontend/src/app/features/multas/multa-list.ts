import { DatePipe } from '@angular/common';
import { Component, computed, inject, input, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin, map, Observable } from 'rxjs';
import { Multa, Usuario } from '../../core/models/models';
import { AuthService } from '../../core/services/auth.service';
import { MultaService } from '../../core/services/multa.service';
import { UsuarioService } from '../../core/services/usuario.service';
import { EstadoCarga } from '../../core/utils/estado-carga';
import { DataState } from '../../shared/components/data-state';
import { EstadoBadge } from '../../shared/components/estado-badge';
import { PageHeader } from '../../shared/components/page-header';
import { SolesPipe } from '../../shared/pipes/soles.pipe';

interface DatosMultas {
  multas: Multa[];
  usuarios: Usuario[];
}

@Component({
  selector: 'app-multa-list',
  imports: [DatePipe, RouterLink, PageHeader, DataState, EstadoBadge, SolesPipe],
  templateUrl: './multa-list.html',
})
export class MultaList implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly multas = inject(MultaService);
  private readonly usuarios = inject(UsuarioService);

  /** Viene de data de la ruta: 'todas' para el personal y 'mias' para el lector. */
  readonly modo = input.required<'todas' | 'mias'>();

  protected readonly soloPendientes = signal(false);
  protected readonly datos = new EstadoCarga<DatosMultas>({ multas: [], usuarios: [] }, () => this.consultar());

  private readonly nombres = computed(
    () => new Map(this.datos.datos().usuarios.map((usuario) => [usuario.id, usuario.nombre])),
  );

  protected readonly visibles = computed(() => {
    const { multas } = this.datos.datos();
    return this.modo() === 'todas' && this.soloPendientes()
      ? multas.filter((multa) => multa.estado === 'PENDIENTE')
      : multas;
  });

  ngOnInit(): void {
    this.datos.cargar();
  }

  protected nombreUsuario(id: number): string {
    return this.nombres().get(id) ?? `Usuario N.° ${id}`;
  }

  protected puedePagar(multa: Multa): boolean {
    return multa.estado === 'PENDIENTE' && (this.modo() === 'todas' || this.auth.puedePagarMultas());
  }

  protected cambiarFiltro(soloPendientes: boolean): void {
    this.soloPendientes.set(soloPendientes);
    if (this.modo() === 'mias') {
      this.datos.cargar();
    }
  }

  private consultar(): Observable<DatosMultas> {
    if (this.modo() === 'todas') {
      return forkJoin({ multas: this.multas.listar(), usuarios: this.usuarios.listar() });
    }
    const id = this.auth.sesion()!.id;
    const consulta = this.soloPendientes() ? this.multas.pendientesDeUsuario(id) : this.usuarios.multas(id);
    return consulta.pipe(map((multas) => ({ multas, usuarios: [] })));
  }
}
