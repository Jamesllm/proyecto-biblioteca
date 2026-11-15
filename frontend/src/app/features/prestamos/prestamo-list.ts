import { DatePipe } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Prestamo } from '../../core/models/models';
import { PrestamoService } from '../../core/services/prestamo.service';
import { EstadoCarga } from '../../core/utils/estado-carga';
import { DataState } from '../../shared/components/data-state';
import { EstadoBadge } from '../../shared/components/estado-badge';
import { PageHeader } from '../../shared/components/page-header';

@Component({
  selector: 'app-prestamo-list',
  imports: [DatePipe, RouterLink, PageHeader, DataState, EstadoBadge],
  templateUrl: './prestamo-list.html',
})
export class PrestamoList implements OnInit {
  private readonly api = inject(PrestamoService);

  protected readonly prestamos = new EstadoCarga<Prestamo[]>([], () => this.api.listar());
  protected readonly estado = signal('');
  protected readonly filtrados = computed(() => {
    const estado = this.estado();
    return this.prestamos.datos().filter((prestamo) => !estado || prestamo.estado === estado);
  });

  ngOnInit(): void {
    this.prestamos.cargar();
  }
}
