import { KeyValuePipe } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { ReporteConteo, ReporteDashboard, ReporteFinanciero } from '../../core/models/models';
import { ReporteService } from '../../core/services/reporte.service';
import { EstadoCarga } from '../../core/utils/estado-carga';
import { DataState } from '../../shared/components/data-state';
import { PageHeader } from '../../shared/components/page-header';
import { SolesPipe } from '../../shared/pipes/soles.pipe';

interface DatosReportes {
  dashboard: ReporteDashboard;
  categorias: ReporteConteo;
  prestamos: ReporteConteo;
  financiero: ReporteFinanciero;
}

@Component({
  selector: 'app-reportes',
  imports: [KeyValuePipe, PageHeader, DataState, SolesPipe],
  templateUrl: './reportes.html',
})
export class Reportes implements OnInit {
  private readonly api = inject(ReporteService);

  protected readonly reportes = new EstadoCarga<DatosReportes | null>(null, () =>
    forkJoin({
      dashboard: this.api.dashboard(),
      categorias: this.api.librosPorCategoria(),
      prestamos: this.api.prestamos(),
      financiero: this.api.financieroMultas(),
    }),
  );

  /** Conserva en pantalla el orden en que el backend entrega las claves. */
  protected readonly sinOrdenar = () => 0;

  ngOnInit(): void {
    this.reportes.cargar();
  }
}
