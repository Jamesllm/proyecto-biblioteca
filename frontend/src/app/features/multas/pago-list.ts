import { DatePipe } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { PagoMulta } from '../../core/models/models';
import { MultaService } from '../../core/services/multa.service';
import { EstadoCarga } from '../../core/utils/estado-carga';
import { DataState } from '../../shared/components/data-state';
import { PageHeader } from '../../shared/components/page-header';
import { SolesPipe } from '../../shared/pipes/soles.pipe';

@Component({
  selector: 'app-pago-list',
  imports: [DatePipe, PageHeader, DataState, SolesPipe],
  template: `
    <app-page-header titulo="Pagos de multas" />
    <app-data-state
      [cargando]="pagos.cargando()"
      [error]="pagos.error()"
      [vacio]="pagos.datos().length === 0"
      mensajeVacio="Todavía no se han registrado pagos."
      (reintentar)="pagos.cargar()"
    >
      <div class="tabla-contenedor">
        <table class="tabla">
          <thead>
            <tr>
              <th>Comprobante</th>
              <th>Multa N.°</th>
              <th>Fecha</th>
              <th>Método</th>
              <th class="numero">Monto</th>
            </tr>
          </thead>
          <tbody>
            @for (pago of pagos.datos(); track pago.id) {
              <tr>
                <td class="dato">{{ pago.comprobante }}</td>
                <td>{{ pago.idMulta }}</td>
                <td>{{ pago.fechaHoraPago | date: 'dd/MM/yyyy HH:mm' }}</td>
                <td>{{ pago.metodoPago }}</td>
                <td class="numero">{{ pago.montoPagado | soles }}</td>
              </tr>
            }
          </tbody>
        </table>
      </div>
    </app-data-state>
  `,
})
export class PagoList implements OnInit {
  private readonly api = inject(MultaService);

  protected readonly pagos = new EstadoCarga<PagoMulta[]>([], () => this.api.listarPagos());

  ngOnInit(): void {
    this.pagos.cargar();
  }
}
